/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author amer
 */
public class Main {
  

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    
    
    try {
      ConfigHandler.loadConfig(constant.CONFIG_FILE);
      ConfigHandler.loadConfig(constant.DBCONN_FILE);
    } catch (Exception e) {
      System.err.println("Error loading config files");
      e.printStackTrace();
      System.exit(1);
    }
    lockCheck();
    envCheck();
    
    int delay = ConfigHandler.getInt("JobIdleInterval");
    
//    envCheck();
    
    ScheduledExecutorService theSlavePool = Executors.newSingleThreadScheduledExecutor();
//    lockCheck();
    
    theSlavePool.scheduleWithFixedDelay(new EmailManager(), 0, delay, TimeUnit.MINUTES);
    theSlavePool.scheduleWithFixedDelay(new LockChecker(theSlavePool), 0, 10, TimeUnit.SECONDS);
    
  }
  
  private static void lockCheck(){
    File lf = new File(".emailsvc.lock");
    if(lf.exists()){
      System.out.println("Service already running..");
      System.exit(0);
    }
    
//    create the locl file
    try {
      lf.createNewFile();
//      lf.deleteOnExit();
    } catch (IOException e) {
      System.out.println("unable to create lock file");
      System.exit(1);
    }
    
    
  }
  
  private static void envCheck(){
    
    TableChecker tc = new TableChecker();
    
    // try to connect to db
    try {
      tc.connect();
    } catch (SQLException e) {
      System.err.println("Error connecting to DB");
      e.printStackTrace();
      System.exit(1);
    }
    
    int errcount = 0;
    
    errcount += tc.checkQueueTable() ? 0 : 1;
    errcount += tc.checkFailedTable()? 0 : 1;
    errcount += tc.checkDetailTable()? 0 : 1;
    errcount += tc.checkProcessTable()? 0 : 1;
    
    tc.cleanup();
    
    if(errcount > 0){
      System.exit(1);
    }
    
  }
  
  
}
