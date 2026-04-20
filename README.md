
# Online Shopping System

A HKMU COMP3510SEF Software Project Management project simulating a large department store shopping system.  
Built with **Spring Boot**, **JSP**, **MySQL**, **JWT authentication**, and **Maven**.

---

## How to Run the Project in IntelliJ IDEA

### Prerequisites
- **JDK 17** (or 11, but 17 is recommended)
- **MySQL 8.0** installed and running
- **IntelliJ IDEA** (Ultimate or Community Edition with Spring Boot plugin)
- **Maven** (bundled with IDEA is fine)

### Step-by-Step Guide

1. **Clone or download the project**  
   Open IntelliJ and select `File → New → Project from Existing Sources`.  
   Choose the project root folder (where `pom.xml` is located) and select **Maven**.

2. **Create the database**  
   Open MySQL client (e.g., MySQL Workbench or terminal) and run:
   ```sql
   CREATE DATABASE IF NOT EXISTS shopping_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Configure database connection**  
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/shopping_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=root
   spring.datasource.password=your_mysql_password
   ```

4. **Build the project**  
   - Open the Maven tool window (`View → Tool Windows → Maven`).  
   - Click **Reload All Projects** (refresh icon).  
   - Run `clean` then `compile` (or use the Maven lifecycle).

5. **Run the application**  
   - Navigate to `src/main/java/com/example/shoppingsystem/ShoppingSystemApplication.java`.  
   - Right-click the file and select **Run 'ShoppingSystemApplication'**.  
   - Alternatively, click the green triangle in the top right corner.

6. **Access the application**  
   - Open your browser and go to:  
     `http://localhost:8080/api/auth/login`  
   - Use the frontend pages (login, register, product list, cart, orders).

> **Note**: The first time you run the application, Hibernate will automatically update the database schema based on the entity classes (if `spring.jpa.hibernate.ddl-auto=update` is set).

---

## Technology Stack

| Category       | Technology                                          |
|----------------|-----------------------------------------------------|
| Backend        | Spring Boot 2.7.x, Spring MVC, Spring Data JPA     |
| Security       | Spring Security, JWT (jjwt 0.11.5)                |
| Database       | MySQL 8.0, Hibernate (ORM)                         |
| Frontend       | JSP, JSTL, Bootstrap (custom CSS), JavaScript (Ajax) |
| Build Tool     | Maven                                               |
| Utilities      | Lombok, Jackson (JSON)                              |
| Other Features | Deep mode (CSS variable + localStorage), Mock payment/logistics |

---

##  API Documentation (Mock Version)

**Base URL**: `http://localhost:8080/api`  
**Authentication**: JWT Bearer token (except for `/auth/**` endpoints)  
**Response format**:

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 1. Authentication (Public)

| Method | Endpoint            | Description                         | Request Body                                         |
|--------|---------------------|-------------------------------------|------------------------------------------------------|
| POST   | `/auth/register`    | Register a new user                 | `{"username":"test","password":"123","email":"a@b.com"}` |
| POST   | `/auth/login`       | Login and receive JWT token         | `{"username":"test","password":"123"}`               |
| GET    | `/auth/login`       | Show login page (JSP)               | -                                                    |
| GET    | `/auth/register`    | Show registration page (JSP)        | -                                                    |

**Login Response**:
```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGc...",
    "username": "test",
    "role": "USER"
  }
}
```

### 2. Product Module

| Method | Endpoint                      | Description                     | Role     |
|--------|-------------------------------|---------------------------------|----------|
| GET    | `/products`                   | List products (paginated, search) | USER/ADMIN |
| GET    | `/products/{id}`              | Product details                 | USER/ADMIN |
| POST   | `/admin/products`             | Create a new product            | ADMIN    |
| PUT    | `/admin/products/{id}`        | Update a product                | ADMIN    |
| DELETE | `/admin/products/{id}`        | Delete a product                | ADMIN    |

**Query parameters for GET `/products`**:  
`page` (default 1), `size` (default 10), `keyword` (optional), `categoryId` (optional)

### 3. Shopping Cart

| Method | Endpoint              | Description                       |
|--------|-----------------------|-----------------------------------|
| GET    | `/cart`               | View current user's cart          |
| POST   | `/cart/add`           | Add a product to cart             |
| PUT    | `/cart/update`        | Change quantity of a cart item    |
| DELETE | `/cart/remove/{productId}` | Remove a product from cart      |

**Request body for add/update**: `{"productId":101, "quantity":2}`

### 4. Order & Mock Payment

| Method | Endpoint                    | Description                                      |
|--------|-----------------------------|--------------------------------------------------|
| POST   | `/orders`                   | Create an order from current cart               |
| POST   | `/orders/{orderId}/pay`     | Mock payment (simulates success/failure)        |
| GET    | `/orders`                   | List current user's orders                      |
| GET    | `/orders/{orderId}`         | Order details (includes mock logistics)         |

**Create order request body (optional)**:
```json
{ "address": "Dormitory 123" }
```

**Mock logistics** – automatically generated when order status is "已支付" (Paid).  
Example:
```json
"logistics": {
  "company": "Mock Express",
  "trackingNo": "SF1234567890",
  "status": "Shipped",
  "traces": [
    {"time": "2026-04-20 10:00:00", "info": "Order packed"},
    {"time": "2026-04-20 14:00:00", "info": "Courier picked up"}
  ]
}
```

---

##  Sample Test Flow

1. **Register** → `POST /api/auth/register`  
2. **Login** → `POST /api/auth/login` → copy returned `token`  
3. **Set Authorization header** for all subsequent requests:  
   `Authorization: Bearer <your_token>`  
4. **Browse products** → `GET /api/products`  
5. **Add to cart** → `POST /api/cart/add`  
6. **View cart** → `GET /api/cart`  
7. **Create order** → `POST /api/orders`  
8. **Pay order** → `POST /api/orders/1/pay` (replace with actual orderId)  
9. **Check order with logistics** → `GET /api/orders/1`

---

##  Project Structure (Key Folders)

```
src/
├── main/
│   ├── java/com/example/shoppingsystem/
│   │   ├── config/           – Security & JWT config
│   │   ├── controller/       – REST controllers & view controllers
│   │   ├── entity/           – JPA entities
│   │   ├── repository/       – Spring Data JPA repositories
│   │   ├── security/         – JwtAuthenticationFilter, UserDetailsService
│   │   ├── service/          – Business logic (OrderService, etc.)
│   │   └── util/             – JwtUtil, ApiResponse, DTOs
│   └── webapp/
│       ├── WEB-INF/views/    – JSP pages (auth, product, cart, order)
│       └── resources/        – CSS, JS, images
└── pom.xml
```

---

##  Important Notes

- **Mock external services**: Payment and logistics are simulated. No real gateway or tracking API is used.
- **Deep mode**: Implemented in frontend (CSS + JavaScript) – no backend involvement.
- **Product reviews**: Optional feature (table `review` exists, but full CRUD may be implemented later).
- **Role-based access**: Admin endpoints require `role = "ADMIN"` in the database. You can manually set a user to ADMIN.

---

##  Troubleshooting

| Problem                          | Solution                                                                 |
|----------------------------------|--------------------------------------------------------------------------|
| `Access denied (403)` on login page | Make sure `JwtAuthenticationFilter` skips `/api/auth/**` paths (see code). |
| JSP not found                    | Verify that `spring.mvc.view.prefix=/WEB-INF/views/` is set in `application.properties`. |
| Database connection error        | Check MySQL is running, database `shopping_db` exists, and credentials are correct. |
| Maven dependencies not resolved  | Run `mvn clean install` or reload Maven project in IDEA.                 |

---

##  Contributors

Student group project – developed as part of a SoftwareProject Management group project.

Kwok Yu Chun 		s14116911
CHAN Lee Po Billy 	s14212230
Hon Chi Tung 		s12945822
Chung Ho Long 		s14204150
Soriano Max Janwayne Sarmiento s14084841
saaid Danish 	s13696301
Wong kai Yuen  s14155451
Wong Chun Ho   s13486322
Tsui Ching Kit s14104856
Ching Man Chung s14187424

