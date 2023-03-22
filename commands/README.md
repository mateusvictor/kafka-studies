# Kafka Topics

Create topic:
```
kafka-topics --bootstrap-server localhost:9092 --create --topic first_topic
```

Create topic with partitions:
```
kafka-topics --bootstrap-server localhost:9092 --create --topic second_topic --partitions 3
``` 

Describe a topic:
```
kafka-topics --bootstrap-server localhost:9092 --describe --topic first_topic
```

List all topics:
```
kafka-topics --bootstrap-server localhost:9092 --list 
```

# Kafka Console Producer
Create a topic first:
```
kafka-topics --bootstrap-server localhost:9092 --topic first_topic --partitions 3 --create
```

Connect to the topic:
```
kafka-console-prodducer --bootstrap-server localhost:9092 --topic first_topic
```

With acks:
```
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all
```

Kafka will automatically create the topic:
```
kafka-console-topics --bootstrap-server localhost:9092 --topic new_topic
```

Produce mensagens com key's:
```
kafka-console-producer --bootstrap-server localhost:9092 --topic first_topic --property parse.key=true --property key.separator=:
```

# Kafka Console Consumer
Connect to the topic:
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic 
```

Read all messages:
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --from-beginning
```

Join a group
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic first_topic --group app-group
```
