package com.datalabchina.bll1;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
 
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import common.KafkaSender;
 
/**
 * 消息消费者
 * 
 */
public class MsgConsumer {
	private String group = "MsgConsumer";
	private final int timeSection = 3000;
	private boolean autoCommit;
	private List<String> topics = Arrays.asList("test");
 
	public MsgConsumer( String group, boolean autoCommit) {
		String topicName = KafkaSender.Topic_Data;
		this.topics = Arrays.asList(topicName);
		this.group = group;
		this.autoCommit = autoCommit;
	}
 
	public void consumer() {
		String serverName = KafkaSender.Servers_Data;
		
		try {
			Properties properties = new Properties();
			properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());// key反序列化方式
			properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getCanonicalName());// value反系列化方式
			properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, autoCommit);// 提交方式
			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"192.168.27.27:9092,192.168.27.28:9092,192.168.27.30:9092");// 指定broker地址，来找到group的coordinator
//			properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,serverName);// 指定broker地址，来找到group的coordinator
			properties.put(ConsumerConfig.GROUP_ID_CONFIG, group);// 指定用户组
	 
			KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
			consumer.subscribe(topics);// 指定topic消费
	 
			while (true) {
				System.out.println("Start poll---------------");
				ConsumerRecords<String, String> records = consumer.poll(timeSection);// 拉取一次数据
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("topic: " + record.topic() + " key: " + record.key() + " value: " + record.value()+ " partition: " + record.partition());
				}
				if (!autoCommit) {
					consumer.commitAsync();// 手动commit
				}
				System.out.println("Finish poll---------------");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void main(String[] args) {
		new MsgConsumer("fx.exchange.rate", true).consumer();
	}
}
