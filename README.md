# Rental House Management System

A full-stack Rental House Management System built with Spring Boot (Java 17), MySQL / TiDB Cloud, and a modern responsive frontend.

---

## Architecture Overview

- **Backend**: Spring Boot 3.3.4, Spring Security (JWT), Spring Data JPA, Hibernate, MySQL Connector
- **Database**: TiDB Cloud (Serverless MySQL-compatible database)
- **Frontend**: HTML5, CSS3, JavaScript (Responsive 20-page web portal)
- **Deployment**:
  - **Backend**: [Render](https://render.com) (Docker / Web Service)
  - **Frontend**: [Vercel](https://vercel.com) (Static Hosting)

---

## Directory Structure

```
├── backend/
│   ├── src/                    # Java source code and resources
│   ├── pom.xml                 # Maven configuration
│   ├── Dockerfile              # Multi-stage Docker container for Render
│   └── .gitignore
├── frontend/
│   ├── css/                    # Stylesheets
│   ├── js/                     # API client and application logic
│   ├── index.html              # Landing page
│   └── vercel.json             # Vercel deployment routing
├── database/
│   └── rental_house_db.sql     # Database schema and seed data
├── render.yaml                 # Render Blueprint configuration
├── .gitignore
└── README.md
```

---

## Deployment Guide

### 1. Backend on Render

1. Log in to [Render](https://render.com).
2. Click **New +** > **Web Service**.
3. Connect your repository: `https://github.com/deepak120507/Renetal-mangement`.
4. Configure settings:
   - **Name**: `rental-house-backend`
   - **Root Directory**: `backend`
   - **Runtime**: `Docker`
   - **Plan**: `Free`
5. Under **Environment Variables**, set:
   - `PORT`: `8080`
   - `DB_URL`: `jdbc:mysql://gateway01.ap-northeast-1.prod.aws.tidbcloud.com:4000/rental_house_db?createDatabaseIfNotExist=true&sslMode=VERIFY_IDENTITY&serverTimezone=UTC`
   - `DB_USERNAME`: `2sNCzaayyhTB8k8.root`
   - `DB_PASSWORD`: `6VdGnBE4D7FSbjt5`
   - `JWT_SECRET`: `any-secure-random-secret-key-at-least-32-chars`
6. Click **Create Web Service**. Render will build and deploy the Spring Boot app.
7. Copy your deployed backend URL (e.g., `https://rental-house-backend.onrender.com`).

---

### 2. Frontend on Vercel

1. Log in to [Vercel](https://vercel.com).
2. Click **Add New...** > **Project**.
3. Import the repository: `deepak120507/Renetal-mangement`.
4. Configure settings:
   - **Root Directory**: Select `frontend`
   - **Framework Preset**: `Other`
   - **Build Command**: Leave empty
   - **Output Directory**: Leave empty (`.`)
5. Update `frontend/vercel.json` with your deployed Render URL:
   ```json
   {
     "version": 2,
     "cleanUrls": true,
     "rewrites": [
       {
         "source": "/api/:match*",
         "destination": "https://<YOUR-RENDER-BACKEND-URL>.onrender.com/api/:match*"
       }
     ]
   }
   ```
6. Click **Deploy**.

---

## Local Development

### Run Backend
```bash
cd backend
mvn spring-boot:run
```
Or run the packaged JAR:
```bash
java -jar target/rental-house-management-1.0.0.jar
```

### Access System
- **API URL**: `http://localhost:8080`
- **Default Admin Account**:
  - Email: `admin@rentalhouse.com`
  - Password: `admin123`
