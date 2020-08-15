package me.chinmaya.examples.kafka;

import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TranslateExample {
	private TopologyTestDriver testDriver;

	private StringDeserializer stringDeserializer = new StringDeserializer();
	private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());

	@Before
	public void setup() {
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, String> capital = builder.stream("capital");
		capital
			.mapValues(new ValueMapper<String, String>() {
				@Override
				public String apply(String value) {
					return value.toLowerCase();
				}
			})
			.to("small");
		

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
		testDriver.pipeInput(recordFactory.create("capital", "A", "A", 9999L));
		ProducerRecord<String, String> output = testDriver.readOutput("small", stringDeserializer, stringDeserializer);
		OutputVerifier.compareKeyValue(output, "A", "a");
		Assert.assertNull(testDriver.readOutput("small", stringDeserializer, stringDeserializer));
	}

}
