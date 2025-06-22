package com.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}
