package com.organize.service;

import com.organize.model.Webhook;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void triggerWebhooks(List<Webhook> webhooks, Map<String, Object> payload) {
        for (Webhook webhook : webhooks) {
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
                restTemplate.postForEntity(webhook.getUrl(), request, String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
