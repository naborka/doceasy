package ru.d1g.doceasy.core.ui;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@Tag("sa-login-view")
@Route(value = LoginView.ROUTE)
@PageTitle("Вход")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    public static final String ROUTE = "login";

    private LoginOverlay login = new LoginOverlay();

    public LoginView() {
        LoginI18n.Header header = new LoginI18n.Header();
        header.setTitle("Проект DocEASY");
        header.setDescription("Вход для пользователей");

        LoginI18n.Form form = new LoginI18n.Form();
        form.setTitle("");
        form.setUsername("Логин");
        form.setPassword("Пароль");
        form.setForgotPassword("Восстановить пароль");
        form.setSubmit("Войти");

        LoginI18n i18n = new LoginI18n();
        i18n.setForm(form);
        i18n.setHeader(header);
        login.setAction("login");
        login.setOpened(true);
        login.setI18n(i18n);
        getElement().appendChild(login.getElement());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        if (!beforeEnterEvent.getLocation().getQueryParameters().getParameters().getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true);
        }
    }
}
