package me.chinmaya.examples.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CountBy2Example {
	private TopologyTestDriver testDriver;

	private ConsumerRecordFactory<String, Long> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new LongSerializer());

	@Before
	public void setup() {
		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, Long> capital = builder.stream("numbers");
		capital
			.mapValues(new ValueMapper<Long, Long>() {
				@Override
				public Long apply(Long value) {
					return value * 2;
				}
			})
			.to("countby2");
		

		// setup test driver
		Properties config = new Properties();
		config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "xample");
		config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
		config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
		config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
		testDriver = new TopologyTestDriver(builder.build(), config);
	}

	@After
	public void tearDown() {
		testDriver.close();
	}
	
	@Test
	public void testPrint() {
		List<KeyValue<String, Long>> stream = Arrays.asList(new KeyValue<String, Long>("1", 1L),
				new KeyValue<String, Long>("2", 2L),
				new KeyValue<String, Long>("3", 3L),
				new KeyValue<String, Long>("4", 4L),
				new KeyValue<String, Long>("5", 5L),
				new KeyValue<String, Long>("6", 6L),
				new KeyValue<String, Long>("7", 7L),
				new KeyValue<String, Long>("8", 8L),
				new KeyValue<String, Long>("9", 9L),
				new KeyValue<String, Long>("10", 10L));
		testDriver.pipeInput(recordFactory.create("numbers", stream, 9999L));
		for (Long i=1L; i<=10L; i++) {
			ProducerRecord<String, Long> output = testDriver.readOutput("countby2", new StringDeserializer(), new LongDeserializer());
			OutputVerifier.compareKeyValue(output, i.toString(), i*2);
		}
	}

}
