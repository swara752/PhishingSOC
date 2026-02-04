# 🛡️ PhishingSOC – Security Operations Center Simulator

A **Spring Boot–based Phishing Detection & SOC Dashboard backend** built for educational and defensive security research. This project focuses on parsing real `.eml` emails, analyzing phishing indicators, and presenting SOC-style dashboard statistics.

---

## 📸 Screenshots (Add Here)

> 📌 *Replace placeholders with real screenshots once frontend is ready.*

* 🔐 **Admin Login Page**
* 📊 **SOC Dashboard View**
* 📧 **Email Analysis Result**

```
/screenshots
 ├── login.png
 ├── dashboard.png
 └── email-analysis.png
```

---

## 🏗️ Project Structure

```
PhishingSOC/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/soc/phishing/
│   │   │       ├── PhishingSocApplication.java
│   │   │       ├── auth/
│   │   │       ├── dashboard/
│   │   │       ├── email/
│   │   │       └── util/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── sample.eml
│
├── pom.xml
└── README.md
```

---

## 🧰 Tech Stack

| Layer         | Technology           |
| ------------- | -------------------- |
| Backend       | Java 21, Spring Boot |
| Database      | MySQL 8              |
| ORM           | Spring Data JPA      |
| Email Parsing | Jakarta Mail         |
| Build Tool    | Maven                |
| API Style     | REST                 |

---

## ⚙️ Prerequisites

Ensure the following are installed:

* ☕ **Java 21**
* 📦 **Maven 3.9+**
* 🐬 **MySQL 8+**
* 💻 Linux / Windows / macOS

---

## 🛠️ Database Setup

```sql
CREATE DATABASE phishing_db;

CREATE USER 'phish_user'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON phishing_db.* TO 'phish_user'@'localhost';
FLUSH PRIVILEGES;
```

---

## 🔧 Configuration (`application.properties`)

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/phishing_db
spring.datasource.username=phish_user
spring.datasource.password=password123

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
```

---

## ▶️ Running the Project

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/swara752/PhishingSOC.git
cd PhishingSOC
```

### 2️⃣ Build the Project

```bash
mvn clean install
```

### 3️⃣ Run the Application

```bash
mvn spring-boot:run
```

*or*

```bash
java -jar target/PhishingSOC-0.0.1-SNAPSHOT.jar
```

---

## 🌐 API Endpoints

### 🔐 Authentication

`POST /api/auth/login`

### 📊 Dashboard

`GET /api/dashboard/stats`

### 📧 Email Analysis

`POST /api/email/analyze`

---

## 📩 Sample `.eml` File

Place email files in:

```
src/main/resources/sample.eml
```

### Extracted Data

* 📤 Sender Email
* 📨 Subject
* 🔗 URLs
* 🚩 Suspicious Keywords

---

## 🎓 College Project Description

**Project Title:** PhishingSOC – Email Phishing Detection System
**Domain:** Cyber Security / SOC Operations
**Objective:**

To design and implement a backend system that simulates a **Security Operations Center (SOC)** capable of analyzing phishing emails, extracting indicators of compromise, and presenting threat statistics through REST APIs.

**Learning Outcomes:**

* Real-world email parsing using `.eml` files
* Secure backend development using Spring Boot
* SOC-style threat analysis logic
* REST API design
* Database integration with MySQL

---

## 🚀 Deployment Guide (Local / Server)

### On Linux Server

1. Install Java 21 & MySQL
2. Configure `application.properties`
3. Build using Maven
4. Run using `java -jar`
5. (Optional) Use `nohup` or `systemd` for background execution

---
