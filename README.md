# 🐕 PawAlert

[![Java Version](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📍 Project Overview

**PawAlert** is a comprehensive REST API built with **Spring Boot 4.0.2** designed to help pet owners and communities manage alerts for lost and found pets. The system enables users to create, search, and subscribe to pet alerts, with real-time notifications delivered via multiple channels including email, Telegram, and Server-Sent Events (SSE).

### Key Capabilities

- 🐕 **Pet Management** - Register and manage pet profiles with images
- 🔔 **Alert System** - Create alerts for lost/found pets with location tracking
- 📍 **Geographic Search** - Find nearby alerts within a configurable radius
- 📧 **Multi-Channel Notifications** - Receive alerts via Email, Telegram, and real-time SSE
- 🖼️ **Image Analysis** - AI-powered pet image validation using Google Cloud Vision
- ☁️ **Cloud Storage** - Image hosting via Cloudinary
- ⚙️ **Admin Dashboard** - Full administrative control over the platform

---

## ⚠️ Experimental Frontend Notice

> **The `frontend-react` application is experimental and may contain bugs or incomplete features.**  
> This is a work in progress and the development team is continuously improving it. Use the backend API for production scenarios.

The frontend is built with React 18, Vite, and Material-UI, providing a modern user interface for interacting with the PawAlert API. However, it should be considered alpha/beta quality.

---

## 📦 Technology Stack

### Backend

| Technology | Version | Purpose |
|------------|---------|---------|
| 🟦 **Java** | 21 | Programming language |
| 🌱 **Spring Boot** | 4.0.2 | Application framework |
| 🐘 **PostgreSQL** | 16 | Primary database |
| 🐰 **RabbitMQ** | 3.13 | Message broker for async notifications |
| 🔐 **JWT** | 0.12.6 | Authentication & authorization |
| 📚 **SpringDoc OpenAPI** | 2.3.0 | API documentation (Swagger) |
| ☁️ **Cloudinary** | 2.0.0 | Image storage and delivery |
| 🔍 **Google Cloud Vision** | 3.35.0 | Image analysis and validation |
| 🧪 **Testcontainers** | 1.19.3 | Integration testing |

### Frontend (Experimental)

| Technology | Version | Purpose |
|------------|---------|---------|
| ⚛️ **React** | 18.2.0 | UI framework |
| ⚡ **Vite** | 7.3.1 | Build tool |
| 🎨 **Material-UI** | 7.3.8 | Component library |
| 🗺️ **Leaflet** | 1.9.4 | Interactive maps |
| 🔶 **TypeScript** | 5.3.0 | Type safety |
| 📡 **Axios** | 1.6.0 | HTTP client |

---

## ⚙️ Architecture

PawAlert follows **Hexagonal Architecture** (Ports & Adapters) with **Domain-Driven Design** patterns, ensuring clean separation of concerns and maintainability.

```
┌─────────────────────────────────────────────────────────────┐
│                      INFRASTRUCTURE                          │
│  ┌─────────────┐ ┌─────────────┐ ┌───────────────────────┐ │
│  │ REST API    │ │ JPA         │ │ Messaging (RabbitMQ)  │ │
│  │ Controllers │ │ Repositories│ │ Notification Adapters │ │
│  └──────┬──────┘ └──────┬──────┘ └───────────┬───────────┘ │
└─────────┼────────────────┼───────────────────┼──────────────┘
          │                │                    │
          ▼                ▼                    ▼
┌─────────────────────────────────────────────────────────────┐
│                      APPLICATION                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │              Use Cases / Services                       │ │
│  │   (AlertService, PetService, UserService, etc.)        │ │
│  └────────────────────────────────────────────────────────┘ │
│                                                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌────────────────────┐  │
│  │ Inbound Ports │  │Outbound Ports│  │ Domain Services    │  │
│  │ (Use Cases)   │  │(Repositories)│  │ (Business Logic)   │  │
│  └──────────────┘  └──────────────┘  └────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
          │                │                    
          ▼                ▼                    
┌─────────────────────────────────────────────────────────────┐
│                        DOMAIN                                 │
│  ┌─────────────┐ ┌─────────────┐ ┌───────────────────────┐ │
│  │ Alert        │ │ Pet         │ │ User                 │ │
│  │ Entities     │ │ Entities    │ │ Entities             │ │
│  └─────────────┘ └─────────────┘ └───────────────────────┘ │
│                                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │              Value Objects & Domain Exceptions          │   │
│  └────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔔 Features

### 🐕 Pet Management
- Create, read, update, and delete pet profiles
- Store pet details: name, species, breed, description, image
- Image validation using Google Cloud Vision AI
- Cloudinary integration for image storage

### 🔔 Alert Management
- Create alerts for lost/found pets
- Track alert status: OPENED → IN_PROGRESS → CLOSED
- Add location coordinates for alerts
- Search and filter alerts by various criteria
- View alert event history

### 📍 Geographic Features
- Search nearby alerts within a configurable radius
- Interactive maps with alert locations
- Location-based alert filtering

### 📧 Notification System
- **Server-Sent Events (SSE)** - Real-time browser notifications
- **Email Notifications** - SMTP with Gmail integration
- **Telegram Bot** - Direct message notifications
- Subscribe/unsubscribe to specific alerts
- User notification preferences (email, Telegram)

### 🔐 User Management
- User registration and authentication
- JWT-based security
- Role-based access control (USER, ADMIN)
- Profile management (username, email, phone, notifications)

### 🖼️ Image Processing
- Image upload to Cloudinary
- AI-powered image analysis with Google Cloud Vision
- Pet species classification (dog/cat/other)
- Image validation for inappropriate content

### ⚙️ Admin Dashboard
- View and manage all users, pets, and alerts
- Delete users, pets, and alerts
- Relaunch notifications for alerts

---

## ⚠️ Configuration Required

> **IMPORTANT:** Certain configuration files contain sensitive credentials and are **NOT committed to the repository**. You must create these files locally before running the application.

### Files to Create Locally

| File | Purpose |
|------|---------|
| `config/pawalert-secrets.properties` | All secrets (database, API keys, tokens) |
| `src/main/resources/google-credentials.json` | Google Cloud service account credentials |
| `.env` (frontend) | Frontend environment variables (if needed) |

### ⚠️ Security Notice

The following files are in `.gitignore` and should **never** be committed:
- `config/pawalert-secrets.properties`
- `src/main/resources/google-credentials.json`
- `frontend-react/.env`
- Any file containing real API keys or passwords

---

## 📋 Configuration Examples

### 1. `config/pawalert-secrets.properties`

Create this file in the `config` directory with the following template:

```properties
# ============================================
# DATABASE CONFIGURATION
# ============================================
spring.datasource.url=jdbc:postgresql://localhost:5432/pawalert
spring.datasource.username=postgres
spring.datasource.password=YOUR_POSTGRES_PASSWORD
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update

# ============================================
# EMAIL CONFIGURATION (Gmail SMTP)
# ============================================
# Using Gmail with App Password
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_GMAIL_APP_PASSWORD
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# ============================================
# TELEGRAM BOT CONFIGURATION
# ============================================
# Get token from @BotFather on Telegram
telegram.bot.token=YOUR_TELEGRAM_BOT_TOKEN

# ============================================
# CLOUDINARY CONFIGURATION
# ============================================
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

### 2. `src/main/resources/google-credentials.json`

Create this file with your Google Cloud service account credentials:

```json
{
  "type": "service_account",
  "project_id": "your-project-id",
  "private_key_id": "your-private-key-id",
  "private_key": "-----BEGIN PRIVATE KEY-----\nYOUR_PRIVATE_KEY\n-----END PRIVATE KEY-----\n",
  "client_email": "your-service-account@your-project.iam.gserviceaccount.com",
  "client_id": "your-client-id",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/your-service-account%40your-project.iam.gserviceaccount.com"
}
```

### 3. Environment Variables (Alternative)

You can also configure via environment variables:

```bash
# Database
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pawalert
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=your_password

# Email
export SPRING_MAIL_USERNAME=your_email@gmail.com
export SPRING_MAIL_PASSWORD=your_app_password

# Telegram
export TELEGRAM_BOT_TOKEN=your_bot_token

# Cloudinary
export CLOUDINARY_CLOUD_NAME=your_cloud_name
export CLOUDINARY_API_KEY=your_api_key
export CLOUDINARY_API_SECRET=your_api_secret
```

---

## 🚀 Quick Start

### Prerequisites

- ☕ **Java 21** or higher
- 🐳 **Docker** and **Docker Compose**
- 📦 **Maven** (included as `mvnw` wrapper)

### Option 1: Using Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/your-repo/pawalert.git
cd pawalert

# Create required configuration files (see Configuration Required section)
# Then start all services:

docker-compose up -d
```

This will start:
- **PostgreSQL** on port 5432
- **RabbitMQ** on ports 5672 (AMQP) and 15672 (Management UI)
- **Backend API** on port 8081
- (Frontend is commented out in docker-compose.yml)

### Option 2: Local Development

```bash
# 1. Start PostgreSQL and RabbitMQ via Docker
docker run -d --name pawalert-postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=pawalert \
  -p 5432:5432 \
  postgres:16

docker run -d --name pawalert-rabbitmq \
  -e RABBITMQ_DEFAULT_USER=pawalert \
  -e RABBITMQ_DEFAULT_PASS=pawalert123 \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.13-management

# 2. Create required config files
# (See Configuration Required section above)

# 3. Build and run the backend
./mvnw clean package -DskipTests
./mvnw spring-boot:run

# 4. Run the frontend (in separate terminal)
cd frontend-react
npm install
npm run dev
```

### Build Commands

```bash
# Compile the project
./mvnw compile

# Package the application
./mvnw package

# Run the application
./mvnw spring-boot:run

# Skip tests during build
./mvnw clean package -DskipTests
```

### Running Tests

```bash
# Run all unit tests
./mvnw test

# Run all tests including integration tests
./mvnw verify

# Run a specific test class
./mvnw test -Dtest=AlertServiceTest
```

---

## 📡 API Endpoints

The API is fully documented with Swagger. Once the application is running:

- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

### Main Endpoint Groups

#### 🔔 Alert Management (`/api/alerts`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/alerts/public/nearby` | Get alerts within radius | Public |
| GET | `/api/alerts/public/active` | Get all active alerts | Public |
| POST | `/api/alerts` | Create new alert | Required |
| GET | `/api/alerts/{id}` | Get alert by ID | Required |
| DELETE | `/api/alerts/{id}` | Delete alert | Required |
| POST | `/api/alerts/{id}/close` | Close alert | Required |
| PATCH | `/api/alerts/{id}/status` | Update status | Required |
| PUT | `/api/alerts/{id}/title` | Update title | Required |
| PUT | `/api/alerts/{id}/description` | Update description | Required |
| GET | `/api/alerts/search` | Search with filters | Required |
| GET | `/api/alerts/{id}/events` | Get alert history | Required |
| GET | `/api/alerts/pets/{petId}/active` | Get pet's active alert | Required |

#### 🔔 Alert Subscriptions (`/api/alerts`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/alerts/{id}/subscribe` | Subscribe to alert | Required |
| DELETE | `/api/alerts/{id}/subscribe` | Unsubscribe | Required |
| GET | `/api/alerts/{id}/subscribed` | Check subscription | Required |
| GET | `/api/alerts/subscriptions/me` | My subscriptions | Required |

#### 👤 User Management (`/api/users`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/users/register` | Register new user | Public |
| GET | `/api/users/{id}` | Get user by ID | Required |
| GET | `/api/users/by-username/{username}` | Get by username | Required |
| GET | `/api/users/by-email/{email}` | Get by email | Required |
| PUT | `/api/users/{id}/change-password` | Change password | Required |
| PUT | `/api/users/{id}/change-username` | Update username | Required |
| PUT | `/api/users/{id}/change-email` | Update email | Required |
| PUT | `/api/users/{id}/email-notifications` | Toggle email notifications | Required |
| PUT | `/api/users/{id}/telegram-notifications` | Toggle Telegram notifications | Required |
| DELETE | `/api/users/{id}` | Delete user | Required |
| GET | `/api/users/admin/all` | Get all users | Admin |

#### 🐕 Pet Management (`/api/pets`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/pets` | Create pet | Required |
| GET | `/api/pets/{id}` | Get pet by ID | Required |
| PATCH | `/api/pets/{id}` | Update pet | Required |
| DELETE | `/api/pets/{id}` | Delete pet | Required |
| GET | `/api/pets/my-pets` | Get my pets | Required |
| POST | `/api/pets/validate-image` | Validate image | Required |

#### 🖼️ Image Processing (`/api/images`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/images/validate` | Validate image | Required |
| POST | `/api/images/upload` | Upload to Cloudinary | Required |
| POST | `/api/images/analyze` | Analyze with Google Vision | Required |
| POST | `/api/images/classify` | Classify pet species | Required |

#### 🔐 Authentication (`/api/auth`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/auth/login` | User login | Public |

#### 📡 Notifications (`/api/notifications`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/notifications/stream` | SSE connection | Required |

#### ⚙️ Admin (`/api/admin`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/api/admin/alerts/{id}/notify` | Relaunch notifications | Admin |

#### 📋 Metadata (`/api/v1/metadata`)
| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/api/v1/metadata/get-metadata` | Get system metadata | Public |

---

## 📁 Project Structure

```
PawAlert/
├── src/main/java/itacademy/pawalert/
│   ├── application/           # Application services and use cases
│   │   ├── alert/            # Alert-related use cases
│   │   │   ├── port/inbound/ # Use case interfaces
│   │   │   ├── port/outbound/# Repository port interfaces
│   │   │   └── service/     # Service implementations
│   │   ├── user/            # User management
│   │   ├── pet/             # Pet management
│   │   ├── image/           # Image processing
│   │   ├── metadata/       # Metadata enums
│   │   └── notification/   # Notification services
│   ├── domain/              # Domain models and business logic
│   │   ├── alert/          # Alert domain
│   │   ├── pet/            # Pet domain
│   │   └── user/           # User domain
│   └── infrastructure/     # Adapters and infrastructure
│       ├── rest/           # REST controllers
│       ├── persistence/    # JPA repositories
│       ├── security/      # JWT, auth filters
│       ├── notifications/ # Notification adapters
│       └── image/         # Image handling
├── src/main/resources/
│   ├── application.properties
│   └── google-credentials.json
├── config/
│   └── pawalert-secrets.properties
├── frontend-react/         # Experimental React frontend
│   ├── src/
│   │   ├── components/    # Reusable UI components
│   │   ├── pages/        # Page components
│   │   ├── services/     # API services
│   │   ├── context/      # React contexts
│   │   └── hooks/        # Custom hooks
│   └── package.json
├── docker-compose.yml
├── Dockerfile
└── pom.xml
```

---

## 🔧 Development Guidelines

### Code Style

The project follows standard Java conventions with specific guidelines:

- **Packages**: Lowercase, singular (e.g., `itacademy.pawalert.domain.alert`)
- **Classes**: PascalCase (e.g., `AlertService`, `AlertController`)
- **Methods**: camelCase (e.g., `createOpenedAlert`, `getAlertById`)
- **DTOs**: Java records with validation annotations

### Testing

- **Unit Tests**: JUnit 5 with Mockito
- **Integration Tests**: Testcontainers for external services
- Run tests with `./mvnw test`

### Logging

- Use appropriate log levels: ERROR, WARN, INFO, DEBUG
- Include contextual information in log messages

---

## 🤝 Contributing

This is an educational project developed as part of the IT Academy bootcamp. Contributions, issues, and feature requests are welcome!

### Getting Help

- Check the [AGENTS.md](AGENTS.md) file for detailed development documentation
- Review API documentation at `/swagger-ui.html` when running
- Check [issues-found.md](issues-found.md) for known issues

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🙏 Acknowledgments

- IT Academy Barcelona - Bootcamp program
- Spring Boot community
- All open-source library maintainers
