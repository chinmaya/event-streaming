package me.chinmaya.examples.kafka.echo;

import java.util.Collections;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import me.chinmaya.examples.kafka.Config;

public class ConsumerOffsetCheck {
    public static void main( String[] args ) {
		while (true) {
			try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Config.consumerProperties())) {
				TopicPartition partition = new TopicPartition(Config.TOPIC_ECHO, 0);
				consumer.assign(Collections.singletonList(partition));
				consumer.assignment().forEach(System.out::println);
				System.out.println("Offset is " + consumer.position(partition));
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
    }
}
