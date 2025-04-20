# ZipDistance

A Spring Boot application for geolocation-based zip code calculations, supporting H2 and PostgreSQL databases with
multiple profiles. The API is secured with JWT authentication and role-based access control.

## Prerequisites

- **Java 17**: Install [OpenJDK 17](https://adoptium.net/). Verify: `java --version`
- **Maven 3.8+**: Install [Apache Maven](https://maven.apache.org/download.cgi). Verify: `mvn --version`
- **PostgreSQL (Optional)**: For `dev-postgres` and `dev-postgres-full` profiles.
    - Install [PostgreSQL](https://www.postgresql.org/download/) (12+).
    - Create databases: `devdb` and `fulldb`.
- **Git**: Install [Git](https://git-scm.com/downloads) to clone the repo.

## Notes

To use the full postal code dataset, navigate to `src/main/resources/sql` and extract postalcode.zip. The full postal
code SQL file is too large to commit to GitHub, so it is provided in compressed form.

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/zipdistance.git
   cd zipdistance
   ```

2. For PostgreSQL profiles:
    - Ensure PostgreSQL is running.
    - Create databases:
      ```sql
      CREATE DATABASE zipdistance;
      CREATE ROLE testadmin WITH LOGIN PASSWORD 'testadmin' SUPERUSER;
      ```
    - Update `src/main/resources/application-dev-postgres.properties` and `application-dev-postgres-full.properties`
      with your credentials if there is any changes.

## Cleaning

Remove build artifacts:

```bash
mvn clean
```

## Compiling

Compile the project:

```bash
mvn compile
```

## Running

Run with a specific profile using Maven:

- **dev-h2** (H2 simplify the postcode):
  ```bash
  mvn spring-boot:run "-Dspring-boot.run.profiles=dev-h2"
  ```

- **dev-h2-full** (H2 full postcode):
  ```bash
  mvn spring-boot:run "-Dspring-boot.run.profiles=dev-h2-full"
  ```

- **dev-postgres** (PostgreSQL):
  ```bash
  mvn spring-boot:run "-Dspring-boot.run.profiles=dev-postgres"
  ```

- **dev-postgres-full** (PostgreSQL full postcode):
  ```bash
  mvn spring-boot:run "-Dspring-boot.run.profiles=dev-postgres-full"
  ```

Or build and run the JAR:

```bash
mvn clean package
java -jar target/zipdistance-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev-h2
```

Access the app at `http://localhost:8080`.

## Testing

Run tests:

```bash
mvn test
```

## Profiles

| Profile             | Database         | Use Case                         |
|---------------------|------------------|----------------------------------|
| `dev-h2`            | H2               | Simply Postcode With H2          |
| `dev-h2-full`       | H2               | Full Postcode with H2            |
| `dev-postgres`      | PostgreSQL       | Simply Postcode with PostgreSQL  |
| `dev-postgres-full` | PostgreSQL       | Full Postcode with PostgreSQL    |

## Logging

By default it will generate the logs at `logs/log.log`. If you wish to modify the path, you can do so in
the `log4j2-spring.xml`

## Usage

The ZipDistance API is secured with **JWT token authentication** and **role-based access control**. Two default accounts
are provided for testing:

| Username | Email | Password | Role | Permissions |
| --- | --- | --- | --- | --- |
| `admin_user` | `admin@example.com` | `hashed_password1` | Admin | Full access to all endpoints |
| `normal_user` | `normal_user@example.com` | `hashed_password2` | User | `VIEW_POSTAL`, `CALCULATE_DISTANCE` only |

### Authentication

1. **Obtain a JWT Token**:

    - Send a `POST` request to `/api/v1/admin/auth/login` with credentials:

      ```json
      {
        "email": "admin@example.com",
        "password": "hashed_password1",
        "confirmPassword": "hashed_password1"
      }
      ```
    - Response includes a JWT token:

      ```json
      eyJhbGciOiJIUzI1NiIs...
      
      ```

2. **Use the Token**:

    - Include the token in the `Authorization` header for API requests:

      ```
      Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
      ```

### Example API Endpoints

- **View Postal Data** (`VIEW_POSTAL`):

    - `GET /api/v1/admin/postcode/{postalCode}`
    - Accessible by both `admin_user` and `normal_user`.
    - Example:

      ```bash
      curl -H "Authorization: Bearer <token>" http://localhost:8080/api/v1/admin/postcode/AB12 9SP
      ```

- **Calculate Distance** (`CALCULATE_DISTANCE`):

    - `POST /api/v1/admin/postcode/calculate`
    - Accessible by both `admin_user` and `normal_user`.
    - Example:

      ```bash
      curl -H "Authorization: Bearer <token>" -H "Content-Type: application/json" \
           -d '{"postcode1": "AB10 1XG", "postcode2": "AB10 6RN"}' http://localhost:8080/api/v1/admin/postcode/calculate
      ```

- **Manage Postal Data** (Admin only):

    - `POST /api/v1/admin/postcode` (create), `PUT /api/v1/admin/postcode/{id}` (update).
    - Example (create):

      ```bash
      curl -H "Authorization: Bearer <admin-token>" -H "Content-Type: application/json" \
           -d '{"postcode": "ABC 123", "latitude": 40.7128, "longitude": -74.0060}' http://localhost:8080/api/v1/admin/postcode
      ```

### Notes

- Replace `<token>` with the JWT token from the login response.
- Use `admin_user` for full access or `normal_user` for limited access.

## Swagger API Documentation

You can find the API documentation in Swagger
format [here](https://github.com/choong2003/zipdistance/blob/main/src/main/resources/swagger/swagger-api.yaml).

You can also access the Swagger UI at `http://localhost:8080/swagger-ui/index.html` after starting the application.

## Troubleshooting

- **Maven errors**: Verify Java 17 and Maven installation.
- **PostgreSQL issues**: Check database connectivity and credentials.
- **Authentication errors**: Ensure correct email/password and valid JWT token.
- **Logs**: Add `logging.level.org.springframework=DEBUG` to `application-<profile>.properties` for detailed logs.
