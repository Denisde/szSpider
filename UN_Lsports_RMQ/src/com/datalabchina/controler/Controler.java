package com.datalabchina.controler;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.common.Config;
import com.datalabchina.bll1.BookOddsBulk;
import com.datalabchina.bll1.Markets;
import com.datalabchina.bll1.OutRight;
import com.datalabchina.bll1.ParseData;
import com.datalabchina.bll1.RecvKafka;
import com.datalabchina.bll1.RecvRMQ;
import com.datalabchina.bll1.SaveAndParseDataThread;
import com.momutech.kafka.consumer.KafkaConsumer;
import com.momutech.kafka.consumer.Receiver;
import com.momutech.kafka.consumer.impl.KafkaConsumerImpl;

import common.KafkaSender;

public class Controler {
	private static Logger logger = Logger.getLogger(Controler.class);
	public static int isExtractFromWeb = 0; 
	public static int isExtractFromLocal = 0;
	public static int isDeleteFile = 0;
	public static String saveFilePath = "F:\\UN\\UN_Lsports_RMQ";
	public static String bakFilePath = "F:\\UN\\UN_Lsports_RMQ";
	public static long sleep = 1000 * 60;
	
	public static String SenderName = "NzXmlOdds";
	public static String WinUpdate = "OddsUpdate";
	public static String PlaceUpdate = "OddsUpdate";
	public static String QuinellaUpdate = "OddsUpdate";
	
	public static String MarketID = "2";
	public static String CountryID = "04";
	public static String CountryShortID = "4";
	
	public static Boolean bSendNotify = false;
	public static String axisURL = "axis:http://192.168.10.6:55555/services/msgDispatcher?method=statusUpdate";
	
	public static String url = "http://prematch.lsports.eu/OddService/";
	
	public static String queue_name = "_1413_";
	public static String host = "prematch-rmq.lsports.eu";
	public static Integer OutRight_Sleep_Hour = 1;
	
	public static HashMap<String, String> qlapoolMap = new HashMap<String, String>();
	public static HashMap<String, String> placeMap = new HashMap<String, String>();
	public static HashMap<String, String> winMap = new HashMap<String, String>();
	public static HashMap<String, String> uRaceIDMap = new HashMap<String, String>();
	
	
	public static String email = "freddygalliers@googlemail.com";
	public static String password = "cdd8b962";
	public static String guid = "a1772950-77f5-4f24-8926-49393f2e7ad5";

	public static String sports = "687888,687894";
	public static String countries = "2,4,14,59,147,172,243";
	
	public static String LocationName = "";
	
	public static void main(String[] args) {
		KafkaSender.init();
		KafkaSender.sendSart();
		
		//PropertyConfigurator.configure("log4j.properties");	
		try {
			logger.info("version:20181220");
			run(args);
//			Recv();
//			Sand();
		} catch (Exception e) {
			logger.error("Controler main ", e);
		}
	}
	
	public static void Recv() {
		try {
			KafkaConsumer consumer = new KafkaConsumerImpl(
					"192.168.128.17:9092,192.168.128.18:9092,192.168.128.19:9092",
//					"192.168.27.30:9092,192.168.27.27:9092,192.168.27.28:9092",
//					"bookie.lsports.usa.odds", "ConsumerTestClient");
//					"JPMotorRaceInfo", "ConsumerTestClient");
					"lsports.horse_racing.odds", "ConsumerTestClient");
//					"testing", "ConsumerTestClient");
			
			Receiver receiver = new Receiver() {
				
				@Override
				public void execute(String arg0) {
					System.out.println(arg0);
				}
			};
			
			consumer.startReceiver(receiver);			
		} catch (Exception e) {
			logger.error("", e);
		}
	}
//	
//	public static void Sand() {
//		try {
//			for (int i = 1; i <= 1000 ; i++) {
//				try {
//					logger.info(i);
//					KafkaSender.send(i+" -------------------------------" );
//					Thread.sleep(2000);
//				} catch (Exception e) {
//					logger.error("", e);
//				}
//			}			
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}	
	
	public static void run(String[] args) {
		try {
			
			Config.configure("config.xml");
			
			//email=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&sports=687888,687894&countries=2,4,14,59,147,172,243
			
			url = Config.getValue("url");
			
			email = Config.getValue("email");
			password = Config.getValue("password");
			guid = Config.getValue("guid");
			
			queue_name = Config.getValue("queue_name");
			host = Config.getValue("host");
			
			LocationName = Config.getValue("LocationName");
			if (LocationName.trim().equals("")) {
				LocationName = null;
			}
			
			if (LocationName != null) {
				LocationName = "," + LocationName;
			}
			
			sports = Config.getValue("sports");
			countries = Config.getValue("countries");
			
			
			try {
				OutRight_Sleep_Hour = Integer.parseInt(Config.getValue("OutRight_Sleep_Hour"));
			} catch (Exception e) {
				OutRight_Sleep_Hour = 1;
			}
			OutRight_Sleep_Hour = OutRight_Sleep_Hour * 1000 * 60 * 60;
			
//			String newHost = Config.getValue("host");
//			if (newHost != null && newHost.trim().length() > 10) {
//				host = newHost;
//			}
//			if (!host.endsWith("/")) host = host + "/";
			
			
			ArrayList aAllBllValue = Config.getBllNoteValue();
			
			for (int i = 0; i < aAllBllValue.size(); i++) {
				String[] bllValue = (String[])aAllBllValue.get(i);
				if (bllValue[5].equals("1")){
					int bllNo = Integer.parseInt(bllValue[0]);
					isExtractFromWeb = Integer.parseInt(bllValue[2]);
					isExtractFromLocal = Integer.parseInt(bllValue[3]);
					saveFilePath = bllValue[4];
					isDeleteFile = Integer.parseInt(bllValue[6]);
				    bakFilePath = bllValue[7];
					
				    Date now = new Date();
		    		Date begin = now;
		    		Date end = now;				    
				    //https://api.timeform.com/HorseRacingApi/OData
				    switch (bllNo) {
				    	case 1:
				    		new OutRight().GetOutrightFixtures();
				    		new Thread(new OutRight(),"OutRight_Thread").start();
				    		new Markets().GetFixtureMarkets();
				    		new Thread(new Markets(),"Markets_Thread").start();
				    		break;
//				    		if (args.length == 1) {
//				    		} else if (args.length == 2) {
//				    		} else {
//				    			logger.info("begin BookOdds");
//				    		}
//				    		while (true) {
//				    			new Thread(new BookOdds(), "Thread_" + DateUtils.getLongStr()).start();
//				    			Thread.sleep(1000 * 30);
//				    		}
//				    		logger.info("end BookOdds");
				    	case 2:
				    		SaveAndParseDataThread.bSendKafka = true;
				    		new RecvRMQ().run();
				    		return;

				    	case 3:
				    		SaveAndParseDataThread.bSendKafka = false;
				    		new RecvKafka().run();
				    		return;
				    }				    
			    } //if
			} //for
			
			
//			logger.info("all finish");
				
		} catch (Exception e) {
			logger.error("Controler run ", e);
		}
	}
}
