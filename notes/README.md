# Kafka

## Kafka Topic
* Um local para armazenar dados
* Como se fosse uma tabela em um banco de dados, mas sem nenhuma constraint
  * Ou seja, um tópico não realiza nenhuma validação sobre a suas informaões
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

## Kafka Safe Producer Settings
* Default configs since kafka > 2.8
* **acks = all**
  * Garante que todas as replicas da partição receberam a mensagem antes de enviar um ack (Acknowledgement)
* **enable.idempotence = true**
  * Idempotencia: garante que uma mensagem duplicada não vai ser gravada duas vezes no tópico (provado por retries)
  * Exemplo: 
    * mensagem A é enviada mas o tópico não retorna nenhum ack
    * mensagem A é enviada novamente (retry)
    * tópico entende que é uma mensagem duplicada e não escreve novamente, enviando apenas um ack para o producer
* **retries = MAX_INT**
  * Garante que todos os retries são executados para uma mensagem 
* **max.in.flight.requests.per.connection = 5**
  * Garante performance máxima enquanto mantém a ordenação de mensagens
* **linger.ms**
  * Tempo (em ms) que o producer vai esperar para enviar o batch

## Network concepts
* **Throughput:** quantidade de dados transferidos de um lugar para o outro
* **Latency:** tempo de resposta de um sistema

## Producer partitioner

### Key != null
* Quando a "key" da mensagem não é nula, o "Partitioner" padrão do Producer define uma partição para a mensagem através de uma fórmula e um algoritmo de hash - murmur2
* A mesma chave sempre irá para a mesma partição

### Key == null
* Quando a key == null, o producer possui dois tipos de partitioners
  
#### Round Robin (kafka ≤ 2.3)
* As mensagens são distribuidas igualmente nas partições
  * Ex: 6 mensagens para 5 partições –> 4 partições receberam 4 mensagens e 1 partição receberá 2 mensagens
* Isso implica em uma maior quantidade lotes pequenos
* Mais requests e maior latência

#### Sticky Partitioner (kafka ≥ 2.4)
* As mensagens são enviadas em lote para a mesma partição
* "Stick" (permanece) em uma partição até o lote estar completo ou o "linger.ms" ser atingido
* Menos requests e menor latência 

## Partition count
* Cada partição suporta um throughput de poucos MB/s
* Quanto maior o número de partições:
  * Melhor paralelismo, melhor throughput
  * Habilidade de rodar mais grupos de consumers
  * Habilidade de adicionarmais brokers
  * MAIS arquivos abertos no Kafka

* Partitions por topico:
  * Small cluter (< 6 brokers): 3 x # brokers
  * Big cluter (> 12 brokers): 2 x # brokers

## Replication factor
* Pelo menos 2, geralmente 3, no máximo 4
* NUNCA colocar apenas 1 em prod
* Quanto maior o replication-factor (N):
  * Maior a durabilidade do sistema (N-1) brokers podem falhar
  * Maior disponibilidade do sistema (N - min.insync.replicas se acks = all)
  * Mais replicações (maior latência se acks = all)
  * Mais espaço em disco consumido 

## [Topic name convention](https://cnr.sh/essays/how-paint-bike-shed-kafka-topic-naming-conventions)


## Kafka Projects Ideas
* **Plataforma de streaming**
  * Usuário precisa saber em qual parte do vídeo ele parou
  * Envio de mensagens (com as informações da transmissão) em tempo real para um tópico
* **Uber**
  * Usuário precisa saber da localização em tempo real do motorista
  * App deve regular o preço de acordo com a quantidade de motoristas e quantidade de usuários
  * Tópicos que armazenam as posições dos motoristas e usuários
* **Social media**
  * Usuário precisa saber informações sobre likes e comentário em posts em tempo real
  * App deve exibir somente os posts mais engajados no feed principal
  * Tópicos que armazenam as informaçoes de likes, comentários e junta tudo em um tópico de posts
* **Sistema de métricas**
  * Desenvolvedor / PO precisa saber informações em tempo real sobre os microserviços de uma empresa
  * Tópico armazena informações de métricas 
