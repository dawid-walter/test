package com.dwalter.bookaro.catalog.db;

import com.dwalter.bookaro.catalog.domain.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, Long> {
}
