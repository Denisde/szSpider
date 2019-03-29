//package com.common;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.jms.Connection;
//import javax.jms.DeliveryMode;
//import javax.jms.Destination;
//import javax.jms.MessageProducer;
//import javax.jms.Session;
//import javax.jms.TextMessage;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.log4j.Logger;
//import org.apache.log4j.PropertyConfigurator;
//
//public class TestMQ extends Thread {
//	private static Logger logger = Logger.getLogger(TestMQ.class);
//	CommonMethod oCommonMethod = new CommonMethod();
//
////	private String raceStartTime = "";
////	private String JMSInfo = "";
//	SimpleDateFormat oSDFTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	
//	public static boolean MQ_SendOddsNotify = false;
//	public static String MQ_Url = "";
//	public static String MQ_Queues = "";
//	public static String MQ_Topics = "";
//	public static String MQ_RacingCode = "";
//	public static boolean MQ_ShowSendText=false;
//	
//	public static Session session = null;
//	public static MessageProducer producer = null;   
//	public static Destination destination = null;
//	
//	public static long sendno = 0;
//	public static long sendedno = 0;
//
//	
//	public static List<String> toSendMQList = new ArrayList<String>();
//
//	public TestMQ() {
//	}
//	
////	public SendOddsChangeNotifyToMQ(String sJMSInfo, String sRaceStartTime) {
////		JMSInfo = sJMSInfo;
////		raceStartTime = sRaceStartTime;
////	}
//	
//	public static void sendMQ(String sJMSInfo, String sRaceStartTime) {
//		if (MQ_SendOddsNotify == false) return;
//		synchronized (toSendMQList) {
//			sendno++;
//			toSendMQList.add(sJMSInfo + "##" + sRaceStartTime);
//			logger.info("toSendMQList.size() = " + toSendMQList.size());
//			if (isRunning("SendOddsChangeNotifyToMQ_Thread") == false) {
//				logger.error("SendOddsChangeNotifyToMQ Thread not find. start one");
//				new Thread(new TestMQ(), "SendOddsChangeNotifyToMQ_Thread").start();
//			}
//			
//		}		
//	}
//	
//	private static boolean isRunning(String name) {
//		ThreadGroup sys;
//		Thread[] all;
//		String sThreadName = null;
//		Boolean isRunning = false;
//
//		sys = Thread.currentThread().getThreadGroup();
//		all = new Thread[sys.activeCount()];
//		sys.enumerate(all);
//		for (int i = 0; i < all.length; i++) {
//			sThreadName = all[i].getName();
////			logger.info("Thread Name=" + sThreadName);
//			if (sThreadName.equals(name)) {
//				isRunning = true;
//				break;
//			}
//		}
//		
//		sys = null;
//		all = null;
//		sThreadName = null;
//		return isRunning;
//	}
//
//	public void run() {
//		try {
//			if (MQ_SendOddsNotify == false)
//				return;
//			
//			while (true) {
//				
//				String line = "";
//				synchronized (toSendMQList) {
//					int s = toSendMQList.size();
//					if (s > 0) {
//						try {
//							line = toSendMQList.get(0);
//							toSendMQList.remove(0);
//						} catch (Exception e) {
//						}
//					}
//				}
//				
//				if (line.equals("")) {
//					Thread.sleep(1000);
//				} else {
//					send(line);								
//				}
//			}
//			
//		} catch (Exception e) {			
//			logger.error("", e);
//		}
//	}
//
//	private void initProducer() {
//		
//		try {
//            session = MQPoolUtil.getConn().createSession(false, Session.AUTO_ACKNOWLEDGE);  
//
//			if (!MQ_Queues.equals("")) {
//				destination = session.createQueue(MQ_Queues);
//			} else if (!MQ_Topics.equals("")) {
//				destination = session.createTopic(MQ_Topics);
//			} else {
//				return;
//			}
//
//			// 创建消息生产者
//			producer = session.createProducer(destination);
//			// 设置持久化，DeliveryMode.PERSISTENT和DeliveryMode.NON_PERSISTENT
//			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//			// 创建消息
//  
//		} catch (Exception e) {			
//			logger.error("", e);
//		}
//	}
//	
//	private void send(String line) {
//		
//		try {
//			
//			String a[] = line.split("##");
////			.add(sJMSInfo + "" + sRaceStartTime);
//			String JMSInfo = a[0];
//			String raceStartTime = a[1];
//
//			if (producer == null || session == null || destination == null) {
//				initProducer();
//			}
//			
//			TextMessage message = session.createTextMessage(JMSInfo);
//			producer.send(message);
//			sendedno++;
//			
//			if (MQ_ShowSendText)
//				logger.info(sendedno + "/" + sendno + " Thread.activeCount = " + Thread.activeCount() + "  " + getLongStr() + " " + raceStartTime + " JMSInfo = " + JMSInfo);
//  
//		} catch (Exception e) {			
//			logger.error("", e);
//			logger.error("Send JMS Error " + getLongStr() + " " + line);
//		}
//	}
//	
//	
//	public void setSendNotifyParameter() {
//
//		boolean bIsExist = CommonMethod.bIfExistFile("sendNotifyToMQ.xml");
//		if (!bIsExist) {
//			logger.warn("sendNotifyToMQ.xml file is not exist ,will not send notify");
//			MQ_SendOddsNotify = false;
//			return;
//		}
//
//		String fileContent = oCommonMethod.readFile("sendNotifyToMQ.xml");
//		String oddsSendStr = CommonMethod.getValueByPatter(fileContent, "<Send>(\\d{1})</Send>");
//		if (oddsSendStr.equals("1")) {
//			MQ_SendOddsNotify = true;
//		} else {
//			logger.warn("Please set sendNotifyToMQ.xml <send> as 1");
//			MQ_SendOddsNotify = false;
//			return;
//		}
//
//		String URL = CommonMethod.getValueByPatter(fileContent, "<URL>(.*?)</URL>");
//		if (URL.equals("")) {
//			logger.warn("Please set sendNotifyToMQ.xml <URL> ");
//			MQ_SendOddsNotify = false;
//			return;
//		} else {
//			MQ_Url = URL;
//		}
//
//		MQ_RacingCode = CommonMethod.getValueByPatter(fileContent, "<RacingCode>(.*?)</RacingCode>");
//		if (MQ_RacingCode.equals("")) {
//			logger.warn("Please set sendNotifyToMQ.xml <RacingCode> ");
//			MQ_SendOddsNotify = false;
//			return;
//		}
//
//		MQ_Queues = CommonMethod.getValueByPatter(fileContent, "<Queues>(.*?)</Queues>");
//
//		MQ_Topics = CommonMethod.getValueByPatter(fileContent, "<Topics>(.*?)</Topics>");
//
//		if (MQ_Queues.equals("") && MQ_Topics.equals("")) {
//			logger.warn("Please set sendNotifyToMQ.xml <Queues> or </Topics>");
//			MQ_SendOddsNotify = false;
//			return;
//		}
//
//		String ShowSendXml = CommonMethod.getValueByPatter(fileContent, "<ShowSendText>(\\d{1})</ShowSendText>");
//		if (ShowSendXml.equals("1")) {
//			MQ_ShowSendText = true;
//		} else {
//			MQ_ShowSendText = false;
//		}
//		
//		MQPoolUtil.init();
//		new Thread(new TestMQ(), "SendOddsChangeNotifyToMQ_Thread").start();
//
//	}
//
//	public void send() {
//		Connection connection = null;
//		Session session = null;
//		try {
//
//			// 创建一个连接工厂
//			//			            String url = "tcp://192.168.60.234:61616";
//			//			String url = "tcp://192.168.14.21:61616";
//			
//			
//			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://prematch-rmq.lsports.eu:5672");
//			// 设置用户名和密码，这个用户名和密码在conf目录下的credentials.properties文件中，也可以在activemq.xml中配置
//			            connectionFactory.setUserName("freddygalliers@googlemail.com");
//			            connectionFactory.setPassword("cdd8b962");
////			            connectionFactory.set
//
//			// 创建连接
//			connection = connectionFactory.createConnection();
//			connection.start();
//			// 创建Session，参数解释：
//			// 第一个参数是否使用事务:当消息发送者向消息提供者（即消息代理）发送消息时，消息发送者等待消息代理的确认，没有回应则抛出异常，消息发送程序负责处理这个错误。
//			// 第二个参数消息的确认模式：
//			// AUTO_ACKNOWLEDGE ： 指定消息提供者在每次收到消息时自动发送确认。消息只向目标发送一次，但传输过程中可能因为错误而丢失消息。
//			// CLIENT_ACKNOWLEDGE ： 由消息接收者确认收到消息，通过调用消息的acknowledge()方法（会通知消息提供者收到了消息）
//			// DUPS_OK_ACKNOWLEDGE ： 指定消息提供者在消息接收者没有确认发送时重新发送消息（这种确认模式不在乎接收者收到重复的消息）。
//			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//
//			// 创建目标，就创建主题也可以创建队列            
//			//            Destination destination = session.createQueue("22.betitems.1");
//			Destination destination = null; //	session.createTopic("22.betitems.1");
//
//			MQ_Queues = "_1413_";
//			
//			if (!MQ_Queues.equals("")) {
//				destination = session.createQueue(MQ_Queues);
//			} else if (!MQ_Topics.equals("")) {
//				destination = session.createTopic(MQ_Topics);
//			} else {
//				return;
//			}
//
//			// 创建消息生产者
//			MessageProducer producer = session.createProducer(destination);
//			// 设置持久化，DeliveryMode.PERSISTENT和DeliveryMode.NON_PERSISTENT
//			producer.setDeliveryMode(DeliveryMode.PERSISTENT);
//			// 创建消息
//
////			if (MQ_ShowSendText)
////				logger.info(getLongStr() + " raceStartTime = " + raceStartTime + " JMSInfo = " + JMSInfo);
//			TextMessage message = session.createTextMessage("JMSInfo");
//			// 发送消息到ActiveMQ
//			producer.send(message);
//			System.out.println("sss");
//		} catch (Exception e) {
//			logger.error("", e);
//		} finally {
//			try {
//				// 关闭资源
//				if (session != null)
//					session.close();
//				if (connection != null)
//					connection.close();
//			} catch (Exception e2) {
//			}
//		}
//	}
//
//	public static String getLongStr() {
//		Date currentTime = new Date();
//		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String dateString = formatter.format(currentTime);
//		return dateString;
//	}
//	
//	public static void main(String arg[]) {
//		PropertyConfigurator.configure("log4j.properties");
//
//		new TestMQ().send();
//		
////		new SendOddsChangeNotifyToMQ().setSendNotifyParameter();
////		new SendOddsChangeNotifyToMQ("raceStartTime", "WinOdds|DataProviderName|Key|CountryID|HorseNumber|LongExtractTime|LongTimestamp|HorseOdds|MinutesTogo|0").send();
//
//		//200710080829507|MR_20071008_07|15:25:17 20071008|WIN_PLACE_VIC
//		//(new SendOddsChangeNotify("200712100829507|MR_20071210_07","15:25:17 20071008","WIN_PLACE_NSW")).start();
//		//		new SendOddsChangeNotifyToMQ("200712100829507","2012-12-29 07:37:33","WIN","1").run();
//		//		send();
//
//	}
//
//}
