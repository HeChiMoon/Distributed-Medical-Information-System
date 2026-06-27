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

$token = $login.data.accessToken
Write-Host "Token acquired: $($token.Substring(0, [Math]::Min(24, $token.Length)))..."

Show-Step "Ensure demo patient exists"
$patientPage = Invoke-DmisGet "/api/patients?page=0&size=1" $token
if ($patientPage.data.total -gt 0) {
    $patientId = $patientPage.data.records[0].id
    Write-Host "Using existing patient id: $patientId"
} else {
    $patientBody = @{
        name = "Demo Patient"
        gender = "MALE"
        birthday = "1988-06-01"
        idCard = "DEMO$([DateTimeOffset]::Now.ToUnixTimeSeconds())"
        phone = "13800000000"
        address = "DMIS Demo Ward"
        bloodType = "O"
        allergyHistory = "None"
        medicalHistory = "Demo baseline record"
    } | ConvertTo-Json

    $createdPatient = Invoke-RestMethod `
        -Method Post `
        -Uri "$GatewayBaseUrl/api/patients" `
        -Headers @{ Authorization = "Bearer $token" } `
        -ContentType "application/json" `
        -Body $patientBody
    $patientId = $createdPatient.data.id
    Write-Host "Created demo patient id: $patientId"
}

Show-Step "Protected API without token should return 401"
try {
    Invoke-RestMethod -Method Get -Uri "$GatewayBaseUrl/api/patients"
} catch {
    Write-Host "Expected failure: $($_.Exception.Message)" -ForegroundColor Yellow
}

Show-Step "Patient Redis cache demo"
Invoke-DmisGet "/api/patients/$patientId/cache-demo" $token | ConvertTo-Json -Depth 8

Show-Step "Feign remote call demo"
Invoke-DmisGet "/api/appointments/demo/remote-patient/$patientId" $token | ConvertTo-Json -Depth 8

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
