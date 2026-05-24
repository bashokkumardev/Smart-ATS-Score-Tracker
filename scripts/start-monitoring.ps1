# Start Prometheus + Grafana for job-tracker metrics.
# Prerequisites: Docker Desktop running, Spring Boot app on http://localhost:8080

$ErrorActionPreference = "Stop"
$MonitoringDir = Join-Path $PSScriptRoot ".." "monitoring" | Resolve-Path

Write-Host "Starting Prometheus (9090) and Grafana (3000)..." -ForegroundColor Cyan
Push-Location $MonitoringDir
try {
    docker compose up -d
    if ($LASTEXITCODE -ne 0) {
        throw "docker compose failed with exit code $LASTEXITCODE"
    }
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "Monitoring stack is up." -ForegroundColor Green
Write-Host "  Prometheus:  http://localhost:9090"
Write-Host "  Grafana:     http://localhost:3000  (admin / admin)"
Write-Host "  Metrics API: http://localhost:8080/actuator/prometheus"
Write-Host ""
Write-Host "Ensure the Spring Boot app is running before checking dashboards."
