# 💻 Miesiany Miesiany Kebab

## 👀 About

Full-stack application for a fictional kebab restaurant called "Miesiany Miesiany Kebab". The application is also intended to serve as a restaurant's business card, in order to reach a larger number of customers, and to enable the fulfillment and tracking of orders.

## 🔧 Tech Stack

- Java 21
- Spring Boot 3
- Spring Security
- Spring Data
- Hibernate
- Lombok
- Maven
- TypeScript
- Angular 18
- JUnit
- Mockito
- PostgreSQL
- SQL
- HTML
- SCSS
- Bootstrap
- Docker
- Bash

## 💡 Features

- Eye-catching, well-tailored and well-thought user interface.
- Responsive Web Design to improve accessibility of page at all types of devices,
- Multilingual pages (Polish or English),
- Multilingual validation for all forms,
- Integration with TomTom Map API to add map with pointer, which aims to help potential customers easily locate restaurant.


## 🔗 API

#### POST /api/v1/auth/login

**Description:**  
Authenticates a user if the credentials are valid. Returns proper validation messages in specified in `Accept-Language` header language and error details, if any.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl`. You can also set `en`.

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```
