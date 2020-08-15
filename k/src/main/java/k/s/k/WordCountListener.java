package k.s.k;

import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class WordCountListener {

	private final Logger log = LoggerFactory.getLogger(WordCountListener.class);

	@Bean
	public NewTopic wordCount() {
		return new NewTopic("wordcount", 1, (short) -1);
	}

	@KafkaListener(groupId = "group1", topics = "wordcount")
	public void count(String line) {
		log.info("Word count for " + line + " is " + line.split(","));
	}
}
