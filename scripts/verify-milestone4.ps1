param(
    [switch]$SkipFrontend
)

$ErrorActionPreference = "Stop"

function Step($Message) {
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

Step "Git working tree"
git status --short --branch

Step "Backend tests"
mvn test

if (-not $SkipFrontend) {
    Step "Frontend production build"
    Push-Location frontend
    try {
        npm run build
    } finally {
        Pop-Location
    }
}

Step "Recent commits"
git log --oneline --decorate -5

Step "Remote repository"
git remote -v

Write-Host ""
Write-Host "Milestone 4 verification completed." -ForegroundColor Green
