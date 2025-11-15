📘 TuterNity Backend

A complete Online Tutoring Platform Backend built with Spring Boot, JWT Authentication, PostgreSQL (NeonDB), Hibernate, and Stripe for secure payments.
This backend handles authentication, scheduling, bookings, payments, teacher–student workflows, and notifications.

🚀 Features

🔐 JWT Authentication & Role-Based Access

👨‍🏫 Teacher & Student Management

📅 Booking System (create, update, cancel)

💳 Stripe Payment Integration (Checkout Session + Success/Cancel)

📧 Email Notifications

🧱 Layered REST API Architecture

🗄️ PostgreSQL (NeonDB) Cloud Database

🐳 Docker Support

☁️ Render Deployment Ready

🏗️ Project Structure
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

⚙️ Installation & Setup
1️⃣ Clone the Repository
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

2️⃣ Configure NeonDB (PostgreSQL)

Create a NeonDB project and copy your DB connection URL.

3️⃣ Update application.properties
spring.datasource.url=jdbc:postgresql://your-neon-url
spring.datasource.username=your_user
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=your_jwt_secret
jwt.expiration=86400000

stripe.api.key=your_stripe_secret_key

4️⃣ Start the Server

Using Maven:

mvn spring-boot:run


Or using JAR:

java -jar target/tuternity-backend.jar

🌐 Server URL
http://localhost:8081

📡 API Endpoints
🔐 Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register user
POST	/api/auth/login	Login & get token
📚 Bookings
Method	Endpoint	Description
POST	/api/bookings	Create booking
GET	/api/bookings/student/{id}	Student bookings
GET	/api/bookings/teacher/{id}	Teacher bookings
💳 Stripe Payments
Method	Endpoint	Description
POST	/api/stripe/create-checkout-session/{bookingId}	Start payment
GET	/api/stripe/success/{bookingId}	Verify payment
GET	/api/stripe/cancel/{bookingId}	Cancel payment
🚀 Deployment (Render)

Dockerfile-based deployment

Auto-deploy on commit

Environment variables stored securely

Free tier autosleep

🔗 Live Backend URL
https://tuternity-backend.onrender.com

🤝 Contributing

Contributions, PRs, and suggestions are welcome.

📬 Contact

Anas Syed
GitHub: https://github.com/anas2k5

Email: (add your email here)
