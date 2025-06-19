package com.example.springBoot2.Repositories;

import com.example.springBoot2.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {
}
