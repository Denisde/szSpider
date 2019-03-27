package com.datalabchina.bll1;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.DateUtils;
import com.common.FileDispose;
import com.common.PageHelper;
import com.datalabchina.controler.Controler;

public class BookOddsBulkByEvent implements Runnable {
	
	private static Logger logger = Logger.getLogger(BookOddsBulkByEvent.class);
	
	private static boolean debug = false;
	
	PageHelper page = PageHelper.getPageHelper();
	
	static DecimalFormat df = new DecimalFormat("######0.00");
	
	static String Timestamp = null;
	
	static String connectionString = "";
	
	
	//static HashMap<String, Date> hmEvent = new HashMap<String, Date>();
	static ConcurrentHashMap<String, Date> hmEvent = new ConcurrentHashMap<String, Date>();
	
	public void run() {
		try {
			
			while (true) {
				long l = System.currentTimeMillis();
				savePage();
				l = System.currentTimeMillis() - l;
				l = (1000 * 5) - l;
				if (l > 1000) {
					logger.info("sleep ms " + l);
					Thread.sleep(l);
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
	}
	
	public void savePage() {
		try {
			
			String date = DateUtils.getLongStr1();
			String year = date.substring(0, 4);
			String month = date.substring(4, 6);
			String day = date.substring(6, 8);
			
//			<Country name="Ireland" id="59"/>
//			<Country name="Great Britain" id="2"/>
			
//			<Country name="United States" id="4"/>
//			<Country name="South Africa" id="14"/>
//			<Country name="France" id="147"/>
//			<Country name="Australia" id="172"/>
			
//			<Country name="England" id="243"/>
						
//			String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888&countries=14,172&lang=en&oddsFormat=EU&";
			
			//http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email={email}&password={password}&guid={guid}&timestamp={timestamp}&sports={sports}&countries={countries}&leagues={leagues}&bookmakers={bookmakers}&offertypes={offertypes}&lang={lang}&oddsFormat={oddsFormat}&
//			String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888,687894&countries=2,4,14,59,147,172,243&lang=en&oddsFormat=EU&";
//			              http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEventByID?email={email}&password={password}&guid={guid}&eventID={eventID}&timestamp={timestamp}&sports={sports}&countries={countries}&bookmakers={bookmakers}&offertypes={offertypes}&lang={lang}&oddsFormat={oddsFormat}&
			String url = Controler.url.replaceAll("GetSportEventByDate", "GetSportEventByID");
			
//			url = url.replaceAll("GetSportEventByDate", "GetSportEvents");
			
			String eventList = "";
			Date dNow = DateUtils.getUTCNow();
			int count = 0;
			synchronized (hmEvent) {
				Set<String> s = hmEvent.keySet();
				logger.info("hmEvent.size = " + s.size());
				for (String eventId : s) {
					count++;
//				for (int i = hmEvent.keySet().size() - 1; i >= 0; i--) {
//					String eventId = hmEvent.keySet().get(i);
					Date dStartDate = hmEvent.get(eventId);
					if ((dNow.getTime() - dStartDate.getTime()) >= 1000 * 60 * 5) {
						hmEvent.remove(eventId);
						continue;
					}
					eventList = eventList + "," + eventId;
//					if (count > 10) break;
				}				
			}
			logger.info("eventList size = " + count);
			
			if (eventList.length() < 3) {
				return;
			}
			
			eventList = eventList.substring(1);
			
//			String sDateStart = DateUtils.getUTCDate(-1);
//			String sDateEnd = DateUtils.getUTCDate(3);
//			url = url + "startDate="+sDateStart+"&endDate="+sDateEnd+"&";
			
			url = url + "eventID="+eventList+"&";
			
			if (Timestamp != null) {
				url = url + "timestamp=" + Timestamp;				
			}
			
			String fileName = Controler.saveFilePath + File.separator + "BookOdds" + File.separator + 
				year + File.separator + month + File.separator + day + File.separator + date + "_event.zip";
			
			if (debug) System.err.println(fileName);
			String body = page.doGet(url);
			logger.info(Thread.currentThread().getName() + " get success");
//			if (debug) 
//			System.err.println(body);
			if (body != null && body.length() > 10) {
				int p = body.indexOf("<");
				body = body.substring(p);
				FileDispose.saveAsZip(body, fileName);
				ParseData.parse(body, fileName);
			} else {
				logger.info("body = " + body);
			}
			
		} catch (Exception e) {
			logger.error("", e);
		}		
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");	
//		debug = true;		
//		new BookOdds().savePage();
		
//		String fileName = "F:\\ZA\\20171105_075113.xml";	
//		BookOddsBulkByEvent bll = new BookOddsBulkByEvent();
		
//		String url = "http://xml.oddservice.com/OS/OddsWebService.svc/GetSportEvents?email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888,687893,687894&lang=en&oddsFormat=EU&";
//		String body = PageHelper.getPageHelper().doGet(url);
//		System.out.println(body);
		
	}
}
