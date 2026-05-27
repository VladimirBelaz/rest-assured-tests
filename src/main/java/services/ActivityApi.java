package services;

import dto.ActivityDTO;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import static io.restassured.RestAssured.given;

public class ActivityApi {
    private final RequestSpecification spec;

    public ActivityApi() {
        spec = given()
                .baseUri("https://fakerestapi.azurewebsites.net")
                .basePath("/api/v1")
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().all();
    }

    public ValidatableResponse getActivities() {
        return given(spec)
                .when()
                .get("/Activities")
                .then()
                .log().all();
    }

    public ValidatableResponse createActivity(ActivityDTO activity) {
        return given(spec)
                .body(activity)
                .when()
                .post("/Activities")
                .then()
                .log().all();
    }
}