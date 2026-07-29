package th.mfu.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import th.mfu.domain.Book;
import th.mfu.domain.Category;
import th.mfu.service.repository.BookRepository;
import th.mfu.service.repository.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    // TODO: (step 4) Add the mapper, once step 3 is written:
    //
    //   @Autowired
    //   private BookMapper bookMapper;
    //
    // You never write `new BookMapperImpl()`. MapStruct generated that class at
    // compile time and marked it @Component, so Spring hands it to you.

    // create new book
    @PostMapping("/books")
    public ResponseEntity<String> createBook(@RequestBody Book book) {
        // TODO: (step 4) Take a BookDTO instead of a Book:
        //
        //   public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO dto)
        //
        // Then: make a new Book, let the mapper fill it from the DTO, look up
        // the category by dto.getCategoryId(), save, and answer 201 with the
        // saved book mapped BACK to a DTO.
        //
        // Notice what this changes for the client - the request body becomes
        //   {"title":"...","publish-year":1949,"category_id":10001}
        // instead of the nested {"category":{"id":10001}} it needs today.

        // check and attach category if provided
        if (book.getCategory() != null) {
            Optional<Category> category = categoryRepository.findById(book.getCategory().getId());
            if (!category.isPresent()) {
                return new ResponseEntity<>("Category not found with ID: " + book.getCategory().getId(),
                        HttpStatus.BAD_REQUEST);
            }
            book.setCategory(category.get());
        }

        Book savedBook = bookRepository.save(book);

        return new ResponseEntity<String>("Book created with ID: " + savedBook.getId(), HttpStatus.CREATED);
    }

    // list all books
    @GetMapping("/books")
    public ResponseEntity<Collection> listBooks() {
        // TODO: (step 4) Return List<BookDTO>.
        //
        // Loop over the books, and for each one create a BookDTO and call
        // bookMapper.updateBookFromEntity(book, dto).
        //
        // Compare the JSON before and after. Today every book carries a nested
        // "category" object; afterwards it should carry a flat category_id and
        // category_name - and nothing should come back the other way.
        List<Book> books = (List<Book>) bookRepository.findAll();
        return new ResponseEntity<Collection>(books, HttpStatus.OK);
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
    public ResponseEntity<Book> getBook(@PathVariable Long id) {
        // TODO: (step 4) Return a BookDTO instead of the Book entity.
        Optional<Book> book = bookRepository.findById(id);
        if (!book.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Book foundBook = book.get();
        return new ResponseEntity<>(foundBook, HttpStatus.OK);
    }

    // TODO: (step 5) THE PARTIAL UPDATE - the point of today's MapStruct lesson.
    //
    //   @PatchMapping("/books/{id}")
    //   public ResponseEntity<BookDTO> patchBook(@PathVariable Long id,
    //                                            @RequestBody BookDTO dto)
    //
    // The recipe, and the order matters:
    //
    //   1. findById(id); answer 404 if it is not there
    //   2. take the EXISTING entity out of the Optional
    //   3. bookMapper.updateBookFromDto(dto, book)   <- merges onto what is there
    //   4. save, and answer 200 with the book mapped back to a DTO
    //
    // Loading first is what makes it a MERGE. Send {"title":"1984 (revised)"}
    // and the author, the year and the date must all survive.
    //
    // Then try the same one-field body as a PUT that builds a `new Book()`
    // instead, and watch those fields go null. PATCH merges, PUT replaces -
    // that difference is the whole reason an API needs both verbs.

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
