# Smart Society System - Local PowerShell Startup Orchestrator
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host "  SMART SOCIETY COMPLAINT & MAINTENANCE SYSTEM" -ForegroundColor Cyan
Write-Host "  PowerShell Startup Orchestrator (Windows)" -ForegroundColor Cyan
Write-Host "========================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/6] Launching Docker Infrastructure (Postgres:5434, Redis:6379, Kafka:9092)..." -ForegroundColor Yellow
docker compose -f infra/docker-compose.yml up -d postgres redis kafka
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Failed to start Docker containers. Make sure Docker Desktop is running!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "`n[2/6] Starting Discovery Service (Port 8761)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvnw.cmd spring-boot:run -pl discovery-service"
Start-Sleep -Seconds 12

Write-Host "`n[3/6] Starting User Service (Port 8081)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvnw.cmd spring-boot:run -pl user-service"

Write-Host "`n[4/6] Starting Complaint Service (Port 8082)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvnw.cmd spring-boot:run -pl complaint-service"

Write-Host "`n[5/6] Starting Notification Service (Port 8083)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvnw.cmd spring-boot:run -pl notification-service"
Start-Sleep -Seconds 8

Write-Host "`n[6/6] Starting API Gateway (Port 8085)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "mvnw.cmd spring-boot:run -pl api-gateway"

Write-Host "`nStarting Frontend Client (Port 5173)..." -ForegroundColor Yellow
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd frontend-client; npm run dev"

Write-Host "`n========================================================" -ForegroundColor Green
Write-Host "  ALL MICROSERVICES LAUNCHED!" -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Green
Write-Host "  - Frontend Portal:    http://localhost:5173"
Write-Host "  - API Gateway:        http://localhost:8085"
Write-Host "  - Eureka Dashboard:   http://localhost:8761"
Write-Host "  - PostgreSQL:         localhost:5434"
Write-Host "  - Redis:              localhost:6379"
Write-Host "  - Kafka:              localhost:9092"
Write-Host "========================================================"
