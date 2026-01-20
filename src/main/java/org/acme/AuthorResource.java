package org.acme;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.model.Author;

@Path("authors")
@Produces(MediaType.APPLICATION_JSON)
public class AuthorResource {

    @PersistenceContext
    EntityManager entityManager;

    @GET
    public List<Author> listAuthors() {
        return entityManager.createQuery(
            "SELECT DISTINCT a FROM Book b JOIN b.authors a ORDER BY a.name",
            Author.class
        ).getResultList();
    }

}
