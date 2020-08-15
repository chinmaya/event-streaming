package me.chinmaya.examples.kafka;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueJoiner;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JoinExample {
	private TopologyTestDriver testDriver;

	private ConsumerRecordFactory<String, String> factory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

	@Before
	public void setup() {
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> letters = builder.stream("letters");
		KStream<String, String> numbers = builder.stream("numbers");
		letters
			.join(numbers, new ValueJoiner<String, String, String>() {
				@Override
				public String apply(String value1, String value2) {
					return value1+value2;
				}
			}, JoinWindows.of(Duration.ofSeconds(60)))
			.to("joined");
		

		// setup test driver
		Properties config = new Properties();
		config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "xample");
		config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
		config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		testDriver = new TopologyTestDriver(builder.build(), config);
	}

	@After
	public void tearDown() {
		testDriver.close();
	}
	
	@Test
	public void testPrint() {
		List<KeyValue<String, String>> numbers = Arrays.asList(
				new KeyValue<String, String>("1", "1"),
				new KeyValue<String, String>("2", "2"),
				new KeyValue<String, String>("3", "3"),
				new KeyValue<String, String>("4", "4"),
				new KeyValue<String, String>("5", "5"),
				new KeyValue<String, String>("6", "6"),
				new KeyValue<String, String>("7", "7"),
				new KeyValue<String, String>("8", "8"),
				new KeyValue<String, String>("9", "9"),
				new KeyValue<String, String>("10", "10"));
		testDriver.pipeInput(factory.create("numbers", numbers, 9999L));

		List<KeyValue<String, String>> letters = Arrays.asList(
				new KeyValue<String, String>("1", "a"),
				new KeyValue<String, String>("2", "b"),
				new KeyValue<String, String>("3", "c"),
				new KeyValue<String, String>("4", "d"),
				new KeyValue<String, String>("5", "e"),
				new KeyValue<String, String>("6", "f"),
				new KeyValue<String, String>("7", "g"),
				new KeyValue<String, String>("8", "h"),
				new KeyValue<String, String>("9", "i"),
				new KeyValue<String, String>("10", "j"));
		testDriver.pipeInput(factory.create("letters", letters, 9999L));

		for (Long i=1L; i<=10L; i++) {
			ProducerRecord<String, String> output = testDriver.readOutput("joined", new StringDeserializer(), new StringDeserializer());
			OutputVerifier.compareKeyValue(output, i.toString(), Character.valueOf((char)(i + 96)) + i.toString());
		}
	}

}
