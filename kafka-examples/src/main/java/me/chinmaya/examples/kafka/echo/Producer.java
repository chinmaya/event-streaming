package me.chinmaya.examples.kafka.echo;

import java.util.Date;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import me.chinmaya.examples.kafka.Config;

public class Producer {
	public static void main(String[] args) {
    	try (KafkaProducer<String, String> producer = new KafkaProducer<>(Config.producerProperties())) {
    		while (true) {
    			long timestamp = System.currentTimeMillis();
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(Config.TOPIC_ECHO, String.valueOf(timestamp ), new Date(timestamp).toString());
				producer.send(record);
				try {
					Thread.sleep(1000 * 5);
				} catch (Exception e) {
				}
    		}
    	}
	}
}
