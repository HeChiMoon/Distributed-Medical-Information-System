param(
    [switch]$SkipInfrastructure,
    [switch]$SkipBackend,
    [switch]$SkipFrontend,
    [switch]$SkipBuild
)

$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")
$LogDir = Join-Path $Root "logs\dev"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

function Step($Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Wait-Port($Port, $Name, $TimeoutSeconds = 90) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        $ready = Test-NetConnection -ComputerName 127.0.0.1 -Port $Port -InformationLevel Quiet
        if ($ready) {
            Write-Host "$Name is ready on port $Port" -ForegroundColor Green
            return
        }
        Start-Sleep -Seconds 2
    }
    throw "$Name did not become ready on port $Port within $TimeoutSeconds seconds."
}

function Start-DmisProcess($Name, $Command, $LogFile) {
    $logPath = Join-Path $LogDir $LogFile
    $wrapped = "Set-Location '$Root'; $Command *> '$logPath'"
    Start-Process `
        -FilePath "powershell" `
        -ArgumentList @("-NoProfile", "-ExecutionPolicy", "Bypass", "-Command", $wrapped) `
        -WindowStyle Hidden
    Write-Host "$Name started. Log: $logPath" -ForegroundColor Green
}

if (-not $SkipInfrastructure) {
    Step "Start infrastructure: MySQL, Redis, Nacos"
    Set-Location $Root
    docker compose up -d mysql redis nacos
    Wait-Port 3306 "MySQL"
    Wait-Port 6379 "Redis"
    Wait-Port 8848 "Nacos"

    Step "Import Nacos demo config"
    & (Join-Path $PSScriptRoot "import-nacos-config.ps1")
}

if (-not $SkipBackend) {
    if (-not $SkipBuild) {
        Step "Package backend services"
        Set-Location $Root
        mvn -DskipTests package
    }

    Step "Start backend services"
    $services = @(
        @{ Name = "auth-service"; Jar = "backend\auth-service\target\auth-service-0.1.0-SNAPSHOT.jar"; Port = 9001 },
        @{ Name = "patient-service"; Jar = "backend\patient-service\target\patient-service-0.1.0-SNAPSHOT.jar"; Port = 9002 },
        @{ Name = "appointment-service"; Jar = "backend\appointment-service\target\appointment-service-0.1.0-SNAPSHOT.jar"; Port = 9003 },
        @{ Name = "medical-record-service"; Jar = "backend\medical-record-service\target\medical-record-service-0.1.0-SNAPSHOT.jar"; Port = 9004 },
        @{ Name = "pharmacy-service"; Jar = "backend\pharmacy-service\target\pharmacy-service-0.1.0-SNAPSHOT.jar"; Port = 9005 },
        @{ Name = "billing-service"; Jar = "backend\billing-service\target\billing-service-0.1.0-SNAPSHOT.jar"; Port = 9006 },
        @{ Name = "admin-service"; Jar = "backend\admin-service\target\admin-service-0.1.0-SNAPSHOT.jar"; Port = 9007 },
        @{ Name = "gateway-service"; Jar = "backend\gateway-service\target\gateway-service-0.1.0-SNAPSHOT.jar"; Port = 9527 }
    )

    foreach ($service in $services) {
        $jarPath = Join-Path $Root $service.Jar
        if (-not (Test-Path $jarPath)) {
            throw "Missing jar for $($service.Name): $jarPath. Run scripts\start-dev.ps1 without -SkipBuild."
        }
        Start-DmisProcess $service.Name "java -jar '$jarPath'" "$($service.Name).log"
    }

    Wait-Port 9527 "API Gateway"
}

if (-not $SkipFrontend) {
    Step "Start Vue frontend"
    $frontendDir = Join-Path $Root "frontend"
    if (-not (Test-Path (Join-Path $frontendDir "node_modules"))) {
        Push-Location $frontendDir
        try {
            npm install
        } finally {
            Pop-Location
        }
    }
    Start-DmisProcess "frontend" "Set-Location '$frontendDir'; npm run dev -- --host 0.0.0.0" "frontend.log"
    Wait-Port 5173 "Frontend"
}

Write-Host ""
Write-Host "DMIS development environment is ready." -ForegroundColor Green
Write-Host "Frontend: http://localhost:5173"
Write-Host "Gateway:  http://localhost:9527"
Write-Host "Nacos:    http://localhost:8848/nacos"
Write-Host "Logs:     $LogDir"
