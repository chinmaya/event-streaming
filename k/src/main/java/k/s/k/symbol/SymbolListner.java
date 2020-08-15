package k.s.k.symbol;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class SymbolListner {

	private final Logger log = LoggerFactory.getLogger(SymbolListner.class);

	@KafkaListener(groupId = "json", topics = "test", containerFactory="symbolKafkaListenerContainerFactory")
	public void symbols(SymbolMessage message) {
		log.info("SymbolMessage: " + message);
	}
}
