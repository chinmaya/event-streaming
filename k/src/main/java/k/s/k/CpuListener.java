package k.s.k;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class CpuListener {

	private final Logger log = LoggerFactory.getLogger(CpuListener.class);

	@Bean
	public NewTopic cpu() {
		return new NewTopic("cpu", 1, (short) -1);
	}

	@KafkaListener(groupId = "cpugroup", topics = "cpu")
	public void cpu(String line) {
		log.info("cpu:" + line);
	}
}
