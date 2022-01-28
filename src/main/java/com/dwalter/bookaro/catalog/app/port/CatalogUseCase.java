package com.dwalter.bookaro.catalog.app.port;

import com.dwalter.bookaro.catalog.domain.model.Book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static java.util.Collections.emptyList;

public interface CatalogUseCase {
    List<Book> getAll();

    List<Book> findByTitle(String title);

    List<Book> findOneByTitleAndAuthor(String title, String author);

    UpdateBookResponse updateBook(UpdateBookCommand command);

    Book addBook(CreateBookCommand command);

    Optional<Book> findById(Long id);

    List<Book> findByAuthor(String author);

    void deleteById(Long id);

    void updateCover(UpdateBookCoverCommand command);

    void deleteCover(Long id);

    record CreateBookCommand(String author, String title, Integer year, BigDecimal price) {
        public Book toBook() {
            return Book.builder()
                    .id(new Random().nextLong())
                    .author(author)
                    .title(title)
                    .year(year)
                    .price(price)
                    .build();
        }
    }

    record UpdateBookCommand(Long id, String author, String title, Integer year, BigDecimal price) {
        public Book updateFields(Book book) {
            if (author != null) {
                book.setAuthor(author);
            }
            if (title != null) {
                book.setTitle(title);
            }
            if (year != null) {
                book.setYear(year);
            }
            if (price != null) {
                book.setPrice(price);
            }
            return book;
        }
    }

    record UpdateBookResponse(boolean success, List<String> errors) {
        public static UpdateBookResponse SUCCESS = new UpdateBookResponse(true, emptyList());
    }

    record UpdateBookCoverCommand(Long id, byte[] file, String ContentType, String filename) {
    }
}
