/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author S53788
 */
public class dFileWriter {

//  private FileOutputStream fos;
  private final FileWriter fw;
  private final BufferedWriter bw;
//  private PrintStream ps;
  private final String inFileName;
  private final String FILE_SEPARATOR;
  private final String LINE_SEPARATOR;

  /**
   * File writer class. It will create the file initially in current directory (same directory with the jar file)
   * 
   * @param filename desired file name
   * @param append append to the end of the file if the file exist
   * @throws IOException 
   */
  public dFileWriter(String filename, boolean append) throws IOException {
    this.FILE_SEPARATOR = System.getProperty("file.separator");
//    this.LINE_SEPARATOR = System.getProperty("line.separator");
    this.LINE_SEPARATOR = "\n";


    fw = new FileWriter(filename, append);
    bw = new BufferedWriter(fw);

    System.out.println("Writer init: " + filename);

    inFileName = filename;
  }
  
  /**
   * File writer class. It will create the file in the specified directory
   * @param filename desired file name
   * @param workingdir where the file should be initially created
   * @param append append to the end of the file if the file exist
   * @throws IOException 
   */
  public dFileWriter(String filename, String workingdir, boolean append) throws IOException {
    this.FILE_SEPARATOR = System.getProperty("file.separator");
//    this.LINE_SEPARATOR = System.getProperty("line.separator");
    this.LINE_SEPARATOR = "\n";
//    fos = new FileOutputStream(filename, true);
//    ps = new PrintStream(fos);
    
    File wdir = new File(workingdir);
    if(!wdir.isDirectory()){
      throw new FileNotFoundException("Working dir not found: " + workingdir);
    }

    inFileName = workingdir + FILE_SEPARATOR + filename;
    
    fw = new FileWriter(inFileName, append);
    bw = new BufferedWriter(fw);

    System.out.println("Writer init: " + inFileName);

    
  }

  public void println(String text) throws IOException {
    //ps.println(text);
    bw.write(text + LINE_SEPARATOR);
  }

  public FileWriter getPrintStream() {
    return fw;
  }

  public void print(String text) throws IOException {
//    ps.print(text);
    bw.write(text);
  }

  /**
   * Finalize the file, moving it the specified destination directory
   * @param destination directory of where the file should be. use "-1" to leave the file where it is
   * @return 0 if successful
   * @throws IOException 
   */
  public int flush(String destination) throws IOException {
//    ps.close();
//    fos.close();

    bw.close();
    fw.close();

    if (destination.equals("-1")) {
      return 0;
    }

    File in = new File(inFileName);
    if (in.length() == 0) {
      return 0;
    }
    String fname = in.getName();

    if (!destination.endsWith(FILE_SEPARATOR)) {
      destination += FILE_SEPARATOR;
    }

    if (!in.renameTo(new File(destination + fname))) {
      System.err.println("Fail to move file : " + inFileName);
      return 1;
    }
    return 0;
  }
  
  /**
   * cancel this writer. closing the stream and delete the generated files
   */
  public void cancel() throws IOException{
    bw.close();
    fw.close();
    
    File in = new File(inFileName);
    in.deleteOnExit();
    
  }

  
}
