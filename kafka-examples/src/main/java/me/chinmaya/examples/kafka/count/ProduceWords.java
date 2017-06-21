package me.chinmaya.examples.kafka.count;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import me.chinmaya.examples.kafka.Config;

public class ProduceWords {
	static class WordsGenerator implements AutoCloseable {
		private int setSize;
		private BufferedReader reader;
		private String[] line;
		private int lineOffset;

		public WordsGenerator (String fileName, int setSize) throws FileNotFoundException {
			this.setSize = setSize;
			this.reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), Charset.forName("UTF-8")));
			this.line = new String[]{};
			this.lineOffset = line.length;
		}

		public WordsGenerator (InputStream is, int setSize) throws FileNotFoundException {
			this.setSize = setSize;
			this.reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			this.line = new String[]{};
			this.lineOffset = line.length;
		}

		@Override
		public void close() throws Exception {
			if (reader != null) {
				reader.close();
			}			
		}

		public String[] next() throws IOException {
			String[] words = new String[setSize];
			int remain = setSize;
			while (remain > 0) {
				if (lineConsumed()) {
					String ln = reader.readLine();
					if (ln != null) {
						line = ln.split("[\\s]");
						lineOffset = 0;
					} else {
						String[] partial = new String[remain];
						System.arraycopy(words, 0, partial, 0, remain);
						return partial;
					}
				}
				if (!"".equals(line[lineOffset].trim())) {
					words[setSize - remain] = line[lineOffset].replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
					remain --;
				}
				lineOffset ++;
			}
			return words;
		}
		
		private boolean lineConsumed() {
			return lineOffset > line.length-1;
		}
		
		public boolean hasNext() throws IOException {
			if (!lineConsumed()) {
				return true;
			}
			String ln = reader.readLine();
			if (ln != null) {
				line = reader.readLine().split("[\\s]");
				lineOffset = 0;
				return true;
			}
			return false;
		}
	}

	public static void main1(String[] args) throws FileNotFoundException, Exception {
		try (WordsGenerator generator = new WordsGenerator(ProduceWords.class.getResourceAsStream("Pride_and_Prejudice.txt"), 10)) {
			long total = 0;
			while (generator.hasNext()) {
				String[] words = generator.next();
				System.out.println(words[0]);
				total += words.length;
			}
			System.out.println("Read " + total);
		}
	}
	public static void main(String[] args) throws FileNotFoundException, Exception {
    	try (WordsGenerator generator = new WordsGenerator(ProduceWords.class.getResourceAsStream("Pride_and_Prejudice.txt"), 10); KafkaProducer<String, String> producer = new KafkaProducer<>(Config.producerProperties())) {
			while (generator.hasNext()) {
				String[] words = generator.next();
				StringBuffer wordsLine = new StringBuffer();
				for (String w : words) {
					wordsLine.append(w).append(" ");
				}
				ProducerRecord<String, String> record = new ProducerRecord<String, String>(Config.TOPIC_WORDS, null, wordsLine.toString());
				producer.send(record);
				try {
					Thread.sleep(1 * 1);
				} catch (Exception e) {
				}
    		}
    	}
    	System.out.println("Done");
	}
}
