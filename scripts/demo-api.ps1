param(
    [string]$GatewayBaseUrl = "http://localhost:9527",
    [string]$Username = "admin",
    [string]$Password = "admin123"
)

$ErrorActionPreference = "Stop"

function Show-Step($Title) {
    Write-Host ""
    Write-Host "==> $Title" -ForegroundColor Cyan
}

function Invoke-DmisGet($Path, $Token) {
    Invoke-RestMethod `
        -Method Get `
        -Uri "$GatewayBaseUrl$Path" `
        -Headers @{ Authorization = "Bearer $Token" }
}

Show-Step "Login through API gateway"
$loginBody = @{
    username = $Username
    password = $Password
} | ConvertTo-Json

$login = Invoke-RestMethod `
    -Method Post `
    -Uri "$GatewayBaseUrl/api/auth/login" `
    -ContentType "application/json" `
    -Body $loginBody

$token = $login.data.token
Write-Host "Token acquired: $($token.Substring(0, [Math]::Min(24, $token.Length)))..."

Show-Step "Protected API without token should return 401"
try {
    Invoke-RestMethod -Method Get -Uri "$GatewayBaseUrl/api/patients"
} catch {
    Write-Host "Expected failure: $($_.Exception.Message)" -ForegroundColor Yellow
}

Show-Step "Patient Redis cache demo"
Invoke-DmisGet "/api/patients/1/cache-demo" $token | ConvertTo-Json -Depth 8

Show-Step "Feign remote call demo"
Invoke-DmisGet "/api/appointments/demo/remote-patient/1" $token | ConvertTo-Json -Depth 8

Show-Step "Config center demo through gateway"
Invoke-DmisGet "/api/config/patient/demo/config" $token | ConvertTo-Json -Depth 8

Show-Step "Module metadata smoke checks"
$paths = @(
    "/api/patients/modules",
    "/api/appointments/modules",
    "/api/records/modules",
    "/api/pharmacy/modules",
    "/api/bills/modules",
    "/api/admin/modules"
)

foreach ($path in $paths) {
    $result = Invoke-DmisGet $path $token
    Write-Host "$path -> $($result.data.serviceName)"
}

Write-Host ""
Write-Host "Gateway demo API smoke test completed." -ForegroundColor Green
