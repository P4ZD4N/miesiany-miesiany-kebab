
# 💻 Miesiany Miesiany Kebab

![](./images/logo.png)

## 📜 Table of Contents

1. [👀 About](#about)
2. [🔧 Tech Stack](#tech-stack)
3. [💡 Features](#features)
4. [🔗 API](#api)
5. [📋 Requirements](#requirements)
6. [🌍 Configuration](#configuration)
7. [▶️ Run](#run)
8. [⚡ Usage](#usage)

## 👀 About

Gastronomic industry plays an important role in the economy. Nowadays many gastronomic businesses, which exists in both big cities and small villages are often visited by people. For this reason, I decided to create full-stack application, which can solve problems typical for this industry. Based on my own preferences, I chose a kebab restaurant, but all functionalities, which I implemented can be also applied to other types of restaurants. Application is intended to serve as a business card, to reach more customers and to encourage them to stay longer, but also to enable order accomplishment and tracking. It take care of availability of all valuable for customer information in a convenient way. System is also convenient for employees of restaurant. It enable to manage restaurant, handle orders and customers in more efficient way. Majority of components visible on website are easy to customize and update by manager.

## 🔧 Tech Stack

**Backend:** Java 21, Spring Boot 3, Spring Security, Spring Data, Hibernate, Thymeleaf, Lombok

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
- Job board, which enable to publish dateiled job offers by manager. Manager can add job offer with information such as position name, description, list of mandatory requirements, list of nice to have requirements, list of employment types and hourly wage gross. Once added, each job offer is fully customizable. Website guests can apply to each job offer by fulfilling form (with attaching a CV in PDF/DOC format). Manager has possibility to display all candidates, which applied to job offer, remove those who do not fit the position or peek/download attached CV of desired candidate.
- Promotions for meals, beverages and addons. It is possible to display promotions details in proper section on website by all users. If some menu position is already added to certain promotion, users can see price change in menu section on website. All promotions are editable and easy to maintain by manager. Manager can add, update and delete promotions.
- Multilingual newsletter with email verification implemented with usage of Observer design pattern. Each customer has possibility to sign up to newsletter focused on promotions and choose preffered language of email messages (Polish or English). When manager adds some promotion, then email is sent to all verified subscribers. Some methods were created as asynchronous to enhance application preformance. It is also possibility to unsubscribe newsletter at any time.
- Discount codes with which customers can reduct price of order. Each discount code has its expiration date and number of remaining uses. Such codes are automatically generated and sending to customers, who provide their email adresse during ordering process - either after every 10 orders or when the order total price exceeds 100 PLN. Manager can add, update and delete discount codes (allowing for manual distribution as well). 
- Possibility to place order by customers in easy and concise way. Each menu position can be selected and added to such order and partially customized (by choosing size, meat, sauce, quantity or capacity). After adding item, customer may continue adding more items, proceed to the next step, or come back later - all order details are saved in local storage. Next step is choosing preferred delivery method: pickup at the restaurant or home delivery. Depending on the choice, a dedicated panel is shown to collect the necessary information. Finally, the customer has the option to leave additional comments and enter a discount code, if available, to receive a price reduction. Managers and employees can add, update and delete orders.
- Track order panel, where customers can easily monitor status of their order in real time. Here they can find information such as order id (number of order displayed on the screen in restaurant), total price, delivery address (if home delivery method was selected), payment methods, ordered items details and current order status. Customers can access this panel for up to two hours after the last update to their order.
- Order status display for in-restaurant screen, which shows numbers of orders currently being prepared (in gray color) and those, that are ready (in green color). This provides clear information to customers waiting in the restaurant, improving communication and overall experience.
- Work schedule panel which allows managers to assign shifts for all employees for a selected month and year. All employees can display and then download a printable PDF document that clearly displays the shift schedule.
- Employee management panel where manager can add, update or remove employees.
- Employee and manager panels which allow authenticated users to view their current contact information and employment details. Each panel includes an actions section with buttons that provide functions available to the particular account type (employee or manager). Both employees and managers can also update their email address and password.

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
Add new addon. It is not possible to add addon with the same name of existing. For example, if db already stores Zapiekanka, you can't add Zapiekanka second time.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "new_addon_name":  "Zapiekanka",
  "new_addon_price":  12
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
  "updated_addon_name":  "Zapiekanka",
  "updated_addon_price":  12
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
  "name":  "Zapiekanka"
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

**Rate Limit without appropriate roles**
1 request / 5 minutes

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
  "description": "Zapiekanka -60%!",
  "discount_percentage": 60,
  "addon_names": [
    "Zapiekanka"
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
  "updated_description": "Zapiekanka -10%!",
  "updated_discount_percentage": 10,
  "updated_addon_names": [
    "Zapiekanka"
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

### NewsletterController
 
#### GET /api/v1/newsletter/subscribers

**Description:**  
Retrieves all subscribers, that are signed up to newsletter.

**Authorization Requirements:**
- Role: `MANAGER`

#### POST /api/v1/newsletter/subscribe

**Description:**
Subscribe newsletter. Customer must specify first name, email and newsletter messages language

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Rate Limit without appropriate roles**
1 request / 5 minutes

**Request Body:**
```json
{
  "first_name": "Wiktor",
  "email": "example@example.com",
  "messages_language":"POLISH" 
}
```

#### PUT /api/v1/newsletter/verify-subscription

**Description:**  
Verifies subscription with OTP code. Verification is mandatory, because email messages are sent only to verified subscribers.


**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "email": "example@example.com",
  "otp": 123456
}
```

#### PUT /api/v1/newsletter/regenerate-otp

**Description:**  
Regenerates OTP code. OTP can be regenerated only at certain intervals.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "email": "example@example.com"
}
```

#### POST /api/v1/newsletter/unsubscribe

**Description:**
Unsubscribe newsletter. Customer can sign out from newsletter at any time.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "email": "example@example.com"
}
```

### DiscountCodesController
 
#### GET /api/v1/discount-codes/all

**Description:**  
Retrieves all discount codes.

**Authorization Requirements:**
- Role: `MANAGER`

#### GET /api/v1/discount-codes/{code}

**Description:**  
Retrieves specific discount code based on the code provided in the URL path.

#### POST /api/v1/discount-codes/add-discount-code

**Description:**
Add new discount code. It is not possible to add discount code with the same code as existing. For example, if db already stores discount code with code '123', you can't add 123 second time.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "code": "kodzik",
  "discount_percentage": 20.00,
  "expiration_date": "2025-12-01",
  "remaining_uses": 2
}
```

#### PUT /api/v1/discount-codes/update-discount-code

**Description:**  
Updates existing discount code. You can specify updated code, discount percentage, expiration date or remaining uses as you want, but it is necessary to enter name of existing code to identify it.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "code": "kodzik",
  "updated_code": "kodzik1"
  "updated_discount_percentage": 20.00,
  "updated_expiration_date": "2025-12-01",
  "updated_remaining_uses": 2
}
```

#### DELETE /api/v1/discount-codes/remove-discount-code

**Description:**  
Removes existing discount code.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "code": "kodzik"
}
```

### OrdersController
 
#### GET /api/v1/orders/all

**Description:**  
Retrieves all orders stored in database.

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

#### POST /api/v1/orders/add-order

**Description:**
Add new order. Necessary fields are order type and order stastus, the rest is optional.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Rate Limit without appropriate roles**
1 request / 5 minutes

**Request Body:**
```json
{
  "order_type": "ON_SITE",
  "order_status": "IN_PREPARATION",
  "customer_phone": "123456789",
  "customer_email": "example@example.com",
  "street": "Szkolna",
  "house_number": 1,
  "postal_code": "00-000",
  "city": "Warszawa",
  "additional_comments": "...",
  "meals": {
    "Pita Kebab Salads and Fries_Chicken_BBQ Sauce": {
      "SMALL": 2,
      "LARGE": 1
    }
  },
  "beverages": {
    "Coca-Cola": {
        "0.33": 1
    },
    "Fanta": {
        "0.33": 2
    }
  },
  "addons": {
    "Zapiekanka": 1
  }
}
```

#### POST /api/v1/orders/track-order

**Description:**
Returns information about order. Both id and customer phone fields are necessary to get access to these information.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
    "id": 4,
    "customer_phone": "123456789"
}
```

#### PUT /api/v1/orders/update-order

**Description:**  
Updates existing order. You can specify all fields as you want, but it is necessary to enter id of existing order to identify it.

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
    "id": 4,
    "updated_order_type": "ON_SITE",
    "updated_order_status": "IN_PREPARATION",
    "updated_customer_phone": "123456789",
    "updated_customer_email": "example@example.com",
    "updated_street": "Szkolna",
    "updated_house_number": 1,
    "updated_postal_code": "00-000",
    "updated_city": "Warszawa",
    "updated_additional_comments": "...",
    "updated_meals": {
        "Pita Kebab Salads and Fries_Chicken_BBQ Sauce": {
        "SMALL": 2,
        "LARGE": 1
        }
    },
    "updated_beverages": {
        "Coca-Cola": {
            "0.33": 1
        },
        "Fanta": {
            "0.33": 2
        }
    },
    "updated_addons": {
        "Zapiekanka": 5
    }
}
```

#### DELETE /api/v1/orders/remove-order

**Description:**  
Removes existing order.

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

**Request Body:**
```json
{
  "id": 4
}
```

### EmployeesController
 
#### GET /api/v1/employees/all

**Description:**  
Retrieves all employees stored in database.

**Authorization Requirements:**
- Role: `MANAGER` 

#### GET /api/v1/employees/current

**Description:**  
Retrieves current authenticated employee data.

#### POST /api/v1/employees/add-employee

**Description:**
Add new employee.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Authorization Requirements:**
- Role: `MANAGER` 

**Request Body:**
```json
{
  "first_name":  "Jan",
  "last_name":  "Kowalski",
  "email":  "jan.kowalski@example.com",
  "password":  "haslo123",
  "phone":  "123456789",
  "job":  "Cook",
  "hourly_wage":  35.50,
  "date_of_birth":  "1990-05-15",
  "employment_type":  "PERMANENT"
}
```

#### PUT /api/v1/employees/update-employee

**Description:**  
Updates existing employee.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "employee_email": "jan.kowalski@example.com",
  "updated_first_name": "Janusz",
  "updated_last_name": "Kowalski-Nowak",
  "updated_email": "janusz.nowak@example.com",
  "updated_password": "noweHaslo123",
  "updated_phone": "987654321",
  "updated_job": "Manager",
  "updated_hourly_wage": 35.75,
  "updated_date_of_birth": "1988-03-22",
  "updated_employment_type": "PERMANENT",
  "updated_active": true,
  "updated_hired_date": "2020-01-15"
}
```

#### PUT /api/v1/employees/update-credentials

**Description:**  
Updates current logged in employee's credentials. It is possible to update email or password.

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Request Body:**
```json
{
  "updated_email": "nowy.email@example.com",
  "password": "stareHaslo123",
  "updated_password": "noweHaslo456"
}
```

#### DELETE /api/v1/employees/remove-employee

**Description:**  
Removes existing employee.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "email": "jan.kowalski@example.com"
}
```

### WorkScheduleController
 
#### GET /api/v1/work-schedule/all-entries

**Description:**  
Retrieves all work schedule entries stored in database.

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

#### GET /api/v1/work-schedule/get-work-schedule-pdf

**Description:**  
Retrieves current authenticated employee data.

**Request Params:**
- `startDate`: Start date of the work schedule period.
	* Type: `LocalDate`
    * Required: Yes
    * Example: `2025-09-01`
- `endDate`: End date of the work schedule period.
	 * Type: `LocalDate`
	 * Required: Yes
	 * Example: `2025-09-30`

**Authorization Requirements:**
- Role: `MANAGER` or `EMPLOYEE`

#### POST /api/v1/work-schedule/add-entry

**Description:**
Add new work schedule entry.

**Request Headers:**
- `Accept-Language`: Specifies preferred language for the response. Default is `pl` (Polish). You can also set `en` (English).

**Authorization Requirements:**
- Role: `MANAGER` 

**Request Body:**
```json
{
  "employee_email": "jan.kowalski@example.com",
  "date": "2025-09-15",
  "start_time": "09:00",
  "end_time": "17:00"
}
```

#### DELETE /api/v1/work-schedule/remove-employee

**Description:**  
Removes existing work schedule entry.

**Authorization Requirements:**
- Role: `MANAGER`

**Request Body:**
```json
{
  "id": 3
}
```

## 📋 Requirements
- Java 21 (or higher)
- Docker
- ng
- npm

## 🌍 Configuration

### Frontend

To run this project and ensure, that app will work properly, you need to add the following line to your `.env ` file. You need it to see the map with restaurant location on home page.

- `MAP_API_KEY=<YOUR_API_KEY>`

Do it step by step:
1. Navigate to `https://developer.tomtom.com/` and log in to your account. Then create new Map Display API key and copy it.
2. Create `.env` file on the following path: `/miesiany-miesiany-kebab/frontend/src/.env`
3. Add previously copied API key to `.env` file

How to make name of whatever record saved in database multilingual? Just enter frontend/public/assets/i18n and add relevant name in both **en.json** and **pl.json** files. For example you would like to store addon with name Fries in English and Frytki in Polish. Then you should add "menu.addons.Fries" key in en.json and "menu.addons.Frytki" in pl.json.

### Backend

You need to add the following lines to your `application.properties` file to make newsletter work properly.

- `spring.mail.host=<MAIL_HOST>`
- `spring.mail.port=<MAIL_PORT>`
- `spring.mail.username=<MAIL_USERNAME>` 
- `spring.mail.password=<APP_PASSWORD>`

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

