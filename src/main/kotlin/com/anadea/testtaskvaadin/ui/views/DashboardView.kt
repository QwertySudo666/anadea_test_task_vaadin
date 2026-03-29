package com.anadea.testtaskvaadin.ui.views

import com.anadea.testtaskvaadin.dtos.UserDto
import com.anadea.testtaskvaadin.services.UserService
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.grid.Grid
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import com.vaadin.flow.spring.security.AuthenticationContext
import jakarta.annotation.security.RolesAllowed
import org.springframework.data.domain.PageRequest
import java.util.UUID

@Route("dashboard")
@PageTitle("Dashboard")
@RolesAllowed("USER", "ADMIN")
class DashboardView(
    private val userService: UserService,
    private val authContext: AuthenticationContext,
) : KComposite() {
    private lateinit var grid: Grid<UserDto>
    private lateinit var nameFilter: TextField
    private lateinit var emailFilter: TextField
    private lateinit var pageLabel: Span

    private val isAdmin = authContext.hasRole("ADMIN")
    private val pageSize = 20
    private var currentPage = 0
    private var totalPages = 0

    private val root =
        ui {
            verticalLayout {
                // --- Header ---
                horizontalLayout {
                    setWidthFull()
                    alignItems = FlexComponent.Alignment.CENTER

                    val title = h1("User List")
                    expand(title)

                    button("Logout") {
                        addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY)
                        onClick { authContext.logout() }
                    }
                }

                if (isAdmin) {
                    horizontalLayout {
                        button("Add User") {
                            onClick { showAddUserDialog() }
                        }
                    }
                }

                // --- Filters ---
                horizontalLayout {
                    nameFilter =
                        textField("Search by Name") {
                            addValueChangeListener {
                                currentPage = 0
                                updateGrid()
                            }
                        }
                    emailFilter =
                        textField("Search by Email") {
                            addValueChangeListener {
                                currentPage = 0
                                updateGrid()
                            }
                        }
                }

                // --- Grid ---
                grid =
                    grid {
                        addItemClickListener { event ->
                            event.item.id?.let { showUserDialog(it) }
                        }

                        addColumn(UserDto::name).setHeader("Name").isSortable = true
                        addColumn(UserDto::email).setHeader("Email").isSortable = true
                        addColumn(UserDto::createdAt).setHeader("Created At").isSortable = true
                        addColumn(UserDto::updatedAt).setHeader("Updated At").isSortable = true
                    }

                // --- Pagination ---
                horizontalLayout {
                    alignItems = FlexComponent.Alignment.CENTER
                    justifyContentMode = FlexComponent.JustifyContentMode.CENTER
                    setWidthFull()
                    isPadding = false

                    button("«") {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                        onClick {
                            if (currentPage > 0) {
                                currentPage = 0
                                updateGrid()
                            }
                        }
                    }
                    button("‹") {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                        onClick {
                            if (currentPage > 0) {
                                currentPage--
                                updateGrid()
                            }
                        }
                    }

                    pageLabel = span("Page 1 of 1")

                    button("›") {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                        onClick {
                            if (currentPage < totalPages - 1) {
                                currentPage++
                                updateGrid()
                            }
                        }
                    }
                    button("»") {
                        addThemeVariants(ButtonVariant.LUMO_TERTIARY)
                        onClick {
                            if (totalPages > 0) {
                                currentPage = totalPages - 1
                                updateGrid()
                            }
                        }
                    }
                }
            }
        }

    init {
        updateGrid()
    }

    private fun updateGrid() {
        val name = nameFilter.value.takeIf { it.isNotBlank() }
        val email = emailFilter.value.takeIf { it.isNotBlank() }
        val page = userService.search(name, email, PageRequest.of(currentPage, pageSize))

        totalPages = page.totalPages

        if (totalPages in 1 downTo currentPage) {
            currentPage = totalPages - 1
            updateGrid()
            return
        }

        grid.setItems(page.content)
        pageLabel.text = "Page ${currentPage + 1} of ${if (totalPages == 0) 1 else totalPages}"
    }

    private fun showAddUserDialog() {
        val dlg = Dialog()
        dlg.headerTitle = "Add User"
        dlg.add(
            UserForm(userService, null) {
                currentPage = 0
                updateGrid()
                dlg.close()
            },
        )
        dlg.open()
    }

    private fun showUserDialog(userId: UUID) {
        val dlg = Dialog()
        dlg.headerTitle = if (isAdmin) "Edit User" else "User Details"

        val form =
            UserForm(
                userService = userService,
                userId = userId,
                isReadOnly = !isAdmin,
                onSave = {
                    updateGrid()
                    dlg.close()
                },
            )

        dlg.add(form)

        if (isAdmin) {
            val deleteBtn =
                com.vaadin.flow.component.button.Button("Delete User") {
                    deleteUser(userId, dlg)
                }
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ERROR)
            dlg.footer.add(deleteBtn)
        }

        dlg.open()
    }

    private fun deleteUser(
        userId: UUID,
        parentDlg: Dialog,
    ) {
        val confirmDlg = Dialog()
        confirmDlg.headerTitle = "Confirm Delete"
        confirmDlg.add(
            VerticalLayout().apply {
                p("Are you sure you want to delete this user?")
                horizontalLayout {
                    button("Delete") {
                        addThemeVariants(ButtonVariant.LUMO_ERROR)
                        onClick {
                            userService.delete(userId)
                            updateGrid()
                            confirmDlg.close()
                            parentDlg.close()
                            Notification.show("User deleted successfully", 3000, Notification.Position.TOP_CENTER)
                        }
                    }
                    button("Cancel") {
                        onClick { confirmDlg.close() }
                    }
                }
            },
        )
        confirmDlg.open()
    }
}
