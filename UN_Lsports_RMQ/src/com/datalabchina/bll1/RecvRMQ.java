package com.datalabchina.bll1;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.common.PageHelper;
import com.common.Utils;
import com.datalabchina.controler.Controler;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class RecvRMQ {

	private static Logger logger = Logger.getLogger(RecvRMQ.class);
	
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
		
		Connection connection = null;
		try {
			
//		    http://prematch.lsports.eu/OddService/DisablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
//		    http://prematch.lsports.eu/OddService/EnablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5			
			
			try {
				//"http://prematch.lsports.eu/OddService/EnablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5";
				String url = Controler.url + "EnablePackage?username=" + Controler.email + "&password=" + Controler.password + "&guid=" + Controler.guid + "&packageid=" + Controler.guid;
				String body = PageHelper.getPageHelper().doGet(url);
				logger.info("Body = \n" + body);
			} catch (Exception e) {
				logger.error("", e);
			}				
				
//				logger.info("Body = \n" + body);			
			
			new Thread(new TimerRMQ()).start();
			
			ConnectionFactory factory = new ConnectionFactory();
//			factory.setHost("inplay-rmq.lsports.eu");
			
//			factory.setHost("prematch-rmq.lsports.eu");
//			factory.setUsername("freddygalliers@googlemail.com");
//			factory.setPassword("cdd8b962");
			
			factory.setHost(Controler.host);
			factory.setUsername(Controler.email);
			factory.setPassword(Controler.password);
			
			factory.setPort(5672);
			factory.setVirtualHost("Customers");
			factory.setRequestedHeartbeat(580);
			factory.setAutomaticRecoveryEnabled(true);	//设置网络异常重连
			factory.setNetworkRecoveryInterval(10000);	//设置10s ，重试一次	
			
			connection = factory.newConnection();
			final Channel channel = connection.createChannel();
	
//			channel.queueDeclare(Controler.queue_name, true, false, false, null);
	
			logger.info(" [*] Waiting for messages. To exit press CTRL+C");
			
			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope,
						AMQP.BasicProperties properties, byte[] body) {
					
					try {
						
						lastReceiveTime = System.currentTimeMillis();
						
						String message = new String(body, "UTF-8");
						int l = message.length();
						if (l > 32) l = 32;
						System.out.println("Received : " + message.substring(0,l));
						
						if (message.indexOf("Type\":32,") == -1) {
//							logger.info("Received : \n" + message);
//							String MsgGuid = Utils.extractMatchValue(message, "\"MsgGuid\":\"(.*?)\"");
//							logger.info("MsgGuid = " + MsgGuid);
							
							lReceiveCount++;
							SaveAndParseDataThread.save(message);
							SaveFileThread.save(message);
						}
						
						//saveToFile(message);
						
//					channel.basicAck(envelope.getDeliveryTag(), false);
//					channel.basicAck(envelope.getDeliveryTag(), true);
					} catch (Exception e) {						
						logger.error("", e);
						System.exit(0);
					}
				}
			};
			channel.basicQos(0, 10000, false);
			channel.basicConsume(Controler.queue_name, true, consumer);
//			channel.basicConsume(Controler.queue_name, false, consumer);
			//https://www.jianshu.com/p/df231c152754
		
		} catch (Exception e) {
			logger.error("", e);
			try {
				connection.close();				
			} catch (Exception e2) {
				logger.error("", e2);
			}
		}
	}
	
//	public static void saveToFile(String conent) {
//		
//		try {
//			
//			String file = Controler.saveFilePath;
//			if (file.endsWith(File.separator) == false) {
//				file = file + File.separator;
//			}
//			
//			lastReceiveTime = System.currentTimeMillis();
//			
//			if (conent.indexOf("Type\":32,") > 0) return;
//			
//			lReceiveCount++;
//			
//			file = file + DateUtils.getFilePath() + File.separator;
//			
//			if (!FileDispose.bIfExistFile(file)) {
//				FileDispose.createDirectory(file);			
//			}
//			file = file + DateUtils.getShortStr1() + ".txt";
//			
//			new Markets().parseLine(conent, file);
//			new OutRight().parseLine(conent, file);
//			
//			BufferedWriter out = null;
//			try {
//				out = new BufferedWriter(new OutputStreamWriter(
//						new FileOutputStream(file, true)));
//				out.write(conent + "\r\n");
//			} catch (Exception e) {
//				logger.error("", e);
//			} finally {
//				try {
//					out.close();
//				} catch (IOException e) {
//					logger.error("", e);
//				}
//			}
//			
//		} catch (Exception e) {
//			logger.error("", e);
//		}
//	}
	
/*

 //			curl -X GET "http://prematch.lsports.eu/OddService/GetEvents?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5" -H "accept: application/json"
//			curl -X GET "http://prematch.lsports.eu/OddService/GetFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5" -H "accept: application/json"
			
//			curl -X GET "http://api.lsports.eu/api/Snapshot?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5" -H "accept: application/json"
//			curl -X GET "http://api.lsports.eu/api/Snapshot?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5"
			
//			http://api.lsports.eu/api/Snapshot/GetSnapshotJson?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5"
					
			try {				
				
				//has data
				//http://prematch.lsports.eu/OddService/GetOutrightFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5			
				//http://prematch.lsports.eu/OddService/GetFixtureMarkets?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
				
				//only header
				//http://prematch.lsports.eu/OddService/GetFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
				//http://prematch.lsports.eu/OddService/GetScores?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
				//http://prematch.lsports.eu/OddService/GetEvents?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
				
				//http://prematch.lsports.eu/OddService/GetEvents?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&Lang=English&Timestamp=1532337621
				
				//packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&
				
				
//				<option value="687888">Horse Racing (687888)</option>
//				<option value="687894">Trotting (687894)</option>
//				<option value="687893">Greyhounds (687893)</option>
				
				//http://prematch.lsports.eu/OddService/GetScores?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
				
//				String url = "http://prematch.lsports.eu/OddService/GetEvents?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5&Timestamp=1532337621";//&Sports=687893&Lang=English&Sports=687888"; //&lang=en&oddsFormat=EU&				
//				String body = PageHelper.getPageHelper().doGet(url);
//				FileDispose.saveFile(body, "GetEvents.json");
//				logger.info("Body = \n" + body);
//				logger.info("---------------------------");
				
//				url = url.replaceAll("GetEvents", "GetFixtures");
//				body = PageHelper.getPageHelper().doGet(url);
//				FileDispose.saveFile(body, "GetFixtures.json");
//				logger.info("Body = \n" + body);
//				logger.info("---------------------------");
				
//				url = "http://prematch.lsports.eu/OddService/GetOutrightFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5";
//				body = PageHelper.getPageHelper().doGet(url);
//				FileDispose.saveFile(body, "GetOutrightFixtures.json");
//				
//				url = "http://prematch.lsports.eu/OddService/GetFixtureMarkets?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=1413&guid=a1772950-77f5-4f24-8926-49393f2e7ad5";
//				body = PageHelper.getPageHelper().doGet(url);
//				FileDispose.saveFile(body, "GetFixtureMarkets.json");

//				?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5"
				
//				http://api.lsports.eu/api/Package/EnablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
//				http://api.lsports.eu/api/Package/DisablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5"
					
//			    http://prematch.lsports.eu/OddService/DisablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5
//			    http://prematch.lsports.eu/OddService/EnablePackage?username=freddygalliers@googlemail.com&password=cdd8b962&packageid=a1772950-77f5-4f24-8926-49393f2e7ad5&guid=a1772950-77f5-4f24-8926-49393f2e7ad5			
				
				
				
//				    private $getEventsURL = 'http://prematch.lsports.eu/OddService/GetEvents';
//				    private $getFixturesURL = 'http://prematch.lsports.eu/OddService/GetFixtures';
				
//				    private $snapshotURL = 'http://api.lsports.eu/api/Snapshot';
				
//				Inplay
//				    private $orderFixturesURL = 'http://api.lsports.eu/api/schedule/OrderFixtures';
//			curl -X GET "http://api.lsports.eu/api/schedule/OrderFixtures?username=freddygalliers@googlemail.com&password=cdd8b962&guid=a1772950-77f5-4f24-8926-49393f2e7ad5" -H "accept: application/json"
//				    private $cancelOrderFixturesURL = 'http://inplay.lsports.eu/api/schedule/CancelFixtureOrders';
				
//				    private $enablePackageURL = 'http://api.lsports.eu/api/Package/EnablePackage';
//				    private $disablePackageURL = 'http://api.lsports.eu/api/Package/DisablePackage';
//				    private $disablePreMatchPackageURL = 'http://prematch.lsports.eu/OddService/DisablePackage';
//				    private $enablePreMatchPackageUrl = 'http://prematch.lsports.eu/OddService/EnablePackage';
			} catch (Exception e) {
				logger.error("", e);
			}
 	
 */
	
/*	
Create a connection factory as follow:
ConnectionFactory connectionFactory = new ConnectionFactory
{
    HostName = "Described below",
    Port = 5672,
    UserName = "MyEmail",
    Password = "Passw0rd1234",
    AutomaticRecoveryEnabled = true,
    VirtualHost = "Customers", //Default value
    RequestedHeartbeat = 580,
    NetworkRecoveryInterval = TimeSpan.FromSeconds(1)
};
RMQ HostName:
InPlay: inplay-rmq.lsports.eu
PreMatch: prematch-rmq.lsports.eu

Create a connection as follows:
IConnection connection = _connectionFactory.CreateConnection();
Create a model as follows:
IModel model = connection.CreateModel();
Configure the quality of service:
model.BasicQos(prefetchSize: 0, prefetchCount: 1000, global: false);
Consume message:
EventingBasicConsumer consumer = new EventingBasicConsumer(model);
consumer.Received += (sender, eventArgs) =>
{
    // Deserialize message
    // Call method to handle deserialized message
};

Start message consumption:
(make sure to type in your package ID using underscores ('_') as describes) For example, if package ID was “102030”
model.BasicConsume(queue: '_102030_', noAck: true, consumer: consumer)
From here you should be able to pull up your RMQ connection and start receiving Heartbeat messages.
Full Snapshot Request
As updates coming through RMQ to you are deltas only, before you start consuming messages coming from LSports, you should request a snapshot of the data you should will be receiving.
You can do so by making an API call to the following:

Inplay Snapshot - Provides data for all events that are currently inplay. 
Prematch Get events - Provides data for requested event(s).
Possible error types

Note: If the queue reaches 10000 unread/unacked messages it will be automatically purged and your package will be disabled.

Most failed connection attempts occurs due to incorrect credentials or incorrect connection details.
Here are the most common errors and possible solutions:

"Connection failed" - Please check that the connection details i.e. Connection factory, RMQ host, VirtualHost were typed correctly.
"Access refused"- Please check that your package is enabled and the login credentials and package ID were typed correctly.

For in-depth explanation of our data-structure, you may continue reading our documentation.
Now all you need to do is, to start ordering our sports data according to your specific needs.
*/
}

