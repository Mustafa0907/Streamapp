# 🎙️ StreamZone Pro - A Modern Live Streaming and Podcasting Platform

<p align="center">
  <img src="https://your-image-host.com/main-screenshot.png" alt="StreamZone Pro Dashboard Screenshot" width="800"/>
</p>

StreamZone Pro is a full-featured, microservices-based live streaming application built with a modern Java and React technology stack. It is designed to be a robust and scalable platform, allowing multiple users to host collaborative podcasts, share their screens and cameras, and interact with a live audience.

This project serves as a comprehensive demonstration of building a real-world, production-quality SaaS application, incorporating best practices in microservice architecture, real-time communication, and modern frontend development.

**Live Demo & Portfolio:** `[Link to your deployed application if available]`

## ✨ Core Features

*   **Multi-Host Streaming:** Go beyond simple screen sharing. Multiple users can join a stream as hosts, sharing their cameras and audio simultaneously to create a professional podcast or co-streaming environment.
*   **Live Audience & Viewers:** In addition to hosts, many viewers can join a stream to watch the live broadcast in real-time.
*   **Secure User Authentication:** A dedicated `user-service` handles secure user registration and login using JWT-based authentication.
*   **Centralized API Gateway:** A single, robust entry point (`api-gateway`) manages all incoming traffic, validates user authentication, and routes requests to the appropriate downstream microservices.
*   **Dynamic Stream Management:** Users can create, start, and end streams via a clean REST API managed by the `stream-service`.
*   **Server-Side Recording:** A dedicated `recording-service` can be triggered to record live sessions directly on the server, saving them as `.mp4` files for later use. This is powered by the **LiveKit Egress API**.
*   **Real-time Media Layer:** Utilizes **LiveKit**, a production-grade, open-source SFU (Selective Forwarding Unit), to efficiently handle complex WebRTC media streams, ensuring low latency and scalability for both hosts and viewers.
*   **Professional Frontend UI:** A sleek, modern frontend built with **React**, **TypeScript**, and **Tailwind CSS**, featuring a "Glassmorphism Studio" design, animations, and a responsive layout for a premium user experience.

## 🛠️ Tech Stack & Architecture

This project is built on a distributed, microservice architecture designed for scalability and maintainability.

<!-- It's highly recommended to create a simple architecture diagram using a tool like diagrams.net (draw.io) and embed it here -->
<!-- ![Architecture Diagram](path/to/your/diagram.png) -->

| Component             | Technology                  | Purpose                                                                   |
| :-------------------- | :-------------------------- | :------------------------------------------------------------------------ |
| **Backend**           | **Java 17, Spring Boot**    | The core framework for building robust, high-performance microservices.   |
| **API Gateway**       | **Spring Cloud Gateway**    | Single entry point, routing, and centralized authentication filter.       |
| **User Service**      | Spring Boot, Spring Security, JPA | Manages user identity, registration, login, and roles.                    |
| **Stream Service**    | Spring Boot, JPA, LiveKit SDK | Manages stream lifecycle and generates LiveKit access tokens.             |
| **Recording Service** | Spring Boot, **LiveKit SDK**    | Handles server-side recording requests via the LiveKit Egress API.        |
| **Database**          | **SQL Server**              | Persistent storage for user and stream data.                              |
| **Media Server**      | **LiveKit (SFU)**           | Handles all real-time WebRTC audio/video transport, ensuring scalability. |
| **Frontend**          | **React, TypeScript, Vite** | A modern, fast, and type-safe foundation for the user interface.          |
| **Styling**           | **Tailwind CSS**            | A utility-first CSS framework for rapid, professional UI development.     |
| **Animation**         | **Framer Motion**           | Provides smooth, beautiful animations for a polished user experience.     |
| **Containerization**  | **Docker & Docker Compose** | Ensures consistent development and production environments.               |

## 🚀 Getting Started

Follow these instructions to get the entire platform running on your local machine.

### Prerequisites

*   **Java 17+** JDK
*   **Maven** (or use the included Maven Wrapper `mvnw`)
*   **Docker** and **Docker Compose**
*   **Node.js** (v20.x or higher) and **npm**
*   A running **SQL Server** instance

### 1. Backend Setup

#### Database Configuration

1.  Connect to your SQL Server instance.
2.  Create a new database for the application:
    ```sql
    CREATE DATABASE streamyard_db;
    ```
3.  Create a dedicated user and grant permissions (replace `'YourStrongPasswordHere'` with a real password):
    ```sql
    USE streamyard_db;
    CREATE LOGIN streamyard_user WITH PASSWORD = 'YourStrongPasswordHere';
    CREATE USER streamyard_user FOR LOGIN streamyard_user;
    ALTER ROLE db_datareader ADD MEMBER streamyard_user;
    ALTER ROLE db_datawriter ADD MEMBER streamyard_user;
    ```

#### Environment Configuration

1.  Navigate to the `docker-compose.yml` file in the project root.
2.  Update the environment variables for `user-service` and `stream-service` with your SQL Server credentials.
3.  Update the environment variables for `recording-service` with your LiveKit API Key and Secret.

### 2. Frontend Setup

1.  Navigate to the `frontend` directory:
    ```bash
    cd frontend
    ```
2.  Install all necessary dependencies:
    ```bash
    npm install
    ```
3.  Create a `.env` file in the `frontend` directory and add your LiveKit client-side URL:
    ```
    VITE_LIVEKIT_URL=wss://your-project-name.livekit.cloud
    ```

### 3. Running the Entire Application

From the **root directory** of the project:

1.  **Build all backend microservices** into JAR files:
    ```bash
    ./mvnw clean package -DskipTests
    ```
2.  **Start all services** (API Gateway, User Service, Stream Service, Recording Service) using Docker Compose:
    ```bash
    docker-compose up --build
    ```
3.  In a **new terminal**, navigate to the `frontend` directory and **start the React development server**:
    ```bash
    cd frontend
    npm run dev
    ```

Your application is now running!
*   **Frontend:** [http://localhost:5173](http://localhost:5173)
*   **API Gateway:** [http://localhost:8080](http://localhost:8080)
