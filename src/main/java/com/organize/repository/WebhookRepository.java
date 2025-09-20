package com.organize.repository;

import com.organize.model.User;
import com.organize.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WebhookRepository extends JpaRepository<Webhook, UUID> {
    List<Webhook> findByEventType(String eventType);
    List<Webhook> findByUser(User user);
}