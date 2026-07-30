# sample-boot-3tier

Day 4 starting point for **Backend Programming** (MFU) — the library app from the
JPA session, split into **three tiers** and waiting for a **DTO layer**.

This is a *skeleton*. Eight `//TODO:` markers are left for the live session.

> **Start here → [TODO.md](TODO.md)** — the step-by-step plan, in order.

Continues from [`sample-boot-basic`](https://github.com/cnacha-mfu/sample-boot-basic)
and leads into the `lab-web-3tier` lab.

**Stuck?** The finished version is in
[`sample-boot-3tier-solution`](https://github.com/cnacha-mfu/sample-boot-3tier-solution),
written up in
[SAMPLE-3TIER.md](https://github.com/cnacha-mfu/sample-boot-3tier-solution/blob/main/SAMPLE-3TIER.md).
Every step in [TODO.md](TODO.md) links to the exact file that answers it.

---

## The three tiers

| Module | Layer | Holds | Port |
| --- | --- | --- | --- |
| `library-domain-model` | Data Source | the 4 JPA entities, `data.sql` | — |
| `library-web-service` | Domain + Service | controllers, repositories, DTOs, mappers | 8080 |
| `library-web-front` | Presentation | `library.html` + jQuery | 8081 |

```
library-web-front  ──HTTP──>  library-web-service  ──depends on──>  library-domain-model
```

`library-web-front` has no JPA and no MySQL in its `pom.xml`, so it *cannot*
reach the database — it can only ask the service. The tier boundary is enforced
by the build.

## What today adds

- **Data Transfer Object** — the entity stops carrying Jackson annotations
- **MapStruct** as the Assembler, including **partial update** (`PATCH`)
- **CORS**, because the page and the service are on different ports
- **Integration tests** with the RESTEasy JAX-RS client, run by `mvn verify`

## Running it

Needs JDK 11+, Maven, and MySQL with:

```sql
CREATE DATABASE spring_2026;
CREATE USER 'springuser_2026'@'localhost' IDENTIFIED BY '1234abcd';
GRANT ALL PRIVILEGES ON spring_2026.* TO 'springuser_2026'@'localhost';
FLUSH PRIVILEGES;
```

```bash
mvn install -DskipTests                      # from THIS folder, always

mvn -pl library-web-service spring-boot:run  # terminal 1 → port 8080
mvn -pl library-web-front   spring-boot:run  # terminal 2 → port 8081
```

Then open <http://localhost:8081/library.html>.

> The service will **not** start until step 1 of [TODO.md](TODO.md) is done.
> That is deliberate — the error it prints is the lesson.

## Trying the API by hand

Import [postman/library-3tier.postman_collection.json](postman/library-3tier.postman_collection.json)
into Postman. Every endpoint is there, with the request bodies for before *and*
after the DTO, so you can watch the same URL change shape as you work.
See [postman/README.md](postman/README.md).

## Checking your work

```bash
mvn verify
```

Runs `BookResourceIT` against the real service over HTTP. It fails at the start;
each step turns more of it green. Do not edit the test — make it pass.

---

The credentials above are local-development only and are meant to be shared with
the class.
