package com.anadea.testtaskvaadin.ui.views

import com.anadea.testtaskvaadin.dtos.CreateUserRequest
import com.anadea.testtaskvaadin.dtos.UpdateUserRequest
import com.anadea.testtaskvaadin.services.UserService
import com.github.mvysny.karibudsl.v10.bind
import com.github.mvysny.karibudsl.v10.button
import com.github.mvysny.karibudsl.v10.comboBox
import com.github.mvysny.karibudsl.v10.emailField
import com.github.mvysny.karibudsl.v10.onClick
import com.github.mvysny.karibudsl.v10.passwordField
import com.github.mvysny.karibudsl.v10.textField
import com.github.mvysny.kaributools.setPrimary
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.textfield.EmailField
import com.vaadin.flow.component.textfield.PasswordField
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import jakarta.annotation.security.RolesAllowed
import org.slf4j.LoggerFactory
import java.util.UUID

@RolesAllowed("ADMIN")
class UserForm(
    private val userService: UserService,
    private val userId: UUID?,
    private val isReadOnly: Boolean = false,
    private val onSave: () -> Unit,
) : FormLayout() {
    private val log = LoggerFactory.getLogger(javaClass)

    private val binder = BeanValidationBinder(CreateUserRequest::class.java)

    private val nameField: TextField =
        textField("Name") {
            isReadOnly = this@UserForm.isReadOnly
            bind(binder).asRequired("Name is required").bind("name")
        }
    private val emailField: EmailField =
        emailField("Email") {
            isReadOnly = this@UserForm.isReadOnly
            bind(binder).asRequired("Email is required").bind("email")
        }
    private val passwordField: PasswordField =
        passwordField("Password") {
            isVisible = userId == null && !isReadOnly
            if (isVisible) {
                bind(binder).asRequired("Password is required").bind("password")
            }
        }
    private val roleField: ComboBox<String> =
        comboBox("Role") {
            setItems("ADMIN", "USER")
            value = "USER"
            isReadOnly = this@UserForm.isReadOnly
            bind(binder).asRequired("Role is required").bind("role")
        }

    init {
        if (!isReadOnly) {
            button("Save") {
                setPrimary()
                onClick { saveUser() }
            }
        }

        loadUserAndAdjustForm()
    }

    private fun loadUserAndAdjustForm() {
        if (userId == null) return

        userService.findById(userId)?.let { user ->
            nameField.value = user.name
            emailField.value = user.email
            roleField.value = user.role
        }
    }

    private fun saveUser() {
        if (binder.validate().isOk) {
            runCatching {
                if (userId == null) {
                    val request =
                        CreateUserRequest(
                            name = nameField.value,
                            email = emailField.value,
                            password = passwordField.value,
                            role = roleField.value,
                        )
                    userService.create(request)
                    log.info("User created successfully")
                } else {
                    val updateRequest =
                        UpdateUserRequest(
                            name = nameField.value,
                            email = emailField.value,
                            password = passwordField.value.takeIf { it.isNotBlank() },
                            role = roleField.value,
                        )
                    userService.update(userId, updateRequest)
                    log.info("User updated successfully")
                }
            }.onSuccess {
                onSave()
                val action = if (userId == null) "created" else "updated"
                Notification.show(
                    "User ${nameField.value} $action successfully",
                    3000,
                    Notification.Position.TOP_CENTER,
                )
            }.onFailure { e ->
                log.error("Save failed", e)
                Notification.show("Error saving user: ${e.message}")
            }
        } else {
            Notification.show("Please fix validation errors")
        }
    }
}
