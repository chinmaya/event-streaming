package me.chinmaya.examples.kafka.count;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.ValueMapper;

import me.chinmaya.examples.kafka.Config;

public class WordCount {
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		KStreamBuilder streamBuilder = new KStreamBuilder();
		KStream<String, String> stream = streamBuilder.stream(Config.TOPIC_WORDS);
		KTable<String, String> counts = stream
				.flatMapValues(new ValueMapper<String, Iterable<String>>() {

					@Override
					public Iterable<String> apply(String value) {
						return Arrays.asList(value.split("[ ]"));
					}
					
				})
				.map(new KeyValueMapper<String, String, KeyValue<String, String>>() {

					@Override
					public KeyValue<String, String> apply(String key, String value) {
						return new KeyValue<>(value, value);
					}
				})
				.groupByKey()
				.count("WordCounts")
				.mapValues(new ValueMapper<Long, String>() {

					@Override
					public String apply(Long value) {
						return Long.toString(value);
					}
				});
		counts.to(Config.TOPIC_WORD_COUNTS);
		
		KafkaStreams streams = new KafkaStreams(streamBuilder, Config.streamProperties());
        streams.start();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        	streams.close();
        }));
        
        Executors.newSingleThreadExecutor().submit(() -> {
    		try (KafkaConsumer<String, Long> consumer = new KafkaConsumer<>(Config.consumerProperties())) {
    			consumer.subscribe(Arrays.asList(Config.TOPIC_WORD_COUNTS));
    			while (true) {
    				ConsumerRecords<String, Long> records = consumer.poll(1000);
    				for (ConsumerRecord<String, Long> record : records) {
    					System.out.println("Count " + record.key() + "=" + record.value());
    				}
    			}
    		}
        }).get();
        Thread.currentThread().join();
	}
}
