//package com.common;
//
//import javax.jms.JMSException;
//
//import org.apache.activemq.ActiveMQConnectionFactory;
//import org.apache.activemq.pool.PooledConnection;
//import org.apache.activemq.pool.PooledConnectionFactory;
//import org.apache.log4j.Logger;
//
//public class MQPoolUtil {
//
//	private static Logger logger = Logger.getLogger(MQPoolUtil.class);
//
//	private static PooledConnection conn;
//
//	public static void init() {
//		//tcp://192.168.14.21:61616?wireFormat.maxInactivityDurationInitalDelay=30000
////		String url = "failover:(tcp://192.168.14.21:61616)?initialReconnectDelay=1000&timeout=30000&startupMaxReconnectAttempts=2";
//		ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(SendOddsChangeNotifyToMQ.MQ_Url);
//		try {
//			PooledConnectionFactory poolFactory = new PooledConnectionFactory(factory);
//			conn = (PooledConnection) poolFactory.createConnection();
//			conn.start();
//
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}
//
//	public static void destroy() {
//		try {
//			if (conn != null) {
//				conn.close();
//			}
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}
//
//	public static PooledConnection getConn() {
//
//		return conn;
//	}
//
//	public static void setConn(PooledConnection conn) {
//
//		MQPoolUtil.conn = conn;
//	}
//
//}
