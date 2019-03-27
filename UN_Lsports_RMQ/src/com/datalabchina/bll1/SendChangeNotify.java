package com.datalabchina.bll1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.CommonMethod;
import com.common.FileDispose;
import com.common.PageHelper;
import com.common.Utils;
import com.datalabchina.controler.Controler;

public class SendChangeNotify extends Thread {
	private static Logger logger = Logger.getLogger("SendChangeNotify");
	CommonMethod oCommonMethod = new CommonMethod();

	private String uraceID = "";
	private String raceID = "";
	private String timeStamp = "";
//	private String poolType = "";
//	private String labelName = "NzXmlOdds";
	//private String updateType = "DividendUpdate";
	SimpleDateFormat oSDFTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	public static ArrayList<String> endpointArrayList = new ArrayList<String>();
//	public static boolean bIsSendOddsNotify;
	
    public enum EnumOddType {
    	Win, Plc, Qla;
    }
	
    private EnumOddType eOddType;
    
	public static HashMap<String, String> hmWinSendedURaceID = new HashMap<String, String>();
	public static HashMap<String, String> hmPlcSendedURaceID = new HashMap<String, String>();
	public static HashMap<String, String> hmQlaSendedURaceID = new HashMap<String, String>();
	
	private String axisURL = "";

	private static final String NEWLINE = System.getProperty("line.separator");

	public SendChangeNotify() {

	}
	
	//, String sTimeStamp, String sPoolType
	public SendChangeNotify(String axisURL, String sUraceID, EnumOddType eOddType, String timeStamp) {
		//2014-12-06 10:51:00
		//2014-12-06 19:42:20
		this.timeStamp = timeStamp;	//new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
		this.axisURL = axisURL;
		this.eOddType = eOddType;
		
		if (sUraceID == null) {
			uraceID = null;
		} else {
			if (sUraceID.indexOf("|") > -1) {
				uraceID = sUraceID.split("\\|")[0];
				raceID = sUraceID.split("\\|")[1];
			} else {
				uraceID = sUraceID;
			}
		}
	}

	public void run() {
		
		if (uraceID == null) return;
		
//		if (eOddType == EnumOddType.Win) {
//			if (hmWinSendedURaceID.get(uraceID) == null) return;
//		}
//		if (eOddType == EnumOddType.Plc) {
//			if (hmPlcSendedURaceID.get(uraceID) == null) return;
//		}
//		if (eOddType == EnumOddType.Qla) {
//			if (hmQlaSendedURaceID.get(uraceID) == null) return;				
//		}		
		
		if (Controler.bSendNotify == false) {		
			logger.info("[sendNotify] bSendNotify == false, append uraceID only. = " + uraceID );
			if (eOddType == EnumOddType.Win) hmWinSendedURaceID.put(uraceID, uraceID);
			if (eOddType == EnumOddType.Plc) hmPlcSendedURaceID.put(uraceID, uraceID);
			if (eOddType == EnumOddType.Qla) hmQlaSendedURaceID.put(uraceID, uraceID);
//			saveFile(eOddType);
			return;
		}
		
		sendChgDivNotify();
	}

	private void sendChgDivNotify() {

		String sCountryID = uraceID.substring(8, 10);
//		String sMarketID = "2";

		// process timestamp
//		String tmpArray[] = timeStamp.split(" ");

//		if (timeStamp.indexOf(":") > -1) {
//			String ymd = tmpArray[0];
//			String hms = tmpArray[1];
//			timeStamp = ymd + " " + hms + ":00";
//		}

		String notifyContent = "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";

		notifyContent += "<soap:Body>" + NEWLINE;// function name
		notifyContent += " <statusUpdate xmlns=\"" + axisURL + "\">" + NEWLINE;
		notifyContent += "  <xmlString>" + NEWLINE;

		
		// start body
		//<StatusUpdate Sender="RaceTab">		
		notifyContent += "&lt;StatusUpdate Sender=\"" + Controler.SenderName + "\"&gt;"
				+ NEWLINE;
		notifyContent += "&lt;CountryID&gt;" + Integer.parseInt(sCountryID)
				+ "&lt;/CountryID&gt;" + NEWLINE;
		//<DividendUpdate URaceID="...
		
		//notifyContent += "&lt;" + updateType + " URaceID=\"" + uraceID + "\" ";
		if (eOddType == EnumOddType.Win) notifyContent += "&lt;" + Controler.WinUpdate + " URaceID=\"" + uraceID + "\" ";
		if (eOddType == EnumOddType.Plc) notifyContent += "&lt;" + Controler.PlaceUpdate+ " URaceID=\"" + uraceID + "\" ";
		if (eOddType == EnumOddType.Qla) notifyContent += "&lt;" + Controler.QuinellaUpdate+ " URaceID=\"" + uraceID + "\" ";
		
		// notifyContent += " RaceID=\""+raceID+"\" ";
		notifyContent += " Timestamp=\"" + timeStamp + "\"";
		// notifyContent += " Sender=\"RaceTab\"" ;
		notifyContent += " MarketID=\"" + Controler.MarketID + "\"";
		notifyContent += " /&gt;" + NEWLINE;
		notifyContent += " &lt;/StatusUpdate&gt;" + NEWLINE;

		// end message body
		notifyContent += "		</xmlString>" + NEWLINE;
		notifyContent += "	</statusUpdate>" + NEWLINE;
		notifyContent += "</soap:Body>" + NEWLINE;

		notifyContent += "</soap:Envelope>";

		logger.info("Sending message : \n" + notifyContent.replaceAll("&lt;", "<").replaceAll("&gt;", ">"));
		
//		HttpClient httpclient = new HttpClient();
//		PostMethod post = new PostMethod(axisURL);

		try {
						
//			StringRequestEntity entity = new StringRequestEntity(notifyContent,"text/xml", "utf-8");
//			post.setRequestEntity(entity);
//
//			int result = httpclient.executeMethod(post);
//			String returnStr = post.getResponseBodyAsString();
			
			String returnStr = PageHelper.getPageHelper().doPost(axisURL, notifyContent);			
			
			String statusUpdateReturn = Utils.extractMatchValue(returnStr,"<statusUpdateReturn.*?>(.*?)</statusUpdateReturn>"); 
			logger.info(uraceID
					+ "|"+ raceID+ "|"+ timeStamp+ "|" //+ " return:"+ result
					+ "|"+ statusUpdateReturn);
			logger.info("\n\nResponse status code: \n" + returnStr + "\n\n");		

			if (statusUpdateReturn.equals("0")) {
				logger.info("[sendNotify] append uraceID = " + uraceID);
				//hmSendedURaceID.put(uraceID, uraceID);
				if (eOddType == EnumOddType.Win) hmWinSendedURaceID.put(uraceID, uraceID);
				if (eOddType == EnumOddType.Plc) hmPlcSendedURaceID.put(uraceID, uraceID);
				if (eOddType == EnumOddType.Qla) hmQlaSendedURaceID.put(uraceID, uraceID);				
//				saveFile(eOddType);
			}
			
		} catch (Exception e) {
			logger.error("sendChgDivNotify ", e);
		} finally {
//			post.releaseConnection();
		}
	
//		httpclient = null;
//		post = null;
	}

	@SuppressWarnings("deprecation")
	public static void getSentURaceIDFromFile(EnumOddType eOddType) {
		
		logger.info("[sendNotify] getSentURaceIDFromFile " + eOddType);
		
		File myFile = new File(eOddType+"_sent.txt");
		if (myFile.exists() == false) return;
		
		String sLine = "";
		String sDate = "";
		
		GregorianCalendar d = new GregorianCalendar();
		String sToDay = new SimpleDateFormat("yyyyMMdd").format(d.getTime());
		d.add(Calendar.DATE, -1);
		String sYesterday = new SimpleDateFormat("yyyyMMdd").format(d.getTime());
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(myFile)));
			sLine = in.readLine();
			while (sLine != null) {
				sLine = sLine.trim();
				if (sLine.equals("")) {
					sLine = in.readLine();
					continue;
				}
				sDate = sLine.substring(0, 8);
				if ((sDate.equals(sToDay)) || (sDate.equals(sYesterday))) {
					if (eOddType == EnumOddType.Win) hmWinSendedURaceID.put(sLine, sLine);
					if (eOddType == EnumOddType.Plc) hmPlcSendedURaceID.put(sLine, sLine);
					if (eOddType == EnumOddType.Qla) hmQlaSendedURaceID.put(sLine, sLine);
				}

				sLine = in.readLine();
			}
			in.close();

		} catch (Exception e) {
			logger.error("getSentURaceIDFromFile", e);
		}
	}
	
	public static synchronized void saveFile(EnumOddType eOddType) {
		try {
			String sBody = "";
			Iterator<String> iter = null;
			if (eOddType == EnumOddType.Win) iter = hmWinSendedURaceID.keySet().iterator();
			if (eOddType == EnumOddType.Plc) iter = hmPlcSendedURaceID.keySet().iterator();
			if (eOddType == EnumOddType.Qla) iter = hmQlaSendedURaceID.keySet().iterator();
						
			while (iter.hasNext()) {
				String key = iter.next();
				sBody = sBody + key + NEWLINE;
			}
			FileDispose.saveFile(sBody, eOddType+"_sent.txt");
		} catch (Exception e) {
			if (e.toString().indexOf("ConcurrentModificationException") == -1) {
				logger.error("saveFile", e);
			}
		}		
	} 
	
	public static void main(String arg[]) {
		PropertyConfigurator.configure("log4j.properties");

//		new SendChangeNotify().setSendNotifyParameter();
		// 200710080829507|MR_20071008_07|15:25:17 20071008|WIN_PLACE_VIC
		// 2011022143010111
		// (new SendChangeNotify(uraceID,sendtimestamp,"",true)).start();

		Controler.bSendNotify = true;
		String axisURL = "axis:http://192.168.10.6:44444/services/msgDispatcher?method=statusUpdate";
		(new SendChangeNotify(axisURL, "2011022143010111", EnumOddType.Win,"")).start();
				
//		System.err.println("201203292704412".substring(11, 13));
//		System.err.println("201203292705512".substring(11, 13));

	}

}
