# 💻 Miesiany Miesiany Kebab

## 📜 Table of Contents
1. [👀 About](#-about)
2. [🔧 Tech Stack](#-tech-stack)
3. [💡 Features](#f-eatures)
4. [🔗 API](#-api)
5. [📋 Requirements](#-requirements)
6. [🌍 Environment Variables](#-environment-variables)
7. [▶️ Run](#-run)
8. [⚡ Usage](#-usage)

## 👀 About

Full-stack application for a fictional kebab restaurant called "Miesiany Miesiany Kebab". The application is also intended to serve as a restaurant's business card, in order to reach a larger number of customers, and to enable the fulfillment and tracking of orders.

## 🔧 Tech Stack

**Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data, Hibernate, Lombok

**Frontend:** TypeScript 5, Angular 18, HTML, SCSS, Bootstrap

**Testing:** JUnit, Mockito

**Databases:** PostgreSQL, SQL

**Build tools:** Maven

**DevOps:** Docker, Bash

**Other tools used during development:** Postman, pgAdmin 4

## 💡 Features

- Session-based authentication system, taking into data security. For this purpose, I implemented storing passwords encrypted with the bcrypt algorithm in database.
- Eye-catching, well-tailored and well-thought user interface.
- Responsive Web Design to improve accessibility of page at all types of devices,
- Multilingual pages (Polish or English),
- Multilingual validation for all forms. Validation messages are sent by backend in appropriate language specified in request header,
- Integration with TomTom Map API to add map with pointer, which aims to help potential customers easily locate restaurant.
- Bash script, which starts PostgreSQL database, backend and frontend with one command.
- Possibility to follow application flow and diagnose potential issues by accessing informational, warning and error logs in console.
- Possibility to display home page. This is the "first contact section", which means, that clients see it firstly, after navigating to /. For this reason, this section had to be best thought out from a marketing perspective. I placed there many informations, that can encourage potential customer to place the order. Home page is divided to four subsections: hero section, about us, awards and location. Subsections contain, among others, many marketing slogans, certificates, guarantees, acknowledgments and a map with a pointer.
- Possibility to display highlighted with proper color opening hours of restaurant on each day. Manager can easily update these hours.
- Possibility to display menu of the restaurant. In menu section clients can see three tables: meals, addons to your meal and beverages, which contains each item details like name, price, capacity or ingredients. This entire section is manageable by the manager. Employee with this role can add, update and remove each type of item. Items in each table are sorted by name.
- Possibility to display contact details. In contact section clients can see contact data (including phone number and email address), nicknames at social media and map with location pointer. Contact data is editable by manager.
- Job board, which enable to publish dateiled job offers by manager. Manager can add job offer with information such as position name, description, list of mandatory requirements, list of nice to have requirements, list of employment types and hourly wage gross. Once added, each job offer is fully customizable. Website guests can apply to each job offer by fulfilling form (with attaching a CV in PDF/DOC format!). Manager has possibility to display all candidates, which applied to job offer, remove those who do not fit the position or peek/download attached CV of desired candidate.
- Promotions for meals, beverages and addons. It is possible to display promotions details in proper section on website by all users. If some menu position is already added to certain promotion, users can see price change in menu section on website. All promotions are editable and easy to maintain by manager. Manager can add, update and delete promotions.


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
  "name":  "Cucumber"
}
```

### ContactController
 
#### GET /api/v1/contact/contacts

**Description:**  
Retrieves all contacts, that are currently stored in the database.

#### PUT /api/v1/contact/update-contact

**Description:**  
Updates existing contact. You can update contacts of type EMAIL or TELEPHONE. 

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "contact_type":  "TELEPHONE",
  "new_value":  "123456789"
}
```

### JobsController

#### GET /api/v1/jobs/job-offers/manager

**Description:**  
Retrieves all job offers, that are currently stored in the database. Response for manager contains all job offers properties including job offer applications.

**Authorization Requirements:**
- Role: `MANAGER`

#### GET /api/v1/jobs/job-offers/general

**Description:**  
Retrieves all job offers, that are currently stored in the database. General response contain limited job offers properties (without job offer applications).

#### POST /api/v1/jobs/add-job-offer

**Description:**  
Add new job offer. It is not possible to add job offer with the same position name as existing job offer. You can add job offer without specyfing job employment types and job requirements.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "position_name":  "Cook",
  "description": "Write something...",
  "hourly_wage": 30,
  "job_employment_types": [
    {
      "employment_type":  "PERMANENT"
    },
    {
      "employment_type":  "MANDATE_CONTRACT"
    }
  ],
  "job_requirements": [
    {
      "requirement_type":  "MANDATORY",
      "description":  ":)"
    },
    {
      "requirement_type":  "NICE_TO_HAVE",
      "description":  "nice to have"
    }
  ]
}
```

#### PUT /api/v1/jobs/update-job-offer

**Description:**  
Updates existing job offer. You can update each property of job offer, but if you enter new position name same as different, existing job offer, it will not work.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "position_name":  "Cook",
  "updated_position_name": "Coook",
  "updated_description": "Write something...",
  "updated_hourly_wage": 30,
  "updated_employment_types": [
    {
      "employment_type":  "PERMANENT"
    },
    {
      "employment_type":  "MANDATE_CONTRACT"
    }
  ],
  "updated_requirements": [
    {
      "requirement_type":  "MANDATORY",
      "description":  ":)"
    },
    {
      "requirement_type":  "NICE_TO_HAVE",
      "description":  "nice to have"
    }
  ],
  "is_active": false
}
```

#### DELETE /api/v1/jobs/remove-job-offer

**Description:**  
Removes existing job offer.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "position_name":  "Cook"
}
```

#### POST /api/v1/jobs/add-job-offer-application

**Description:**  
Add new job offer application. You can add job offer application without adding optional additional message.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "position_name":  "Cook",
  "applicant_first_name": "Wiktor",
  "applicant_last_name": "Chudy",
  "applicant_email": "wiko700@gmail.com",
  "applicant_telephone": "123456789",
  "additional_message": "Write something...",
  "is_student": true
}
```

#### POST /api/v1/jobs/add-cv

**Description:**  
Add CV to the job offer application.

**Request Params:**
- `applicationId`: The ID of application, to which the CV is being attached. Must be a numeric value.
	* Type: `Long`
    * Required: Yes
    * Example: `12345`
- `cv`: The CV file in multipart/form-data format. Supported file formats: PDF, DOCX.
	 * Type: `MultipartFile`
	 * Required: Yes
	 * Example: File named `cv.pdf` uploaded as an attachment in the form.

#### GET /api/v1/jobs/download-cv/{id}

**Description:**  
Download CV with specified ID.

#### DELETE /api/v1/jobs/remove-job-application

**Description:**  
Removes existing job application from job offer.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "position_name":  "Cook",
  "application_id": 1
}
```

### PromotionsController

#### GET /api/v1/promotions/meal-promotions

**Description:**  
Retrieves all meal promotions, that are currently stored in the database.

#### POST /api/v1/promotions/add-meal-promotion

**Description:**
Add new meal promotion. You can add meal promotion without specifying meal names and sizes. Description and discount percentage are necessary fields.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "description":  "All Large and XL -50%!",
  "discount_percentage": 50,
  "meal_names":  [
    "Pita Kebab Salads",
    "Pita Kebab Salads and Fries"
  ],
  "sizes": [
    "LARGE",
    "XL"
  ]
}
```

#### PUT /api/v1/promotions/update-meal-promotion

**Description:**
Update existing meal promotion. You can update meal promotion without specifying any property except id. Id is necessary to identify proper promotion. If you won't update certain property, you shouldn't specify it in request.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "id": 1,
  "updated_description": "All Large -40%!",
  "updated_discount_percentage": 40,
  "updated_meal_names":  [
    "Pita Kebab Salads",
    "Pita Kebab Salads and Fries"
  ],
  "updated_sizes": [
    "LARGE"
  ]
}
```

#### DELETE /api/v1/promotions/remove-meal-promotion

**Description:**  
Removes existing meal promotion.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "id": 1
}
```

#### GET /api/v1/promotions/beverage-promotions

**Description:**  
Retrieves all beverage promotions, that are currently stored in the database.

#### POST /api/v1/promotions/add-beverage-promotion

**Description:**
Add new beverage promotion. You can add beverage promotion without specifying beverage names with their capacities. Description and discount percentage are necessary fields.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "description": "All Coca-Cola -30%!",
  "discount_percentage": 30,
  "beverages_with_capacities": {
    "Coca-Cola": [0.33,  0.5],
    "Coca-Cola Zero": [0.33]
  }
}
```

#### PUT /api/v1/promotions/update-beverage-promotion

**Description:**
Update existing beverage promotion. You can update meal promotion without specifying any property except id. Id is necessary to identify proper promotion. If you won't update certain property, you shouldn't specify it in request.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "id": 1,
  "updated_description": "All Fanta -40%!",
  "updated_discount_percentage": 40,
  "updated_beverages_with_capacities": {
    "Fanta": [0.33]
  }
}
```

#### DELETE /api/v1/promotions/remove-beverage-promotion

**Description:**  
Removes existing beverage promotion.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "id": 1
}
```
-------

#### GET /api/v1/promotions/addon-promotions

**Description:**  
Retrieves all addon promotions, that are currently stored in the database.

#### POST /api/v1/promotions/add-addon-promotion

**Description:**
Add new addon promotion. You can add addon promotion without specifying addon names. Description and discount percentage are necessary fields.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "description": "Jalapeno and Feta -60%!",
  "discount_percentage": 60,
  "addon_names": [
    "Jalapeno",
    "Feta"
  ]
}
```

#### PUT /api/v1/promotions/update-addon-promotion

**Description:**
Update existing addon promotion. You can update addon promotion without specifying any property except id. Id is necessary to identify proper promotion. If you won't update certain property, you shouldn't specify it in request.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "id": 1,
  "updated_description": "Herbs -10%!",
  "updated_discount_percentage": 10,
  "updated_addon_names": [
    "Herbs"
  ]
}
```

#### DELETE /api/v1/promotions/remove-addon-promotion

**Description:**  
Removes existing addon promotion.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "id": 1
}
```


## 📋 Requirements
- Java 21 (or higher)
- Docker
- ng
- npm

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

Navigate to the frontend directory

```bash
cd /path/to/miesiany-miesiany-kebab/frontend
```

Install dependencies

```bash
npm install
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

