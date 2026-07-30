# Postman collection

`library-3tier.postman_collection.json` holds every endpoint of the REST service,
with a description on each request explaining what it proves.

Postman is a program that sends HTTP requests for you. It is the same thing `curl`
does, with a window instead of a command line.

## Import it

1. Open Postman.
2. Click **Import** (top left).
3. Drag `library-3tier.postman_collection.json` in, or click **files** and pick it.
4. The collection **Library 3-tier (Day 4)** appears in the left sidebar.

No account and no login are needed. Everything runs on your own machine.

## Before you send anything

The service must be running. From the **top folder** of the project:

```bash
mvn install -DskipTests
mvn -pl library-web-service spring-boot:run
```

Then open the folder **0. Is the service up?** and send `GET /hi`.
You should get `Hi There!`. If you do not, no other request will work either —
fix that first.

## The four folders

| Folder | When to run it |
| --- | --- |
| **0. Is the service up?** | Any time. Checks the service is alive. |
| **1. Books — before the DTO** | Now, before you change any code. |
| **2. Books — after the DTO** | After steps 2, 3, 4 and 5 of [TODO.md](../TODO.md). |
| **3. Transactions** | Any time. This controller has no DTO. |

Folders 1 and 2 hit the **same URLs on purpose**. The service changes shape while
you work, so the same endpoint needs a different request body and answers with a
different response. Run folder 1 first and keep the response open. Comparing the
two is the lesson.

## Variables

Click the collection name, then the **Variables** tab, to see them.

| Variable | Value | What it is for |
| --- | --- | --- |
| `baseUrl` | `http://localhost:8080/api` | Change the port in one place, not in 18 requests. |
| `bookId` | `10002` | "1984". `data.sql` always creates it. |
| `categoryId` | `10002` | "Science Fiction". |
| `newBookId` | *(empty)* | Fills itself in when you create a book, so DELETE has a target. |

`{{baseUrl}}` in a URL means "put the variable's value here".

## Two things worth knowing

**Postman cannot see a CORS error.** Only a browser enforces CORS. Step 7 will look
perfectly fine from Postman even when the web page is broken. *"It works in Postman"*
proves nothing about `library.html` — open the page and read the browser console.

**Dates are `dd-MM-yyyy`.** `30-07-2026`, not `2026-07-30`. That is what
`LocalDateDeserializer` accepts, and the wrong order gives you a 400.

## Nothing you do here is permanent

`spring.jpa.hibernate.ddl-auto=create-drop` means Hibernate drops the tables and
re-runs `data.sql` every time the service starts. Delete a book, break a title,
send fifty transactions — restart the service and it is all back.

So experiment. Change a body and see what the service says. A 400 you caused on
purpose teaches more than a 200 you copied.
