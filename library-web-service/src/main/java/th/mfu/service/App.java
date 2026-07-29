package th.mfu.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The REST service - the middle tier. Port 8080.
 */
// TODO: (step 1) This app will NOT start yet. Run it and read the error first:
//
//         Not a managed type: class th.mfu.domain.Book
//
//       @SpringBootApplication only scans below ITS OWN package, th.mfu.service.
//       The entities now live in th.mfu.domain, in the other module - outside
//       that tree - so Hibernate never finds them.
//
//       Fix it by naming the package explicitly:
//
//         @EntityScan(basePackages = { "th.mfu.domain" })
//
//       (import org.springframework.boot.autoconfigure.domain.EntityScan)
//
//       The repositories need no such fix: they sit in
//       th.mfu.service.repository, which IS below th.mfu.service.
//
//       This is the first row of the lab's troubleshooting table - you will
//       meet it again in lab-web-3tier.
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
