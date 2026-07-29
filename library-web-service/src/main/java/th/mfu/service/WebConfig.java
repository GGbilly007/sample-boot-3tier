package th.mfu.service;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lets the web page on port 8081 call this service on port 8080.
 *
 * Different port means different ORIGIN, and a browser refuses to let a page
 * read a response from another origin unless the server says it is allowed.
 * That rule is CORS.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // TODO: (step 7) Open library.html on port 8081 FIRST and look at the
    //       browser console. You will see:
    //
    //         Access to XMLHttpRequest at 'http://localhost:8080/api/books'
    //         from origin 'http://localhost:8081' has been blocked by CORS policy
    //
    //       Then add the override that fixes it:
    //
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**")
    //                     .allowedOrigins("http://localhost:8081")
    //                     .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    //         }
    //
    //       (import org.springframework.web.servlet.config.annotation.CorsRegistry)
    //
    //       Two things worth remembering:
    //
    //       - Only BROWSERS enforce CORS. curl and Postman never see this
    //         error, so "it works in Postman" proves nothing here.
    //       - Name the one origin you trust, not "*".
    //
    //       This is the tier boundary made visible: the split into two servers
    //       is not a diagram, it is something the browser can feel.
}
