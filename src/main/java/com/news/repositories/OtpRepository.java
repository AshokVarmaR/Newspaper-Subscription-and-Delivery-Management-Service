package com.news.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Otp;

public interface OtpRepository extends JpaRepository<Otp, Long> {

	Optional<Otp> findByUser_Id(long id);

	Optional<Otp> findByUser_Email(String email);

	

}
