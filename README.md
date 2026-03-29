# Anadea Test Task Vaadin

A user management dashboard built with Vaadin 25, Spring Boot 4, and PostgreSQL.

## Tech Stack

- **Frontend/Backend:** Vaadin 25 + Kotlin
- **Framework:** Spring Boot 4
- **Database:** PostgreSQL 17
- **Migrations:** Liquibase
- **Security:** Spring Security

## Requirements

- Docker + Docker Compose

## Run

```bash
docker compose up --build
```

App will be available at: [http://localhost:8080](http://localhost:8080)

On first startup Liquibase will automatically:
1. Create the database schema
2. Seed the admin user and 500 test users

## Default Credentials

| Role  | Email                        | Password |
|-------|------------------------------|----------|
| Admin | admin@example.com            | admin    |
| User  | user1@example.com            | password |
| User  | user2@example.com ... user500@example.com | password |

## Local Development (without Docker)

1. Start PostgreSQL locally on port `5432`
2. (Will be created automatically during on first startup) Create database `anadea_test` with user `anadea` / password `anadea`
3. Run:

```bash
./gradlew bootRun
```

App will be available at: [http://localhost:8080](http://localhost:8080)

## Project Structure

```
src/
└── main/
    ├── kotlin/               # Application source code
    └── resources/
        ├── application.yml   # App configuration
        └── db/changelog/     # Liquibase migrations
            ├── db.changelog-master.xml
            └── changesets/
                ├── 001-create-users-table.sql
                └── 002-insert-test-data.sql
```

## Features

- **Admin** can: view, create, edit, and delete users
- **User** can: view user list (read-only)
- Search/filter by name and email
- Pagination (20 users per page)
- Click on a row to view or edit user details

## Assumptions & Trade-offs

- `ddl-auto: validate` — schema is fully managed by Liquibase, Hibernate only validates it
- Seed data uses `ON CONFLICT DO NOTHING` — safe to run multiple times without duplicates
- Vaadin production mode is enabled in Docker via `-Dvaadin.productionMode=true`
- `launch-browser: false` in Docker since there is no browser in the container