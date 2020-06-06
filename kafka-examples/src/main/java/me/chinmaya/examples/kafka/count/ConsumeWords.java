package me.chinmaya.examples.kafka.count;

import java.time.Duration;
import java.util.Arrays;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import me.chinmaya.examples.kafka.Config;

public class ConsumeWords {
    public static void main( String[] args ) {
		try (KafkaConsumer<String, String> consumer = new KafkaConsumer<>(Config.consumerProperties())) {
			consumer.subscribe(Arrays.asList(Config.TOPIC_WORDS));
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("From partition " + record.partition() + ", At " + record.offset() + ", " + record.key() + "->" + record.value());
				}
			}
		}
    }
}
