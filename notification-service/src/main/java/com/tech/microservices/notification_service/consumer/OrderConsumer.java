package com.tech.microservices.notification_service.consumer;

import com.tech.microservices.dto.response.OrderResponse;
import com.tech.microservices.events.OrderEvent;
import com.tech.microservices.notification_service.client.OrderClient;
import com.tech.microservices.type.OrderEventAction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderConsumer {
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    OrderClient orderClient;
    @KafkaListener(topics = "orders", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderEvent orderEvent) {
        log.info("Got Message for {}", orderEvent);
        OrderResponse orderResponse = orderClient.getOrderById(orderEvent.getOrderId());
        String subject = "";
        String body = "";
        if(orderEvent.getAction()== OrderEventAction.CREATED){
            subject = getCreateMailSubject(orderResponse);
            body = getCreateMailBody(orderResponse);
        } else if(orderEvent.getAction()== OrderEventAction.CANCELLED){
            subject = getCancelledMailSubject(orderResponse);
            body = getCancelledMailBody(orderResponse);
        } else {
            subject = getCompletedMailSubject(orderResponse);
            body = getCompletedMailBody(orderResponse);
        }
        String finalSubject = subject;
        String finalBody = body;
        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
            messageHelper.setSubject(finalSubject);
            messageHelper.setText(finalBody);
            messageHelper.setFrom("spring-microservices@email.com");
            messageHelper.setTo(orderEvent.getEmail());
        };
        try{
            javaMailSender.send(messagePreparator);
            log.info("Order notification mail send.");
        } catch (MailException e) {
            log.info("Error while sending Order notification mail.\nError {}",e.getMessage());
        }

    }

    private String getCreateMailSubject(OrderResponse orderResponse){
        return String.format("Your Order with orderId %s is placed successfully", orderResponse.getOrderId());
    }

    private String getCreateMailBody(OrderResponse orderResponse){
        return String.format("""
                Hi,
                Your Order details are
                OrderId: %s
                Product: %s
                Quantity: %d
                TotalPrice: %f
                
                Best Regards
                Spring Microservices Shop
                """, orderResponse.getOrderId(),orderResponse.getProduct().getName(),
                orderResponse.getQuantity(),orderResponse.getTotalPrice());
    }

    private String getCancelledMailSubject(OrderResponse orderResponse){
        return String.format("Your Order with orderId %s is Cancelled!!!", orderResponse.getOrderId());
    }

    private String getCancelledMailBody(OrderResponse orderResponse){
        return String.format("""
                Hi,
                Your Order with orderId %s is Cancelled.
                
                Best Regards
                Spring Microservices Shop
                """, orderResponse.getOrderId());
    }

    private String getCompletedMailSubject(OrderResponse orderResponse){
        return String.format("Your Order with orderId %s is Completed.", orderResponse.getOrderId());
    }

    private String getCompletedMailBody(OrderResponse orderResponse){
        return String.format("""
                Hi,
                Your Order with orderId %s is Completed.
                
                Best Regards
                Spring Microservices Shop
                """, orderResponse.getOrderId());
    }
}
