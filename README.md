🚀 TuterNity Backend

A complete backend service for the TuterNity Online Tutoring Platform, built with Spring Boot, JWT Authentication, PostgreSQL (NeonDB), Hibernate, and Stripe for secure online payments.

This backend powers authentication, teacher–student interactions, scheduling, bookings, payments, and notifications.

✨ Features

🔐 JWT Authentication & Role-Based Access

👨‍🏫 Teacher & Student Management

📅 Booking System (create, update, cancel)

💳 Stripe Payment Integration (Checkout + success/cancel flow)

📧 Email Notifications

🧱 Layered Architecture (Controller → Service → Repository)

🗄️ PostgreSQL (NeonDB) Cloud Database

🐳 Docker Support

☁️ Render Deployment Ready

📁 Project Structure
tuternity-backend/
│── src/
│   ├── main/
│   │   ├── java/com/smarttutor/backend/
│   │   │   ├── controller/      
│   │   │   ├── model/           
│   │   │   ├── repository/      
│   │   │   ├── service/         
│   │   │   ├── security/        
│   │   │   ├── util/            
│   │   │   └── config/          
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   └── static/
│   ├── test/
│
├── Dockerfile
├── run.sh
├── pom.xml
└── README.md

🛠️ Installation & Setup
1. Clone the Repository
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

2. Configure PostgreSQL (NeonDB)

Create a new Neon project and copy your DB connection URL.

3. Update application.properties
spring.datasource.url=jdbc:postgresql://your-neon-url
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_jwt_secret
jwt.expiration=86400000

stripe.api.key=your_stripe_secret_key

4. Run the Application

Using Maven:

mvn spring-boot:run


Or using the jar:

java -jar target/tuternity-backend.jar

🌐 Local Development URL
http://localhost:8081

📡 API Endpoints
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register user
POST	/api/auth/login	Login & return JWT token
📚 Bookings
Method	Endpoint	Description
POST	/api/bookings	Create a booking
GET	/api/bookings/student/{id}	Get student bookings
GET	/api/bookings/teacher/{id}	Get teacher bookings
💳 Stripe Payments
Method	Endpoint	Description
POST	/api/stripe/create-checkout-session/{bookingId}	Start payment
GET	/api/stripe/success/{bookingId}	Payment verified
GET	/api/stripe/cancel/{bookingId}	Payment canceled
🚀 Deployment (Render)

Dockerized Deployment

Auto-deploy on push

Environment variables configured

Free-tier autosleep

Live Backend URL
https://tuternity-backend.onrender.com

🤝 Contributing

Contributions, issues, and feature requests are welcome!

📬 Contact

Anas Syed
GitHub: https://github.com/anas2k5

Email: (add here)
