package com.news.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.news.models.NewsPaper;

public interface NewsPaperRepository extends JpaRepository<NewsPaper, Long> {

	Optional<NewsPaper> findByName(String name);

	Optional<NewsPaper> findByNameIgnoreCase(String name);

}
