package com.friendfinder.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint som frontend forbinder til (ws://.../ws-chat)
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")   // evt. stram til din origin
                .withSockJS();                   // hvis I bruger SockJS i frontenden
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Her sender serveren beskeder ud til klienterne
        registry.enableSimpleBroker("/topic", "/queue");

        // Alt som klient sender til server går til /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }
}

