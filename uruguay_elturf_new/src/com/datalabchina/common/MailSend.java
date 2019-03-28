package com.datalabchina.common;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * @author wgb
 */
public class MailSend {
	public CommonMethod oCommonMethod = new CommonMethod();

	public String msgText = "";

	public String msgSubject = " Spider log ";

	private String filename = "";

	private String sSpiderName = "";

	private String sStartTime = "";

	public void sendMail(String sSpiderName, String sStartTime) {
		@SuppressWarnings("rawtypes")
		Vector vMailInof = readConfigInfo();
		String sRetrieveMail = vMailInof.get(0).toString();
		String sSendMail = vMailInof.get(1).toString();
		String sSendPassWord = vMailInof.get(2).toString();
		String sSmtpHost = vMailInof.get(3).toString();
		this.sSpiderName = sSpiderName;
		this.sStartTime = sStartTime;
		msgText = "Dear  " + sSpiderName + " Spider  administrator :"+ System.getProperty("line.separator") + "\t";
		msgSubject = sSpiderName + msgSubject + " " + new Date();
		if (vMailInof.get(4).toString().equals("1")) {
			sendMail(sRetrieveMail, sSendMail, sSendPassWord, sSmtpHost);
		}
	}
	@SuppressWarnings("rawtypes")
	public void sendMail(String subject,String emailText,int flag) {
		Vector vMailInof = readConfigInfo();
		String sRetrieveMail = vMailInof.get(0).toString();
		String sSendMail = vMailInof.get(1).toString();
		String sSendPassWord = vMailInof.get(2).toString();
		String sSmtpHost = vMailInof.get(3).toString();
		msgText = emailText;
		msgSubject = subject + " " + new Date();
		if (vMailInof.get(4).toString().equals("1")) {
			sendMail(sRetrieveMail, sSendMail, sSendPassWord, sSmtpHost);
		}
	}
	public void sendMail(String sRetrieveMail, String sSendMail,String sSendPassWord, String sSmtpHost) {
		String smtpHost = sSmtpHost;
		String from = sSendMail;
		String to[] = sRetrieveMail.split(";");
		String sSendName = "";
		if (sSendMail.indexOf("@") > -1) {
			sSendName = sSendMail.substring(0, sSendMail.indexOf("@"));
		}
		final PasswordAuthentication pa = new PasswordAuthentication(sSendName,sSendPassWord);
		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return pa;
			}
		};
		Properties props = new Properties();
		if(smtpHost.indexOf("gmail")>-1){
			props.put("mail.smtp.host", "smtp.gmail.com");
	    	props.put("mail.smtp.socketFactory.port", "465");
	    	props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
	    	props.put("mail.smtp.auth", "true");
	    	props.put("mail.smtp.port", "465");
		}else{
			props.put("mail.smtp.host", smtpHost);
			props.put("mail.smtp.auth", "true");
		}
		// props.setProperty( "mail.smtp.port", "25");
		// props.setProperty( "mail.imap.port", "1025");
		// props.setProperty( "mail.pop3.port", "995");
		Session session = Session.getDefaultInstance(props, auth);
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(from));
			InternetAddress[] address = new InternetAddress[to.length];
			for (int i = 0; i < to.length; i++) {
				address[i] = new InternetAddress(to[i]);
			}
			message.setRecipients(Message.RecipientType.TO, address);
			message.setSubject(msgSubject);
			msgText += sSpiderName + " spider start From  " + sStartTime+ System.getProperty("line.separator");
			Multipart mp = new MimeMultipart();
			@SuppressWarnings("rawtypes")
			Vector vFile = new Vector();
			vFile = attachfile();
			for (int i = 0; i < vFile.size(); i++) {
				MimeBodyPart mbp = new MimeBodyPart();
				filename = vFile.get(i).toString().trim();
				//System.out.println("filename=" + filename);
				FileDataSource fds = new FileDataSource(filename);
				mbp.setDataHandler(new DataHandler(fds));
				mbp.setFileName(fds.getName());
				mp.addBodyPart(mbp);
				String sError = getErrorLine(FileDispose.readFile(filename)).trim();
				if (sError.equals("")) {
					msgText = msgText+ "\tAll  Extraction successfully finished at "+ new Date() + System.getProperty("line.separator");
					// sError="\t the spider normal Exit !!!";
				} else {
					sError = "the spider meet with some problem  please check !"+ System.getProperty("line.separator")+ "error information:"
							+ System.getProperty("line.separator")
							+ this.filename+ System.getProperty("line.separator") + sError;
					this.msgText = this.msgText
							+ System.getProperty("line.separator") + sError;
				}
			}
			vFile.removeAllElements();
			message.setContent(mp);
			message.setSentDate(new Date());
			message.setText(msgText);
			Transport.send(message);
			System.out.println("Send " + sRetrieveMail + " Mail finish !");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector attachfile() {
		Vector vFile = new Vector();
		String sLOG4jPathName = System.getProperty("user.dir")+ System.getProperty("file.separator") + "log4j.properties";
		String sLOG4jContent = FileDispose.readFile(sLOG4jPathName);
		String sLOG4jContentLine[] = sLOG4jContent.split(System.getProperty("line.separator"));
		String sFileName = "";
		for (int i = 0; i < sLOG4jContentLine.length; i++) {
			String sTempFileName = sLOG4jContentLine[i];
			if (sTempFileName.indexOf("A2.File=") > -1) {
				sFileName = sTempFileName.substring(sTempFileName.indexOf("=") + 1);
				if (sFileName.indexOf("./") > -1) {
					sFileName = sFileName.replaceAll("./", "");
				}
				String sJavaLog = System.getProperty("user.dir")+ System.getProperty("file.separator")+ sFileName.trim();
				if (oCommonMethod.bIfExistFile(sJavaLog)) {
					vFile.addElement(sJavaLog);
				}
			}
		}
		return vFile;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Vector readConfigInfo() {
		String sConfigPath = System.getProperty("user.dir")+ System.getProperty("file.separator") + "config.xml";
		Config cfg = new Config();
		Vector vMailInfo = new Vector();
		cfg.loadcfg(new File(sConfigPath));
		Vector vMialList = cfg.getMailList();
		Vector vBLLItem = (Vector) vMialList.get(0);
		vMailInfo.addElement(vBLLItem.get(0));
		vMailInfo.addElement(vBLLItem.get(1));
		vMailInfo.addElement(vBLLItem.get(2));
		vMailInfo.addElement(vBLLItem.get(3));
		vMailInfo.addElement(vBLLItem.get(4));
		return vMailInfo;
	}

	private String getErrorLine(String sContent) {
		String sErrorLine = "";
		try {
			String sErrorTemp[] = sContent.split(System.getProperty("line.separator"));
			for (int i = 0; i < sErrorTemp.length; i++) {
				String sLineTemp = sErrorTemp[i];
				if (sLineTemp.indexOf("ERROR") > -1) {
					sErrorLine += sLineTemp+ System.getProperty("line.separator");
					sErrorLine += sErrorTemp[i + 1]+ System.getProperty("line.separator");
				}
			}
			sErrorLine = sErrorLine.trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sErrorLine;
	}
}