/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package emailbillsender;

import java.io.File;
import java.io.UnsupportedEncodingException;
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

  // all of the attachments
  private BodyPart pTopHeader;
  private BodyPart pOtopay;
  private BodyPart pChat;
  private BodyPart pUnifi;
  private BodyPart pHidupSenang;
  private BodyPart pTM;
  private BodyPart pTwitter;
  private BodyPart pFB;

  /**
   * The constructor
   */
  public EmailSender(String host, boolean verbose) {
    this.prop = new Properties();
    this.prop.put("mail.smtp.host", host);

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
    pTopHeader = new MimeBodyPart();
    DataSource fds = new FileDataSource("asset/banner.png");
    pTopHeader.setDataHandler(new DataHandler(fds));
    pTopHeader.setHeader("Content-ID", "<topbanner>");
    pTopHeader.setDisposition(MimeBodyPart.INLINE);

    pOtopay = new MimeBodyPart();
    fds = new FileDataSource("asset/icon_autopay.png");
    pOtopay.setDataHandler(new DataHandler(fds));
    pOtopay.setHeader("Content-ID", "<otopay>");
    pOtopay.setDisposition(MimeBodyPart.INLINE);

    pChat = new MimeBodyPart();
    fds = new FileDataSource("asset/icon_chat-live.png");
    pChat.setDataHandler(new DataHandler(fds));
    pChat.setHeader("Content-ID", "<chathidup>");
    pChat.setDisposition(MimeBodyPart.INLINE);

    pFB = new MimeBodyPart();
    fds = new FileDataSource("asset/fbtm.png");
    pFB.setDataHandler(new DataHandler(fds));
    pFB.setHeader("Content-ID", "<fbtm>");
    pFB.setDisposition(MimeBodyPart.INLINE);

    pHidupSenang = new MimeBodyPart();
    fds = new FileDataSource("asset/lme.png");
    pHidupSenang.setDataHandler(new DataHandler(fds));
    pHidupSenang.setHeader("Content-ID", "<lme>");
    pHidupSenang.setDisposition(MimeBodyPart.INLINE);

    pTwitter = new MimeBodyPart();
    fds = new FileDataSource("asset/twittm.png");
    pTwitter.setDataHandler(new DataHandler(fds));
    pTwitter.setHeader("Content-ID", "<twittm>");
    pTwitter.setDisposition(MimeBodyPart.INLINE);

    pUnifi = new MimeBodyPart();
    fds = new FileDataSource("asset/logo_unifi2.png");
    pUnifi.setDataHandler(new DataHandler(fds));
    pUnifi.setHeader("Content-ID", "<logo_unifi2>");
    pUnifi.setDisposition(MimeBodyPart.INLINE);

    pTM = new MimeBodyPart();
    fds = new FileDataSource("asset/smtm.png");
    pTM.setDataHandler(new DataHandler(fds));
    pTM.setHeader("Content-ID", "<smtm>");
    pTM.setDisposition(MimeBodyPart.INLINE);
  }

  public String send(String name, String bano, String enddate, String amount, File pdf, String emailaddr, String ccaddr) throws MessagingException {

    if (emailaddr.trim().isEmpty()) {
      return "empty email";
    }

    // set the recipient
    setRecip(emailaddr);
    setCCRecip(ccaddr);

    // set the subject
    msg.setSubject("Your unifi bill dated " + enddate + " for account " + bano + " is ready");

    // set the email body
    MimeMultipart multipart = new MimeMultipart("related");
    BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(buildEmailContent(name, bano, enddate, amount), "text/html");
    multipart.addBodyPart(messageBodyPart);

    // add the pdf attachment
    BodyPart pPdfAttach = new MimeBodyPart();
    DataSource fds = new FileDataSource(pdf);
    pPdfAttach.setDataHandler(new DataHandler(fds));
    pPdfAttach.setFileName(pdf.getName());
    multipart.addBodyPart(pPdfAttach);

    // add the rest of the inlines
    multipart.addBodyPart(pTopHeader);
    multipart.addBodyPart(pOtopay);
    multipart.addBodyPart(pChat);
    multipart.addBodyPart(pUnifi);
    multipart.addBodyPart(pHidupSenang);
    multipart.addBodyPart(pTM);
    multipart.addBodyPart(pTwitter);
    multipart.addBodyPart(pFB);

    msg.setContent(multipart);

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

  private String buildEmailContent(String name, String bano, String enddate, String amount) {

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
