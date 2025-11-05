![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Email](https://img.shields.io/badge/Email-Reminders-orange)
![Render](https://img.shields.io/badge/Deployed%20On-Render-purple)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![GitHub last commit](https://img.shields.io/github/last-commit/SarikaG13/taskapp-backend)
![Repo size](https://img.shields.io/github/repo-size/SarikaG13/taskapp-backend)

# 📘 TaskApp Manager Backend — Spring Boot + JWT + Email + SQL
 A secure and scalable backend for TaskApp, built with Spring Boot and MySQL.  
Implements stateless JWT authentication, email reminder scheduling, and RESTful APIs for task and subtask management.  
Deployed on Render with Maven build and PlanetScale database integration.

> **Live API:** [https://taskapp-backend-1-ryqr.onrender.com](https://taskapp-backend-1-ryqr.onrender.com)  
> **Frontend Repo:** [https://github.com/SarikaG13/taskapp-frontend](https://github.com/SarikaG13/taskapp-frontend)


## 🚀 Tech Stack

| Layer        | Technology                  |
|--------------|-----------------------------|
| Language     | Java 17                     |
| Framework    | Spring Boot 3.2             |
| Auth         | JWT (Stateless)             |
| Database     | MySQL (PlanetScale)         |
| ORM          | Spring Data JPA + Hibernate |
| Email        | JavaMailSender              |
| Deployment   | Render (Maven build)        |

---

## 📁 Project Structure

src/
├── controller/       # REST endpoints (Task, Subtask, Auth)
├── dto/              # Request/response wrappers (TaskRequest, Response<T>)
├── entity/           # JPA entities (Task, Subtask, User)
├── repo/             # JPA repositories
├── security/         # JWT filters, config, AuthUser
├── service/          # Business logic (TaskService, EmailService)
├── pom.xml      


## 🔐 Auth Flow

- `/auth/register` → creates user
- `/auth/login` → returns JWT
- JWT validated via `AuthUser` principal
- All `/api/**` routes require `Authorization: Bearer <token>`


## 📦 Features

- ✅ Task CRUD with user binding
- ✅ Subtask CRUD with parent linkage
- ✅ Email reminders for overdue tasks
- ✅ Summary endpoint with completion stats
- ✅ Search by title, priority, status
- ✅ Reminder status tracking
- ✅ Manual trigger for email reminders


## 🧪 Installation Steps

```bash
git clone https://github.com/SarikaG13/taskapp-backend.git
cd taskapp-backend
```

Set environment variables via `.env` or Render dashboard:

```properties
DB_URL=jdbc:mysql://your-planetscale-url
DB_USERNAME=your-db-username
DB_PASSWORD=your-db-password

JWT_SECRET=your-jwt-secret
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

Build and run:

```bash
./mvnw clean install
./mvnw spring-boot:run
```


## 🗄️ SQL Schema

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE tasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  description TEXT,
  priority ENUM('LOW','MEDIUM','HIGH'),
  completed BOOLEAN,
  due_date DATE,
  created_at DATETIME,
  updated_at DATETIME,
  user_id BIGINT,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE subtasks (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255),
  completed BOOLEAN,
  task_id BIGINT,
  FOREIGN KEY (task_id) REFERENCES tasks(id)
);

---

## 📧 Email Integration

- Uses `JavaMailSender` with SMTP config
- Sends reminders for overdue tasks
- Triggered via:
  - `@Scheduled` job
  - `/api/tasks/trigger-reminder` (manual)


## 👥 Contributors

| Name   | Role                     |
|--------|--------------------------|
| **Sarika G** | Fullstack Developer & Architect |

---

## 🧠 Future Enhancements

- OAuth2 login (Google/GitHub)
- Admin dashboard for task analytics
- Dockerization for local dev
- Multi-user collaboration

