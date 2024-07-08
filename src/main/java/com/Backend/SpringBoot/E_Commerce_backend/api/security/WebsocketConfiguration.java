package com.Backend.SpringBoot.E_Commerce_backend.api.security;


import com.Backend.SpringBoot.E_Commerce_backend.model.LocalUser;
import com.Backend.SpringBoot.E_Commerce_backend.services.UserServices;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationEventPublisher;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.authorization.SpringAuthorizationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Map;

@Configuration
@EnableWebSocket
@EnableWebSocketMessageBroker
public class WebsocketConfiguration implements WebSocketMessageBrokerConfigurer {
    private ApplicationContext context;
    private JWTRequestFilter jwtRequestFilter;
    private static final AntPathMatcher MATCHER=new AntPathMatcher();
    private UserServices userServices;


    public WebsocketConfiguration(ApplicationContext context, JWTRequestFilter jwtRequestFilter,UserServices userServices) {
        this.context = context;
        this.jwtRequestFilter = jwtRequestFilter;
        this.userServices=userServices;
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

    private AuthorizationManager<Message<?>> makeMessageAuthorizationManager() {
        MessageMatcherDelegatingAuthorizationManager.Builder messages = new MessageMatcherDelegatingAuthorizationManager.Builder();
        messages.
                simpDestMatchers("topic/user/**").authenticated()
//                .simpTypeMatchers(SimpMessageType.MESSAGE).denyAll()
                .anyMessage().permitAll();
        return messages.build();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        AuthorizationManager<Message<?>> authorizationManager = makeMessageAuthorizationManager();
        AuthorizationChannelInterceptor authInterceptor = new AuthorizationChannelInterceptor(authorizationManager);
        AuthorizationEventPublisher publisher = new SpringAuthorizationEventPublisher(context);
        authInterceptor.setAuthorizationEventPublisher(publisher);
        registration.interceptors(jwtRequestFilter, authInterceptor,new RejectClientMessageOnChannelInterceptor(),new DestinationLevelAuthorizationChanelInterceptor());
    }

    private class RejectClientMessageOnChannelInterceptor implements ChannelInterceptor {
        private String[] paths = new String[]{
                "/topic/user/*/address"
        };

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if (message.getHeaders().get("simpMessageType").equals(SimpMessageType.MESSAGE)) {
                String destinasi = (String) message.getHeaders().get("simpDestination");
                for(String path: paths){
                    if(MATCHER.match(path,destinasi))
                        message=null;
                }

            }
            return message;
        }
    }
    private class DestinationLevelAuthorizationChanelInterceptor implements ChannelInterceptor{

        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            if(message.getHeaders().get("simpMessageType").equals(SimpMessageType.SUBSCRIBE)){
                String destinasi = (String) message.getHeaders().get("simpDestination");
                Map<String,String > params=MATCHER.extractUriTemplateVariables("/topic/user/{userId)}/**",destinasi);
                try{
                    Long userId=Long.valueOf(params.get("userId"));
                    Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
                    if(authentication!=null){
                        LocalUser localUser=(LocalUser) authentication.getPrincipal();
                        if(userServices.userHasPermissionToUser(localUser,userId)){
                            message=null;
                        }
                    }else{
                        message=null;
                    }
                }catch(NumberFormatException ex){
                    message=null;
                }
            }
            return message;
        }
    }

}
