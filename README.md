# Employee CRUD REST API

A Spring Boot REST API for managing employee records, built with a clean layered architecture using JPA for database interaction.

---

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [API Endpoints](#api-endpoints)
- [Entity](#entity)
- [Getting Started](#getting-started)
- [Technologies Used](#technologies-used)

---

## Overview

This project is a fully functional CRUD (Create, Read, Update, Delete) REST API for employee management. It supports standard HTTP methods including a `PATCH` endpoint for partial updates using Jackson's `JsonMapper`.

---

## Project Structure

```
src/
├── entity/
│   └── Employee.java               # JPA entity mapped to the 'employee' table
├── dao/
│   ├── EmployeeDAO.java            # DAO interface
│   └── EmployeeDAOJpaImpl.java     # JPA implementation using EntityManager
├── service/
│   ├── EmployeeService.java        # Service interface
│   └── EmployeeServiceImpl.java    # Service implementation with transaction management
└── rest/
    └── EmployeeRestController.java # REST controller exposing all endpoints
```

---

## Architecture

This project follows a **3-layer architecture**:

### 1. Controller Layer — `EmployeeRestController`
Handles all incoming HTTP requests at `/api/employees`. It maps each request to the appropriate service method and returns JSON responses. It also uses Jackson's `JsonMapper` to handle partial updates in the PATCH endpoint.

### 2. Service Layer — `EmployeeService` / `EmployeeServiceImpl`
Acts as the middleman between the controller and the data access layer. It manages `@Transactional` boundaries, meaning database write operations (save, delete) are wrapped in transactions here.

### 3. DAO Layer — `EmployeeDAO` / `EmployeeDAOJpaImpl`
Directly interacts with the database using JPA's `EntityManager`. Handles all queries and persistence operations:
- `findAll()` — runs a JPQL query to fetch all employees
- `findById()` — uses `entityManager.find()`
- `save()` — uses `entityManager.merge()` (handles both insert and update)
- `deleteById()` — finds the entity then calls `entityManager.remove()`

---

## API Endpoints

| Method   | Endpoint                    | Description                          |
|----------|-----------------------------|--------------------------------------|
| `GET`    | `/api/employees`            | Returns a list of all employees      |
| `GET`    | `/api/employees/{id}`       | Returns a single employee by ID      |
| `POST`   | `/api/employees`            | Creates a new employee               |
| `PUT`    | `/api/employees`            | Updates an existing employee (full)  |
| `PATCH`  | `/api/employees/{id}`       | Partially updates an employee        |
| `DELETE` | `/api/employees/{id}`       | Deletes an employee by ID            |

### Notes
- **POST**: The `id` field in the request body is always set to `0` to force creation of a new record rather than an update.
- **PUT**: Requires a full employee object in the request body including the `id`.
- **PATCH**: Accepts a partial JSON body (e.g., just `{ "email": "new@email.com" }`). The `id` field is not allowed in the request body. Uses `JsonMapper.updateValue()` to apply changes.
- **Error handling**: All endpoints throw a `RuntimeException` if the employee is not found.

### Example Request Bodies

**POST /api/employees**
```json
{
  "firstName": "Jane",
  "lastName": "Doe",
  "email": "jane.doe@example.com"
}
```

**PUT /api/employees**
```json
{
  "id": 3,
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@example.com"
}
```

**PATCH /api/employees/3**
```json
{
  "email": "updated@example.com"
}
```

---

## Entity

The `Employee` class is a JPA entity mapped to the `employee` database table.

| Field       | Column       | Type   |
|-------------|--------------|--------|
| `id`        | `id`         | `int`  |
| `firstName` | `first_name` | String |
| `lastName`  | `last_name`  | String |
| `email`     | `email`      | String |

The `id` is auto-generated using `GenerationType.IDENTITY`, meaning the database handles ID assignment on insert.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven
- A relational database (e.g., MySQL) with an `employee` table

### Database Setup

```sql
CREATE TABLE employee (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(45) DEFAULT NULL,
    last_name VARCHAR(45) DEFAULT NULL,
    email VARCHAR(45) DEFAULT NULL,
    PRIMARY KEY (id)
);
```

### Configuration

In `src/main/resources/application.properties`, configure your datasource:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/your_database
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Run the App

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api/employees`.

---

## Technologies Used

- **Java** — Core language
- **Spring Boot** — Application framework
- **Spring MVC** — REST controller support
- **Spring Data JPA / Hibernate** — ORM and database interaction via `EntityManager`
- **Jackson (`JsonMapper`)** — Used for partial updates in the PATCH endpoint
- **Jakarta Persistence** — JPA annotations for entity mapping
