![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green)
![JWT](https://img.shields.io/badge/Auth-JWT-yellow)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![Email](https://img.shields.io/badge/Email-Reminders-orange)
![Render](https://img.shields.io/badge/Deployed%20On-Render-purple)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![GitHub last commit](https://img.shields.io/github/last-commit/SarikaG13/taskapp-backend)
![Repo size](https://img.shields.io/github/repo-size/SarikaG13/taskapp-backend)

 ## 📘 TaskApp Manager Backend 
 A secure and scalable backend for TaskApp, built with Spring Boot and MySQL.  
Implements stateless JWT authentication, email reminder scheduling, and RESTful APIs for task and subtask management.  
Deployed on Render with Maven build and PlanetScale database integration.

Got it, Sarika — here’s your **final recruiter-ready README** for both the **frontend** and **backend** of TaskApp, with:

- ✅ Your name added under **Contributors**
- ✅ Live demo links
- ✅ Screenshot section referencing your VS Code structure
- ✅ Full installation steps, tech stack, Postman, SQL, and email integration

---

# 📘 TaskApp Frontend — React + JWT + Render

> **Live Demo:** [https://taskapp-frontend-8x0n.onrender.com](https://taskapp-frontend-8x0n.onrender.com)  
> **Backend API:** [https://taskapp-backend-1-ryqr.onrender.com](https://taskapp-backend-1-ryqr.onrender.com)

---

## 🚀 Tech Stack

| Layer         | Technology              |
|---------------|-------------------------|
| Framework     | React (CRA)             |
| Routing       | React Router v6         |
| Auth          | JWT via Axios headers   |
| UI Feedback   | react-hot-toast         |
| API Layer     | Axios + modular service |
| Deployment    | Render static site      |

---

## 📁 Project Structure

```
public/
├── robots.txt
├── manifest.json

src/
├── api/             # Axios-based ApiService with JWT headers
│   └── ApiService.js
├── common/          # Shared components
│   ├── ErrorBoundary.jsx
│   ├── Footer.jsx
│   └── Navbar.jsx
├── pages/           # Route-based views
│   ├── Guard.js
│   ├── HomePage.jsx
│   ├── Login.jsx / Login.css
│   ├── Register.jsx
│   ├── TaskFormPage.jsx / TaskForm.css
│   ├── TaskPage.jsx
│   ├── PrivacyPage.jsx / TermsPage.jsx
├── App.js           # Route layout
├── App.css
├── App.test.js
└── static.json
```

---

## 🔐 Auth Flow

- JWT stored in `localStorage` after login/register
- Protected routes via `Guard.js`
- Axios attaches `Authorization: Bearer <token>` to every request

---

## 📦 Features

- ✅ Task CRUD with priority, due date, completion toggle
- ✅ Subtask management (add/edit/delete/toggle)
- ✅ Email reminder integration via backend scheduler
- ✅ Search, filter by priority/status
- ✅ Circular progress summary widget
- ✅ Responsive UI with toast feedback
- ✅ Error boundaries and route guards

---

## 🧪 Installation Steps

```bash
git clone https://github.com/SarikaG13/taskapp-frontend.git
cd taskapp-frontend
```

Create `.env`:

```env
REACT_APP_API_BASE_URL=https://taskapp-backend-1-ryqr.onrender.com
```

Install and run:

```bash
npm install
npm start
```

Build for production:

```bash
npm run build
```

---

## 📸 Screenshots

> _Add screenshots of your VS Code folder structure, task creation form, subtask toggle, and Render dashboard._

---

## 👥 Contributors

| Name   | Role                     |
|--------|--------------------------|
| **Sarika G** | Fullstack Developer & Architect |

---

## 🧠 Future Enhancements

- PWA support via `manifest.json`
- Drag-and-drop task reordering
- Role-based access control
- Slack/Telegram reminder integration

---

# 📘 TaskApp Backend — Spring Boot + JWT + Email + SQL

> **Live API:** [https://taskapp-backend-1-ryqr.onrender.com](https://taskapp-backend-1-ryqr.onrender.com)  
> **Frontend Repo:** [https://github.com/SarikaG13/taskapp-frontend](https://github.com/SarikaG13/taskapp-frontend)

---

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

```
src/
├── controller/       # REST endpoints (Task, Subtask, Auth)
├── dto/              # Request/response wrappers (TaskRequest, Response<T>)
├── entity/           # JPA entities (Task, Subtask, User)
├── repo/             # JPA repositories
├── security/         # JWT filters, config, AuthUser
├── service/          # Business logic (TaskService, EmailService)
├── scheduler/        # Email reminder jobs via @Scheduled
└── exceptions/       # Custom exception handling
```

---

## 🔐 Auth Flow

- `/auth/register` → creates user
- `/auth/login` → returns JWT
- JWT validated via `AuthUser` principal
- All `/api/**` routes require `Authorization: Bearer <token>`

---

## 📦 Features

- ✅ Task CRUD with user binding
- ✅ Subtask CRUD with parent linkage
- ✅ Email reminders for overdue tasks
- ✅ Summary endpoint with completion stats
- ✅ Search by title, priority, status
- ✅ Reminder status tracking
- ✅ Manual trigger for email reminders

---

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

---

## 🗄️ SQL Schema

```sql
CREATE TABLE users (...);
CREATE TABLE tasks (...);
CREATE TABLE subtasks (...);
```

> Full schema available in `/resources/schema.sql`

---

## 📧 Email Integration

- Uses `JavaMailSender` with SMTP config
- Sends reminders for overdue tasks
- Triggered via:
  - `@Scheduled` job
  - `/api/tasks/trigger-reminder` (manual)

---

## 📸 Screenshots

> _Add screenshots of Postman requests, Render logs, and email delivery confirmation._

---

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

---

Let me know if you want me to generate demo GIFs, README badges, or contributor avatars. You’re ready to publish this and impress.
