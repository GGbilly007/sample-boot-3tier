package th.mfu.domain;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A book in the library.
 *
 * THE QUESTION FROM THE LECTURE: this one class is doing two jobs. @Entity and
 * @Id are for the database; @JsonProperty and @JsonSerialize are for the wire.
 * Should the JSON rules live here, in the data source layer?
 */
// TODO: (step 6, do this LAST - after the DTO and the mapper both work)
//
//       Delete every Jackson annotation from this class:
//         - @JsonProperty("publish-year")   on the year field
//         - @JsonSerialize / @JsonDeserialize on getAddedDate()
//         - the three com.fasterxml.jackson imports
//
//       Move LocalDateSerializer and LocalDateDeserializer out of this module
//       and into th.mfu.service.dto, next to the DTOs where they belong.
//
//       Then delete the jackson-databind dependency from
//       library-domain-model/pom.xml. If this module still compiles, the
//       entities are genuinely free of JSON - and that is the answer to the
//       question above.
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String title;
    private String author;
    private int year;
    private LocalDate addedDate;

    @ManyToOne
    private Category category;

    public Book() {
    }

    public Book(long id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public LocalDate getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(LocalDate addedDate) {
        this.addedDate = addedDate;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    
    
}
