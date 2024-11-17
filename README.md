# Task Management System API

A comprehensive task management system built with Spring Boot, featuring project management, task tracking, and email notifications.

## Features
- Project management (create, update, delete projects)
- Task tracking with priority and status
- User management and role-based access control
- Real-time email notifications via RabbitMQ
- Firebase authentication integration
- PostgreSQL database for data persistence

## Prerequisites
- Docker and Docker Compose
- Port 5432 (PostgreSQL), 5672 & 15672 (RabbitMQ), 8080 (Task Service), and 8081 (Notification Service) must be available

## Quick Start

### 1. Create docker-compose.yml
Create a new file named `docker-compose.yml` and copy the following configuration:

```yaml
networks:
  mktxp: {}
services:
  postgres:
    image: postgres:17
    container_name: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: 2004
      POSTGRES_DB: progress-automation-db
    networks:
      mktxp: null
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./task-service/src/main/resources/db/init:/docker-entrypoint-initdb.d
    ports:
      - "5432:5432"
  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    ports:
      - "5672:5672"
      - "15672:15672"
    networks:
      mktxp: null
    environment:
      - RABBITMQ_DEFAULT_USER=guest
      - RABBITMQ_DEFAULT_PASS=guest
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq
    healthcheck:
      test: ["CMD", "rabbitmq-diagnostics", "-q", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5
  task-service:
    image: vladmarvit/hackaton:progress-automation-back
    container_name: progress-automation-back
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/progress-automation-db
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: 2004
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_RABBITMQ_USERNAME: guest
      SPRING_RABBITMQ_PASSWORD: guest
    networks:
      mktxp: null
    ports:
      - "8080:8080"
    depends_on:
      - postgres
      - rabbitmq
  notification-service:
    image: vladmarvit/hackaton:notification-service
    container_name: notification-service
    environment:
      SPRING_RABBITMQ_HOST: rabbitmq
      SPRING_RABBITMQ_PORT: 5672
      SPRING_RABBITMQ_USERNAME: guest
      SPRING_RABBITMQ_PASSWORD: guest
    networks:
      mktxp: null
    ports:
      - "8081:8081"
    depends_on:
      - postgres
      - rabbitmq
volumes:
  postgres_data:
  rabbitmq_data:
```

### 2. Launch the Services
Open a terminal in the directory containing your `docker-compose.yml` file and run:
```bash
docker-compose up -d
```

This command will:
- Pull all required images from Docker Hub
- Create necessary networks and volumes
- Start all services in the correct order

### 3. Verify Installation
Check if all services are running:
```bash
docker-compose ps
```

## Accessing the Services

### API Documentation
- Swagger UI: http://localhost:8080/swagger-ui.html

### RabbitMQ Management Console
- URL: http://localhost:15672
- Username: guest
- Password: guest

## Authentication
The API uses Firebase Authentication. Include in each request:
- Header: `Authorization: Bearer <firebase-token>`

## Troubleshooting

### Common Issues
1. Port conflicts
```bash
# Check if ports are already in use
netstat -an | grep "5432\|5672\|15672\|8080\|8081"
```

2. Service not starting
```bash
# Check service logs
docker-compose logs <service-name>
```

### Restarting Services
```bash
# Restart all services
docker-compose restart

# Restart specific service
docker-compose restart <service-name>
```

### Cleanup
To stop and remove all containers, networks, and volumes:
```bash
docker-compose down -v
```

## Notes
- All data is persisted in Docker volumes `postgres_data` and `rabbitmq_data`
- The system uses Firebase for authentication - ensure you have proper Firebase configuration
- Email notifications are handled asynchronously via RabbitMQ

For additional support or issues, please contact the development team.
