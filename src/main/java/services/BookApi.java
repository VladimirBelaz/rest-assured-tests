package services;

import dto.BookDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class BookApi {
    private final RequestSpecification spec;

    public BookApi() {
        spec = given()
                .baseUri("https://fakerestapi.azurewebsites.net")
                .basePath("/api/v1")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    public ValidatableResponse getBookById(Long id) {
        return given(spec)
                .pathParam("id", id)
                .when()
                .get("/Books/{id}")
                .then()
                .log().all();
    }

    public ValidatableResponse createBook(BookDTO book) {
        return given(spec)
                .body(book)
                .when()
                .post("/Books")
                .then()
                .log().all();
    }
}