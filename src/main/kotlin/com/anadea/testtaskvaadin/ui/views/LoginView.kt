package com.anadea.testtaskvaadin.ui.views

import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.login.LoginForm
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("login")
@PageTitle("Login")
@AnonymousAllowed
class LoginView :
    VerticalLayout(),
    BeforeEnterObserver {
    private val login = LoginForm()

    init {
        setSizeFull()
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        alignItems = FlexComponent.Alignment.CENTER

        login.action = "login"

        add(
            H1("Anadea Test Task Vaadin"),
            login,
        )
    }

    override fun beforeEnter(event: BeforeEnterEvent) {
        if (event.location.queryParameters.parameters
                .containsKey("error")
        ) {
            login.isError = true
        }
    }
}
