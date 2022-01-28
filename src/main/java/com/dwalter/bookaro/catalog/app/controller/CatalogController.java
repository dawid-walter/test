package com.dwalter.bookaro.catalog.app.controller;

import com.dwalter.bookaro.catalog.app.port.CatalogUseCase;
import com.dwalter.bookaro.catalog.domain.model.Book;
import com.dwalter.bookaro.order.web.CreatedURI;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import static com.dwalter.bookaro.catalog.app.port.CatalogUseCase.*;


@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
class CatalogController {
    private final CatalogUseCase catalog;

    @GetMapping
    public List<Book> get() {
        return catalog.getAll();
    }

    @GetMapping("/byParams")
    public List<Book> getByParams(@ParameterObject BookParams params) {
        return findByParams(params);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> get(@PathVariable Long id) {
        return catalog.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void addCover(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        catalog.updateCover(new UpdateBookCoverCommand(id, file.getBytes(), file.getContentType(), file.getOriginalFilename()));
    }

    @DeleteMapping("/{id}/cover")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCover(@PathVariable Long id) {
        catalog.deleteCover(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> add(@Validated(CreateValidation.class) @RequestBody BookRequest createRequest) {
        Book book = catalog.addBook(createRequest.toCreateCommand());
        URI uri = createdBookUri(book);
        return ResponseEntity.created(uri).build();
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void update(@PathVariable Long id, @Validated(UpdateValidation.class) @RequestBody BookRequest updateRequest) {
        UpdateBookResponse response = catalog.updateBook(updateRequest.toUpdateCommand(id));
        if (!response.success()) {
            String message = String.join(", ", response.errors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        catalog.deleteById(id);
    }

    private URI createdBookUri(Book book) {
        return new CreatedURI("/" + book.getId().toString()).uri();    }

    private List<Book> findByParams(BookParams params) {
        if (params.getTitle().isPresent() && params.getAuthor().isPresent()) {
            return catalog.findOneByTitleAndAuthor(params.getTitle().get(), params.getAuthor().get());
        } else if (params.getTitle().isPresent()) {
            return catalog.findByTitle(params.getTitle().get());
        } else if (params.getAuthor().isPresent()) {
            return catalog.findByAuthor(params.getAuthor().get());
        }
        return catalog.getAll();
    }

    @Value
    class BookParams {
        @Parameter(name = "Book title")
        Optional<String> title;
        @Parameter(name = "Book author")
        Optional<String> author;
        @NonFinal
        @Parameter(name = "Default value for note", required = true, example = "false")
        Boolean debug = Boolean.TRUE;
    }

    @Data
    private static class BookRequest {
        @NotBlank(message = "Please provide a title", groups = {CreateValidation.class, UpdateValidation.class})
        private String author;
        @NotBlank(message = "please provide an author", groups = {CreateValidation.class})
        private String title;
        @NotNull
        @PositiveOrZero
        private Integer year;
        @NotNull(groups = {CreateValidation.class})
        @DecimalMin(value = "0.00", groups = {CreateValidation.class, UpdateValidation.class})
        private BigDecimal price;

        CreateBookCommand toCreateCommand() {
            return new CreateBookCommand(author, title, year, price);
        }

        UpdateBookCommand toUpdateCommand(Long id) {
            return new UpdateBookCommand(id, author, title, year, price);
        }
    }

    interface UpdateValidation {
    }

    interface CreateValidation {
    }
}
