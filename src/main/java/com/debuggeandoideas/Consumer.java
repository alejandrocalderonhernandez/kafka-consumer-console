package com.debuggeandoideas;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


public class Consumer {

    private static Consumer consumer;
    private  KafkaConsumer<String, String> kafkaConsumer;

    private Consumer() {
        try {
            var configs = new Properties();
            configs.load(new FileReader("src/main/resources/consumer.properties"));
            kafkaConsumer = new KafkaConsumer(configs);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void start() {
        while (true) {
            try {
                kafkaConsumer.subscribe(List.of(TOPIC));
                    ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofSeconds(20));
                    records.forEach(r -> {
                        var msg = String.format(
                                "offset %s, partition %s, key %s, value %s", r.offset(), r.partition(), r.key(), r.value());
                        log.info(msg);
                    });
            } catch (KafkaException e) {
                kafkaConsumer.close();
            }
        }
    }

    public static Consumer getInstance() {
        return (Objects.nonNull(consumer)) ? consumer : new Consumer();
    }

    private static final String TOPIC = "debuggeando-ideas";
    private static final Logger log = LogManager.getLogger(Consumer.class);

}
