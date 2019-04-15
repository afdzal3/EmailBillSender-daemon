/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author amer
 */
public class EmailManager implements Runnable {

  private ArrayList<Thread> tlist;
  private dbHandler dbstg;
  private int jobid;
  private int prev_job_id;
  private EmailSlave es;
  private String hostname;
  private int threadcount;

  /**
   * this class will check if there's any new job to be processed.<br />
   * if there is any, it will spawn the child threads to process it
   */
  public EmailManager() {

    tlist = new ArrayList<Thread>();
    // prepare the worker thread
    es = new EmailSlave();
    threadcount = ConfigHandler.getInt("ThreadCount");
    hostname = ConfigHandler.get("HostName");
    prev_job_id = 0;

  }

  @Override
  public void run() {
    Utilities.log("Checking for new job", 0);
    // always handle unexpected exception so that this object wont die
    try {

      // 1. connect to DB
      try {
        init();
      } catch (SQLException e) {
        Utilities.log("unable to connect to db: " + e.getMessage(), 1);
        return;
      }

      // 2. get next job id
      while (getJobID()) {

        // 3. load the bills for this job
        try {
          loadBills();
        } catch (SQLException e) {
          Utilities.log("error loading bill nos for job#" + jobid + " : " + e.getMessage(), jobid);
          updateJobStatus("F", "completed_time");
          break;
        }

        // start the threads
        es.init(jobid);
        startThreads();

        // updatae the status
        if (es.getCritErrorCount() == threadcount) {
          updateJobStatus("F", "completed_time");
        } else {
          updateJobStatus("C", "completed_time");
        }

      }

      // cleanup
      cleanup();

    } catch (Exception e) {
      Utilities.logStack(e);
    }

  }

  private void startThreads() {
    Utilities.log("Starting slaves", 0);

    tlist.clear();

    for (int i = 0; i < threadcount; i++) {
      tlist.add(new Thread(es));
    }

    for (Thread t : tlist) {
      t.start();
    }

    for (Thread t : tlist) {
      try {
        t.join();
      } catch (Exception e) {
      }

    }

    Utilities.log("All slaves completed", 0);

  }

  private void init() throws SQLException {
    dbstg = new dbHandler("m staging");
    dbstg.setDBConnInfo(ConfigHandler.get("infranet.Stg.DBtns"));
    dbstg.setUserPass(ConfigHandler.get("infranet.Stg.DBuser"), ConfigHandler.get("infranet.Stg.DBpassword"));
    dbstg.openConnection();

    es.connectDB();
  }

  private void cleanup() {
    try {
      dbstg.closeConnection();
      es.cleanup();
    } catch (Exception e) {
    }

  }

  private void updateJobStatus(String status) {

  }

  private boolean getJobID() {
    jobid = -1;
    while (true) {

      try {
        ResultSet rs = dbstg.executeSelect("select max(job_id) mv from tm_ebill_queue where status = 'N'");
        if (rs.next()) {
          String val = dbHandler.dbGetString(rs, "mv");
          if (!val.isEmpty()) {
            jobid = Integer.parseInt(val);

            // if it's the same as previous result, something went wrong. reset it
            if (jobid == prev_job_id) {
              Utilities.log("getting same job id as previous run: " + jobid, 1);
              jobid = -1;
              break;
            }
            prev_job_id = jobid;

            // try to 'book' this job
            try {
              dbstg.executeUpdate("insert into tm_ebill_processed (job_id, hostname) values (" + jobid + ", '" + hostname + "')");

              // then update the job status
              try {
                updateJobStatus("P", "process_time");
              } catch (SQLException e) {
                Utilities.log("unable to update job status P for #" + jobid + ": " + e.getMessage(), jobid);
              }

              break;

            } catch (SQLException e) {
              // if it errors here, it should means someone else already took that job
            }
          } else {
            // it returns null. meaning no more job to process
            break;
          }
        }

      } catch (Exception e) {
        Utilities.log("error getting new job: " + e.getMessage(), 1);
        Utilities.logStack(e);
        break;
      }

      // wait 2 secs before looking for new job id
      try {
        Thread.sleep(2000);
      } catch (Exception e) {
      }

    }

    return jobid != -1;
  }

  private void loadBills() throws SQLException {
    es.clearBillList();
    Utilities.log("Loading bills for job " + jobid, 0);

    ResultSet rs = dbstg.executeSelect("select bill_no from tm_ebill_detail where job_id = " + jobid);

    while (rs.next()) {
      es.addBill(dbHandler.dbGetString(rs, "bill_no"));
    }

    Utilities.log(es.getLoadSize() + " bills loaded", 0);

  }

  private void updateJobStatus(String status, String col) throws SQLException {
    String sql = "update tm_ebill_queue set status = '" + status + "'"
            + ", " + col + " = sysdate"
            + " where job_id = " + jobid;

    dbstg.executeUpdate(sql);

  }

}
