package com.halmber.springordersapi;

import com.halmber.springordersapi.model.dto.messaging.EmailMessageDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.utils.ContainerTestUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class KafkaTestHelper {

    public static KafkaMessageListenerContainer<String, EmailMessageDto> createEmailConsumer(
            String groupId,
            BlockingQueue<ConsumerRecord<String, EmailMessageDto>> records
    ) {
        Map<String, Object> consumerProps = new HashMap<>();
        consumerProps.put("bootstrap.servers", "localhost:9092");
        consumerProps.put("group.id", groupId);
        consumerProps.put("auto.offset.reset", "earliest");
        consumerProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.put("value.deserializer", "org.springframework.kafka.support.serializer.JsonDeserializer");
        consumerProps.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EmailMessageDto.class.getName());

        DefaultKafkaConsumerFactory<String, EmailMessageDto> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProps);

        ContainerProperties containerProperties = new ContainerProperties("emailSend");
        KafkaMessageListenerContainer<String, EmailMessageDto> container =
                new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        container.setupMessageListener((MessageListener<String, EmailMessageDto>) records::add);
        container.start();

        ContainerTestUtils.waitForAssignment(container, 2);

        return container;
    }

    public static BlockingQueue<ConsumerRecord<String, EmailMessageDto>> createRecordsQueue() {
        return new LinkedBlockingQueue<>();
    }
}