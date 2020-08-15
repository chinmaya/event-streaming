package k.s.k.symbol;

import java.time.Duration;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.Uninterruptibles;

@Component
public class SymbolProducer {

	private final Logger log = LoggerFactory.getLogger(SymbolProducer.class);

	static class MessageSource {
		private final Random random = new Random(System.currentTimeMillis() * 31);
		public MessageSource() {
		}

		SymbolMessage next() {
			switch(random.nextInt(3)) {
			case 0:
				return new SymbolMessage(UUID.randomUUID().toString(), 2, "symbol", randomSymbol());
			case 1:
				return new SymbolMessage(UUID.randomUUID().toString(), 1, "number", String.valueOf(random.nextInt(100000)));
			default:
				return new SymbolMessage(UUID.randomUUID().toString(), 0, "alphabet", randomString());
			}
		}
		
		String randomString() {
			int min = 8; int max = 12;
			int range = max - min;
			int length = min + random.nextInt(range);
			StringBuilder builder = new StringBuilder();
			while (length > 0) {
				builder.append((char)(random.nextInt(26) + (random.nextInt(2) == 0?65:97)));
				length--;
			}
			return builder.toString();
		}
		
		String randomSymbol() {
			int min = 8; int max = 12;
			int range = max - min;
			int length = min + random.nextInt(range);
			StringBuilder builder = new StringBuilder();
			while (length > 0) {
				if (random.nextInt(2) == 0) {
					builder.append((char)(random.nextInt(15) + 33));
				} else {
					builder.append((char)(random.nextInt(7) + 58));
				}
				
				length--;
			}
			return builder.toString();
		}
	}

	private final KafkaTemplate<String, SymbolMessage> template;
	private final MessageSource source;

	public SymbolProducer() {
		this.template = SymbolTopicConfig.kafkaTemplate();
		this.source = new MessageSource();
	}
	
	@PostConstruct
	public void start() {
		log.info("Starting producer");
		Executors.newScheduledThreadPool(1).schedule(this::producer, 5, TimeUnit.SECONDS);
	}
	
	public void producer() {
		while (true) {
			SymbolMessage message = source.next();
			log.info("Sending", message);
			template.send("test", message.getPartitionId(), message.getIdentifier(), message);
			Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
		}
	}
	
}
