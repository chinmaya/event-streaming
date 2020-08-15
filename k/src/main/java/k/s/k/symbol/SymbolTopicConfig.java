package k.s.k.symbol;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class SymbolTopicConfig {

	@Bean
	public NewTopic test() {
		return new NewTopic("test", 1, (short) -1);
	}

	public static ProducerFactory<String, SymbolMessage> producerFactory() {
		return new DefaultKafkaProducerFactory<>(producerConfigs());
	}

	public static KafkaTemplate<String, SymbolMessage> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}

	private static Map<String, Object> producerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		//props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
		//props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
		//props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, SymbolMessageSerializer.class);
		return props;
	}

	public Map<String, Object> symbolConsumerConfigs() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, SymbolMessageDeserializer.class);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "json");
		return props;
	}

	public ConsumerFactory<String, SymbolMessage> symbolConsumerFactory() {
		return new DefaultKafkaConsumerFactory<>(symbolConsumerConfigs(), new StringDeserializer(),
				new SymbolMessageDeserializer());
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, SymbolMessage> symbolKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, SymbolMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConcurrency(3);
		factory.setConsumerFactory(symbolConsumerFactory());
		return factory;
	}
}
