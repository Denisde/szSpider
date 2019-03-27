package com.datalabchina.bll1;

import java.net.InetAddress;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.PageHelper;
import com.common.Utils;
import com.datalabchina.controler.Controler;
import com.momutech.kafka.consumer.KafkaConsumer;
import com.momutech.kafka.consumer.Receiver;
import com.momutech.kafka.consumer.impl.KafkaConsumerImpl;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import common.KafkaSender;

public class RecvKafka {

	private static Logger logger = Logger.getLogger(RecvKafka.class);
	
	public static long lastReceiveTime = System.currentTimeMillis();
	
	public static long lReceiveCount = 0;
	
	public static void main(String[] argv) {
		PropertyConfigurator.configure("log4j.properties");
			run();
		try {
		} catch (Exception e) {
		}
	}
	
	

	public static void run() {	
		
		try {
			
			new Thread(new TimerKafka()).start();
			
//			String serverName = "192.168.128.17:9092,192.168.128.18:9092,192.168.128.19:9092";
//			String topicName = "lsports.horse_racing.odds";
			
//			String serverName = "192.168.27.27:9092,192.168.27.28:9092,192.168.27.30:9092";
//			String topicName = "bookie.lsports.usa.odds";
			
			String serverName = "192.168.27.27:9092,192.168.27.28:9092,192.168.27.30:9092";
			String topicName = "tote.fr.racepulse.horse_racing.xml.marketdata";
			
//			String serverName = KafkaSender.Servers_Data;
//			String topicName = KafkaSender.Topic_Data;
			
			String ip = getLocalHostIPAddress();
			String consumerName = RecvKafka.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			consumerName = ip + ":" + consumerName;
			/*
				Properties props = new Properties();
				props.put("bootstrap.servers", "broker1:9092,broker2:9092");
				props.put("group.id", "CountryCounter");
				props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
				KafkaConsumer<String, String> consumer = new KafkaConsumer<String,String>(props);
			 * */
			KafkaConsumer consumer = new KafkaConsumerImpl(serverName,topicName,consumerName);
			Receiver receiver = new Receiver() {
				@Override
				public void execute(String message) {
					try {
						lastReceiveTime = System.currentTimeMillis();
						int l = message.length();
						if (l > 32) l = 32;
						System.out.println("Received : " + message.substring(0,l));
						if (message.indexOf("Type\":32,") == -1) {
//							logger.info("Received : \n" + message);
//							String MsgGuid = Utils.extractMatchValue(message, "\"MsgGuid\":\"(.*?)\"");
//							logger.info("MsgGuid = " + MsgGuid);
							lReceiveCount++;
//							SaveAndParseDataThread.save(message);
							SaveFileThread.save(message);
						}
					} catch (Exception e) {						
						logger.error("", e);
						System.exit(0);
					}
				}
			};
			consumer.startReceiver(receiver);		
		} catch (Exception e) {
			logger.error("", e);
			try {
			} catch (Exception e2) {
				logger.error("", e2);
			}
		}
	}
	
	public static String getLocalHostIPAddress(){
		String ip="";
		try{
			 InetAddress addr = InetAddress.getLocalHost();			 
			 ip=addr.getHostAddress().toString();
			 if (ip.startsWith("127.0.0")) {
				 ip = addr.getHostName();
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
		return ip;
	}
	
}

