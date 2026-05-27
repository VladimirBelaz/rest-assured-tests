package tests;

import dto.BookDTO;
import extensions.ApiServiceExtension;
import io.restassured.module.jsv.JsonSchemaValidator;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import services.BookApi;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;

@ExtendWith(ApiServiceExtension.class)
@DisplayName("Тесты для Books")
public class BooksTest {

    @Test
    @DisplayName("GET /Books/{id} – получение книги по существующему ID")
    void getBookByIdShouldReturnCorrectBook(BookApi bookApi) {
        int existingId = 1; // гарантированно существующий ID
        // Проверяем: статус 200, ID совпадает, заголовок не пустой, количество страниц > 0
        bookApi.getBookById((long)existingId)
                .statusCode(HttpStatus.SC_OK)
                .body("id", equalTo(existingId))
                .body("title", not(blankString()))
                .body("pageCount", greaterThan(0));
    }

    @Test
    @DisplayName("GET /Books/{id} – проверка JSON Schema ответа")
    void getBookByIdShouldMatchSchema(BookApi bookApi) {
        Long existingId = 1L;
        // Проверяем, что структура ответа соответствует схеме
        bookApi.getBookById(existingId)
                .statusCode(HttpStatus.SC_OK)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schema/book-schema.json"));
    }


    static Stream<Arguments> bookDataProvider() {
        return Stream.of(
                Arguments.of(BookDTO.builder()
                        .title("Война и мир")
                        .description("Роман-эпопея")
                        .pageCount(1300)
                        .excerpt("Начало")
                        .publishDate(LocalDateTime.now().minusYears(150))
                        .build(), HttpStatus.SC_OK),
                Arguments.of(BookDTO.builder()
                        .title("Краткая книга")
                        .description("Очень короткая")
                        .pageCount(10)
                        .build(), HttpStatus.SC_OK)
        );
    }

    @ParameterizedTest(name = "Создание книги: {0}")
    @MethodSource("bookDataProvider")
    @DisplayName("POST /Books – создание книги с разными наборами полей")
    void createBookShouldReturnSavedData(BookDTO bookToCreate, Integer expectedStatus, BookApi bookApi) {
        // Проверяем, что созданная книга содержит отправленные поля
        bookApi.createBook(bookToCreate)
                .statusCode(expectedStatus)
                .body("title", equalTo(bookToCreate.getTitle()))
                .body("pageCount", equalTo(bookToCreate.getPageCount()));
        // Если передавали publishDate, проверяем, что он не null
        if (bookToCreate.getPublishDate() != null) {
            bookApi.createBook(bookToCreate)
                    .body("publishDate", notNullValue());
        }
    }

    @Test
    @DisplayName("POST /Books – создание книги и извлечение DTO для проверки")
    void createBookAndExtractDto(BookApi bookApi) {
        BookDTO newBook = BookDTO.builder()
                .title("Тестовая книга")
                .description("Описание тестовой книги")
                .pageCount(250)
                .excerpt("Отрывок текста")
                .publishDate(LocalDateTime.now())
                .build();

        BookDTO created = bookApi.createBook(newBook)
                .statusCode(HttpStatus.SC_OK)
                .extract().as(BookDTO.class);

        org.junit.jupiter.api.Assertions.assertAll(
                () -> org.junit.jupiter.api.Assertions.assertEquals(newBook.getTitle(), created.getTitle()),
                () -> org.junit.jupiter.api.Assertions.assertEquals(newBook.getPageCount(), created.getPageCount())
        );
    }
}