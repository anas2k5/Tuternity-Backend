Tuternity Backend

This repository contains the backend services for the Tuternity platform.
It is built using Spring Boot and provides REST APIs for authentication, teacher and student management, bookings, and payment processing through Stripe.

Technologies Used

Java 17

Spring Boot

Spring Security (JWT Authentication)

PostgreSQL (NeonDB)

Hibernate / JPA

Stripe API

Maven

Docker

Render (deployment)

Features

User login and registration

Student and teacher role management

Teacher availability and booking system

Stripe Checkout payment integration

Payment verification and cancel flow

Email notifications

Dashboard APIs for teachers and students

Running Locally
Clone the repository
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

Configure environment variables

Create an .env file or set these values in application.properties:

SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=

MAIL_USERNAME=
MAIL_PASSWORD=

JWT_SECRET=

STRIPE_SECRET_KEY=
STRIPE_PUBLISHABLE_KEY=
STRIPE_WEBHOOK_SECRET=

Start the application
./mvnw spring-boot:run


The backend will run at:

http://localhost:8081

Deployment

The backend is deployed on Render using Docker.
The production API base URL is:

https://tuternity-backend.onrender.com/api

Project Structure (Simplified)
src/main/java/com/smarttutor/backend
│
├── controller      → API controllers
├── model           → Entity classes
├── repository      → JPA repositories
├── service         → Business logic
├── config          → Security, CORS, etc.
└── dto             → Request/response objects

Developer

Backend developed and maintained by Anas Syed (anas2k5).
