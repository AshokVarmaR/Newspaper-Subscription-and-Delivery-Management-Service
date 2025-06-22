package com.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Card;

public interface CardRepository extends JpaRepository<Card, Long> {

}
