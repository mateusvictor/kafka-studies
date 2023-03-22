# Run Kafka and Zoo with docker

```bash
colima start
```

```bash
docker-compose -f zk-kafka.yml up -d
```

```bash
docker-compose -f zk-kafka.yml ps
```

## Stop

```bash
docker-compose -f zk-kafka.yml stop
```

```bash
docker-compose -f zk-kafka.yml down
```
