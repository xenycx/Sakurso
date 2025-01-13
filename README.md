---

# Sakurso

## Table of Contents

1. [Introduction](#introduction)
2. [Project Structure](#project-structure)
3. [Installation](#installation)
4. [Database Setup](#database-setup)
5. [Usage](#usage)
6. [Features](#features)
7. [Use Cases](#use-cases)
8. [IP Address Handling](#ip-address-handling)
9. [Contributing](#contributing)
10. [License](#license)
11. [Contact](#contact)

---

## Introduction

Sakurso is a Spring Boot application for monitoring university lectures. It provides functionalities for managing users, rooms, and lectures.

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── tlat/
│   │           ├── Controller/     # Contains controller classes
│   │           ├── Dto/            # Contains Data Transfer Object classes
│   │           ├── Entity/         # Contains entity classes
│   │           ├── service/        # Contains service classes
│   │           └── tlat.java       # Main application class
│   └── resources/
│       ├── application.properties  # Configuration file
│       └── templates/              # Contains HTML templates
└── test/
```

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/xenyc1337/Sakurso.git
   ```
2. Navigate into the project directory:
   ```bash
   cd Sakurso
   ```
3. Build the project:
   ```bash
   ./mvnw clean install
   ```

## Database Setup

1. Configure the database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/sakurso
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.jpa.hibernate.ddl-auto=update
   ```

2. Create the database schema:

   ```sql
   CREATE DATABASE sakurso;
   ```

   The application will create the necessary tables automatically on startup.

## Usage

1. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

2. Access the application at `http://localhost:8080`.

## Features

- User registration and login
- Room management
- Lecture scheduling and monitoring

## Use Cases

### User Management
- Admins can add, edit, and delete users.
- Users can register, log in, and view their details.

### Room Management
- Admins can add, edit, and delete rooms.
- Each room has a unique room number and IP address.

### Lecture Management
- Admins and users can schedule lectures.
- Lectures include details such as room number, date, time, lecturer, and status.
- Users can import lecture schedules from a CSV file.

## IP Address Handling

The application includes functionality to retrieve the IP address of the client making requests. The `RoomController` class handles the extraction of the IP address from the request headers.

```java
@GetMapping("/ip")
@ResponseBody
public ResponseEntity<String> getLocalIpAddress(HttpServletRequest request) {
    String ipAddress = extractIpAddress(request);
    return ResponseEntity.ok(ipAddress);
}

private String extractIpAddress(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isEmpty()) {
        return forwardedFor.split(",")[0].trim();
    }
    String[] headerNames = {
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_REAL_IP"
    };
    for (String headerName : headerNames) {
        String header = request.getHeader(headerName);
        if (header != null && !header.isEmpty() && !"unknown".equalsIgnoreCase(header)) {
            return header;
        }
    }
    String remoteAddr = request.getRemoteAddr();
    if ("0:0:0:0:0:0:0:1".equals(remoteAddr)) {
        remoteAddr = "127.0.0.1";
    }
    return remoteAddr;
}
```

## Contributing

Contributions are welcome! Please submit a pull request or open an issue for any bugs or feature requests.

## License

This project is licensed under the MIT License.

## Contact

For any questions or support, please contact the repository owner.

---
