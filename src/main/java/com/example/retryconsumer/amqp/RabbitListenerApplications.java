package com.example.retryconsumer.amqp;

import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RabbitListenerApplications {

    @Autowired
    AmqpTemplate amqpTemplate;

    // exchange to use
    @Value("${app.rabbitMQ.exchange}")
    String exchange;

    //routingkey of the exchange
    @Value("${app.rabbitMQ.outgoing.RoutingKey}")
    String routingKey;

    //use @rabbitlistener to initialize a rabbitmq consumer to consume from queue
    @RabbitListener(queues = "${app.rabbitMQ.retry}", containerFactory = "rabbitListenerContainerFactory", concurrency = "${app.rabbitMQ.retry.concurrency}" )
    public void listenToRetryQueue(String message){

        /**
         * Basically here we get the json string from the queue and convert to json object
         * get the number of sends of the message and the maxsend allowed for the message
         * if numOfSends <= max sends:
         *      increment num of sends by 1
         *      update the value on db(Not done on this code
         *      update the value of numberofsends on the payload
         *      push to outgoing queue which takes care of sending the messages
         * else:
         *      log num of sends exceeded and remove the message from the retry queue
         *
         * @params String
         * @Return void
         */
        try {
            //convert to JSON
            JSONParser parser = new JSONParser();
            JSONObject payload = (JSONObject) parser.parse(message);

            Long currsends = (Long) payload.get("NUMBEROFSENDS");
            Long maxsend = (Long) payload.get("MAXSENDS");

            //if number of sends is equal or less than maxsends
            if(currsends <= maxsend){

                //increment  the number of sends by one
                currsends += 1;

                //update on the payload
                payload.put("NUMBEROFSENDS", currsends);

                //update on outbound table in db
                log.info("payload | " + payload);

                //send to outqueue
                amqpTemplate.convertAndSend(exchange,routingKey , payload.toString());

            }else{
                log.info("Number of sends exceeded .... message will be removed from the queue");
            }

        }catch (Exception e){
            log.error(String.valueOf(e));
        }

    }
}
