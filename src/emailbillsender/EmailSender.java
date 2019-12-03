/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author amer
 */
public class EmailSender {

  private Properties prop;
  private Session ses;
  private MimeMessage msg;
  private final String curSys;
  private final String sender;

  // all of the attachments
  private BodyPart pAutopay;
  private BodyPart pPaynowBM;
  private BodyPart pPaynowEn;
  private BodyPart pWherepayBM;
  private BodyPart pWherepayEn;
  private BodyPart pFooterFb;
  private BodyPart pFootLinkdinTmone;
  private BodyPart pFooterTmBM;
  private BodyPart pFooterTmEn;
  private BodyPart pFooterTw;
  private BodyPart pFooterTwTmone;
  private BodyPart pFooterWeb;
  private BodyPart pFooterWebGlobal;
  private BodyPart pFooterWebTmone;
  private BodyPart pHeroConsumer;
  private BodyPart pHeroGlobal;
  private BodyPart pHeroSme;
  private BodyPart pHeroTmone;

  
  public EmailSender(String host, boolean verbose, String sys, String sendermail) {
    this.prop = new Properties();
    this.prop.put("mail.smtp.host", host);
    this.curSys = sys.toLowerCase();
    this.sender = sendermail;

    ses = Session.getDefaultInstance(prop);
    ses.setDebug(verbose);
//    System.out.println("Mail Session v2 Initialized!");

    msg = new MimeMessage(ses);
  }

  /**
   * prepare all the common things
   *
   * @throws java.io.UnsupportedEncodingException
   * @throws javax.mail.MessagingException
   */
  public void init() throws UnsupportedEncodingException, MessagingException {
    InternetAddress addrFrom = new InternetAddress("tmbilling@tmbilling.com.my", "unifi Billing");
    msg.setFrom(addrFrom);
    msg.setReplyTo(new javax.mail.Address[]{
      new javax.mail.internet.InternetAddress("help@tm.com.my")
    });

    // set all of the common attachments
    pAutopay = new MimeBodyPart();
    DataSource fds = new FileDataSource("asset/cta-autopay.png");
    pAutopay.setDataHandler(new DataHandler(fds));
    pAutopay.setHeader("Content-ID", "<pAutopay>");
    pAutopay.setDisposition(MimeBodyPart.INLINE);

    pPaynowBM = new MimeBodyPart();
    fds = new FileDataSource("asset/cta-paynow-bm.jpg");
    pPaynowBM.setDataHandler(new DataHandler(fds));
    pPaynowBM.setHeader("Content-ID", "<pPaynowBM>");
    pPaynowBM.setDisposition(MimeBodyPart.INLINE);

    pPaynowEn = new MimeBodyPart();
    fds = new FileDataSource("asset/cta-paynow-eng.jpg");
    pPaynowEn.setDataHandler(new DataHandler(fds));
    pPaynowEn.setHeader("Content-ID", "<pPaynowEn>");
    pPaynowEn.setDisposition(MimeBodyPart.INLINE);

    pWherepayBM = new MimeBodyPart();
    fds = new FileDataSource("asset/cta-wherecanipaymybill-bm.png");
    pWherepayBM.setDataHandler(new DataHandler(fds));
    pWherepayBM.setHeader("Content-ID", "<topbanner>");
    pWherepayBM.setDisposition(MimeBodyPart.INLINE);

    pWherepayEn = new MimeBodyPart();
    fds = new FileDataSource("asset/cta-wherecanipaymybill-eng.png");
    pWherepayEn.setDataHandler(new DataHandler(fds));
    pWherepayEn.setHeader("Content-ID", "<pWherepayEn>");
    pWherepayEn.setDisposition(MimeBodyPart.INLINE);

    pFooterFb = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_fb.jpg");
    pFooterFb.setDataHandler(new DataHandler(fds));
    pFooterFb.setHeader("Content-ID", "<pFooterFb>");
    pFooterFb.setDisposition(MimeBodyPart.INLINE);

    pFootLinkdinTmone = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_linkedin-tmone.jpg");
    pFootLinkdinTmone.setDataHandler(new DataHandler(fds));
    pFootLinkdinTmone.setHeader("Content-ID", "<pFootLinkdinTmone>");
    pFootLinkdinTmone.setDisposition(MimeBodyPart.INLINE);

    pFooterTmBM = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_tm-bm.jpg");
    pFooterTmBM.setDataHandler(new DataHandler(fds));
    pFooterTmBM.setHeader("Content-ID", "<pFooterTmBM>");
    pFooterTmBM.setDisposition(MimeBodyPart.INLINE);

    pFooterTmEn = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_tm-eng.jpg");
    pFooterTmEn.setDataHandler(new DataHandler(fds));
    pFooterTmEn.setHeader("Content-ID", "<pFooterTmEn>");
    pFooterTmEn.setDisposition(MimeBodyPart.INLINE);

    pFooterTw = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_tw.jpg");
    pFooterTw.setDataHandler(new DataHandler(fds));
    pFooterTw.setHeader("Content-ID", "<pFooterTw>");
    pFooterTw.setDisposition(MimeBodyPart.INLINE);

    pFooterTwTmone = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_twitter-tmone.jpg");
    pFooterTwTmone.setDataHandler(new DataHandler(fds));
    pFooterTwTmone.setHeader("Content-ID", "<pFooterTwTmone>");
    pFooterTwTmone.setDisposition(MimeBodyPart.INLINE);

    pFooterWeb = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_web.jpg");
    pFooterWeb.setDataHandler(new DataHandler(fds));
    pFooterWeb.setHeader("Content-ID", "<pFooterWeb>");
    pFooterWeb.setDisposition(MimeBodyPart.INLINE);

    pFooterWebGlobal = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_web-global.jpg");
    pFooterWebGlobal.setDataHandler(new DataHandler(fds));
    pFooterWebGlobal.setHeader("Content-ID", "<pFooterWebGlobal>");
    pFooterWebGlobal.setDisposition(MimeBodyPart.INLINE);

    pFooterWebTmone = new MimeBodyPart();
    fds = new FileDataSource("asset/footer_web-tmone.jpg");
    pFooterWebTmone.setDataHandler(new DataHandler(fds));
    pFooterWebTmone.setHeader("Content-ID", "<pFooterWebTmone>");
    pFooterWebTmone.setDisposition(MimeBodyPart.INLINE);

    pHeroConsumer = new MimeBodyPart();
    fds = new FileDataSource("asset/hero-image-consumer.jpg");
    pHeroConsumer.setDataHandler(new DataHandler(fds));
    pHeroConsumer.setHeader("Content-ID", "<pHeroConsumer>");
    pHeroConsumer.setDisposition(MimeBodyPart.INLINE);

    pHeroGlobal = new MimeBodyPart();
    fds = new FileDataSource("asset/hero-image-global.jpg");
    pHeroGlobal.setDataHandler(new DataHandler(fds));
    pHeroGlobal.setHeader("Content-ID", "<pHeroGlobal>");
    pHeroGlobal.setDisposition(MimeBodyPart.INLINE);

    pHeroSme = new MimeBodyPart();
    fds = new FileDataSource("asset/hero-image-sme.jpg");
    pHeroSme.setDataHandler(new DataHandler(fds));
    pHeroSme.setHeader("Content-ID", "<pHeroSme>");
    pHeroSme.setDisposition(MimeBodyPart.INLINE);

    pHeroTmone = new MimeBodyPart();
    fds = new FileDataSource("asset/hero-image-tmone.jpg");
    pHeroTmone.setDataHandler(new DataHandler(fds));
    pHeroTmone.setHeader("Content-ID", "<pHeroTmone>");
    pHeroTmone.setDisposition(MimeBodyPart.INLINE);

  }

  private void setSubject(String bano, Date bdate, String lob, String lang) throws MessagingException {
    String dateformat = Utilities.dateFormat(bdate, "MM yyyy");
    // set the subject

    if (curSys.equals("icp")) {
      if (lang.equals("mal")) {
        msg.setSubject("Bil TM anda (No Akaun: " + bano + ") untuk bulan " + dateformat);
      } else {
        // english
        msg.setSubject("Your TM bill for " + dateformat + " is ready! (Account no: " + bano + ")");
      }

    } else {
      // nova
      if (lob.equals("consumer")) {
        msg.setSubject("Your unifi Home bill for " + dateformat + " is ready! (Account no: " + bano + ")");
      } else if (lob.equals("sme")) {
        msg.setSubject("Your unifi bill for " + dateformat + " is ready! (Account no: " + bano + ")");
      } else {
        // tmone and global
        msg.setSubject("Your TM bill for " + dateformat + " is ready! (Account no: " + bano + ")");
      }
    }

  }

  public String send(String name, String bano, Date enddate, double amount, File pdf
          , String emailaddr, String ccaddr, String lob, String lang,
          String currency, double overdue, double cmc, Date duedate
  ) throws MessagingException {

    if (emailaddr.trim().isEmpty()) {
      return "empty email";
    }

    // set the recipient
    setRecip(emailaddr);
    setCCRecip(ccaddr);

    setSubject(bano, enddate, lob, lang);

    // set the email body
    if(lob.equals("unifi") || lob.equals("sme")){
      buildEmailContentUnifiEn(name, bano, Utilities.dateFormat(enddate, "dd MMM yyyy"), amount, lob, currency, overdue, cmc, Utilities.dateFormat(duedate, "dd MMM yyyy"), pdf);
    }

    // send the email
    Transport.send(msg);

    return "success";
  }

  /// ----------------------------------------------------------------------
  private void setRecip(String recipient) throws MessagingException {

    if (recipient.contains(",")) {

      String[] rlist = recipient.split(",");
      int dsize = rlist.length;
      InternetAddress[] adrss = new InternetAddress[dsize];

      for (int i = 0; i < dsize; i++) {
        adrss[i] = new InternetAddress(rlist[i]);
      }

      msg.setRecipients(Message.RecipientType.TO, adrss);
    } else {
      InternetAddress addrTo = new InternetAddress(recipient);
      msg.setRecipient(Message.RecipientType.TO, addrTo);
    }

  }

  private void setCCRecip(String recipient) throws MessagingException {

    if (recipient.trim().isEmpty()) {
      return;
    }

    if (recipient.contains(",")) {

      String[] rlist = recipient.split(",");
      int dsize = rlist.length;
      InternetAddress[] adrss = new InternetAddress[dsize];

      for (int i = 0; i < dsize; i++) {
        adrss[i] = new InternetAddress(rlist[i]);
      }

      msg.setRecipients(Message.RecipientType.CC, adrss);
    } else {
      InternetAddress addrTo = new InternetAddress(recipient);
      msg.setRecipient(Message.RecipientType.CC, addrTo);
    }

  }

  private void buildEmailContentUnifiEn(String name, String bano, String enddate,
          double totout, String lob, String currency,
          double overdue, double cmc, String due_date,
          File pdf
  ) throws MessagingException {

    // set the email body
    MimeMultipart multipart = new MimeMultipart("related");
    BodyPart messageBodyPart = new MimeBodyPart();

    String sLabel = this.curSys.toLowerCase().equals("nova") ? "unifi Home" : "TM";
    String otopaybg = lob.toLowerCase().equals("sme") ? "96d8ff" : "f7941d";
    String herobanner = lob.toLowerCase().equals("sme") ? "pHeroSme" : "pHeroConsumer";
    String overduedisp = "&nbsp;";
    String overdueamt = currency + String.format("%.2f", overdue);
    String cmcamt = currency + String.format("%.2f", cmc);
    String totdueamt = currency + String.format("%.2f", totout);

    if (overdue > 0) {
      overduedisp = "                    	<font style=\"color:#20409a; font-size:14px;\">Payment Due Date</font>\n"
              + "                        <font style=\"color:#f7941d; font-size:14px;\">PAY IMMEDIATELY</font>\n";
    }

    String mainbody = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">\n"
            + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
            + "  <head>\n"
            + "    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n"
            + "    <title>unifi</title>\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "  </head>\n"
            + "  <body style=\"margin: 0; padding: 0;\">\n"
            + "    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n"
            + "      <tr>\n"
            + "        <td>\n"
            + "          <table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse:collapse;\">\n"
            + "            <tr>\n"
            + "              <td bgcolor=\"#ffffff\" align=\"center\" style=\"font-family:Arial, Helvetica, sans-serif;font-size:9px;color:#464646;padding:8px 0px 4px 0px;\">\n"
            + "                To ensure you receive our emails, save <a style=\"color:#025ba5;\" href=\"mailto:" + sender + "\" target=\"_blank\">&lt;" + sender + "&gt;</a> to your email address book.<br>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "          </table>\n"
            + "\n"
            + "          <table bgcolor=\"#e4e8ee\" align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" style=\"border-collapse:collapse;\">\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 0px 0px 0px 0px;\">\n"
            + "                <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:18px;\" src=\"cid:" + herobanner + "\" alt=\"TM\" width=\"600\" height=\"305\">\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" style=\"padding: 0px 0px 0px 0px;\">\n"
            + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse; font-family:Arial, Helvetica, sans-serif; color: #000000;\">\n"
            + "                  <tr>\n"
            + "                    <td align=\"center\" style=\"padding: 30px 30px 25px 30px; font-size: 17px; color: #272727;\">\n"
            + "                    	<font style=\"font-size:24px;\">Hello " + name + "</font><br /><br />\n"
            + "                        Here’s a summary of your latest <b>" + sLabel + " bill</b>.<br />\n"
            + "                        A PDF bill copy is attached for your reference.\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" style=\"padding: 0px 20px 20px 20px;\">\n"
            + "                <table bgcolor=\"#ffffff\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse; font-family:Arial, Helvetica, sans-serif; color: #000000;\">\n"
            + "                  <tr>\n"
            + "                    <td colspan=\"2\" width=\"297\" align=\"center\" style=\"padding:12px 12px 12px 12px; border-right:2px solid #d1d3d7; background-color:#f7941d; color:#ffffff;\">\n"
            + "                    	<font style=\"font-size:14px;\">Bill Date</font><br />\n"
            + "                    	<font style=\"font-size:22px;\">" + enddate + "</font>\n"
            + "                    </td>\n"
            + "                    <td width=\"223\" align=\"center\" style=\"padding:12px 12px 12px 12px; background-color:#20409a; color:#ffffff;\">\n"
            + "                    	<font style=\"font-size:14px;\">Account Number</font><br />\n"
            + "                        <font style=\"font-size:22px;\">" + bano + "</font>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                  <tr>\n"
            + "                    <td width=\"148\" align=\"center\" style=\"padding:15px 15px 15px 15px; border-right:2px solid #d1d3d7;\">\n"
            + "                    	<font style=\"color:#20409a; font-size:14px;\">Overdue Amount</font>\n"
            + "                        <font style=\"color:#f7941d; font-size:21px;\">" + overdueamt + "</font>\n"
            + "                    </td>\n"
            + "                    <td width=\"148\" align=\"center\" style=\"padding:15px 15px 15px 15px; border-right:2px solid #d1d3d7;\"\">\n"
            + "                    	<font style=\"color:#20409a; font-size:14px;\">Current Charges</font>\n"
            + "                        <font style=\"color:#f7941d; font-size:21px;\">" + cmcamt + "</font>\n"
            + "                    </td>\n"
            + "                    <td align=\"center\" style=\"padding:15px 15px 15px 15px;\">\n"
            + "                    	<font style=\"color:#20409a; font-size:18px;\">Total Amount Due*</font>\n"
            + "                        <font style=\"color:#f7941d; font-size:21px;\">" + totdueamt + "</font>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                  <tr>\n"
            + "                    <td width=\"148\" align=\"center\" style=\"padding:0px 15px 15px 15px; border-right:2px solid #d1d3d7;\">\n"
            + overduedisp
            + "                    </td>\n"
            + "                    <td width=\"148\" align=\"center\" style=\"padding:0px 15px 15px 15px; border-right:2px solid #d1d3d7;\">\n"
            + "                    	<font style=\"color:#20409a; font-size:14px;\">Payment Due Date</font>\n"
            + "                        <font style=\"color:#f7941d; font-size:21px;\">" + due_date + "</font>\n"
            + "                    </td>\n"
            + "                    <td align=\"center\" style=\"padding:0px 0px 10px 0px;\">\n"
            + "                        <a href=\"https://unifi.com.my/personal\" target=\"_blank\">\n"
            + "                            <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pPaynowEn\" alt=\"Pay Now\" width=\"145\" height=\"35\">\n"
            + "						</a>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>            \n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" style=\"padding: 0px 0px 0px 0px;\">\n"
            + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse; font-family:Arial, Helvetica, sans-serif; color: #000000;\">\n"
            + "                  <tr>\n"
            + "                    <td align=\"center\" style=\"padding: 0px 30px 25px 30px; color: #272727;\">\n"
            + "                    	<font style=\"font-size:12px; font-style:italic;\">*If you have already signed up with our <b>Autopay</b> service, the total amount above will be deducted automatically from your card within the next few days.</font>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" style=\"padding: 0px 0px 0px 0px;\" bgcolor=\"#" + otopaybg + "\">\n"
            + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse; font-family:Arial, Helvetica, sans-serif; color: #000000;\">\n"
            + "                  <tr>\n"
            + "                    <td align=\"center\" style=\"padding: 20px 30px 20px 30px; color: #272727;\">\n"
            + "                    	<font style=\"font-size:14px;\">\n"
            + "                            Disclaimer: Please do not reply to this email.<br />\n"
            + "                            For further enquiries or feedback, you can e-mail to <a href=\"mailto:help@tm.com.my\" style=\"text-decoration:underline; color:#272727;\">help@tm.com.my</a>\n"
            + "                        </font>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"center\" style=\"padding: 0px 0px 0px 0px;\" bgcolor=\"#" + otopaybg + "\">\n"
            + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse; font-family:Arial, Helvetica, sans-serif; color: #000000;\">\n"
            + "                  <tr>\n"
            + "                    <td align=\"center\" style=\"padding: 0px 0px 20px 0px; color: #272727;\">\n"
            + "                        <a href=\"https://unifi.com.my/personal\" target=\"_blank\">\n"
            + "                            <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pAutopay\" alt=\"Auto Pay\" width=\"200\" height=\"70\">\n"
            + "						</a>\n"
            + "                    </td>\n"
            + "                    <td align=\"center\" style=\"padding: 0px 0px 20px 0px; color: #272727;\">\n"
            + "                        <a href=\"https://community.unifi.com.my/t5/Bill-Payment/Where-can-I-pay-my-unifi-bill/ta-p/8927\" target=\"_blank\">\n"
            + "                            <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pWherepayEn\" alt=\"Where can i pay my bill\" width=\"200\" height=\"70\">\n"
            + "						</a>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "            <tr>\n"
            + "              <td colspan=\"3\" align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 12px 0px 12px 0px;\">\n"
            + "                <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;\">\n"
            + "                  <tr>\n"
            + "                    <td width=\"80\" align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 0px 0px 0px 25px;\">\n"
            + "                      <a href=\"https://unifi.com.my/personal\" target=\"_blank\">\n"
            + "                        <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pFooterWeb\" alt=\"www.unifi.com.my\" width=\"80\" height=\"24\">\n"
            + "                      </a>\n"
            + "                    </td>\n"
            + "                    <td width=\"71\" align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 0px 0px 0px 8px;\">\n"
            + "                      <a href=\"https://www.facebook.com/weareunifi/\" target=\"_blank\">\n"
            + "                        <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pFooterFb\" alt=\"weareunifi\" width=\"71\" height=\"24\">\n"
            + "                      </a>\n"
            + "                    </td>\n"
            + "                    <td width=\"58\" align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 0px 0px 0px 8px;\">\n"
            + "                      <a href=\"https://twitter.com/unifi?lang=en\" target=\"_blank\">\n"
            + "                        <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pFooterTw\" alt=\"@unifi\" width=\"58\" height=\"24\">\n"
            + "                      </a>\n"
            + "                    </td>          \n"
            + "                    <td width=\"145\" bgcolor=\"#ffffff\">\n"
            + "                    </td>\n"
            + "                    <td width=\"88\" align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 0px 0px 0px 0px;\">\n"
            + "                      <a href=\"https://www.tm.com.my/Pages/Home.aspx\" target=\"_blank\">\n"
            + "                        <img style=\"display:block; font-family:Arial, Helvetica, sans-serif; font-size:14px;\" src=\"cid:pFooterTmEn\" alt=\"TM Group\" width=\"180\" height=\"36\">\n"
            + "                      </a>\n"
            + "                    </td>\n"
            + "                  </tr>\n"
            + "                </table>\n"
            + "              </td>\n"
            + "            </tr>\n"
            + "          </table>\n"
            + "        </td>\n"
            + "      </tr>\n"
            + "    </table>\n"
            + "  </body>\n"
            + "</html>";

    messageBodyPart.setContent(multipart, "text/html");
    multipart.addBodyPart(messageBodyPart);

    // add the pdf attachment
    BodyPart pPdfAttach = new MimeBodyPart();
    DataSource fds = new FileDataSource(pdf);
    pPdfAttach.setDataHandler(new DataHandler(fds));
    pPdfAttach.setFileName(pdf.getName());
    multipart.addBodyPart(pPdfAttach);

    // add the rest of the inlines
    if(lob.toLowerCase().equals("sme")){
      multipart.addBodyPart(pHeroSme);
    } else {
      multipart.addBodyPart(pHeroConsumer);
    }
    
    multipart.addBodyPart(pPaynowEn);
    multipart.addBodyPart(pAutopay);
    multipart.addBodyPart(pWherepayEn);
    multipart.addBodyPart(pFooterWeb);
    multipart.addBodyPart(pFooterFb);
    multipart.addBodyPart(pFooterTw);
    multipart.addBodyPart(pFooterTmEn);

    msg.setContent(multipart);

  }

  private String buildEmailContentBm(String name, String bano, String enddate, double amount, String lob) {

    String emc = "<!doctype html>\n"
            + "<html lang=\"en-US\">\n"
            + "  <head>\n"
            + "    <meta charset=\"utf-8\">\n"
            + "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
            + "\n"
            + "    <style>\n"
            + "\n"
            + "    .container {\n"
            + "      width: 800px;\n"
            + "      margin: auto;\n"
            + "      background-color: #ffffff;\n"
            + "      font-family: Helvetica, Arial, sans-serif;\n"
            + "    }\n"
            + "\n"
            + "    .content {\n"
            + "      margin: auto;\n"
            + "      width: 780px;\n"
            + "      padding: 5px;\n"
            + "      font-weight: 300;\n"
            + "      color: #231f20;\n"
            + "    }\n"
            + "\n"
            + "    .pink-header {\n"
            + "      font-size: 20px;\n"
            + "      font-weight: 800;\n"
            + "      color: #e40a7d;\n"
            + "    }\n"
            + "\n"
            + "    .stmt {\n"
            + "      border: 1px solid black;\n"
            + "      border-radius: 6px;\n"
            + "      margin: 5px;\n"
            + "      text-align: center;\n"
            + "      float: left;\n"
            + "      width: 31.5%;\n"
            + "      background-color: #eeeeee;\n"
            + "      padding-top: 10px;\n"
            + "      padding-bottom: 10px;\n"
            + "    }\n"
            + "\n"
            + "    .info-biru {\n"
            + "      background-color: #37376e;\n"
            + "      border-radius: 6px;\n"
            + "      width: 700px;\n"
            + "      color: white;\n"
            + "      text-align: center;\n"
            + "    }\n"
            + "\n"
            + "    .name-header {\n"
            + "      font-size: 34px;\n"
            + "      font-weight: 900;\n"
            + "      color: #37376e;\n"
            + "    }\n"
            + "\n"
            + "    .info {\n"
            + "      font-size: 18px;\n"
            + "    }\n"
            + "\n"
            + "    .smallf {\n"
            + "      font-size: 11px;\n"
            + "    }\n"
            + "\n"
            + "    .bigstmt {\n"
            + "      font-size:20px;\n"
            + "      font-weight:600;\n"
            + "      color: blue;\n"
            + "    }\n"
            + "\n"
            + "    .disclaim {\n"
            + "      background-color: #dddddd;\n"
            + "      padding: 5px;\n"
            + "      text-align: justify;\n"
            + "    }\n"
            + "\n"
            + "    .followus {\n"
            + "      width: 550px;\n"
            + "      height: 50px;\n"
            + "      border-radius: 100px 0px 0px 100px;\n"
            + "      background-color: #ee7202;\n"
            + "      text-align: center;\n"
            + "      float: right;\n"
            + "    }\n"
            + "\n"
            + "    .infollow {\n"
            + "      display: table-cell;\n"
            + "      vertical-align: middle;\n"
            + "      height: 50px;\n"
            + "      color: white;\n"
            + "      font-size: 15px;\n"
            + "    }\n"
            + "\n"
            + "    .clears {\n"
            + "      clear: both;\n"
            + "    }\n"
            + "\n"
            + "\n"
            + "    </style>\n"
            + "\n"
            + "  </head>\n"
            + "  <body bgcolor=\"#eeeeee\">\n"
            + "    <div class=\"container\">\n"
            + "      <div class=\"content\">\n"
            + "        <img src=\"cid:topbanner\" />\n"
            + "      </div>\n"
            + "      <div class=\"content name-header\">\n"
            + "  hello " + name + " :(\n"
            + "      </div>\n"
            + "      <div class=\"content info\">\n"
            + "        <p>Thanks for being a part of <span style=\"font-weight: 800;\">unifi</span>.<br />\n"
            + "          Here's your quick bill summary for this month</p>\n"
            + "      </div>\n"
            + "      <div class=\"content pink-header\">\n"
            + "        bill summary\n"
            + "      </div>\n"
            + "      <div class=\"content smallf\">\n"
            + "        <div class=\"stmt\">\n"
            + "          Statement Date<br />\n"
            + "          <span class=\"bigstmt\">" + enddate + "</span>\n"
            + "        </div>\n"
            + "        <div class=\"stmt\">\n"
            + "          Account No.<br />\n"
            + "          <span class=\"bigstmt\">" + bano + "</span>\n"
            + "        </div>\n"
            + "        <div class=\"stmt\">\n"
            + "          Amount<br />\n"
            + "          <span class=\"bigstmt\">" + amount + "</span>\n"
            + "        </div>\n"
            + "        <div class=\"clears\"></div>\n"
            + "      </div>\n"
            + "      <div class=\"content smallf\" style=\"text-align: right;margin-right:80px;\">\n"
            + "        <span style=\"font-weight:800\";>Payment Due Date:</span> 22/03/2019\n"
            + "      </div>\n"
            + "      <div class=\"content info-biru\">\n"
            + "        <a href=\"https://unifi.com.my\"><img src=\"cid:otopay\" /></a>\n"
            + "        malas nak buat part ni\n"
            + "        <a href=\"https://unifi.com.my\"><img src=\"cid:chathidup\" /></a>\n"
            + "      </div>\n"
            + "      <div class=\"content pink-header\">\n"
            + "        view your full bill\n"
            + "      </div>\n"
            + "      <div class=\"content\" style=\"font-size:15px\">\n"
            + "  We've also attached the PDF version of your full bill.\n"
            + "  <br /><br />\n"
            + "  If you have already signed up with our auto pay service, that's great! The total amount above will be deducted automatically from your card within the next few days. Thank you.\n"
            + "  <br /><br />\n"
            + "  Sincerely,\n"
            + "      </div>\n"
            + "      <div class=\"content\">\n"
            + "        <img src=\"cid:logo_unifi2\" />\n"
            + "      </div>\n"
            + "      <div class=\"content\">\n"
            + "        <img src=\"cid:lme\" />\n"
            + "      </div>\n"
            + "      <div class=\"content\">\n"
            + "        <div class=\"followus\">\n"
            + "          <div class=\"infollow\" style=\"width:35%\">\n"
            + "            Follow us to keep track of what's happening\n"
            + "          </div>\n"
            + "          <div class=\"infollow\" style=\"width:65%\">\n"
            + "            <a href=\"https://unifi.com.my\"><img src=\"cid:smtm\" /></a>\n"
            + "            <a href=\"https://twitter.com/HelpMeunifi\"><img src=\"cid:twittm\" /></a>\n"
            + "            <a href=\"https://www.facebook.com/weareunifi/\"><img src=\"cid:fbtm\" /></a>\n"
            + "          </div>\n"
            + "        </div>\n"
            + "        <div class=\"clears\"></div>\n"
            + "      </div>\n"
            + "      <div class=\"content smallf disclaim\">\n"
            + "  Hello. There may be confidential information contained in this email and any files transmitted with it (Message). Excuse us if you are not the addressee of this Message. Please do not disseminate, distribute, print or copy this Message or any part thereof. Also, we would greatly appreciate if you could delete this Message immediately and advise the sender by return email.\n"
            + "  <br /><br />\n"
            + "  Note: No assumption of responsibility or liability whatsoever is undertaken by TM in respect of prohibited and unauthorized use by any other person.<br />\n"
            + "      </div>\n"
            + "\n"
            + "    </div>\n"
            + "  </body>\n"
            + "</html>";

    return emc;
  }

}
