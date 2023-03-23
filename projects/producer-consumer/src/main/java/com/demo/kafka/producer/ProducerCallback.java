package com.demo.kafka.producer;

import java.util.Objects;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerCallback implements Callback {
  private static final Logger log = LoggerFactory.getLogger(ProducerDemo.class.getSimpleName());

  private static final String MESSAGE_LOG_FORMAT = "Topic=%s - Key=%s - Partition=%s - Offset=%s - Timestamp=%s";

  private final ProducerRecord<String, String> producerRecord;

  public ProducerCallback(ProducerRecord<String, String> record){
    producerRecord = record;
  }

  @Override
  public void onCompletion(RecordMetadata metadata, Exception exception) {
    if (Objects.nonNull(exception)){
      log.error("Error when sending message to topic", exception);
    }

    log.info("Topic received message!");
    log.info(
        String.format(
            MESSAGE_LOG_FORMAT,
            metadata.topic(),
            producerRecord.key(),
            metadata.partition(),
            metadata.offset(),
            metadata.timestamp()));
  }
}
