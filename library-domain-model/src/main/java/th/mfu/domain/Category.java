package th.mfu.domain;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * A group of books.
 */
// TODO: (step 6) Delete @JsonIgnore from the books field below, and its import.
//
//       @JsonIgnore is here to stop Jackson looping forever:
//       category -> books -> category -> books -> ...
//
//       Once the controller sends a CategoryDTO instead of this entity, the
//       loop cannot happen - a DTO simply has no books field. The need for
//       @JsonIgnore disappears rather than being worked around, and that is one
//       of the better arguments for the pattern.
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "category")
    private List<Book> books;


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<Book> getBooks() {
        return books;
    }


    public void setBooks(List<Book> books) {
        this.books = books;
    }

    

}
