package org.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class SnsService {

    private final SnsClient snsClient;

    @Value("${aws.sns.topic-arn}")
    private String topicArn;

    public SnsService(SnsClient snsClient) {
        this.snsClient = snsClient;
    }

    public void publicarMensaje(String asunto, String mensaje) {
        PublishRequest request = PublishRequest.builder()
                .topicArn(topicArn)
                .subject(asunto)
                .message(mensaje)
                .build();
        snsClient.publish(request);
    }
}
