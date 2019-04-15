/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.sql.SQLException;

/**
 *
 * @author amer
 */
public class TableChecker {

  private dbHandler dbh;

  public TableChecker() {
    dbh = new dbHandler("BIPDB");
    dbh.setDBConnInfo(ConfigHandler.get("infranet.BIP.DBtns"));
    dbh.setUserPass(ConfigHandler.get("infranet.BIP.DBuser"), ConfigHandler.get("infranet.BIP.DBpassword"));
  }

  public void connect() throws SQLException {
    dbh.openConnection();
  }

  /*
  
  create table tm_ebill_queue (
    job_id integer,
    queue_time timestamp(0) default sysdate,
    process_time timestamp(0),
    completed_time timestamp(0),
    status varchar2(1)
  );
  
   */
  public boolean checkQueueTable() {
    // check if the required table exists
    try {
      dbh.executeSelect("select 1 from tm_ebill_queue");
    } catch (Exception e) {

      if (e.getMessage().contains("table") && e.getMessage().contains("not exist")) {
        // create the table
        try {
          dbh.executeUpdate("create table tm_ebill_queue (\n"
                  + "    job_id integer,\n"
                  + "    queue_time timestamp(0) default sysdate,\n"
                  + "    process_time timestamp(0),\n"
                  + "    completed_time timestamp(0),\n"
                  + "    status varchar2(1)\n"
                  + "  )");
        } catch (SQLException sqle) {
          System.err.println("Error creating the queue table");
          sqle.printStackTrace();
          return false;
        }

      } else {
        System.err.println("Error while checking for main queue table");
        e.printStackTrace();
        return false;
      }

    }

    return true;
  }
  
  public boolean checkDetailTable() {
    // check if the required table exists
    try {
      dbh.executeSelect("select 1 from tm_ebill_detail");
    } catch (Exception e) {

      if (e.getMessage().contains("table") && e.getMessage().contains("not exist")) {
        // create the table
        try {
          dbh.executeUpdate("create table tm_ebill_detail (\n"
                  + "    job_id integer,\n"
                  + "    bill_no varchar2(20),\n"
                  + "    process_time timestamp(0),\n"
                  + "    status varchar2(1)\n"
                  + "  )");
        } catch (SQLException sqle) {
          System.err.println("Error creating the detail table");
          sqle.printStackTrace();
          return false;
        }

      } else {
        System.err.println("Error while checking for detail table");
        e.printStackTrace();
        return false;
      }

    }

    return true;
  }
  
  public boolean checkProcessTable() {
    // check if the required table exists
    try {
      dbh.executeSelect("select 1 from tm_ebill_processed");
    } catch (Exception e) {

      if (e.getMessage().contains("table") && e.getMessage().contains("not exist")) {
        // create the table
        try {
          dbh.executeUpdate("create table tm_ebill_processed (\n"
                  + "    job_id integer,\n"
                  + "    hostname varchar2(50),\n"
                  + "    CONSTRAINT ebill_process_pk PRIMARY KEY (job_id) \n"
                  + "  )");
        } catch (SQLException sqle) {
          System.err.println("Error creating the processed table");
          sqle.printStackTrace();
          return false;
        }

      } else {
        System.err.println("Error while checking for processed table");
        e.printStackTrace();
        return false;
      }

    }

    return true;
  }
  
  public boolean checkFailedTable() {
    // check if the required table exists
    try {
      dbh.executeSelect("select 1 from tm_ebill_failed");
    } catch (Exception e) {

      if (e.getMessage().contains("table") && e.getMessage().contains("not exist")) {
        // create the table
        try {
          dbh.executeUpdate("create table tm_ebill_failed (\n"
                  + "    job_id integer,\n"
                  + "    bill_no varchar2(20),\n"
                  + "    reason varchar2(255),\n"
                  + "    status varchar2(1)\n"
                  + "  )");
        } catch (SQLException sqle) {
          System.err.println("Error creating the failed table");
          sqle.printStackTrace();
          return false;
        }

      } else {
        System.err.println("Error while checking for failed ebill table");
        e.printStackTrace();
        return false;
      }

    }

    return true;
  }
  
  public void cleanup(){
    try {
      dbh.closeConnection();
    } catch (Exception e) {
    }
  }

}
