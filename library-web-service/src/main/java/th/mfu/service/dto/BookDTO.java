package th.mfu.service.dto;

/**
 * What a book looks like ON THE WIRE.
 *
 * Right now this class is empty and nothing uses it - the controller still
 * sends the Book entity straight out. Compare what that produces today:
 *
 *   {"id":10001,"title":"1984","author":"George Orwell","addedDate":"15-01-2026",
 *    "category":{"id":10001,"name":"Fiction","description":"..."},"publish-year":1949}
 *
 * ...with what we want by the end of the session:
 *
 *   {"id":10001,"title":"1984","author":"George Orwell","publish-year":1949,
 *    "added-date":"15-01-2026","category_id":10001,"category_name":"Fiction"}
 *
 * Flat instead of nested, and the client gets the category name without a
 * second request.
 */
public class BookDTO {

    // TODO: (step 2) Give this class the fields the wire format needs:
    //
    //   Long    id
    //   String  title
    //   String  author
    //   Integer year          -> @JsonProperty("publish-year")
    //   LocalDate addedDate   -> @JsonProperty("added-date")
    //                            plus @JsonSerialize / @JsonDeserialize with
    //                            LocalDateSerializer / LocalDateDeserializer
    //                            on the GETTER (move them here from the entity)
    //   Long    categoryId    -> @JsonProperty("category_id")
    //   String  categoryName  -> @JsonProperty("category_name")
    //
    // Then generate the getters and setters.
    //
    // WHY Integer AND NOT int: a primitive can never be null, and the partial
    // update in step 5 works by skipping the fields that ARE null. With `int`,
    // a PATCH that does not mention the year would silently overwrite it with 0.
    //
    // Notice there is no "transactions" field and no "books" field anywhere in
    // these DTOs. That is the point: the DTO decides what travels, so the
    // @JsonIgnore currently sitting on Category.books becomes unnecessary.
}
