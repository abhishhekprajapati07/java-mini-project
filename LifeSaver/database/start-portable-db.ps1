# ============================================================================
# LifeSaver - Zero-Install Portable Database Runner for Windows PowerShell
# ============================================================================
# This script sets up and launches a lightweight, portable MySQL-compatible
# MariaDB engine on port 3306 without requiring Windows Administrator rights.
# Wire-compatible with standard MySQL connector (com.mysql.cj.jdbc.Driver).
# ============================================================================

$port = 3306
$toolsDir = "C:\Users\Abhishek\tools"
$dbBaseDir = "$toolsDir\mariadb-10.11.8-winx64"
$zipPath = "$toolsDir\mariadb.zip"
$dataDir = "$dbBaseDir\data"
$downloadUrl = "https://archive.mariadb.org/mariadb-10.11.8/winx64-packages/mariadb-10.11.8-winx64.zip"

Write-Host ">>> [LifeSaver] Checking port $port..." -ForegroundColor Cyan

# Check if port 3306 is already running
$testConn = Test-NetConnection -Port $port -ComputerName 127.0.0.1 -WarningAction SilentlyContinue
if ($testConn.TcpTestSucceeded) {
    Write-Host ">>> [LifeSaver] MySQL/MariaDB is ALREADY RUNNING on port $port!" -ForegroundColor Green
    exit 0
}

if (-not (Test-Path $dbBaseDir)) {
    Write-Host ">>> [LifeSaver] Downloading portable MySQL engine (~85 MB)..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null
    curl.exe -sL -o $zipPath $downloadUrl
    Write-Host ">>> [LifeSaver] Extracting portable database..." -ForegroundColor Yellow
    Expand-Archive -Path $zipPath -DestinationPath $toolsDir -Force
    Remove-Item $zipPath -Force
}

$mysqld = "$dbBaseDir\bin\mysqld.exe"
$installDb = "$dbBaseDir\bin\mariadb-install-db.exe"
if (-not (Test-Path $installDb)) {
    $installDb = "$dbBaseDir\bin\mysql_install_db.exe"
}

if (-not (Test-Path "$dataDir\mysql")) {
    Write-Host ">>> [LifeSaver] Initializing database data directory..." -ForegroundColor Yellow
    & $installDb "--datadir=$dataDir"
}

Write-Host ">>> [LifeSaver] Starting portable MySQL database on port $port..." -ForegroundColor Green
& $mysqld "--datadir=$dataDir" "--port=$port" "--console"
