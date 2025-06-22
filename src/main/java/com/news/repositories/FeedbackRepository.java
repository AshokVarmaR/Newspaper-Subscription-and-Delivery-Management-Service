package com.news.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	List<Feedback> findByCustomer_Id(long id);

}
