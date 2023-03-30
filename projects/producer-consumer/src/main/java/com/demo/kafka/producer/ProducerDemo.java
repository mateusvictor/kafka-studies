package com.demo.kafka.producer;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerDemo {
  private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

  private static final String SERVER = "localhost:9092";

  private static final String TOPIC = "demo_java";

  public static KafkaProducer<String, String> getKafkaProducer(){
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
    properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, String.valueOf(32 * 1024));
    properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");

    return new KafkaProducer<>(properties);
  }

  public static void main(String[] args) {
    KafkaProducer<String, String> producer = getKafkaProducer();

    for (int i = 1; i <= 10; i++){
      String key = "id_" + i;
      String value = "hello_world" + i;

      ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, key, value);

      producer.send(producerRecord, new ProducerCallback(producerRecord));
    }

    producer.flush();
    producer.close();
  }
}
