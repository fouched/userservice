package com.example.userservice.kafka.service;

import com.example.userservice.kafka.dto.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // topics
    private static final String USER_REGISTRATION_TOPIC = "user-registration-topic";

    public void publishUserRegistrationEvent(UserRegistrationEvent userRegistrationEvent) {
        log.info("Publishing user registration event to Kafka topic: {}", USER_REGISTRATION_TOPIC);
        // send() is asynchronous - broker failures are only visible on the returned future
        kafkaTemplate.send(USER_REGISTRATION_TOPIC, userRegistrationEvent.getEmail(), userRegistrationEvent)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("User registration event published successfully for email: {}", userRegistrationEvent.getEmail());
                    } else {
                        log.error("Failed to publish user registration event for email: {}. Error: {}", userRegistrationEvent.getEmail(), ex.getMessage());
                    }
                });
    }
}
