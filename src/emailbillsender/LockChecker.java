/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 *
 * @author amer
 */
public class LockChecker implements Runnable {

  protected ExecutorService pool;

  public LockChecker(ExecutorService p) {
    pool = p;
  }

  @Override
  public void run() {
    try {
      File lf = new File(".emailsvc.lock");

      if (!lf.exists()) {
        Utilities.log("lock file has been removed. shutting down worker threads", 0);
        pool.shutdown();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
