# Kafka

## Kafka Topic
* Um local para armazenar dados
* Como se fosse uma tabela em um banco de dados, mas sem nenhuma constraint
  * Ou seja, um tópico não realiza nenhuma validaão sobre a suas informaões
* Não é consultável
  * A fim de inserir uma informação em um tópico, é necessário um Producer
  * A fim de ler um dado de um tópico, é necessário um Consumer
* Suporta qualquer tipo de dado
* Uma sequência de mensagem é denominada "data stream"

### Partitions
* Um tópico é formado por múltiplas partições ("filas")
* As mensagens em uma partição são ordenadas através de um ID incremental -> offset
* As mensagens são imutáveis
* Mensagens possuem um TTL 

<img src="https://github.com/mateusvictor/kafka-studies/blob/main/notes/imgs/partitions.png">

## Producer
* Envia dados para uma partição de um tópico
* Já sabe qual partição enviar

## Messages
* Key - binary
* Value - binary
* Headers (optional)
  * Key, values
* Partiton + Offset
* Timestamp

### Kafka Message Serializer
* Envia e recebe apenas bytes de Producers e Consumers

## Consumer
* Um Consumer lê dados de um tópico - pull model
  * Consumer que envia a requisição
* Lê mensagens de um tópico em ordem crescente de offset
* Periodicamente, envia os offsets lidos de volta para o tópico, a fim de comunicar qual foi a última mensagem lida
* Offsets comitados são armazenados no tópico __consumer_offsets
* Três tipos de envios de offsets de volta para o tópico:

1. Pelo menos uma vez (recomendado):
  * Offsets são enviados após o processamento de um mensagem
  * Se der erro, a mensagem é reenviada
  * __Este processo pode gerar mensagens publicadas:__ processamento deve ser idempotent - característica que define que uma mensagem duplicada não deve impactar os sistemas
2. No máximo uma vez:
  * Offsets são enviados logo após o recebimento da mensagem
  * Caso ocorra erro no processamento da mensagem, a mesma não será reenviada
3. Apenas uma vez:
   
## Broker
* Um cluster é composto por múltiplos brokers
* Cada broker possui determinadas partições de diversos tópicos
* Após se conectar com um broker, o client será conectado ao Cluster inteiro
* 3 a 100 brokers por Cluster
* Cada broker possui conhecimento de os outros brokers, topics e partitions - metadata

## Topic Replication Factor
* Quantidade de brokers que cada partição do tópico estará replicada

<img src="https://github.com/mateusvictor/kafka-studies/blob/main/notes/imgs/replication.png">

### Partition Leader
* A única partição que recebe dados do Producer e replica os dados para outras partições
* Cada partição possui um PL e múltiplos ISR (in-sync replica)
* Producers apenas enviam dados para os PLs
* Consumers apenas leem dados dos PLs 

## Producer Acknowledgements (acks)
* Confirmações de recebimento da mensagem enviadas pelo tópico para o Producer
* Producers podem decidir se querem receber acks de data writes
  * acks=0 -> Producer não espera por acknowledgement
  * acks=1 -> Producer espera pela resposta de recebimento do PL
  * acks=all -> Producer espera pela resposta de recebimento do PL e todas as réplicas da partição

## Kafka Producer

* Round Robin: every message is sent to a different partition (single produce)
* Sticky Partitioner: Kafka send multiple messages in batch mode

## Consumer Groups e Partition Rebalance
* Re-atribuição de partições aos consumers
* Acontece quando um consumidor entra ou sai de um grupo
* Acontece também quando novas partições são adicionadas 

### Eager rebalance
* Todos os consumidores param de processar por um período
* Os consumers entram novamente no grupo e recebem uma atribuição de partição
* Os consumers não nencessariamente irão consumir das partições prévias


### Cooperative rebalance
* Apenas um pequeno grupo de partições são migradas de um consumer para outro
* Os outros consumers continuam processando normalmente