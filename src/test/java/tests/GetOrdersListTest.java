package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrdersListTest {

    @Test
    @DisplayName("GET /api/v1/orders - Получение списка заказов") // имя теста
    @Description("Проверка получения списка заказа") // описание теста
    @Step("Получение списка заказов")
    public void getOrdersListTest() {
        getOrders()
                .then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Step("Получить список заказов")
    private Response getOrders() {
        return given()
                .get("https://qa-scooter.praktikum-services.ru/api/v1/orders");
    }
}