@echo off
TITLE Smart Society System - Orchestrator
echo ========================================================
echo   SMART SOCIETY COMPLAINT ^& MAINTENANCE SYSTEM
echo   Local Startup Orchestrator (Windows)
echo ========================================================
echo.

echo [1/6] Starting Docker Infrastructure (PostgreSQL:5434, Redis:6379, Kafka:9092)...
docker compose -f infra/docker-compose.yml up -d postgres redis kafka
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to start Docker containers. Please verify Docker Desktop is running!
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [2/6] Starting Netflix Eureka Discovery Service (Port 8761)...
start "Eureka Discovery Service [Port 8761]" cmd /k "mvnw.cmd spring-boot:run -pl discovery-service"
timeout /t 12 /nobreak >nul

echo.
echo [3/6] Starting User Service (Port 8081)...
start "User Service [Port 8081]" cmd /k "mvnw.cmd spring-boot:run -pl user-service"

echo.
echo [4/6] Starting Complaint Service (Port 8082)...
start "Complaint Service [Port 8082]" cmd /k "mvnw.cmd spring-boot:run -pl complaint-service"

echo.
echo [5/6] Starting Notification Service (Port 8083)...
start "Notification Service [Port 8083]" cmd /k "mvnw.cmd spring-boot:run -pl notification-service"
timeout /t 8 /nobreak >nul

echo.
echo [6/6] Starting Spring Cloud API Gateway (Port 8085)...
start "API Gateway [Port 8085]" cmd /k "mvnw.cmd spring-boot:run -pl api-gateway"

echo.
echo Starting React Vite Frontend Client (Port 5173)...
start "Frontend Client [Port 5173]" cmd /k "cd frontend-client && npm run dev"

echo.
echo ========================================================
echo   ALL SERVICES LAUNCHED SUCCESSFULLY!
echo ========================================================
echo   - Frontend Portal:    http://localhost:5173
echo   - API Gateway:        http://localhost:8085
echo   - Eureka Dashboard:   http://localhost:8761
echo   - PostgreSQL (Docker):localhost:5434
echo   - Redis:              localhost:6379
echo   - Kafka:              localhost:9092
echo ========================================================
echo.
pause
