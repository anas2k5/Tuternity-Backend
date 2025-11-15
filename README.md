# 📘 TuterNity Backend

A complete backend service for the **TuterNity Online Tutoring Platform**, built using **Spring Boot**, **JWT Authentication**, **PostgreSQL (NeonDB)**, **Hibernate**, and **Stripe** for secure online payments.

This backend manages authentication, teacher–student interactions, scheduling, bookings, payments, email notifications, and more.

---

## 🚀 Features

- **JWT Authentication** & Role-Based Access  
- **Teacher & Student Management**  
- **Booking System** (create, update, cancel)  
- **Stripe Payment Integration** (Checkout session)  
- **Email Notifications**  
- **Secure REST API** following clean layered architecture  
- **PostgreSQL (NeonDB)** for cloud-hosted database  
- **Docker Support** for containerized deployment  
- **Render Deployment Ready**

---

## 🛠️ Technologies Used

### **Backend**
- Java 17  
- Spring Boot 3  
- Spring Security + JWT  
- Spring Data JPA (Hibernate)  
- PostgreSQL (NeonDB)  
- Stripe Payments API  
- Maven  

### **Tools & Libraries**
- Lombok  
- ModelMapper  
- Docker  
- Render Cloud Deployment  

---

## 📦 Project Structure
tuternity-backend/
│
├── src/
│ ├── main/
│ │ ├── java/com/smarttutor/backend/
│ │ │ ├── controller/ # REST Controllers
│ │ │ ├── model/ # Entities
│ │ │ ├── repository/ # JPA Repositories
│ │ │ ├── service/ # Business Logic
│ │ │ ├── security/ # JWT Security Config
│ │ │ ├── util/ # Helpers
│ │ │ └── config/ # App Configurations
│ │ └── resources/
│ │ ├── application.properties
│ │ └── static/
│ └── test/ # Unit Tests
│
├── Dockerfile
├── run.sh
├── pom.xml
└── README.md


---

## ⚙️ Installation & Setup

### **1️⃣ Clone the Repository**
```bash
git clone https://github.com/anas2k5/Tuternity-Backend.git
cd Tuternity-Backend

2️⃣ Configure PostgreSQL (NeonDB)

Create a new NeonDB project and get your connection URL.

3️⃣ Update application.properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://your-neon-url
spring.datasource.username=your_user
spring.datasource.password=your_password

# Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Keys
jwt.secret=your_secret_key
jwt.expiration=86400000

# Stripe Key
stripe.api.key=your_stripe_secret_key

4️⃣ Run the Application
mvn spring-boot:run


Server runs on ➜ http://localhost:8081

🔌 API Endpoints (Sample)
Authentication
Method	Endpoint	Description
POST	/api/auth/register	Register user
POST	/api/auth/login	Login & get JWT token
Bookings
Method	Endpoint	Description
POST	/api/bookings	Create booking
GET	/api/bookings/student/{id}	Student bookings
GET	/api/bookings/teacher/{id}	Teacher bookings
Payments (Stripe)
Method	Endpoint	Description
POST	/api/stripe/create-checkout-session/{bookingId}	Start payment
GET	/api/stripe/success/{bookingId}	Verify payment
GET	/api/stripe/cancel/{bookingId}	Cancel payment
☁️ Deployment
Render Deployment Includes

Dockerfile

Auto-build on commit

Environment variables

Free-tier auto sleep

Backend Live URL:
👉 https://tuternity-backend.onrender.com

🤝 Contributing

Contributions, issues, and feature requests are welcome.

📬 Contact

Anas Syed
GitHub: anas2k5
