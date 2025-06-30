# Modern Blog Application

Full-stack blog with Spring Boot, HTMX, and Tailwind CSS.

## Prerequisites

- Docker & Docker Compose
- Java 21+ and Gradle (via SDKMAN recommended)

## Features

- 📝 Markdown blog posts with syntax highlighting
- 🔐 GitHub OAuth2 authentication  
- 💬 Real-time commenting
- 🎨 Responsive Tailwind UI
- 🖼️ Image uploads

## Tech Stack

- **Backend**: Spring Boot 3, Spring Security 6, PostgreSQL 17
- **Frontend**: Thymeleaf, HTMX, Tailwind CSS, Prism.js
- **Infrastructure**: Docker Compose, Caddy reverse proxy

## Quick Start

1. **Setup environment**
   ```bash
   cp example.env .env
   # Add GitHub OAuth credentials to .env
   ```

2. **Run**
   ```bash
   gradle build
   docker compose up --build
   ```

3. **Access**
   - Blog: http://localhost:8080
   - Write posts: http://localhost:8080/writepost (requires AUTHOR role)
   - Edit posts: https://localhost:8080/posts/{post_id}/edit (requires AUTHOR role)

## Configuration

### GitHub OAuth
1. Create OAuth App: https://github.com/settings/applications/new
2. Callback URL: `http://localhost:8080/login/oauth2/code/github`
3. Add credentials to `.env`

### User Roles
Grant AUTHOR role in database after first login with GitHub:
```sql
UPDATE users SET role = 'ROLE_AUTHOR' WHERE username = 'your-username';
```
