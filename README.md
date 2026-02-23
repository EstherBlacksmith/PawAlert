# PawAlert 🐾

A comprehensive pet alert and subscription system built with Spring Boot and React. PawAlert enables users to create, manage, and subscribe to pet-related alerts with real-time notifications and location-based features.

## ⚠️ Frontend Status: EXPERIMENTAL

**The React frontend (`frontend-react/`) is currently in an experimental state.** While the backend API is production-ready and fully functional, the frontend is still under active development and may contain:

- Incomplete features
- UI/UX refinements in progress
- Potential bugs and edge cases
- Ongoing design iterations

**Recommendation:** Use the backend API directly via Swagger/OpenAPI for production integrations. The frontend is suitable for development and testing purposes.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [Project Structure](#project-structure)
- [Features](#features)
- [Configuration](#configuration)
- [Docker Deployment](#docker-deployment)
- [Contributing](#contributing)
- [License](#license)

---

## 🎯 Project Overview

PawAlert is a full-stack application designed to help pet owners and communities stay informed about lost, found, or at-risk pets. The system provides:

- **Alert Management**: Create, update, and close pet alerts
- **Real-time Notifications**: Server-Sent Events (SSE) for instant updates
- **Subscription System**: Users can subscribe to alerts and receive notifications
- **Location-Based Search**: Find nearby alerts using geolocation
- **Image Processing**: Google Vision AI for pet image validation
- **Admin Dashboard**: Comprehensive management interface for administrators
- **User Authentication**: JWT-based secure authentication

---

## 🏗️ Architecture

PawAlert follows **Hexagonal Architecture** (Ports & Adapters pattern) for the backend, ensuring clean separation of concerns and high testability.

### Backend Architecture Layers

```
┌─────────────────────────────────────────────────────────────────┐
│                      EXTERNAL WORLD                             │
│  (REST API, Web UI, Mobile App, External Services)              │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────┐
│              ADAPTERS LAYER (Inbound/Outbound)                  │
│  ┌──────────────────────┐  ┌──────────────────────────────────┐ │
│  │  REST Controllers    │  │  Database Repositories           │ │
│  │  (Driving Adapters)  │  │  (Driven Adapters)               │ │
│  └──────────────────────┘  └──────────────────────────────────┘ │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────┐
│                    PORTS LAYER (Interfaces)                     │
│  ┌──────────────────────┐  ┌──────────────────────────────────┐ │
│  │  Inbound Ports       │  │  Outbound Ports                  │ │
│  │  (Use Cases)         │  │  (Repository Contracts)          │ │
│  └──────────────────────┘  └──────────────────────────────────┘ │
└────────────────────────────┬────────────────────────────────────┘
                             │
┌────────────────────────────┼────────────────────────────────────┐
│              APPLICATION CORE (Domain & Services)               │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  Domain Models (Alert, User, Pet, Subscription)         │   │
│  │  Application Services (Business Logic)                  │   │
│  │  Domain Events (Event-Driven Architecture)              │   │
│  └──────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

### Frontend Architecture

The React frontend uses:
- **Component-Based Architecture**: Modular, reusable components
- **Context API**: State management for authentication and notifications
- **Service Layer**: Centralized API communication
- **Chakra UI**: Modern, accessible component library
- **TypeScript**: Type-safe development

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.2
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate
- **Authentication**: JWT (JSON Web Tokens)
- **API Documentation**: SpringDoc OpenAPI / Swagger
- **Image Processing**: Google Cloud Vision API
- **Cloud Storage**: Cloudinary
- **Email**: Spring Mail with Mailgun
- **Real-time**: Server-Sent Events (SSE)
- **Build Tool**: Maven

### Frontend
- **Framework**: React 18.2
- **Language**: TypeScript 5.3
- **UI Library**: Chakra UI 3.0
- **HTTP Client**: Axios
- **Routing**: React Router 6.20
- **Maps**: Leaflet + React Leaflet
- **Build Tool**: Vite 5.0
- **Icons**: React Icons

### DevOps
- **Containerization**: Docker
- **Orchestration**: Docker Compose
- **CI/CD**: Ready for GitHub Actions

---

## 📦 Prerequisites

### System Requirements
- **Java**: JDK 21 or higher
- **Node.js**: 18.x or higher
- **npm**: 9.x or higher
- **Docker**: 20.x or higher (optional, for containerized deployment)
- **PostgreSQL**: 14.x or higher (or use Docker Compose)

### Required Credentials
- **Google Cloud Vision API**: For image validation
- **Cloudinary Account**: For image storage
- **Mailgun Account**: For email notifications (optional)
- **JWT Secret**: For token generation

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/PawAlert.git
cd PawAlert
```

### 2. Backend Setup

#### Option A: Using Docker Compose (Recommended)

```bash
# Start PostgreSQL and other services
docker-compose up -d

# Install Maven dependencies
mvn clean install

# Run the application
mvn spring-boot:run
```

#### Option B: Local PostgreSQL

```bash
# Create database
createdb pawalert

# Install Maven dependencies
mvn clean install

# Configure environment variables (see Configuration section)

# Run the application
mvn spring-boot:run
```

### 3. Frontend Setup

```bash
cd frontend-react

# Install dependencies
npm install

# Start development server
npm run dev
```

---

## ▶️ Running the Application

### Backend

```bash
# Development mode
mvn spring-boot:run

# Production build
mvn clean package
java -jar target/PawAlert-0.0.1-SNAPSHOT.jar
```

**Backend URL**: `http://localhost:8080`

### Frontend

```bash
cd frontend-react

# Development mode
npm run dev

# Production build
npm run build
npm run preview
```

**Frontend URL**: `http://localhost:5173`

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up --build

# Access the application
# Backend: http://localhost:8080
# Frontend: http://localhost:3000
```

---

## 📚 API Documentation

### Swagger UI

Once the backend is running, access the interactive API documentation:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

### Main Endpoints

#### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

#### Alerts
- `GET /api/alerts/public/active` - Get all active public alerts
- `GET /api/alerts/public/nearby` - Get nearby alerts by location
- `POST /api/alerts` - Create new alert (authenticated)
- `GET /api/alerts/{id}` - Get alert details
- `PUT /api/alerts/{id}` - Update alert (authenticated)
- `DELETE /api/alerts/{id}` - Close alert (authenticated)

#### Subscriptions
- `POST /api/alerts/{alertId}/subscribe` - Subscribe to alert
- `DELETE /api/alerts/{alertId}/subscribe` - Unsubscribe from alert
- `GET /api/subscriptions/my-subscriptions` - Get user's subscriptions

#### Notifications
- `GET /api/notifications/subscribe` - SSE endpoint for real-time notifications

#### Admin
- `GET /api/admin/users` - List all users (admin only)
- `GET /api/admin/alerts` - List all alerts (admin only)
- `DELETE /api/admin/users/{id}` - Delete user (admin only)

For complete API documentation, see [`SWAGGER_GUIDE.md`](SWAGGER_GUIDE.md).

---

## 📁 Project Structure

```
PawAlert/
├── src/
│   ├── main/
│   │   ├── java/itacademy/pawalert/
│   │   │   ├── domain/                    # Domain models (DDD)
│   │   │   ├── application/               # Application services & ports
│   │   │   │   ├── port/
│   │   │   │   │   ├── inbound/          # Use case interfaces
│   │   │   │   │   └── outbound/         # Repository interfaces
│   │   │   │   └── service/              # Business logic
│   │   │   ├── infrastructure/            # Adapters & external integrations
│   │   │   │   ├── rest/                 # REST controllers
│   │   │   │   ├── persistence/          # Database adapters
│   │   │   │   ├── config/               # Spring configuration
│   │   │   │   └── external/             # External service integrations
│   │   │   └── PawAlertApplication.java  # Main application class
│   │   └── resources/
│   │       ├── application.properties     # Configuration
│   │       └── db/migration/              # Database migrations
│   └── test/
│       └── java/itacademy/pawalert/       # Unit & integration tests
│
├── frontend-react/
│   ├── src/
│   │   ├── components/                    # Reusable React components
│   │   │   ├── alerts/                   # Alert-related components
│   │   │   ├── layout/                   # Layout components
│   │   │   ├── map/                      # Map components
│   │   │   └── notifications/            # Notification components
│   │   ├── pages/                        # Page components
│   │   ├── services/                     # API service layer
│   │   ├── context/                      # React Context providers
│   │   ├── hooks/                        # Custom React hooks
│   │   ├── types/                        # TypeScript type definitions
│   │   ├── utils/                        # Utility functions
│   │   ├── App.tsx                       # Main app component
│   │   └── main.tsx                      # Entry point
│   ├── public/                           # Static assets
│   ├── package.json                      # Dependencies
│   └── vite.config.ts                    # Vite configuration
│
├── docker-compose.yml                    # Docker Compose configuration
├── Dockerfile                            # Backend Docker image
├── pom.xml                               # Maven configuration
├── HEXAGONAL_ARCHITECTURE.md             # Architecture documentation
├── SWAGGER_GUIDE.md                      # API documentation
└── README.md                             # This file
```

---

## ✨ Features

### Core Features
- ✅ User authentication with JWT
- ✅ Create and manage pet alerts
- ✅ Real-time notifications via SSE
- ✅ Alert subscription system
- ✅ Location-based alert search
- ✅ Image upload and validation with Google Vision AI
- ✅ Admin dashboard for system management
- ✅ User profile management
- ✅ Pet management

### Advanced Features
- ✅ Automatic subscription when creating alerts
- ✅ Alert status management (OPENED, CLOSED)
- ✅ Closure reason tracking
- ✅ Nearby alerts map visualization
- ✅ Email notifications (optional)
- ✅ Responsive design for mobile and desktop
- ✅ Dark mode support (Chakra UI)

### Experimental Features (Frontend)
- 🔄 Admin dashboard (in development)
- 🔄 Advanced filtering and search
- 🔄 User profile customization
- 🔄 Notification preferences

---

## ⚙️ Configuration

### Backend Configuration

Create a `.env` file in the project root:

```env
# Database
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/pawalert
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update

# JWT
JWT_SECRET=your_very_long_and_secure_secret_key_here_minimum_32_characters

# Google Cloud Vision
GOOGLE_APPLICATION_CREDENTIALS=/path/to/google-credentials.json

# Cloudinary
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret

# Email (Optional)
SPRING_MAIL_HOST=smtp.mailgun.org
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your_email@mailgun.org
SPRING_MAIL_PASSWORD=your_mailgun_password

# Swagger
SPRINGDOC_SWAGGER_UI_ENABLED=true
```

### Frontend Configuration

The frontend automatically connects to the backend at `http://localhost:8080`. To change this, modify the API base URL in [`frontend-react/src/services/api.ts`](frontend-react/src/services/api.ts).

---

## 🐳 Docker Deployment

### Using Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down
```

### Building Custom Docker Images

```bash
# Backend
docker build -t pawalert-backend:latest .

# Frontend
docker build -t pawalert-frontend:latest ./frontend-react

# Run containers
docker run -p 8080:8080 pawalert-backend:latest
docker run -p 3000:3000 pawalert-frontend:latest
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines
- Follow the existing code style
- Write unit tests for new features
- Update documentation as needed
- Ensure all tests pass before submitting PR

---

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🆘 Support & Troubleshooting

### Common Issues

**Issue**: Database connection refused
```bash
# Solution: Ensure PostgreSQL is running
docker-compose up -d postgres
```

**Issue**: JWT token expired
```bash
# Solution: Login again to get a new token
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password"}'
```

**Issue**: Frontend cannot connect to backend
```bash
# Solution: Check CORS configuration and ensure backend is running
# Backend should be accessible at http://localhost:8080
```

For more detailed troubleshooting, see [`SWAGGER_GUIDE.md`](SWAGGER_GUIDE.md#troubleshooting).

---

## 📞 Contact

For questions or issues, please open an issue on GitHub or contact the development team.

---

## 🎓 Learning Resources

- [Hexagonal Architecture Guide](HEXAGONAL_ARCHITECTURE.md)
- [API Documentation](SWAGGER_GUIDE.md)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [React Documentation](https://react.dev)
- [Chakra UI Documentation](https://chakra-ui.com)

---

**Last Updated**: February 2026  
**Version**: 0.0.1-SNAPSHOT  
**Status**: Active Development
