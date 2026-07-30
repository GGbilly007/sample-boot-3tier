package th.mfu.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import th.mfu.domain.Book;
import th.mfu.domain.Category;
import th.mfu.service.dto.BookDTO;
import th.mfu.service.dto.mapper.BookMapper;
import th.mfu.service.repository.BookRepository;
import th.mfu.service.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Books.
 *
 * This is still yesterday's controller: ENTITIES in, ENTITIES out. It works -
 * so run it and look at the JSON before changing anything.
 *
 * By the end of the session every method should follow the same three steps:
 *
 *     1. talk to the repository, in ENTITIES
 *     2. use the mapper to convert
 *     3. answer the client, in DTOs
 *
 * An entity should never leave this class, and a DTO should never reach the
 * database.
 */
@RestController
@RequestMapping("/api")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookMapper bookMapper;

    // create new book
    @PostMapping("/books")
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO dto) {
        Book book = new Book();
        bookMapper.updateBookFromDto(dto, book);

        if (dto.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
            if (!category.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            book.setCategory(category.get());
        }

        Book savedBook = bookRepository.save(book);
        BookDTO response = new BookDTO();
        bookMapper.updateBookFromEntity(savedBook, response);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // list all books
    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> listBooks() {
        List<BookDTO> result = new java.util.ArrayList<>();
        for (Book book : bookRepository.findAll()) {
            BookDTO dto = new BookDTO();
            bookMapper.updateBookFromEntity(book, dto);
            result.add(dto);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // search books by title
    @GetMapping("/books/title/{title}")
    public ResponseEntity<Collection> searchBooksByTitle(@PathVariable String title) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCase(title);
        return new ResponseEntity<Collection>(books, HttpStatus.OK);
    }

    // search books by year
    @GetMapping("/books/year/{year}")
    public ResponseEntity<Collection> searchBooksByYear(@PathVariable int year) {
        List<Book> books = bookRepository.findByYear(year);
        return new ResponseEntity<Collection>(books, HttpStatus.OK);
    }

    // get book by id
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDTO> getBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BookDTO dto = new BookDTO();
        bookMapper.updateBookFromEntity(book.get(), dto);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PatchMapping("/books/{id}")
    public ResponseEntity<BookDTO> patchBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        Optional<Book> existing = bookRepository.findById(id);
        if (!existing.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Book book = existing.get();
        bookMapper.updateBookFromDto(dto, book);

        if (dto.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
            if (!category.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            book.setCategory(category.get());
        }

        Book savedBook = bookRepository.save(book);
        BookDTO response = new BookDTO();
        bookMapper.updateBookFromEntity(savedBook, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        Optional<Book> existing = bookRepository.findById(id);
        if (!existing.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Book book = new Book();
        bookMapper.updateBookFromDto(dto, book);

        if (dto.getCategoryId() != null) {
            Optional<Category> category = categoryRepository.findById(dto.getCategoryId());
            if (!category.isPresent()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            book.setCategory(category.get());
        }

        book.setId(id);
        Book savedBook = bookRepository.save(book);
        BookDTO response = new BookDTO();
        bookMapper.updateBookFromEntity(savedBook, response);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // delete book by id
    @DeleteMapping("/books/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        bookRepository.deleteById(id);
        return new ResponseEntity<>("Book with ID: " + id + " deleted.", HttpStatus.OK);
    }
}
