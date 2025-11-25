# Blog Management - QuickBlog

A full-stack blog management application built with a modern React frontend and a robust Spring Boot backend.

## Project Structure

- **blog-management-react**: The frontend application.
- **blogapp**: The backend REST API application.

## Frontend (blog-management-react)

### Tech Stack
- **Framework**: React 19, Vite
- **Styling**: TailwindCSS 4, Material UI (MUI), Emotion
- **State/Routing**: React Router DOM 7
- **HTTP Client**: Axios
- **Utilities**: Moment.js, Motion (Framer Motion), React Hot Toast, React Icons
- **Editor**: Quill

### Setup & Run
1. Navigate to the frontend directory:
   ```bash
   cd blog-management-react
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Run the development server:
   ```bash
   npm run dev
   ```

## Backend (blogapp)

### Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA
- **Security**: Spring Security, JWT (JSON Web Tokens)
- **Documentation**: SpringDoc OpenAPI (Swagger UI)
- **Tools**: Lombok, MapStruct, Maven

### Setup & Run
1. Navigate to the backend directory:
   ```bash
   cd blogapp
   ```
2. Configure your database settings in `src/main/resources/application.properties` (or `application.yml`).
3. Run the application using Maven Wrapper:
   ```bash
   ./mvnw spring-boot:run
   ```
