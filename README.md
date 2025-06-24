# Blog application showcasing some common standards and libs

* Spring Boot 3
* Spring Security 6
* OAuth2 for GitHub social login
* Flyway
* Thymeleaf
* HTMX
* CSS Animations
* Markdown language
* Docker and Docker Compose

## Prerequisites
- Docker and docker compose installed
- SDKMAN (sdk install java && sdk install gradle)

## Run
- Rename example.env file to .env and add your own configuration
- ./gradlew build 
- docker compose up --build

Main pages are localhost/ or localhost/blog,  localhost/login and localhost/writepost (you need your ROLE to be 'ROLE_AUTHOR' in DB)