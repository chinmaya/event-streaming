package me.chinmaya.examples.kafka;

import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

public class Config {
	public static final String TOPIC_ECHO = "echo";
	public static final String TOPIC_WORDS = "words";
	public static final String TOPIC_WORD_COUNTS = "word_counts";

	public static Properties producerProperties() {
    	Properties properties = new Properties();
    	properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    	properties.put("key.serializer", StringSerializer.class.getName());
    	properties.put("key.deserializer", StringDeserializer.class.getName());
    	properties.put("value.serializer", StringSerializer.class.getName());
    	properties.put("value.deserializer", StringDeserializer.class.getName());
    	return properties;
	}

	public static Properties consumerProperties() {
    	Properties properties = producerProperties();
    	properties.put("group.id", "me.chinmaya.example");
    	properties.put("client.id", "me.chinmaya.example.0");
    	return properties;
	}
	
	public static Properties streamProperties() {
		Properties properties = new Properties();
		properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count");
		properties.put(StreamsConfig.KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		properties.put(StreamsConfig.VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    	return properties;
	}

	public static Properties wordCountConsumerProperties() {
    	Properties properties = new Properties();
    	properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    	properties.put("key.serializer", LongSerializer.class.getName());
    	properties.put("key.deserializer", StringDeserializer.class.getName());
    	properties.put("value.serializer", LongSerializer.class.getName());
    	properties.put("value.deserializer", StringDeserializer.class.getName());
    	properties.put("group.id", "me.chinmaya.example");
    	properties.put("client.id", "me.chinmaya.example.0");
    	return properties;
	}
}
