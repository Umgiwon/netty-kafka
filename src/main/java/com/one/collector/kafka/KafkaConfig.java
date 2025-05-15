package com.one.collector.kafka;

import lombok.Getter;

@Getter
public class KafkaConfig {
    public static final String TOPIC = "severance-data";
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
}
