package th.mfu.service.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import th.mfu.domain.Book;
public interface BookRepository extends CrudRepository<Book, Long> {
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByYear(int year);
}
