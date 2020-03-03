package com.example.retryconsumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //declare retry queues
    // make the queue durable to persist
    @Bean
    Queue retryQueue(@Value("${app.rabbitMQ.retry}") String retryQueue){
        return new Queue(retryQueue,true);
    }

    //declare outgoing queue
    // make the queue durable to persist
    @Bean
    Queue outgoingQueue(@Value("${app.rabbitMQ.out}") String outQueue){
        return new Queue(outQueue, true);
    }

    //exchange
    // we will be using a direct queue so we declare direct queue
    @Bean
    DirectExchange directExchange(@Value("${app.rabbitMQ.exchange}") String exchange){
        return new DirectExchange(exchange);
    }

    //Binding retryQueue to the direct exchange using a routing key
    @Bean
    Binding retryBinding(Queue retryQueue, DirectExchange directExchange, @Value("${app.rabbitMQ.retry.RoutingKey}") String routeKey){
        return BindingBuilder.bind(retryQueue).to(directExchange).with(routeKey);
    }

    //Binding outgoingQueue to exchange
    @Bean
    Binding outgoingBinding(Queue outgoingQueue, DirectExchange directExchange, @Value("${app.rabbitMQ.outgoing.RoutingKey}") String routeKey){
        return BindingBuilder.bind(outgoingQueue).to(directExchange).with(routeKey);
    }

    //factory listener so as to set prefetch for the consumers
    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer>
            rabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory, @Value("${app.rabbitMQ.Prefetch}") String prefetch ){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(Integer.valueOf(prefetch));
        return factory;
    }

}
