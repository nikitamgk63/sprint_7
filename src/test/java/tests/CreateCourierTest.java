package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    private Integer courierId;
    private String login;
    private String password;
    private String firstName;

    @Before
    public void setUp() {
        login = "courier_" + System.currentTimeMillis();
        password = "CustomPassword";
        firstName = "TestName";
    }

    @After
    public void tearDown() {
        if (courierId != null) {
            deleteCourier(courierId);
            courierId = null;
        }
    }

    @Test
    @DisplayName("POST /api/v1/courier - Создание курьера с firstName")
    @Description("Проверка успешного создания курьера со всеми полями")
    @Step("Создание курьера с firstName")
    public void createCourierWithFirstNameTest() {
        createCourier(login, password, firstName)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("POST /api/v1/courier - Успешное создание курьера")
    @Description("Проверка успешного создания курьера с обязательными полями")
    @Step("Создание курьера с логином и паролем")
    public void successfulCourierCreationTest() {
        courierId = createCourier(login, password, null)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true))
                .extract()
                .path("id");
    }

    @Test
    @DisplayName("POST /api/v1/courier - Дубликат курьера")
    @Description("Проверка ошибки при создании курьера с существующим логином")
    @Step("Попытка создания дубликата курьера")
    public void duplicateCourierCreationTest() {
        courierId = createCourier(login, password, null)
                .then()
                .statusCode(201)
                .extract()
                .path("id");

        createCourier(login, password, null)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется"));
    }

    @Test
    @DisplayName("POST /api/v1/courier - Отсутствие обязательных полей")
    @Description("Проверка ошибки при отсутствии обязательных полей")
    @Step("Создание курьера без обязательных полей")
    public void courierCreationWithoutRequiredFieldsTest() {
        createCourierWithOptionalFields(null, password, firstName)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        createCourierWithOptionalFields(login, null, firstName)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Создание курьера с логином: {login}, паролем: {password} и именем: {firstName}")
    private Response createCourier(String login, String password, String firstName) {
        Map<String, String> body = new HashMap<>();
        body.put("login", login);
        body.put("password", password);
        if (firstName != null) {
            body.put("firstName", firstName);
        }
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    @Step("Создание курьера с неполными данными")
    private Response createCourierWithOptionalFields(String login, String password, String firstName) {
        Map<String, String> body = new HashMap<>();
        if (login != null) {
            body.put("login", login);
        }
        if (password != null) {
            body.put("password", password);
        }
        if (firstName != null) {
            body.put("firstName", firstName);
        }
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    @Step("Удаление курьера с ID: {courierId}")
    private void deleteCourier(int courierId) {
        given()
                .delete("https://qa-scooter.praktikum-services.ru/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}