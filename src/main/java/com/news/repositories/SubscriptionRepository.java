package com.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

}
