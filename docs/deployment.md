# Deployment Process and Requirements

This document describes how to build and deploy the Advertisement Platform across environments using Docker and Spring Boot.

## Prerequisites
- Java 21 (LTS) installed for local builds
- Docker and Docker Compose installed (latest stable)
- Access to a container registry (e.g., GitHub Container Registry or Docker Hub)
- Network access to the database instance (if using external DB)
- Environment variables configured per environment

## Environments
- Local: developer machine using docker-compose
- Dev: shared environment, runs latest develop branch images
- Prod: production environment, versioned immutable images

## Build Backend
1. Package the Spring Boot application (CI recommended):
   - Using Maven: `mvn -DskipTests package` (CI runner)
2. A Dockerfile (backend module) should produce an image exposing port 8080.
3. Tag and push image to your registry: `docker build -t <registry>/ad-platform-backend:<tag> .` then `docker push <registry>/ad-platform-backend:<tag>`.

## Build Frontend
1. Next.js app uses `npm ci` then `npm run build`.
2. Build Docker image that serves the `.next` build via Node or a production web server (e.g., `node server.js` or Next start).
3. Tag and push: `<registry>/ad-platform-frontend:<tag>`.

## Configuration
- Spring profiles: `dev`, `prod` (use `SPRING_PROFILES_ACTIVE`)
- Database connection via standard Spring properties (URL, username, password)
- Liquibase changelogs are applied on startup
- Actuator health endpoints enabled for readiness/liveness checks

## Secrets Management
- Use environment variables or a secrets manager (Vault, AWS Secrets Manager)
- Never commit secrets to VCS

## Database Migrations
- Managed by Liquibase on application startup
- Validate changelog on CI as part of pipeline

## Deploy with Docker Compose (Local)
1. From repo root: `docker-compose up --build`
2. Backend available at `http://localhost:8080`
3. Frontend available at `http://localhost:3000`

## Kubernetes (Optional)
- Create Deployments and Services for backend and frontend
- Configure Ingress with TLS termination
- Configure Horizontal Pod Autoscaler for backend CPU/memory thresholds
- Configure ConfigMaps/Secrets for environment-specific config

## Observability
- Micrometer + Prometheus enabled for metrics (collected via Spring AOP aspects to keep services clean). Expose metrics at `/actuator/prometheus` (ensure port 8080 accessible). Scrape with Prometheus by pointing to this endpoint.
- Custom domain metrics (Prometheus names without dots will be converted with `_` when scraped):
  - `advertisements_saved_total` (counter): tags `operation` = create|update, `status` = success|failure|validation_error.
  - `advertisements_validation_errors_total` (counter): total number of validation errors raised across requests.
  - `advertisements_fetch_byId_seconds` (timer): tags `status` = success|not_found|error.
  - `advertisements_targeting_compute_seconds` (timer): tags `strategy` = combined|geo|bio|mood, `status` = success|error.
- Cache metrics for Caffeine caches are auto-exposed by Spring Boot Actuator.
- Centralized logging (to be added) via ELK/EFK

## Rollback Strategy
- Keep previous stable tags available in registry
- Roll back by redeploying previous version tag

## Verification Post-Deploy
- Check `/actuator/health` returns `UP`
- Run smoke tests against critical API endpoints
- Verify frontend loads and can retrieve advertisements
