# AI Shop - Eureka Server

This is a simple Eureka Server implementation using Spring Cloud Eureka. This is required for local development and testing of the services in this project.

The Eureka Server in the `java-ai` project provides a robust service discovery mechanism, enabling microservices to register, discover, and communicate with each other dynamically for the local development. Its features like client-side caching, heartbeats, and resilience ensure that the system remains operational even in the face of server failures or network partitions.

Start the server using the following command in folder `src/eureka-server/`:

```bash
./mvnw spring-boot:run
```

The Eureka Server is available under: http://localhost:8761
