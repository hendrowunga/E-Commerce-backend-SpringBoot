package com.Backend.SpringBoot.E_Commerce_backend.api.security;


import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.function.Supplier;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private ApplicationContext context;
    private JWTRequestFilter jwtRequestFilter;

    public WebsocketConfiguration(ApplicationContext context,JWTRequestFilter jwtRequestFilter) {
        this.context = context;
        this.jwtRequestFilter=jwtRequestFilter;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/websocket").setAllowedOriginPatterns("**").withSockJS();

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }
    private AuthorizationManager<Message<?>>  makeMessageAuthorizationManager(){
        MessageMatcherDelegatingAuthorizationManager.Builder messages=new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages.
                simpDestMatchers("topic/user/**").authenticated()
//                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .anyMessage().permitAll();
        return messages.build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> authorizationManager=makeMessageAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor=new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher publisher=new SpringAuthorizationEventPublisher(context) ;
        authInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(jwtRequestFilter,authInterceptor);
    }


}
