package com.news.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {

}
