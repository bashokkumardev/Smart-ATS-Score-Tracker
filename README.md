# Smart ATS Score Tracker

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen)
![MongoDB](https://img.shields.io/badge/MongoDB-7-green)
![License](https://img.shields.io/badge/License-MIT-blue)

Spring Boot API — register users, login with JWT, upload **PDF/DOCX resume** + **job description**, get a **weighted ATS score**, recommendations, and score history.

**Repo:** https://github.com/bashokkumardev/Smart-ATS-Score-Tracker

---

## Features

- PDF & DOCX resume text extraction
- Weighted ATS scoring (known tech skills + phrases like `Spring Boot`, `REST API`)
- Recommendations for missing skills
- Score history per user
- JWT auth with rate limiting on login/register
- Prometheus + Grafana monitoring (optional)

---

## Setup

1. Install **JDK 17+** and **MongoDB** (`localhost:27017`)
2. Copy env template:

```powershell
copy .env.example .env
```

3. Start the app:

```powershell
.\mvnw spring-boot:run
```

App: http://localhost:8080  
Swagger: http://localhost:8080/swagger-ui.html

---

## APIs

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| GET | `/health` | No | Health check |
| POST | `/auth/register` | No | Register user |
| POST | `/auth/login` | No | Login → JWT |
| GET | `/auth/register/{registerId}` | Yes | Own profile (or admin) |
| DELETE | `/auth/register/{registerId}` | Yes | Delete own account (or admin) |
| GET | `/auth/register` | Admin | List all users |
| DELETE | `/auth/register` | Admin | Delete all users |
| POST | `/score/upload` | Yes | Upload resume + JD → score |
| GET | `/score/history` | Yes | Past scores |
| GET | `/score/{id}` | Yes | One score detail |

---

## Postman — score upload

**POST** `/score/upload`  
Authorization: Bearer Token → paste JWT (`eyJ...` only)

**form-data:**

| Key | Type | Required |
|-----|------|----------|
| `resume` | File (PDF or DOCX) | Yes |
| `jobDescription` | Text | Yes |
| `jobTitle` | Text | No |
| `companyName` | Text | No |

**Response example:**

```json
{
  "score": 72,
  "matchedSkills": ["java", "spring boot", "rest api"],
  "missingSkills": ["kafka", "python"],
  "recommendations": ["Add or highlight 'Kafka' on your resume if you have this experience"],
  "summary": "3 of 5 key skills matched (72% weighted ATS score)",
  "jobTitle": "Java Developer",
  "companyName": "Acme Corp"
}
```

---

## Monitoring (optional)

```powershell
.\scripts\start-monitoring.ps1
```

- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000 (`admin` / `admin`)
