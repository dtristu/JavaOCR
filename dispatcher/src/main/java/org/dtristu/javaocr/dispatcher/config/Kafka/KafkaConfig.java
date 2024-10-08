package org.dtristu.javaocr.dispatcher.config.Kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.topic-out-converter}")
    private String topicOutConverter;
    @Value(value = "${spring.kafka.topic-out-user}")
    private String topicOutUser;

    @Bean
    public KafkaAdmin admin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,bootstrapAddress);
        return new KafkaAdmin(configs);
    }
    @Bean
    public NewTopic topicOutConverter() {
        return new NewTopic(topicOutConverter, 1, (short) 1);
    }
    public String getTopicOutConverter() {
        return topicOutConverter;
    }

    @Bean
    public NewTopic topicOutUser() {
        return new NewTopic(topicOutUser, 1, (short) 1);
    }
    public String getTopicOutUser() {
        return topicOutUser;
    }

}
