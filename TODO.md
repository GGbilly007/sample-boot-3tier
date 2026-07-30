# Day 4 — live coding plan

Yesterday's library app, split into **three tiers** and given a **DTO layer**.

Everything structural is already done. What is left are eight `//TODO:` markers,
in the order we will do them. Search the project for `TODO:` to find them all.

---

## Where the answers are

The finished version is in
[`cnacha-mfu/sample-boot-3tier-solution`](https://github.com/cnacha-mfu/sample-boot-3tier-solution).
Every step below links straight to the file that answers it.

- 📖 **[SAMPLE-3TIER.md](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/SAMPLE-3TIER.md)**
  — the full write-up of everything we are about to do
- 🌿 [browse the whole solution repo](https://github.com/cnacha-mfu/sample-boot-3tier-solution)
- ⏪ [`spring-jpa-day3`](https://github.com/cnacha-mfu/sample-boot-basic/tree/spring-jpa-day3)
  — the same app *before* the split, if you want to show the starting point

> **The solution goes further than we do today.** It also has DTOs, mappers and
> controllers for Category, Member and Transaction. We only need `Book` — the rest
> are there to show that this is a repeatable pattern and not a one-off.
>
> Paths and database now match this project exactly (`/api/books`, `spring_2026`),
> so anything you copy across works unchanged.

--- | --- | --- |
> | paths | `/api/books` | `/books` (no prefix) |
> | database | `spring_2026` / `springuser_2026` | `spring_db` / `spring_user` |
>
> The solution also has DTOs and mappers for Category, Member and Transaction.
> We only need `Book` today — the rest are there if you want to show that it is
> a repeatable pattern and not a one-off.

---

## Before the demo

```bash
# 1. build everything, from THIS folder
mvn install -DskipTests

# 2. start the REST service (terminal 1)
mvn -pl library-web-service spring-boot:run

# 3. start the web page (terminal 2)
mvn -pl library-web-front spring-boot:run
```

Page: <http://localhost:8081/library.html> · Service: <http://localhost:8080/api/books>

> **Always run Maven from the top folder.** The web service needs the domain
> model, and the domain model has to be built first. Run it inside one module
> and you get `Could not find artifact th.mfu:library-3tier:pom`.

Check your work at any time with:

```bash
mvn verify        # runs BookResourceIT against the real service
```

`BookResourceIT` is written for you and should not be edited. It fails at the
start — each step turns more of it green.

---

## What moved, and where

| Module | Layer | Holds | Port |
| --- | --- | --- | --- |
| `library-domain-model` | Data Source | `th.mfu.domain` — the 4 entities, `data.sql` | — |
| `library-web-service` | Domain + Service | controllers, repositories, **DTOs + mappers** | 8080 |
| `library-web-front` | Presentation | `library.html`, jQuery | 8081 |

The dependency arrow goes **one way**:

```
library-web-front  ──HTTP──>  library-web-service  ──depends on──>  library-domain-model
```

Open `library-web-front/pom.xml` and notice there is no JPA and no MySQL in it.
That module *cannot* reach the database — it can only ask the service. The tier
boundary is enforced by the build, not by good intentions.

---

# The eight steps

## Step 1 — `@EntityScan` (5 min)

**File:** `library-web-service/.../service/App.java`

> 💡 **Solution:** [`App.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/App.java)

Start the service *before* changing anything. It will not start:

```
Not a managed type: class th.mfu.domain.Member
```

**Why:** `@SpringBootApplication` only scans below its own package,
`th.mfu.service`. The entities now live in `th.mfu.domain` — in the other
module, outside that tree — so Hibernate never finds them.

**Fix:**

```java
@SpringBootApplication
@EntityScan(basePackages = { "th.mfu.domain" })
public class App { ... }
```

The repositories need no such fix — they are in `th.mfu.service.repository`,
which *is* below `th.mfu.service`.

> This is row 1 of the lab's troubleshooting table. They will meet it again in
> `lab-web-3tier`.

✅ `testServiceIsUp` passes.

---

## Step 2 — write `BookDTO` (15 min)

**File:** `library-web-service/.../service/dto/BookDTO.java`

> 💡 **Solution:** [`BookDTO.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/dto/BookDTO.java)

First, show them what the API sends **today**:

```bash
curl -s http://localhost:8080/api/books/10002
```
```json
{"id":10002,"title":"1984","author":"George Orwell","addedDate":"15-01-2026",
 "category":{"id":10002,"name":"Science Fiction","description":"Speculative fiction..."},
 "publish-year":1949}
```

A whole nested category object — including a description nobody asked for.

**The question from the lecture:** `Book.java` carries `@Entity` *and*
`@JsonProperty`. Should the JSON rules live in the data source layer?

**Answer:** neither the entity nor the service — put them on a **DTO**.

Fill in the fields (all object types, never `int`):

| Java field | JSON name |
| --- | --- |
| `Long id` | `id` |
| `String title` | `title` |
| `String author` | `author` |
| `Integer year` | `publish-year` |
| `LocalDate addedDate` | `added-date` |
| `Long categoryId` | `category_id` |
| `String categoryName` | `category_name` |

> **Ask the class why `Integer` and not `int`.** A primitive can never be null,
> and step 5 works by skipping the fields that *are* null. With `int`, a PATCH
> that omits the year would silently overwrite it with `0`. This is the single
> most common bug in this topic.

---

## Step 3 — write `BookMapper` (15 min)

**File:** `library-web-service/.../service/dto/mapper/BookMapper.java`

> 💡 **Solution:** [`BookMapper.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/dto/mapper/BookMapper.java)

The DTO must be independent of the entity, so something has to copy between
them — the **Assembler**. MapStruct writes it for us.

```java
@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(source = "category.id",   target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    void updateBookFromEntity(Book entity, @MappingTarget BookDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id",       ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateBookFromDto(BookDTO dto, @MappingTarget Book entity);
}
```

**Then build and show them the generated class** — this is the moment the magic
disappears:

```
library-web-service/target/generated-sources/annotations/
    th/mfu/service/dto/mapper/BookMapperImpl.java
```

```java
if ( dto.getTitle()  != null ) entity.setTitle( dto.getTitle() );
if ( dto.getAuthor() != null ) entity.setAuthor( dto.getAuthor() );
if ( dto.getYear()   != null ) entity.setYear( dto.getYear() );
```

Those `!= null` guards **are** `IGNORE`. That is the whole partial-update
feature — no reflection, no runtime magic, generated at compile time.

`componentModel = "spring"` makes it a `@Component`, so it can be `@Autowired`.
Nobody ever writes `new BookMapperImpl()`.

---

## Step 4 — use them in `BookController` (20 min)

**File:** `library-web-service/.../service/BookController.java`

> 💡 **Solution:** [`BookController.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/BookController.java)

Every method becomes the same three steps:

> 1. talk to the repository, in **entities**
> 2. use the mapper to convert
> 3. answer the client, in **DTOs**

- `@Autowired BookMapper bookMapper;`
- `listBooks()` → build a `List<BookDTO>`
- `getBook(id)` → return a `BookDTO`
- `createBook(...)` → take `@RequestBody BookDTO`, look the category up by
  `dto.getCategoryId()`, save, answer **201** with the saved book as a DTO

Show the same curl again:

```json
{"id":10002,"title":"1984","author":"George Orwell","publish-year":1949,
 "added-date":"15-01-2026","category_id":10002,"category_name":"Science Fiction"}
```

Flat. Renamed. No description nobody asked for. And the client still gets the
category name without a second request — *"reduce the number of method calls"*.

✅ `testBookIsFlatOnTheWire`, `testBookHidesTheEntityCollections`,
`testCreateAcceptsTheDtoShape` pass.

---

## Step 5 — PATCH, the partial update (20 min) ⭐

**File:** `library-web-service/.../service/BookController.java`

> 💡 **Solution:** [`BookController.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/BookController.java)
> (the `patchBook` method, next to `updateBook` so you can compare them)

This is the centrepiece. Add:

```java
@PatchMapping("/books/{id}")
public ResponseEntity<BookDTO> patchBook(@PathVariable Long id, @RequestBody BookDTO dto)
```

1. `findById(id)` → **404** if absent
2. take the **existing** entity out of the `Optional`
3. `bookMapper.updateBookFromDto(dto, book)` ← merges onto what is already there
4. save, answer **200** with the book mapped back to a DTO

**Loading first is what makes it a merge.**

### The demo that makes it land

Send the *same one-field body* twice, with two different verbs:

```bash
# PATCH — merge
curl -X PATCH -H "Content-Type: application/json" \
     -d '{"title":"1984 (revised)"}' http://localhost:8080/api/books/10002
```
```json
{"title":"1984 (revised)","author":"George Orwell","publish-year":1949,
 "added-date":"15-01-2026","category_name":"Science Fiction"}
```

```bash
# PUT — replace  (build a `new Book()` instead of loading)
curl -X PUT -H "Content-Type: application/json" \
     -d '{"title":"1984 (replaced)"}' http://localhost:8080/api/books/10002
```
```json
{"title":"1984 (replaced)","author":null,"publish-year":null,
 "added-date":null,"category_name":null}
```

Same body, opposite outcome. **That is why an API needs both verbs.**

> If PATCH wipes the fields too, it is one of two things: `@BeanMapping(...IGNORE)`
> is missing, or a DTO field is `int` instead of `Integer`. Go back to step 2.

✅ `testPatchKeepsTheFieldsYouDidNotSend`, `testPatchUnknownBookIs404` pass —
`mvn verify` is now fully green.

---

## Step 6 — clean the entities (10 min)

**Files:** `library-domain-model/.../domain/Book.java`, `Category.java`,
`library-domain-model/pom.xml`

> 💡 **Solution:** [`Book.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-domain-model/src/main/java/th/mfu/domain/Book.java) · [`Category.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-domain-model/src/main/java/th/mfu/domain/Category.java) · [`pom.xml`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-domain-model/pom.xml) — note the entities there have no Jackson import at all, and the pom has no `jackson-databind`.

Now that the DTO owns the wire format, delete from the entities:

- `@JsonProperty("publish-year")` on `Book.year`
- `@JsonSerialize` / `@JsonDeserialize` on `Book.getAddedDate()`
- `@JsonIgnore` on `Category.books`
- all the `com.fasterxml.jackson` imports

Move `LocalDateSerializer` and `LocalDateDeserializer` out of
`library-domain-model` and into `th.mfu.service.dto`, next to the DTOs.

**Then delete the `jackson-databind` dependency from
`library-domain-model/pom.xml` and rebuild.**

If it still compiles, the data source layer is genuinely free of JSON — and
that is the answer to the lecture's question, proved by the build rather than
asserted on a slide.

> Worth pausing on `@JsonIgnore`. It existed only to stop Jackson looping
> `category → books → category → …`. With a DTO the loop *cannot happen*,
> because `CategoryDTO` has no `books` field. The problem disappears instead of
> being worked around.

---

## Step 7 — CORS (10 min)

**File:** `library-web-service/.../service/WebConfig.java`

> 💡 **Solution:** [`WebConfig.java`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-service/src/main/java/th/mfu/service/WebConfig.java)

Open <http://localhost:8081/library.html> **first** and show the console:

```
Access to XMLHttpRequest at 'http://localhost:8080/api/books' from origin
'http://localhost:8081' has been blocked by CORS policy
```

Then add:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost:8081")
            .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
}
```

Two things to say out loud:

- **Only browsers enforce CORS.** curl and Postman never see this error, so
  *"it works in Postman"* proves nothing.
- Name the one origin you trust, not `*`.

This is the tier boundary made physical — the split into two servers is not a
diagram, it is something the browser can feel.

---

## Step 8 — the web page (20 min)

**File:** `library-web-front/src/main/resources/static/library.html`

> 💡 **Solution:** [`library.html`](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/library-web-front/src/main/resources/static/library.html)

Three `//TODO:` markers, all jQuery `$.ajax`:

- **8a** `loadBooks()` — `GET /api/books`, draw a row per book
- **8b** form submit — `POST /api/books`, then reload
- **8c** PATCH form — `PATCH /api/books/{id}` with only a title, then reload

Use the **DTO's** JSON names, not the entity's field names:

```javascript
book.id, book.title, book.author,
book['publish-year'],   // bracket form: the minus sign breaks dot notation
book['added-date'],
book.category_name
```

> `contentType: 'application/json'` is required on POST/PATCH. Leave it out and
> jQuery sends a form encoding, and Spring answers **415 Unsupported Media Type**.

Finish by pressing **PATCH title only** and watching the table: the title
changes, the author and year stay. Same lesson as step 5, now visible to a
non-programmer.

---

## Wrap-up

```bash
mvn verify     # all of BookResourceIT green
```

What we did, in the vocabulary of the lecture:

| Pattern | Where it ended up |
| --- | --- |
| Layers (Presentation / Domain / Data Source) | the three Maven modules |
| Data Transfer Object | `service/dto/*DTO.java` |
| Assembler / Mapper | `service/dto/mapper/*Mapper.java` (MapStruct) |
| Loose coupling | `library-domain-model` depends on nothing of ours |
| Integration test | `BookResourceIT`, run by `mvn verify` |

Then: **`lab-web-3tier`**. It is the same shape with concerts instead of books.

| In the lab | Here |
| --- | --- |
| `lab-concert-domain-model` | `library-domain-model` |
| `lab-concert-web-service` | `library-web-service` |
| `lab-concert-web-front` | `library-web-front` |
| `Concert` / `Performer` | `Book` / `Category` |
| `ConcertResourceIT` | `BookResourceIT` |

Two things the lab needs that we did **not** use — flag them now:

- `@ManyToOne(cascade = CascadeType.ALL)`, so saving a concert also saves a new
  performer. Ours requires the category to exist already.
- The lab keeps its date serializers **on the entity**. That is deliberate, so
  they meet the problem this lecture is about.

---

## If something breaks

| Message | Cause |
| --- | --- |
| `Could not find artifact th.mfu:library-3tier:pom` | Ran Maven inside one module. Run `mvn install -DskipTests` from the top folder |
| `Not a managed type: class th.mfu.domain.Book` | Step 1 — `@EntityScan` missing |
| `required a bean of type 'BookMapper'` | MapStruct did not run. `mvn clean install`, then check `target/generated-sources` |
| PATCH wipes the other fields | Missing `@BeanMapping(...IGNORE)`, or a DTO field is `int` not `Integer` |
| `blocked by CORS policy` | Step 7 — `WebConfig` not filled in |
| `415 Unsupported Media Type` | Missing `contentType: 'application/json'` in `$.ajax` |
| `Duplicate entry '10001' for key 'category.PRIMARY'` | Stale tables from a killed run. Drop them: `DROP TABLE IF EXISTS transaction, book, category, member, hibernate_sequence;` |
| `Could not figure out if the application has started ... port 9001` | A `spring-boot:run` is still on 8080. `mvn verify` starts its own — stop the other first |
| `Communications link failure` | MySQL is not running |

**Database:** `spring_2026`, user `springuser_2026`. `ddl-auto=create-drop`, so
Hibernate builds the tables and `data.sql` fills them on every start.
