package org.acme;

import java.util.List;
import java.util.stream.Collectors;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.Book;

@Path("authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @GET
    public List<String> listAuthors() {
        return Book.<Book>listAll().stream()
                .flatMap(book -> book.authors.stream())
                .map(author -> author.name)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

}
