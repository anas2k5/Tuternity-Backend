TuterNity Backend

A complete backend service for the TuterNity Online Tutoring Platform, built with Spring Boot, JWT Authentication, PostgreSQL (NeonDB), Hibernate, and Stripe for secure online payments.

This backend powers authentication, teacher–student interactions, scheduling, bookings, payments, and notifications.

🚀 Features

🔐 JWT Authentication & Role-Based Access

👨‍🏫 Teacher & Student Management

📅 Booking System (create, update, cancel)

💳 Stripe Payment Integration
Checkout session + success/cancel status

📧 Email Notifications

🧱 Secure REST API (layered architecture)

🗄️ PostgreSQL (NeonDB)

🐳 Docker Support

☁️ Render Deployment Ready

📁 Project Structure
tuternity-backend/
│── src/
│   ├── main/
│   │   ├── java/com/smarttutor/backend/
│   │   │   ├── controller/      # REST Controllers
│   │   │   ├── model/           # Entities
│   │   │   ├── repository/      # JPA Repositories
│   │   │   ├── service/         # Business Logic
│   │   │   ├── security/        # JWT Security Config
│   │   │   ├── util/            # Helper Classes
│   │   │   └── config/          # App Configurations
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── static/
│   ├── test/                    # Unit Tests
│
├── Dockerfile
├── run.sh
├── pom.xml
└── README.md

🛠 Installation & Setup
1. Clone the Repository
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

2. Configure NeonDB (PostgreSQL)

Create a new NeonDB project and copy the database URL.

3. Update application.properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://your-neon-url
spring.datasource.username=your_user
spring.datasource.password=your_password

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT
jwt.secret=your_jwt_secret
jwt.expiration=86400000

# Stripe
stripe.api.key=your_stripe_secret_key

4. Run the Application
Using Maven:
mvn spring-boot:run

Or run the JAR:
java -jar target/tuternity-backend.jar

🌐 Local Server
http://localhost:8081

📡 API Endpoints
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register user
POST	/api/auth/login	Login & get JWT token
📚 Bookings
Method	Endpoint	Description
POST	/api/bookings	Create booking
GET	/api/bookings/student/{id}	Student bookings
GET	/api/bookings/teacher/{id}	Teacher bookings
💳 Stripe Payments
Method	Endpoint	Description
POST	/api/stripe/create-checkout-session/{bookingId}	Start Stripe checkout
GET	/api/stripe/success/{bookingId}	Payment success
GET	/api/stripe/cancel/{bookingId}	Payment cancelled
🚀 Deployment (Render)

Dockerfile deployment

Auto-deploy on commit

Environment variables added

Free-tier autosleep

Live Backend URL
https://tuternity-backend.onrender.com

🤝 Contributing

Pull requests, issues, and feature requests are welcome.

📬 Contact

Anas Syed
GitHub: https://github.com/anas2k5

Email: (add your email here)
