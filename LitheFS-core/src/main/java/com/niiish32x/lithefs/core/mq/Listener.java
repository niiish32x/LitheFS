package com.niiish32x.lithefs.core.mq;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Listener {

    @RabbitListener( bindings = @QueueBinding(
            value = @Queue(name = "minio.download.queue" ,durable = "true"),
            exchange = @Exchange(name = "minio.topic", type = ExchangeTypes.TOPIC),
            key = "download.success"
    )

    )
    public void uploadListener(String msg){
        System.out.println(msg);
    }
}
