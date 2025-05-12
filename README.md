# Netty TCP Server → Kafka 데이터 전송

Netty 기반 TCP 서버에서 수신한 데이터를 Kafka로 전송하는 시스템입니다.

---

## Kafka 서버 실행 (Docker 기반)

### 1. Docker Compose로 Kafka 환경 구성

다음 `docker-compose.yml` 파일을 작성한 후 Kafka와 Zookeeper를 실행합니다.

```yaml
version: '3'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    container_name: kafka
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
    depends_on:
      - zookeeper
```

### 2. Docker 명령어

```bash
# 컨테이너 실행
docker-compose up -d

# 컨테이너 중지
docker-compose down

# 로그 확인
docker-compose logs -f
```

---

## 🧵 Kafka Topic 생성

### 1. Kafka 컨테이너에 접속해서 Topic 생성

```bash
# Kafka 컨테이너 접속
docker exec -it kafka bash

# Topic 생성
kafka-topics --create \
  --bootstrap-server localhost:9092 \
  --topic severance-comp-data \
  --partitions 1 \
  --replication-factor 1

# 종료
exit
```

### 2. Topic 목록 확인

```bash
docker exec -it kafka kafka-topics \
  --list --bootstrap-server localhost:9092
```

> 💡 외부에서 직접 Topic을 생성할 수도 있습니다:

```bash
kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --topic severance-comp-data \
  --partitions 1 \
  --replication-factor 1
```

---

## 🔁 Netty ↔ Kafka 통신 테스트

> **데이터 흐름 순서**
> 
> TCP Client → Netty Server → Kafka Producer → Kafka Broker → Kafka Consumer

### 1. Kafka가 실행 중인지 확인

(기본 포트: `localhost:9092`)

### 2. Kafka Topic(`severance-comp-data`)이 생성되어 있어야 합니다

### 3. Netty 서버 실행 후 TCP 클라이언트로 메시지 전송

```bash
echo "hello kafka!" | nc localhost 9999
```

### 4. Kafka Consumer로 메시지 수신 확인

```bash
docker exec -it kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic severance-comp-data \
  --from-beginning
```

---

## ✅ 참고 사항

* Netty 서버 포트: `9999`
* Kafka Broker: `localhost:9092`
* Kafka Topic 이름: `severance-comp-data`
