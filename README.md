# Bookmark API
 
A production-grade RESTful API for managing bookmarks, built with Java 21 and Spring Boot 3.x. Users can register an account, organize bookmarks into categories, label them with tags, and search or filter their collection. The project demonstrates real-world backend engineering practices including JWT authentication, soft deletes, pagination, input validation, and database migrations.
 
---
 
## Tech Stack
 
| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.14 |
| Build Tool | Gradle (Groovy DSL) |
| Database | PostgreSQL 16 |
| Migrations | Flyway |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Documentation | SpringDoc OpenAPI / Swagger UI |
| Containerization | Docker |
| Secrets Management | spring-dotenv |
 
---
 
## Features
 
- User registration and login with JWT authentication
- Bookmark CRUD with soft delete (records are hidden, not removed)
- Categories to organize bookmarks (one category per bookmark)
- Tags with many-to-many relationship to bookmarks
- Keyword search across bookmark title and URL (case-insensitive)
- Filter bookmarks by category or tag
- Pagination and sorting on all list endpoints
- Input validation with password strength enforcement
- Global exception handling with structured JSON error responses
- Swagger UI with bearer token authorization for interactive testing
---
 
## Prerequisites
 
Before running the project locally, make sure you have the following installed:
 
- **Java 21** - <a href = "https://adoptium.net" target = "_blank" rel = "noopener noreferrer">Download here</a>
- **Docker** - <a href = "https://docker.com/products/docker-desktop" target = "_blank" rel = "noopener noreferrer">Download here</a>
- **Git**
---
 
## Local Setup
 
### 1. Clone the repository
 
```bash
git clone https://github.com/small-python/bookmark-api.git
cd bookmark-api
```
 
### 2. Create your environment file
 
Copy the example environment file and fill in your values:
 
```bash
cp .env.example .env
```
 
Open `.env` and set the following values:
 
```env
DB_URL=jdbc:postgresql://localhost:5432/bookmark_db
DB_USERNAME=bookmark_user
DB_PASSWORD=your_password
JWT_SECRET=your_jwt_secret_key_must_be_at_least_32_characters
JWT_EXPIRATION=86400000
```
 
> The JWT secret must be at least 32 characters long. You can generate one at <a href = "https://randomkeygen.com/" target = "_blank" rel = "noopener noreferrer">randomkeygen.com</a>
 
### 3. Start the database
 
```bash
docker-compose up -d
```
 
This starts a PostgreSQL 16 container on port 5432. Flyway will automatically run all migrations when the application starts.
 
### 4. Run the application
 
```bash
./gradlew bootRun
```
 
The application starts on port `8080`. You should see Flyway migration logs followed by the Spring Boot startup banner.
 
---
 
## API Documentation
 
Swagger UI is available at:
 
```
http://localhost:8080/swagger-ui/index.html
```
 
### How to test protected endpoints in Swagger UI
 
1. Open Swagger UI
2. Use `POST /api/v1/auth/register` to create an account
3. Use `POST /api/v1/auth/login` to log in and copy the `token` from the response
4. Click the **Authorize** button at the top right
5. Paste the token and click **Authorize**
6. All protected endpoints are now unlocked for testing
---
 
## API Endpoints
 
### Authentication
 
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/v1/auth/register` | Register a new user | No |
| POST | `/api/v1/auth/login` | Login and receive JWT token | No |
 
### Categories
 
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/categories` | Get all categories for current user | Yes |
| GET | `/api/v1/categories/{id}` | Get a category by ID | Yes |
| POST | `/api/v1/categories` | Create a new category | Yes |
| PUT | `/api/v1/categories/{id}` | Update a category | Yes |
| DELETE | `/api/v1/categories/{id}` | Delete a category | Yes |
 
### Tags
 
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/tags` | Get all tags for current user | Yes |
| GET | `/api/v1/tags/{id}` | Get a tag by ID | Yes |
| POST | `/api/v1/tags` | Create a new tag | Yes |
| PUT | `/api/v1/tags/{id}` | Update a tag | Yes |
| DELETE | `/api/v1/tags/{id}` | Delete a tag | Yes |
 
### Bookmarks
 
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/v1/bookmarks` | Get all bookmarks (paginated) | Yes |
| GET | `/api/v1/bookmarks/{id}` | Get a bookmark by ID | Yes |
| POST | `/api/v1/bookmarks` | Create a new bookmark | Yes |
| PUT | `/api/v1/bookmarks/{id}` | Update a bookmark | Yes |
| DELETE | `/api/v1/bookmarks/{id}` | Soft delete a bookmark | Yes |
| GET | `/api/v1/bookmarks/search?keyword=` | Search by title or URL | Yes |
| GET | `/api/v1/bookmarks/category/{categoryId}` | Filter by category | Yes |
| GET | `/api/v1/bookmarks/tag/{tagId}` | Filter by tag | Yes |
 
### Pagination Parameters
 
All list endpoints support the following query parameters:
 
| Parameter | Default | Description |
|---|---|---|
| `page` | `0` | Page number (zero-indexed) |
| `size` | `20` | Number of results per page |
| `sortBy` | `createdAt` | Field to sort by |
| `sortDir` | `desc` | Sort direction (`asc` or `desc`) |
 
---
 
## Project Structure
 
```
src/main/java/com/ahmed/bookmark/
├── config/          # Security and OpenAPI configuration
├── controller/      # REST controllers
├── dto/
│   ├── request/     # Incoming request bodies
│   └── response/    # Outgoing response bodies
├── entity/          # JPA entities (User, Category, Tag, Bookmark)
├── exception/       # Custom exceptions and global exception handler
├── repository/      # Spring Data JPA repositories
├── security/        # JWT utilities and filters
└── service/         # Business logic
 
src/main/resources/
├── db/migration/    # Flyway SQL migration scripts (V1–V5)
└── application.yaml # Application configuration
```
 
---
 
## Postman Collection
 
A Postman collection is included in the repository at `postman/bookmark-api.json`.
 
Import it into Postman and create an environment with a `token` variable. The login request includes a test script that captures the JWT token automatically after a successful login.
 
---
 
## Author
 
**Ahmed Yinusa**
Java Backend Developer
<a href = "https://github.com/small-python" target = "_blank" rel = "noopener noreferrer">GitHub</a> · yinusaahmed80@gmail.com
