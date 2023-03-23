package com.demo.kafka.consumer;

import com.demo.kafka.producer.ProducerDemo;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.CooperativeStickyAssignor;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerDemo {
  private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

  private static final String GROUP_ID = "java-application";

  private static final String SERVER = "localhost:9092";

  private static final String TOPIC = "demo_java";

  private static final String MESSAGE_LOG_FORMAT = "Key=%7s - " +
      "Value=%15s - " +
      "Partition=%3s - " +
      "Offset=%4s - " +
      "Timestamp=%15s";

  public static KafkaConsumer<String, String> getKafkaConsumer(){
    Properties properties = new Properties();
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, SERVER);
    properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

    properties.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    properties.setProperty("group.id", GROUP_ID);
    properties.setProperty("auto.offset.reset", "earliest");
    properties.setProperty("partition.assignment.strategy", CooperativeStickyAssignor.class.getName());

    return new KafkaConsumer<>(properties);
  }

  public static void main(String[] args) {
    KafkaConsumer<String, String> consumer = getKafkaConsumer();

    Thread mainThread = Thread.currentThread();

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Detecting a shutdown. Exiting by calling consumer.wakeup()");
      consumer.wakeup();

      try {
        mainThread.join();
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }));

    try {
      consumer.subscribe(Collections.singleton(TOPIC));

      log.info("Consuming messages from topic: " + TOPIC);

      while (true) {
        ConsumerRecords <String, String> records = consumer.poll(Duration.ofMillis(1000));
        records.forEach(
            record -> log.info(String.format(
                MESSAGE_LOG_FORMAT,
                record.key(),
                record.value(),
                record.partition(),
                record.offset(),
                record.timestamp())));
      }
    } catch (WakeupException e){
      log.info("Consumer is starting to shutdown");
    } catch (Exception e){
      log.error("Unexpected error", e);
    } finally {
      consumer.close();
      log.info("The consumer was gracefully shut down");
    }
  }
}
