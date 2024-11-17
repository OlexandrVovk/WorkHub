# Task Management System API

A comprehensive task management system built with Spring Boot, featuring project management, task tracking, and email notifications.

## API Endpoint

The API is publicly available at:
```
http://20.215.40.193:8080
```
You can interact with the API directly through this endpoint.


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

### 1. Build docker container
Build a file named `docker-compose.yml`
```bash
docker-compose build
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
