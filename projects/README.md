# Projects

## [1. Producer-Consumer - first impl](https://github.com/mateusvictor/kafka-studies/blob/main/projects/producer-consumer)
* Basic implementation of a consumer and a producer using kafka-clients (org.apache.kafka)
* Implements a consumer subscribed to a consumer group for a topic.

### Initialization
* Create a topic with multiple partitions using ```kafka-topics``` command:
```
kafka-topics --bootstrap-server localhost:9092 --topic demo_java --create --partitions 3 --replication-factor 1
```

* Run the class [ConsumerDemo.java](https://github.com/mateusvictor/kafka-studies/blob/main/projects/producer-consumer/src/main/java/com/demo/kafka/consumer/ConsumerDemo.java)in 2 or more different terminals

* Run the class [ProducerDemo.java](https://github.com/mateusvictor/kafka-studies/blob/main/projects/producer-consumer/src/main/java/com/demo/kafka/producer/ProducerDemo.java)

* Note that all the messages are distributed across the consumers (because they are subscribed to the same consumer group)

