package com.one.collector.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

/**
 * Kafka 프로듀서를 사용해 메시지를 전송하는 클래스
 */
@Slf4j
public class KafkaSender {
    private final KafkaProducer<String, String> producer;
    private final String topic = "severance-comp-data"; // 전송할 kafka 토픽 이름

    public KafkaSender() {

        // kafka 설정 정보 구성
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka 서버 주소
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // key 직렬화 방식
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName()); // value 직렬화 방식

        // kafkaProducer 생성
        this.producer = new KafkaProducer<>(props);
    }

    /**
     * kafka에 메시지를 전송하는 메소드
     * @param message 메시지
     */
    public void send(String message) {
        ProducerRecord<String, String> record = new ProducerRecord<>(topic, message);
        producer.send(record, (metadata, exception) -> {
            if(exception != null) {
                log.error("Kafka 전송 실패: {}", exception.getMessage());
            } else {
                log.info("Kafka 전송 성공 (offset={}): {}", metadata.offset(), message);
            }
        });
    }

    public void close() {
        producer.close();
    }
}
