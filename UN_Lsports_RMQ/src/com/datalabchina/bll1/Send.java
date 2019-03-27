package com.datalabchina.bll1;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Send {

	private final static String QUEUE_NAME = "_1413_";

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
//    factory.setHost("prematch-rmq.lsports.eu");
    factory.setHost("inplay-rmq.lsports.eu");
    factory.setUsername("freddygalliers@googlemail.com");
    factory.setPassword("cdd8b962");
    factory.setPort(5672);
    factory.setAutomaticRecoveryEnabled(true);
    factory.setVirtualHost("Customers");
    factory.setRequestedHeartbeat(580);
    factory.setNetworkRecoveryInterval(1000);
//    factory.set
//    		    NetworkRecoveryInterval = TimeSpan.FromSeconds(1)
    
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
    String message = "Hello World!";
    channel.basicPublish("AMQPLAIN", QUEUE_NAME, null, message.getBytes("UTF-8"));
    System.out.println(" [x] Sent '" + message + "'");
    

    channel.close();
    connection.close();
  }
}