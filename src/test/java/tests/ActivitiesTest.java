package tests;

import dto.ActivityDTO;
import extensions.ApiServiceExtension;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.ActivityApi;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@ExtendWith(ApiServiceExtension.class)
@DisplayName("Тесты для Activities")
public class ActivitiesTest {

    @Test
    @DisplayName("GET /Activities – получение списка активностей")
    void getActivitiesShouldReturnNonEmptyList(ActivityApi activityApi) {
        // Проверяем: статус 200, тело — массив, не пустой, каждый элемент имеет id
        activityApi.getActivities()
                .statusCode(HttpStatus.SC_OK)
                .body("$", not(emptyArray()))
                .body("id", everyItem(notNullValue()));
    }

    @Test
    @DisplayName("GET /Activities – проверка JSON Schema")
    void getActivitiesShouldMatchSchema(ActivityApi activityApi) {
        // Проверяем структуру ответа с помощью JSON Schema
        activityApi.getActivities()
                .statusCode(HttpStatus.SC_OK)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/activity-schema.json"));
    }

    static Stream<Arguments> activityDataProvider() {
        return Stream.of(
                Arguments.of(ActivityDTO.builder()
                        .title("Купить молоко")
                        .dueDate(LocalDateTime.now().plusDays(1))
                        .completed(false)
                        .build(), HttpStatus.SC_OK),
                Arguments.of(ActivityDTO.builder()
                        .title("Сделать домашку")
                        .dueDate(LocalDateTime.now().plusHours(2))
                        .completed(true)
                        .build(), HttpStatus.SC_OK)
        );
    }

    @ParameterizedTest(name = "Создание активности: {0}")
    @MethodSource("activityDataProvider")
    @DisplayName("POST /Activities – создание активности с разными данными")
    void createActivityShouldReturnSavedData(ActivityDTO activityToCreate, Integer expectedStatus,
                                             ActivityApi activityApi) {
        // Проверяем, что созданная активность возвращается с теми же полями, что отправляли
        activityApi.createActivity(activityToCreate)
                .statusCode(expectedStatus)
                .body("title", equalTo(activityToCreate.getTitle()))
                .body("completed", equalTo(activityToCreate.getCompleted()))
                .body("dueDate", notNullValue());
    }

    @Test
    @DisplayName("POST /Activities – создание активности и извлечение DTO для проверки")
    void createActivityAndExtractDto(ActivityApi activityApi) {
        ActivityDTO newActivity = ActivityDTO.builder()
                .title("Позвонить маме")
                .dueDate(LocalDateTime.now().plusDays(1))
                .completed(false)
                .build();

        ActivityDTO created = activityApi.createActivity(newActivity)
                .statusCode(HttpStatus.SC_OK)
                .extract().as(ActivityDTO.class);

        // Дополнительная проверка через JUnit Assertions (не только Hamcrest)
        org.junit.jupiter.api.Assertions.assertAll(
                () -> org.junit.jupiter.api.Assertions.assertEquals(newActivity.getTitle(), created.getTitle(), "Заголовок не совпадает"),
                () -> org.junit.jupiter.api.Assertions.assertEquals(newActivity.getCompleted(), created.getCompleted(), "Статус completed не совпадает")
        );
    }
}