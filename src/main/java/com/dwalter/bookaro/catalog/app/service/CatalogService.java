package com.dwalter.bookaro.catalog.app.service;

import com.dwalter.bookaro.catalog.app.port.CatalogUseCase;
import com.dwalter.bookaro.catalog.db.BookJpaRepository;
import com.dwalter.bookaro.catalog.domain.model.Book;
import com.dwalter.bookaro.upload.app.port.UploadUseCase;
import com.dwalter.bookaro.upload.domian.Upload;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.dwalter.bookaro.upload.app.port.UploadUseCase.SaveUploadCommand;

@Log4j2
@Service
@RequiredArgsConstructor
class CatalogService implements CatalogUseCase {

    private final BookJpaRepository repository;
    private final UploadUseCase upload;

    @Override
    public List<Book> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Book> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return repository.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(author.toLowerCase()))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public void updateCover(UpdateBookCoverCommand command) {
        repository.findById(command.id())
                .ifPresent(book -> {
                    Upload savedUpload = upload.save(new SaveUploadCommand(command.filename(), command.file(), command.ContentType()));
                    book.setCoverId(savedUpload.getId());
                    repository.save(book);
                });
    }

    @Override
    public void deleteCover(Long id) {
        repository.findById(id).ifPresent(book -> {
            if (book.getCoverId() != null) {
                upload.deleteById(book.getCoverId());
                book.setCoverId(null);
                repository.save(book);
            }
        });
    }

    @Override
    public List<Book> findByTitle(String title) {
        return repository.findAll().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .toList();
    }

    @Override
    public List<Book> findOneByTitleAndAuthor(String title, String author) {
        return repository.findAll()
                .stream()
                .filter(book -> book.getTitle().toLowerCase().startsWith(title.toLowerCase()))
                .filter(book -> book.getAuthor().toLowerCase().startsWith(author.toLowerCase()))
                .toList();
    }

    /*private List<Book> returnBooksFromParams(Object o) {
        return switch (o){
            case BookParams params && params.getAuthor().isPresent() && params.getTitle().isPresent() -> catalog.findOneByTitleAndAuthor(params.getTitle().get(), params.getAuthor().get());
            case BookParams params && params.getTitle().isPresent() -> catalog.findByTitle(params.getTitle().get());
            case BookParams params && params.getAuthor().isPresent() -> catalog.findByAuthor(params.getAuthor().get());
            default -> catalog.getAll();
        };
    }*/

    @Override
    @Transactional
    public UpdateBookResponse updateBook(UpdateBookCommand command) {
        return repository.findById(command.id())
                .map(book -> {
                    command.updateFields(book);
                    return UpdateBookResponse.SUCCESS;
                }).orElseGet(() -> new UpdateBookResponse(false, List.of("Book not found with id: " + command.id())));
    }

    @Override
    public Book addBook(CreateBookCommand command) {
        return repository.save(command.toBook());
    }
}


