# PhishingSOC

A Spring Boot-based Security Operations Center (SOC) for phishing email detection and analysis.

## Project Structure

```
PhishingSOC/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/soc/phishing/
│       │       ├── PhishingSocApplication.java     (Spring Boot entry point)
│       │       ├── auth/                           (Authentication module)
│       │       ├── dashboard/                      (Dashboard API)
│       │       ├── email/                          (Email upload & analysis)
│       │       ├── logs/                           (Logging & audit trails)
│       │       └── utils/                          (Helper utilities)
│       └── resources/
│           ├── application.properties              (Configuration)
│           └── uploads/eml/                        (Email uploads directory)
├── frontend/                                        (HTML/CSS/JS frontend)
│   ├── login.html
│   ├── login.css
│   ├── login.js
│   ├── dashboard.html
│   ├── dashboard.css
│   ├── dashboard.js
│   └── assets/
│       └── worldmap.png
└── pom.xml                                          (Maven config)
```

## Requirements

- **Java 11+**
- **Maven 3.6+**

## Quick Start

### Build
```bash
cd PhishingSOC
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`.

### Frontend
Open `frontend/login.html` in a browser for the login interface.

## API Endpoints (Planned)

- **POST** `/api/auth/login` - User login
- **POST** `/api/auth/logout` - User logout
- **GET** `/api/dashboard/stats` - Dashboard statistics
- **GET** `/api/dashboard/threats` - Active threats
- **POST** `/api/email/upload` - Upload EML file
- **GET** `/api/email/analyze/{emailId}` - Analyze email
- **GET** `/api/logs` - Retrieve audit logs
- **POST** `/api/logs/clear` - Clear logs

## Next Steps

1. Implement authentication & JWT tokens
2. Add email parsing & phishing detection algorithms
3. Integrate with database (MySQL/PostgreSQL)
4. Connect frontend to backend APIs
5. Add testing (JUnit, MockMvc)

## Notes

- All controllers and utilities are scaffolded with TODO comments for further development.
- The sample EML file is located at `src/main/resources/uploads/eml/sample-email.eml`.
