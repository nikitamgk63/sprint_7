package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final Map<String, Object> orderData;

    public CreateOrderTest(Map<String, Object> orderData) {
        this.orderData = orderData;
    }

    @Parameterized.Parameters(name = "Цвета: {0}")
    public static Object[][] getOrderData() {
        return new Object[][] {
                { Map.of(
                        "firstName", "Naruto",
                        "lastName", "Uzumaki",
                        "address", "Konoha, 142 apt.",
                        "metroStation", 4,
                        "phone", "+7 800 355 35 35",
                        "rentTime", 5,
                        "deliveryDate", "2023-12-31",
                        "comment", "Sasuke, come back to Konoha",
                        "color", List.of("BLACK")
                )},
                { Map.of(
                        "firstName", "Sasuke",
                        "lastName", "Uchiha",
                        "address", "Orochimaru's Hideout",
                        "metroStation", 3,
                        "phone", "+7 800 355 35 36",
                        "rentTime", 3,
                        "deliveryDate", "2023-12-30",
                        "comment", "I'll come back when I'm ready",
                        "color", List.of("GREY")
                )},
                { Map.of(
                        "firstName", "Sakura",
                        "lastName", "Haruno",
                        "address", "Konoha Medical Center",
                        "metroStation", 5,
                        "phone", "+7 800 355 35 37",
                        "rentTime", 7,
                        "deliveryDate", "2024-01-01",
                        "comment", "Waiting for both of you",
                        "color", List.of("BLACK", "GREY")
                )},
                { Map.of(
                        "firstName", "Kakashi",
                        "lastName", "Hatake",
                        "address", "Hokage Office",
                        "metroStation", 1,
                        "phone", "+7 800 355 35 38",
                        "rentTime", 1,
                        "deliveryDate", "2023-12-25",
                        "comment", "Mission complete"
                )}
        };
    }

    @Test
    @DisplayName("POST /api/v1/orders - Создание заказа с различными параметрами цвета")
    @Description("Проверка создания заказа с разными комбинациями цветов: BLACK, GREY, оба цвета, без цвета")
    @Step("Создание заказа с параметрами: {orderData}")
    public void createOrderWithDifferentColorsTest() {
        createOrder(orderData)
                .then()
                .statusCode(201)
                .body("track", notNullValue());
    }

    @Step("Создание заказа")
    private Response createOrder(Map<String, Object> orderData) {
        return given()
                .contentType(ContentType.JSON)
                .body(orderData)
                .post("https://qa-scooter.praktikum-services.ru/api/v1/orders");
    }
}