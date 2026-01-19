package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class AuthorResourceTest {

    @Test
    void testListAuthors() {
        given()
          .when().get("/authors")
          .then()
             .statusCode(200)
             .body("size()", is(3))
             .body("", hasItems("Dan Brown", "Douglas Adams", "Neal Stephenson"));
    }

}
