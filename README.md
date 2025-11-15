📘 TuterNity Backend

A complete backend service for the TuterNity online tutoring platform, built with Spring Boot, JWT-based authentication, PostgreSQL (NeonDB), and Stripe for payment processing.
This service handles user management, teacher–student interactions, bookings, secure payments, notifications, and more.

🚀 Features
🔐 Authentication & Authorization

JWT-based login and registration

Role-based access (Student / Teacher / Admin)

👨‍🏫 Teacher & Student Management

Teacher profile creation and updates

Student profile management

Fetch teacher availability & hourly rates

📅 Booking System

Create, update, and manage session bookings

Prevent double-booking

Automatic status updates (Pending → Paid / Cancelled)

💳 Payment Integration (Stripe)

Stripe Checkout Session API

Automatic payment verification

Record transactions in database

Status sync between Booking & Payment

📬 Notifications

Email notifications for bookings & updates

Receipt confirmation for successful payments

🌐 REST API Architecture

Clean and well-structured controller layout

Consistent response models

Proper error handling

🛠️ Technologies Used
Backend

Java 17

Spring Boot 3

Spring Security (JWT)

Spring Data JPA / Hibernate

PostgreSQL (NeonDB)

Maven for build management

Integrations

Stripe API (Payments)

Java Mail Sender

Docker (Deployment)

Render (Hosting)

📌 Prerequisites

Before running the project, ensure you have:

JDK 17+ installed

Maven 3.8+

PostgreSQL / NeonDB

An IDE (IntelliJ IDEA / Eclipse / VS Code)

⚙️ Installation & Setup
1️⃣ Clone the Repository
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

2️⃣ Configure Database

Create a new PostgreSQL / NeonDB database.

Example:

CREATE DATABASE tuternity_db;

3️⃣ Update Application Properties

Edit:

src/main/resources/application.properties

# Database Configuration
spring.datasource.url=jdbc:postgresql://<your-db-url>/tuternity_db
spring.datasource.username=<db-username>
spring.datasource.password=<db-password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Secret
jwt.secret=<your-secret-key>

# Stripe Secret Key
stripe.api.key=<your-stripe-secret-key>

# Email Config
spring.mail.username=<your-email>
spring.mail.password=<your-password>

4️⃣ Run the Application

Using Maven:

mvn spring-boot:run


Or run directly:

java -jar target/tuternity-backend-0.0.1-SNAPSHOT.jar


The application will start at:

👉 http://localhost:8081

🔌 API Endpoints
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register new user
POST	/api/auth/login	Login & get JWT token
👨‍🏫 Teachers
Method	Endpoint	Description
GET	/api/teachers	Get all teachers
GET	/api/teachers/{id}	Get teacher profile
PUT	/api/teachers/{id}	Update teacher details
👨‍🎓 Students
Method	Endpoint	Description
GET	/api/students/{id}	Student profile
PUT	/api/students/{id}	Update student info
📅 Bookings
Method	Endpoint	Description
POST	/api/bookings	Create new booking
GET	/api/bookings/student/{id}	Student bookings
GET	/api/bookings/teacher/{id}	Teacher bookings
💳 Payments (Stripe)
Method	Endpoint	Description
POST	/api/stripe/create-checkout-session/{bookingId}	Create Stripe session
GET	/api/stripe/success/{bookingId}	Verify success
GET	/api/stripe/cancel/{bookingId}	Handle cancellation
📂 Project Structure
Tuternity-Backend/
│── src/
│   └── main/
│       ├── java/com/tuternity/backend/
│       │   ├── controller/     # API Controllers
│       │   ├── model/          # Entity Models
│       │   ├── repository/     # JPA Repositories
│       │   ├── service/        # Business Logic
│       │   ├── security/       # JWT Config & Filters
│       │   └── dto/            # Response/Request DTOs
│       └── resources/
│           ├── application.properties
│           └── static/
│── Dockerfile
│── pom.xml
└── README.md

📬 Contact

Anas Syed

GitHub: https://github.com/anas2k5

Email: you can add your email if you want
