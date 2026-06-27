param(
    [switch]$KeepInfrastructure,
    [switch]$StopLocalMySql
)

$ErrorActionPreference = "Stop"

$Root = Resolve-Path (Join-Path $PSScriptRoot "..")

function Step($Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

Step "Stop DMIS frontend and backend processes"
$processes = Get-CimInstance Win32_Process | Where-Object {
    ($_.Name -in @("java.exe", "node.exe", "cmd.exe", "esbuild.exe", "powershell.exe")) -and
    ($_.ProcessId -ne $PID) -and
    ($_.CommandLine -match [regex]::Escape("$Root") -or $_.CommandLine -match "vite")
}

if ($processes) {
    $processes | ForEach-Object {
        Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
    }
    Write-Host "Stopped processes: $($processes.ProcessId -join ', ')" -ForegroundColor Green
} else {
    Write-Host "No DMIS app processes found."
}

if (-not $KeepInfrastructure) {
    Step "Stop Docker infrastructure"
    Set-Location $Root
    docker compose down
}

if ($StopLocalMySql) {
    Step "Stop local MySQL process"
    $mysql = Get-CimInstance Win32_Process | Where-Object {
        $_.Name -eq "mysqld.exe" -and $_.CommandLine -match "MYSQL"
    }
    if ($mysql) {
        $mysql | ForEach-Object {
            Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue
        }
        Write-Host "Stopped local MySQL processes: $($mysql.ProcessId -join ', ')" -ForegroundColor Green
    } else {
        Write-Host "No local MySQL process found."
    }
}

Step "Port check"
$ports = 5173, 9527, 9001, 9002, 9003, 9004, 9005, 9006, 9007, 8848, 9848, 6379, 3306
$listeners = Get-NetTCPConnection -State Listen -ErrorAction SilentlyContinue |
    Where-Object { $ports -contains $_.LocalPort } |
    Select-Object LocalAddress, LocalPort, OwningProcess |
    Sort-Object LocalPort

if ($listeners) {
    $listeners
} else {
    Write-Host "No DMIS ports are listening." -ForegroundColor Green
}
