# Modern Blog Application

Full-stack blog with Spring Boot, HTMX, and Tailwind CSS.

## Prerequisites

- Docker & Docker Compose
- Java 21+ and Gradle (via SDKMAN recommended)

## Features

- 📝 Markdown blog posts with syntax highlighting
- 🔐 OAuth2 authentication (GitHub, Google, Microsoft)
- 💬 Comments in blog posts
- 🎨 Responsive Tailwind UI
- 🖼️ Image uploads for writers

## Tech Stack

- **Backend**: Spring Boot 3, Spring Security 6, PostgreSQL 17
- **Frontend**: Thymeleaf, HTMX, Tailwind CSS, Prism.js
- **Infrastructure**: Docker Compose, Caddy reverse proxy

## Quick Start

1. **Setup environment**
   ```bash
   cp example.env .env
   # Add GitHub/Microsoft/Google OAuth credentials to .env
   # see Configuration OAuth2/OIDC
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

## Configuration OAuth2/OIDC

### GitHub
1. Create OAuth App: `https://github.com/settings/applications/new`
2. Callback URL: `http://localhost:8080/login/oauth2/code/github`
3. Add credentials to `.env` (copy client id and client secret)
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT-ID
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT-SECRET

### Google

1. Create OAuth Client: Go to `https://console.cloud.google.com/apis/credentials`
2. Create credentials > OAuth client ID
3. Application type: Web application
4. Authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
5. Add credentials to `.env` (copy client id and client secret)
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT-ID
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT-SECRET

### Microsoft

1. Create App Registration. Go to https://portal.azure.com/#blade/Microsoft_AAD_RegisteredApps/ApplicationsListBlade
2. New registration (with a meaningful name)
3. Supported account types: Personal Microsoft accounts only
3. Redirect URI: `http://localhost:8080/login/oauth2/code/microsoft` (type/platform: Web)
4. Create client secret: Certificates & secrets → New client secret
5. Add credentials to `.env` (copy Application/client id and Secret Value)
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_MICROSOFT_CLIENT-ID
    - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_MICROSOFT_CLIENT-SECRET

### User Roles
Grant AUTHOR role in database after first login with GitHub:
```sql
UPDATE users SET role = 'ROLE_AUTHOR' WHERE username = 'your-username';
```
