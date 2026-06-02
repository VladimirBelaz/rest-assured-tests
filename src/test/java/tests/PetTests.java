// tests/PetTests.java
package tests;

import dto.CategoryDTO;
import dto.PetDTO;
import dto.TagDTO;
import extensions.ApiServiceExtension;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import services.PetApi;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;

@ExtendWith(ApiServiceExtension.class)
@DisplayName("Тесты для Pet API (Swagger Petstore)")
public class PetTests {

    private Long createdPetId; // для очистки после тестов

    // Очистка: удаляем созданного питомца после каждого теста
    @AfterEach
    void cleanup(PetApi petApi) {
        if (createdPetId != null) {
            petApi.deletePet(createdPetId);
            createdPetId = null;
        }
    }

    // ==================== GET /pet/{petId} ====================

    @Test
    @DisplayName("GET /pet/{petId} – получение существующего питомца по ID")
    void getPetByIdShouldReturnExistingPet(PetApi petApi) {
        Long existingPetId = 1L; // в Petstore есть питомец с ID=1

        petApi.getPetById(existingPetId)
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(1))
                .body("name", not(blankString()));
    }

    @Test
    @DisplayName("GET /pet/{petId} – запрос несуществующего питомца возвращает 404")
    void getPetByIdShouldReturn404ForNonExistingPet(PetApi petApi) {
        Long nonExistingPetId = 999999L;

        petApi.getPetById(nonExistingPetId)
                .statusCode(HttpStatus.SC_NOT_FOUND)
                .body("message", containsString("Pet not found"));
    }

    // ==================== POST /pet ====================

    @Test
    @DisplayName("POST /pet – создание нового питомца с корректными данными")
    void createPetShouldAddNewPet(PetApi petApi) {
        PetDTO newPet = PetDTO.builder()
                .id(0L) // 0 означает, что ID сгенерирует сервер
                .name("Rex")
                .photoUrls(Collections.singletonList("https://example.com/rex.jpg"))
                .status("available")
                .build();

        PetDTO created = petApi.createPet(newPet)
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo("Rex"))
                .body("status", equalTo("available"))
                .extract().as(PetDTO.class);

        createdPetId = created.getId();

        org.junit.jupiter.api.Assertions.assertNotNull(created.getId(), "ID должен быть сгенерирован");
        org.junit.jupiter.api.Assertions.assertEquals("Rex", created.getName());
    }

    @Test
    @DisplayName("POST /pet – создание питомца со всеми полями (категория, теги)")
    void createPetWithAllFieldsShouldSucceed(PetApi petApi) {
        CategoryDTO category = new CategoryDTO(1L, "dogs");
        TagDTO tag = new TagDTO(1L, "friendly");

        PetDTO newPet = PetDTO.builder()
                .id(0L)
                .name("Buddy")
                .category(category)
                .photoUrls(Collections.singletonList("https://example.com/buddy.jpg"))
                .tags(Collections.singletonList(tag))
                .status("pending")
                .build();

        PetDTO created = petApi.createPet(newPet)
                .statusCode(HttpStatus.SC_OK)
                .body("name", equalTo("Buddy"))
                .body("category.name", equalTo("dogs"))
                .body("tags[0].name", equalTo("friendly"))
                .body("status", equalTo("pending"))
                .extract().as(PetDTO.class);

        createdPetId = created.getId();
    }
}