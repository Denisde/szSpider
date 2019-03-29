package com.datalabchina.controler;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.common.Config;
import com.datalabchina.bll1.RecvKafka;
import com.datalabchina.bll1.saveMessageToDB;
import com.datalabchina.common.BukCopeDBUtil;
import com.momutech.kafka.consumer.KafkaConsumer;
import com.momutech.kafka.consumer.Receiver;
import com.momutech.kafka.consumer.impl.KafkaConsumerImpl;

import common.KafkaSender;

public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	public static int isExtractFromWeb = 0; 
	public static int isExtractFromLocal = 0;
	public static int isDeleteFile = 0;
	public static String sSaveFilePath = "F:\\UN\\UN_Lsports_RMQ";
	public static String bakFilePath = "F:\\UN\\UN_Lsports_RMQ";
	public static long sleep = 1000 * 60;
	public static  String connectionString = BukCopeDBUtil.getConnectString();
	
	public static void main(String[] args) {
		KafkaSender.init();
		KafkaSender.sendSart();
		try {
			logger.info("version:20181220");
//			Sand();
			run(args);
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
	public static void Sand() {
		try {
			for (int i = 1; i <= 1000 ; i++) {
				try {
					logger.info(i);
					KafkaSender.send(i+" -------------------------------" );
					Thread.sleep(2000);
				} catch (Exception e) {
					logger.error("", e);
				}
			}			
		} catch (Exception e) {
			logger.error("", e);
		}
	}	
	
	public static void run(String[] args) {
		try {
			Config.configure("config.xml");
			ArrayList aAllBllValue = Config.getBllNoteValue();
			for (int i = 0; i < aAllBllValue.size(); i++) {
				String[] bllValue = (String[])aAllBllValue.get(i);
				if (bllValue[5].equals("1")){
					int bllNo = Integer.parseInt(bllValue[0]);
					isExtractFromWeb = Integer.parseInt(bllValue[2]);
					isExtractFromLocal = Integer.parseInt(bllValue[3]);
					sSaveFilePath = bllValue[4];
					isDeleteFile = Integer.parseInt(bllValue[6]);
				    bakFilePath = bllValue[7];
				    switch (bllNo) {
				    	case 3:
//				    		SaveAndParseDataThread.bSendKafka = false;
				    		new RecvKafka().run();
//				    		new MsgConsumer("tote.fr.racepulse.horse_racing.xml.marketdata", true).consumer();
				    	return;
				    	case 4:
				    		String filePath =null;
				    		if(args.length==1)
				    			filePath =args[0];
				    		else
				    			filePath =sSaveFilePath;
				    		if(filePath!=null)
				    			saveMessageToDB.saveToDBbyPath(filePath);
				    		return;
				    }				    
			    } 
			}
		} catch (Exception e) {
			logger.error("Controler run ", e);
		}
	}
}
