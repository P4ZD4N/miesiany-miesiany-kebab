
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

- Session-based authentication system, taking into data security. For this purpose, I implemented storing passwords encrypted with the bcrypt algorithm in database.
- Eye-catching, well-tailored and well-thought user interface.
- Responsive Web Design to improve accessibility of page at all types of devices,
- Multilingual pages (Polish or English),
- Multilingual validation for all forms,
- Integration with TomTom Map API to add map with pointer, which aims to help potential customers easily locate restaurant.
- Possibility to display highlighted with proper color opening hours of restaurant on each day. Manager can easily update these hours.
- Bash script, which starts PostgreSQL database, backend and frontend with one command.


## 🔗 API

### AuthenticationController

#### POST /api/v1/auth/login

**Description:**  
Authenticates a user if the credentials are valid. Returns proper validation messages in specified in `Accept-Language` header language and error details, if any.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### POST /api/v1/auth/logout

**Description:**  
 Logs out currently logged in user, invalidating his session.

 ### HoursController

#### GET /api/v1/hours/opening-hours

**Description:**  
Retrieves currently set up opening hours on all days of the week from the database.

#### PUT /api/v1/hours/update-opening-hour

**Description:**  
Updates the opening hours for a specific day of the week.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "day_of_week":  "TUESDAY",
  "opening_time":  "20:00",
  "closing_time":  "23:00"
}
```


## 🌍 Environment Variables

To run this project and ensure, that app will work properly, you need to add the following line to your .env file. You need it to see the map with restaurant location on home page.

- `MAP_API_KEY=<YOUR_API_KEY>`

Do it step by step:
1. Navigate to `https://developer.tomtom.com/` and log in to your account. Then create new Map Display API key and copy it.
2. Create `.env` file on the following path: `/miesiany-miesiany-kebab/frontend/src/.env`
3. Add previously copied API key to `.env` file

## ▶️ Run

Clone the project

```bash
git clone git@github.com:P4ZD4N/miesiany-miesiany-kebab.git
```

Navigate to the project directory

```bash
cd /path/to/miesiany-miesiany-kebab
```

Run app

```bash
./start.sh
```

## ⚡ Usage

Navigate to the following URL in your web browser

```
http://localhost:4200
```

That's It! You can start using kebab app :) 
