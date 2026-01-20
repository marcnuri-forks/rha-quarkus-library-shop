package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

@QuarkusTest
class AuthorResourceTest {

    @Test
    void testListAuthorsEndpoint() {
        given()
            .when().get("/authors")
            .then()
                .statusCode(200)
                .body("$.size()", is(3))
                .body("name", hasItem("Douglas Adams"))
                .body("name", hasItem("Neal Stephenson"))
                .body("name", hasItem("Dan Brown"));
    }

}
