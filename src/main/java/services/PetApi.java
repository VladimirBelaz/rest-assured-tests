package services;

import dto.PetDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class PetApi {
    private final RequestSpecification spec;
    private static final String API_KEY = "special-key"; // или любой ключ

    public PetApi() {
        spec = given()
                .baseUri("https://petstore.swagger.io")
                .basePath("/v2")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header("api_key", API_KEY)  // ← добавляем API key
                .log().all();
    }

    public ValidatableResponse getPetById(Long petId) {
        return given(spec)
                .pathParam("petId", petId)
                .when()
                .get("/pet/{petId}")
                .then()
                .log().all();
    }

    public ValidatableResponse createPet(PetDTO pet) {
        return given(spec)
                .body(pet)
                .when()
                .post("/pet")
                .then()
                .log().all();
    }

    public ValidatableResponse deletePet(Long petId) {
        return given(spec)
                .pathParam("petId", petId)
                .when()
                .delete("/pet/{petId}")
                .then()
                .log().all();
    }
}