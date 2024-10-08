

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
- Multilingual validation for all forms. Validation messages are sent by backend in appropriate langueage specified in request header,
- Integration with TomTom Map API to add map with pointer, which aims to help potential customers easily locate restaurant.
- Bash script, which starts PostgreSQL database, backend and frontend with one command.
- Possibility to display highlighted with proper color opening hours of restaurant on each day. Manager can easily update these hours.
- Possibility to display menu of the restaurant. In menu section clients can see three tables: meals, addons to your meal and beverages, which contains each item details like name, price, capacity or ingredients. This entire section is manageable by the manager. Employee with this role can add, update and remove each type of item. Items in each table are sorted by name.


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

<br>

 ### HoursController

#### GET /api/v1/hours/opening-hours

**Description:**  
Retrieves currently set up opening hours on all days of the week from the database.

#### PUT /api/v1/hours/update-opening-hour

**Description:**  
Updates the opening hours for a specific day of the week.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "day_of_week":  "TUESDAY",
  "opening_time":  "20:00",
  "closing_time":  "23:00"
}
```

<br>

 ### MenuController
 
#### GET /api/v1/menu/beverages

**Description:**  
Retrieves all beverages, that are currently stored in the database.

#### POST /api/v1/menu/add-beverage

**Description:**  
Add new beverage. It is possible to add new beverage with name of existing, but new beverage must have different capacity. For example, if db stores Coca-Cola with capacity 0.33L, you can add Coca-Cola with capacity 0.5L, but not with 0.33L.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "new_beverage_name":  "Lipton",
  "new_beverage_capacity":  0.5,
  "new_beverage_price":  6
}
```

#### PUT /api/v1/menu/update-beverage

**Description:**  
Updates existing beverage. If db already stores Coca-Cola with capacity 0.33L, you can't update Coca-Cola with capacity 0.5L to Coca-Cola with capacity 0.33L.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "updated_beverage_name":  "Lipton",
  "updated_beverage_price":  6,
  "updated_beverage_new_capacity":  0.33,
  "updated_beverage_old_capacity": 0.5
}
```

#### DELETE /api/v1/menu/remove-beverage

**Description:**  
Removes existing beverage.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "name":  "Lipton",
  "capacity":  0.33
}
```

#### GET /api/v1/menu/addons

**Description:**  
Retrieves all addons, that are currently stored in the database.

#### POST /api/v1/menu/add-addon

**Description:**  
Add new addon. It is not possible to add addon with the same name of existing. For example, if db already stores Jalapeno, you can't add Jalapeno second time.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "new_addon_name":  "Jalapeno",
  "new_addon_price":  3
}
```

#### PUT /api/v1/menu/update-addon

**Description:**  
Updates existing addon.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "updated_addon_name":  "Jalapeno",
  "updated_addon_price":  2.5
}
```

#### DELETE /api/v1/menu/remove-addon

**Description:**  
Removes existing addon.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "name":  "Jalapeno"
}
```

#### GET /api/v1/menu/meals

**Description:**  
Retrieves all meals, that are currently stored in the database.

#### POST /api/v1/menu/add-meal

**Description:**  
Add new meal. It is not possible to add meal with the same name of existing. For example, if db already stores 'Pita Kebab', you can't add 'Pita Kebab' second time. You can specify prices as you want. Even just with SMALL and MEDIUM sizes or with SMALL and XL, but it is necessary to specify at least one size. You should also specify at least one ingredient, which must be already added in database. Ingredients, which are displayed as "Base Ingredients" on the website are of type: BREAD, VEGETABLE and OTHER.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "new_meal_name":  "Kebab",
  "new_meal_prices":  {
    "SMALL":  20,
    "MEDIUM":  22,
    "LARGE":  25.5,
    "XL":  30
  },
  "new_meal_ingredients": [
    {
      "name":  "Tortilla",
      "ingredient_type":  "BREAD"
    },
    {
      "name":  "Tomato",
      "ingredient_type":  "VEGETABLE"
    }
  ]
}
```

#### PUT /api/v1/menu/update-meal

**Description:**  
Updates existing meal. You can specify prices and ingredients as you want, but it is necessary to add at least one for both.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "updated_meal_name":  "Kebab",
  "updated_meal_prices":  {
    "SMALL":  20,
    "XL":  35
  },
  "updated_meal_ingredients": [
    {
      "name":  "Pita",
      "ingredient_type":  "BREAD"
    }
  ]
}
```

#### DELETE /api/v1/menu/remove-meal

**Description:**  
Removes existing meal.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "name":  "Kebab"
}
```

#### GET /api/v1/menu/ingredients

**Description:**  
Retrieves all ingredients, that are currently stored in the database.

#### POST /api/v1/menu/add-ingredient

**Description:**  
Add new ingredient. It is not possible to add ingredient with the same name as existing ingredient. It is possible to add ingredients of types: BREAD, MEAT, VEGETABLE, SAUCE and OTHER.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "new_ingredient_name":  "Cucumber",
  "new_ingredient_type": "VEGETABLE"
}
```

#### DELETE /api/v1/menu/remove-ingredient

**Description:**  
Removes existing ingredient.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "name":  "Cucumber",
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
