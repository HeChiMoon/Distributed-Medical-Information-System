param(
    [string]$NacosBaseUrl = "http://localhost:8848",
    [string]$Group = "DEFAULT_GROUP"
)

$ErrorActionPreference = "Stop"

$configDir = Join-Path $PSScriptRoot "..\infra\nacos\config"
$files = Get-ChildItem -Path $configDir -Filter "*.yml"

foreach ($file in $files) {
    Write-Host "Publishing $($file.Name) to Nacos..." -ForegroundColor Cyan
    $body = @{
        dataId = $file.Name
        group = $Group
        content = Get-Content -Raw -Path $file.FullName
        type = "yaml"
    }

    Invoke-RestMethod `
        -Method Post `
        -Uri "$NacosBaseUrl/nacos/v1/cs/configs" `
        -ContentType "application/x-www-form-urlencoded" `
        -Body $body | Out-Null
}

Write-Host "Nacos config import completed." -ForegroundColor Green
