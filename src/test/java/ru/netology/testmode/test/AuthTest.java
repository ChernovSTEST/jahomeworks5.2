package ru.netology.testmode.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.testmode.data.DataGenerator;
import ru.netology.testmode.data.DataGenerator.RegistrationDto;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    @Test
    @DisplayName("Should successfully login with an active registered user")
    void shouldSuccessfullyLoginWithActiveRegisteredUser() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        login(registeredUser);
        verifyLoggedIn("Личный кабинет");
    }

    @Test
    @DisplayName("Should get an error message if login with an unregistered user")
    void shouldGetErrorIfUnregisteredUser() {
        RegistrationDto unregisteredUser = DataGenerator.Registration.getUser("active");
        login(unregisteredUser);
        verifyErrorMessage("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get an error message if login with a blocked user")
    void shouldGetErrorIfBlockedUser() {
        RegistrationDto blockedUser = DataGenerator.Registration.getRegisteredUser("blocked");
        login(blockedUser);
        verifyErrorMessage("Ошибка! Пользователь заблокирован");
    }

    @Test
    @DisplayName("Should get an error message if login with a wrong username")
    void shouldGetErrorIfWrongUsername() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String wrongUsername = DataGenerator.getRandomLogin();
        login(wrongUsername, registeredUser.getPassword());
        verifyErrorMessage("Ошибка! Неверно указан логин или пароль");
    }

    @Test
    @DisplayName("Should get an error message if login with a wrong password")
    void shouldGetErrorIfWrongPassword() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String wrongPassword = DataGenerator.getRandomPassword();
        login(registeredUser.getLogin(), wrongPassword);
        verifyErrorMessage("Ошибка! Неверно указан логин или пароль");
    }

    private void login(String login, String password) {
        $("[data-test-id='login'] input").setValue(login);
        $("[data-test-id='password'] input").setValue(password);
        $("button.button").click();
    }

    private void login(RegistrationDto user) {
        login(user.getLogin(), user.getPassword());
    }

    private void verifyLoggedIn(String expectedHeaderText) {
        $("h2").shouldHave(Condition.exactText(expectedHeaderText)).shouldBe(Condition.visible);
    }

    private void verifyErrorMessage(String expectedErrorMessage) {
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text(expectedErrorMessage))
                .shouldBe(Condition.visible);
    }
}
