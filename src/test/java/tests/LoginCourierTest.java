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
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {

    private String login;
    private String password;
    private Integer courierId;

    @Before
    public void setUp() {
        login = "courier_" + System.currentTimeMillis(); // Уникальный логин для каждого теста
        password = "CustomPassword";

        // Создаем тестового курьера
        courierId = createCourier(login, password)
                .then()
                .statusCode(201) // Проверка, что курьер создан успешно
                .extract()
                .path("id");
    }

    @After
    public void tearDown() {
        // Удаляем курьера после тестов, если он был создан
        if (courierId != null) {
            deleteCourier(courierId);
        }
    }

    @Test
    @DisplayName("POST /api/v1/courier/login - Успешная авторизация")
    @Description("Проверка успешной авторизации с валидными логином и паролем")
    @Step("Авторизация с валидными данными")
    public void successfulLoginTest() {
        loginCourier(login, password)
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @DisplayName("POST /api/v1/courier/login - Неверные учетные данные")
    @Description("Проверка ошибки при неверной паре логин-пароль")
    @Step("Авторизация с неверными данными")
    public void loginWithInvalidCredentialsTest() {
        loginCourier("invalid_" + login, "wrong_" + password)
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("POST /api/v1/courier/login - Отсутствие логина")
    @Description("Проверка ошибки при отсутствии логина")
    @Step("Авторизация без логина")
    public void loginWithoutLoginTest() {
        loginCourier(null, password)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("POST /api/v1/courier/login - Отсутствие пароля")
    @Description("Проверка ошибки при отсутствии пароля")
    @Step("Авторизация без пароля")
    public void loginWithoutPasswordTest() {
        loginCourier(login, null)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("POST /api/v1/courier/login - Пустые данные")
    @Description("Проверка ошибки при пустых логине и пароле")
    @Step("Авторизация с пустыми данными")
    public void loginWithEmptyCredentialsTest() {
        loginCourier("", "")
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }

    // Вспомогательные методы

    @Step("Создание курьера")
    private Response createCourier(String login, String password) {
        Map<String, String> body = Map.of(
                "login", login,
                "password", password
        );
        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier");
    }

    @Step("Авторизация курьера")
    private Response loginCourier(String login, String password) {
        Map<String, String> body = new HashMap<>();
        if (login != null) body.put("login", login);
        if (password != null) body.put("password", password);

        return given()
                .contentType(ContentType.JSON)
                .body(body)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/courier/login");
    }

    @Step("Удаление курьера")
    private void deleteCourier(int courierId) {
        given()
                .delete("https://qa-scooter.praktikum-services.ru/api/v1/courier/" + courierId)
                .then()
                .statusCode(200);
    }
}
