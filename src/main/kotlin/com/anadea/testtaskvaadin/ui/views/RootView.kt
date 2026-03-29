package com.anadea.testtaskvaadin.ui.views

import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.router.BeforeEnterObserver
import com.vaadin.flow.router.Route
import com.vaadin.flow.server.auth.AnonymousAllowed

@Route("")
@AnonymousAllowed
class RootView : VerticalLayout(), BeforeEnterObserver {
    override fun beforeEnter(event: BeforeEnterEvent) {
        event.forwardTo(DashboardView::class.java)
    }
}