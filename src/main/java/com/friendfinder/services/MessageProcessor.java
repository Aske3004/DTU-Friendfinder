package com.friendfinder.services;

import com.friendfinder.exceptions.InvalidMessageContentException;
import com.friendfinder.model.Message;
import com.friendfinder.strategy.MessageTypeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageProcessor {

    private final Map<String, MessageTypeStrategy> strategies;

    @Autowired
    public MessageProcessor(List<MessageTypeStrategy> strategyList) {
        this.strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        MessageTypeStrategy::getMessageType,
                        Function.identity()
                ));

        // debug step log
        System.out.println("MessageProcessor initialized with strategies: " + strategies.keySet());

    }

    // procces a message using the appropriate strategy
    public void processMessage(Message message) {
        if (message == null) {
            throw new InvalidMessageContentException("Message cannot be null");
        }

        String messageType = message.getMessageType();

        if (messageType == null || messageType.trim().isEmpty()) {
            throw new InvalidMessageContentException("Message type cannot be null or empty");
        }

        MessageTypeStrategy strategy = strategies.get(messageType);

        if (strategy != null) {
            strategy.processMessage(message);
        } else {
            throw new UnsupportedOperationException("No strategy found for message type: " + messageType);
        }
    }


    // check messagetype support
    public boolean isMessageTypeSupported(String messageType) {
        return messageType != null && strategies.containsKey(messageType);
    }

    // get all supported message types
    public List<String> getSupportedMessageTypes() {
        return strategies.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    // get number of registered strategies
    public int getStrategyCount() {
        return strategies.size();
    }

    // process a message with fallback(alternative)
    public void processMessageWithFallback(Message message, MessageTypeStrategy fallbackStrategy) {
        if (message == null) {
            throw new InvalidMessageContentException("Message cannot be null");
        }

        String messageType = message.getMessageType();
        MessageTypeStrategy strategy = strategies.getOrDefault(messageType, fallbackStrategy);

        strategy.processMessage(message);
    }
}
