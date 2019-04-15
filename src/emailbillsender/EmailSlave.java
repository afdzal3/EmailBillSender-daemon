/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.File;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.mail.MessagingException;

/**
 *
 * @author amer
 */
public class EmailSlave implements Runnable {

  private ArrayList<String> billnolist;
  private dbHandler dbStg;
  private dbHandler dbApps;
  private int jobid;
  private int errcount;
  private EmailSender es;
  private volatile int index;
  private Iterator it;
  private String pdfDir;

  public EmailSlave() {
    index = 0;
    billnolist = new ArrayList<String>();
    pdfDir = ConfigHandler.get("PDFLocation");
    
    dbStg = new dbHandler("s bipdb");
    dbStg.setDBConnInfo(ConfigHandler.get("infranet.Stg.DBtns"));
    dbStg.setUserPass(ConfigHandler.get("infranet.Stg.DBuser"), ConfigHandler.get("infranet.Stg.DBpassword"));
    
    dbApps = new dbHandler("s apps");
    dbApps.setDBConnInfo(ConfigHandler.get("infranet.Apps.DBtns"));
    dbApps.setUserPass(ConfigHandler.get("infranet.Apps.DBuser"), ConfigHandler.get("infranet.Apps.DBpassword"));
    
  }

  /**
   * add the bill in the job queue
   *
   * @param billno
   */
  public void addBill(String billno) {
    billnolist.add(billno);
  }

  public void clearBillList() {
    billnolist.clear();
    index = 0;
  }

  public int getLoadSize() {
    return billnolist.size();
  }
  
  public void connectDB() throws SQLException{
    dbStg.openConnection();
    dbApps.openConnection();
  }

  public void init(int jobid)  {
    errcount = 0;
    it = billnolist.iterator();
    this.jobid = jobid;
  }

  public int getCritErrorCount() {
    return errcount;
  }

  public void cleanup() {
    try {
      dbStg.closeConnection();
      dbApps.closeConnection();
      billnolist.clear();
    } catch (Exception e) {
    }
  }

  /**
   * flow: <br>
   * 0. inits then for each bills <br>
   * 1. get the bill info from BIP data backup table <br>
   * 2. find the pdf <br>
   * 3. build the email <br>
   * 4. send the email <br>
   * 5. update the status <br>
   * 6. cleanup (delete from ref and file)
   */
  @Override
  public void run() {
    long tid = Thread.currentThread().getId();
    File indir = new File(pdfDir);
    if (!indir.isDirectory()) {
      log(tid, "invalid input pdf dir", 1);
      critError();
      return;
    }
    EmailSender es = new EmailSender("smtp.tm.com.my", false);
    try {
      es.init();
    } catch (Exception e) {
      log(tid, "unable to initialize Email Sender", 1);
      Utilities.logStack(e);
      critError();
      return;
    }

    // create local prepared statements
    PreparedStatement psGetBillInfo;
    PreparedStatement psUpdateEbillDetail;
    PreparedStatement psPickupEbillDetail;
    PreparedStatement psUpdateBillExtended; // might not be applicable for ICP
    PreparedStatement psInsertSentMail;
    PreparedStatement psInsertDetailError;
    PreparedStatement psDeleteRef;

    try {
      psGetBillInfo = dbStg.createPS("select * from tm_ebill_reference where bill_no = ? ");

      psUpdateEbillDetail = dbStg.createPS("update TM_EBILL_DETAIL set status = ? where bill_no = ? and job_id = " + jobid);
      psPickupEbillDetail = dbStg.createPS("update TM_EBILL_DETAIL set status = 'P', process_time = sysdate where bill_no = ? and job_id = " + jobid);

      psUpdateBillExtended = dbApps.createPS("update bill_extended_t set email_sent = 1 where bill_no = ? and bill_media = 0 ");

      psInsertSentMail = dbApps.createPS("insert into TM_BILL_SENT_MAIL (bill_no, account_no, to_email, cc_email, bip_status) values (?, ?, ?, ?, 'success')");

      psInsertDetailError = dbStg.createPS("insert into TM_EBILL_FAILED (job_id, bill_no, reason, status) values (" + jobid + ", ?, ?, 'F')");
      
      psDeleteRef = dbStg.createPS("delete from tm_ebill_reference where bill_no = ? ");

    } catch (Exception e) {
      log(tid, "unable to initialize prepared statements", 1);
      Utilities.logStack(e);
      critError();
      return;
    }

    String billno = getNextBill();

    while (!billno.equals("-1")) {

//      log(tid, billno, 0);

      // 1. get the bill info
      try {
        pickupDetail(billno, psPickupEbillDetail);
        
        psGetBillInfo.setString(1, billno);
        ResultSet rs = psGetBillInfo.executeQuery();
        if (rs.next()) {

          String ACCOUNT_NO = dbHandler.dbGetString(rs, "ACCOUNT_NO");
//          String BILL_NO = dbHandler.dbGetString(rs, "BILL_NO");
          String BILL_TYPE = dbHandler.dbGetString(rs, "BILL_TYPE");
          String BILL_PERIOD = dbHandler.dbGetString(rs, "BILL_PERIOD");
          String BILL_STREAM = dbHandler.dbGetString(rs, "BILL_STREAM");
          String TOEMAIL = dbHandler.dbGetString(rs, "TOEMAIL");
          String CCEMAIL = dbHandler.dbGetString(rs, "CCEMAIL");
          Date BILL_DATE = rs.getDate("BILL_DATE");
          Date BILL_DUE_DATE = rs.getDate("BILL_DUE_DATE");
          double TOTAL_OUTSTANDING = rs.getDouble("TOTAL_OUTSTANDING");
          String CURRENCY = dbHandler.dbGetString(rs, "CURRENCY");
          String BILL_SEG = dbHandler.dbGetString(rs, "BILL_SEG");
          double CMC = rs.getDouble("CMC");
          double PREVIOUS_OST = rs.getDouble("PREVIOUS_OST");
          // todo: get outstanding and cmc

          // 2. find the pdf
          String pdfname = "HSBB_BP" + BILL_PERIOD + "_" + BILL_STREAM
                  + "_" + Utilities.dateFormat(BILL_DATE, "yyyyMMdd") + "_" + billno
                  + "_" + (BILL_TYPE.equals("0") ? "CP" : "IM") + ".pdf";
          File pdffile = new File(indir, pdfname);

          if (pdffile.isFile()) {
            // 3 + 4. send the email
            try {
//              sendEmail(es, pdffile, ACCOUNT_NO, Utilities.dateFormat(BILL_DATE, ""), ACCOUNT_NO, pdfname, TOEMAIL);
              
              // 5. update the statuses
              updateOtherStatuses(tid, billno, psInsertSentMail, psUpdateBillExtended, ACCOUNT_NO, TOEMAIL, CCEMAIL);
              
              // then clean it
              clean(billno, psDeleteRef, indir);
            } catch (Exception e) {
              updateDetailStatus(tid, billno, psUpdateEbillDetail, "E", psInsertDetailError, "error send: " + e.getMessage());
            }
          } else {
            // pdf file not found
            updateDetailStatus(tid, billno, psUpdateEbillDetail, "E", psInsertDetailError, "pdf not found: " + pdfname);
          }

        } else {
          updateDetailStatus(tid, billno, psUpdateEbillDetail, "E", psInsertDetailError, "no info in tm_ebill_reference");
        }

      } catch (Exception e) {
//        hasError(billno, "S#1: " + e.getMessage());
        updateDetailStatus(tid, billno, psUpdateEbillDetail, "E", psInsertDetailError, e.toString());
        Utilities.logStack(e);
      }

      billno = getNextBill();
    }

  }

  private synchronized String getNextBill() {
    String val = "-1";
    if (it.hasNext()) {
      val = (String) it.next();
    }

    return val;

  }

  private synchronized void critError() {
    errcount++;
  }
  
  private void clean(String billno, PreparedStatement psDeleteRef, File indir){
    
    try {
      // first, remove from reference table
      psDeleteRef.setString(1, billno);
      psDeleteRef.executeUpdate();
      
      // then, remove the pdf file
      BillPdfFilter pbf = new BillPdfFilter(billno);
      
      File[] flist = indir.listFiles(pbf);
      for(File f : flist){
        f.delete();
      }
      
    } catch (Exception e) {
    }
    
    
  }

  
  private void pickupDetail(String billno, PreparedStatement psPickupEbillDetail) throws SQLException{
    psPickupEbillDetail.setString(1, billno);
    psPickupEbillDetail.executeUpdate();
  }

  /**
   * do necessary action if successfully sent<br />
   * eg: remove from data ref table, update bill delivery status, etc
   *
   * @param billno
   */
  private void updateDetailStatus(long tid, String billno, PreparedStatement psUpdateEbillDetail, String status,
           PreparedStatement psInsertDetailError, String reason) {
    try {
      psUpdateEbillDetail.setString(1, status);
      psUpdateEbillDetail.setString(2, billno);

      psUpdateEbillDetail.executeUpdate();

      if (status.equals("E")) {
        // add to error details
        psInsertDetailError.setString(1, billno);
        psInsertDetailError.setString(2, reason);
        psInsertDetailError.execute();
      }

    } catch (Exception e) {
      log(tid, "error updating detail status: j#" + jobid + " b#" + billno, 1);
      Utilities.logStack(e);
    }
  }
  
  

  private void sendEmail(EmailSender es, File inf, String bano, String enddate, String amount, String name, String emailaddr) throws MessagingException {

      es.send(name, bano, enddate, amount, inf, emailaddr, "");

  }
  
  private void updateOtherStatuses(long tid, String billno, PreparedStatement psInsertSentMail, PreparedStatement psUpdateBillExtended
          , String account_no, String to_email, String cc_email
  ){
    // first, insert into tm_bill_sent_mail
    // insert into TM_BILL_SENT_MAIL (bill_no, account_no, to_email, cc_email, bip_status) values (?, ?, ?, ?, 'success')
    try {
      psInsertSentMail.setString(1, billno);
      psInsertSentMail.setString(2, account_no);
      psInsertSentMail.setString(3, to_email);
      psInsertSentMail.setString(4, cc_email);
      
      psInsertSentMail.executeUpdate();
      
    } catch (SQLException e) {
      log(tid, "error inserting to TM_BILL_SENT_MAIL: j#" + jobid + " b#" + billno + " - " + e.getMessage(), 1);
      Utilities.logStack(e);
    }
    
    // then update bill_extended_T
    try {
      psUpdateBillExtended.setString(1, billno);
      psUpdateBillExtended.executeUpdate();
    } catch (SQLException e) {
      log(tid, "error updating bill_extended_t: j#" + jobid + " b#" + billno + " - " + e.getMessage(), 1);
      Utilities.logStack(e);
    }
    
    
  }

  private void log(long tid, String line, int mode) {
    Utilities.log("T#" + tid + ": " + line, mode);
  }
  
  class BillPdfFilter implements FilenameFilter{

    private final String bill_no;
    
    public BillPdfFilter(String billno){
      bill_no = billno;
    }
    
    @Override
    public boolean accept(File dir, String name) {
      return (name.contains(bill_no) && name.toLowerCase().endsWith("pdf"));
    }
    
    
  }

}
