# Job Management API

A Spring Boot REST API for managing job postings with JWT authentication.

## Features

- User registration and authentication
- JWT token-based security
- CRUD operations for job posts
- Job search functionality
- Token blacklisting for secure logout

## Tech Stack

- **Framework**: Spring Boot 3.x
- **Database**: PostgreSQL
- **Security**: Spring Security with JWT
- **ORM**: JPA/Hibernate
- **Build Tool**: Maven

## API Endpoints

### Authentication
- `POST /register` - Register a new user
- `POST /login` - Login and get JWT token
- `POST /logout` - Logout and blacklist token
- `GET /blacklistSize` - Get blacklist size (debug)

### Job Management
- `GET /allJobs` - Get all job posts
- `GET /jobPost/{id}` - Get job by ID
- `POST /jobPost` - Create new job post
- `PUT /jobPost` - Update job post
- `DELETE /jobPost/{id}` - Delete job post
- `GET /jobPost/search?keyword=value` - Search jobs
- `GET /load` - Load sample data

## Setup

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### Database Setup
1. **Create PostgreSQL database**:
   ```sql
   CREATE DATABASE job_management_db;
   ```

2. **Configure Database Connection**:
   Copy `application.properties.example` to `application.properties` and update with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/job_management_db
   spring.datasource.username=your_db_username
   spring.datasource.password=your_db_password
   ```

### Installation
1. **Clone the repository**
   ```bash
   git clone https://github.com/your-username/job-management-api.git
   cd job-management-api
   ```

2. **Configure Database** (see Database Setup above)

3. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

The application will start on `http://localhost:8080`

## Usage

1. **Register a user**:
   ```bash
   curl -X POST http://localhost:8080/register \
   -H "Content-Type: application/json" \
   -d '{"username":"john","password":"pass123"}'
   ```

2. **Login to get JWT token**:
   ```bash
   curl -X POST http://localhost:8080/login \
   -H "Content-Type: application/json" \
   -d '{"username":"john","password":"pass123"}'
   ```

3. **Use the token for authenticated requests**:
   ```bash
   curl -X GET http://localhost:8080/allJobs \
   -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

## Sample Job Post Data

```json
{
  "postId": 1,
  "postProfile": "Software Engineer",
  "postDesc": "Develop and maintain software applications.",
  "reqExperience": 3,
  "postSkills": ["Java", "Spring Boot", "SQL"]
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
