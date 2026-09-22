# ============================================================
# GYM MICROSERVICES - PRUEBA COMPLETA
# ============================================================

$ErrorActionPreference = "Continue"

Write-Host ""
Write-Host "============================================================"
Write-Host "       GYM MICROSERVICES - TEST COMPLETO"
Write-Host "============================================================"
Write-Host ""

# ------------------------------------------------------------
# 1. CONFIGURACION
# ------------------------------------------------------------

$memberUrl       = "http://localhost:8081"
$classUrl        = "http://localhost:8082"
$trainerUrl      = "http://localhost:8083"
$equipmentUrl    = "http://localhost:8084"
$notificationUrl = "http://localhost:8085"

# ------------------------------------------------------------
# FUNCION PARA MOSTRAR RESULTADOS HTTP
# ------------------------------------------------------------

function Test-GetEndpoint {
    param(
        [string]$Name,
        [string]$Url
    )

    Write-Host ""
    Write-Host "------------------------------------------------------------"
    Write-Host $Name
    Write-Host $Url
    Write-Host "------------------------------------------------------------"

    try {
        $response = Invoke-WebRequest `
            -Uri $Url `
            -Method GET `
            -UseBasicParsing `
            -ErrorAction Stop

        Write-Host "HTTP: $($response.StatusCode)" -ForegroundColor Green

        if ($response.Content) {
            try {
                $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
            }
            catch {
                Write-Host $response.Content
            }
        }

        return $true
    }
    catch {
        if ($_.Exception.Response) {
            Write-Host "HTTP ERROR: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        }
        else {
            Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
        }

        return $false
    }
}

function Test-PostEndpoint {
    param(
        [string]$Name,
        [string]$Url,
        [object]$Body,
        [int]$ExpectedStatus = 200
    )

    Write-Host ""
    Write-Host "------------------------------------------------------------"
    Write-Host $Name
    Write-Host $Url
    Write-Host "------------------------------------------------------------"

    $json = $Body | ConvertTo-Json -Depth 10

    try {
        $response = Invoke-WebRequest `
            -Uri $Url `
            -Method POST `
            -ContentType "application/json; charset=utf-8" `
            -Body $json `
            -UseBasicParsing `
            -ErrorAction Stop

        Write-Host "HTTP: $($response.StatusCode)" -ForegroundColor Green

        if ($response.Content) {
            try {
                $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
            }
            catch {
                Write-Host $response.Content
            }
        }

        if ($response.StatusCode -eq $ExpectedStatus) {
            Write-Host "RESULTADO: OK" -ForegroundColor Green
            return $true
        }
        else {
            Write-Host "RESULTADO: STATUS INESPERADO" -ForegroundColor Yellow
            return $false
        }
    }
    catch {
        if ($_.Exception.Response) {
            $status = $_.Exception.Response.StatusCode.value__

            Write-Host "HTTP ERROR: $status" -ForegroundColor Yellow

            if ($status -eq $ExpectedStatus) {
                Write-Host "RESULTADO: OK - STATUS ESPERADO" -ForegroundColor Green
                return $true
            }
        }
        else {
            Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
        }

        return $false
    }
}

# ============================================================
# 2. COMPROBAR PUERTOS
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "1. COMPROBANDO PUERTOS"
Write-Host "============================================================"

$ports = @(8081,8082,8083,8084,8085,5672)

foreach ($port in $ports) {

    $connection = Get-NetTCPConnection `
        -LocalPort $port `
        -State Listen `
        -ErrorAction SilentlyContinue

    if ($connection) {
        Write-Host "Puerto $port : LISTENING" -ForegroundColor Green
    }
    else {
        Write-Host "Puerto $port : NO ESTA ESCUCHANDO" -ForegroundColor Red
    }
}

# ============================================================
# 3. COMPROBAR PROCESOS JAVA 17
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "2. COMPROBANDO JAVA 17"
Write-Host "============================================================"

java -version

Write-Host ""

$java17 = Get-CimInstance Win32_Process -Filter "Name='java.exe'" |
    Where-Object {
        $_.CommandLine -like "*jdk-17*"
    }

if ($java17) {
    Write-Host "Se encontraron procesos ejecutándose con Java 17." -ForegroundColor Green
    $java17 | Select-Object ProcessId, CommandLine | Format-Table -AutoSize
}
else {
    Write-Host "NO se encontraron procesos Java 17." -ForegroundColor Red
}

# ============================================================
# 4. GET MEMBERS
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "3. MEMBER SERVICE"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET MEMBERS" `
    "$memberUrl/api/members"

# ============================================================
# 5. GET TRAINERS
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "4. TRAINER SERVICE"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET TRAINERS" `
    "$trainerUrl/api/trainers"

# ============================================================
# 6. GET EQUIPMENT
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "5. EQUIPMENT SERVICE"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET EQUIPMENT" `
    "$equipmentUrl/api/equipment"

# ============================================================
# 7. GET CLASSES
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "6. CLASS SERVICE"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET CLASSES" `
    "$classUrl/api/classes"

# ============================================================
# 8. GET TRAINER POR ID
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "7. TRAINER POR ID"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET TRAINER ID 1" `
    "$trainerUrl/api/trainers/1"

# ============================================================
# 9. VERIFICAR EXISTS
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "8. TRAINER EXISTS"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET TRAINER 1 EXISTS" `
    "$trainerUrl/api/trainers/1/exists"

# ============================================================
# 10. CREAR ENTRENADOR
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "9. CREAR ENTRENADOR"
Write-Host "============================================================"

$newTrainer = @{
    nombre       = "Diana Torres"
    especialidad = "CrossFit"
}

Test-PostEndpoint `
    "POST TRAINER" `
    "$trainerUrl/api/trainers" `
    $newTrainer

# ============================================================
# 11. COMPROBAR ENTRENADORES DESPUES DEL POST
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "10. VERIFICAR ENTRENADOR CREADO"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET TRAINERS DESPUES DEL POST" `
    "$trainerUrl/api/trainers"

# ============================================================
# 12. CREAR CLASE VALIDA
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "11. CREAR CLASE VALIDA"
Write-Host "============================================================"

$newClass = @{
    nombre          = "CrossFit Nocturno"
    horario         = "2026-08-10T19:00:00"
    capacidadMaxima = 12
    entrenadorId    = 3
}

Test-PostEndpoint `
    "POST CLASS CON ENTRENADOR EXISTENTE" `
    "$classUrl/api/classes" `
    $newClass

# ============================================================
# 13. COMPROBAR CLASE Y ENRIQUECIMIENTO
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "12. VERIFICAR COMUNICACION CLASS -> TRAINER"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET CLASSES CON ENTRENADOR ENRIQUECIDO" `
    "$classUrl/api/classes"

# ============================================================
# 14. CREAR CLASE CON ENTRENADOR INEXISTENTE
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "13. PROBAR ENTRENADOR INEXISTENTE"
Write-Host "============================================================"

$invalidClass = @{
    nombre          = "Clase Fantasma"
    horario         = "2026-08-10T19:00:00"
    capacidadMaxima = 5
    entrenadorId    = 999
}

Test-PostEndpoint `
    "POST CLASS CON TRAINER 999" `
    "$classUrl/api/classes" `
    $invalidClass `
    409

# ============================================================
# 15. GET POR ID
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "14. GET POR ID"
Write-Host "============================================================"

Test-GetEndpoint `
    "GET MEMBER 1" `
    "$memberUrl/api/members/1"

Test-GetEndpoint `
    "GET CLASS 1" `
    "$classUrl/api/classes/1"

Test-GetEndpoint `
    "GET TRAINER 1" `
    "$trainerUrl/api/trainers/1"

Test-GetEndpoint `
    "GET EQUIPMENT 1" `
    "$equipmentUrl/api/equipment/1"

# ============================================================
# 16. RABBITMQ - NOTIFICACION ASINCRONA DE NUEVO MIEMBRO
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "15. RABBITMQ - NUEVO MIEMBRO (member.registered)"
Write-Host "============================================================"

$newMember = @{
    nombre = "Sofia Ramirez"
    email  = "sofia.ramirez.$([int](Get-Date -UFormat %s))@test.com"
}

Test-PostEndpoint `
    "POST MEMBER (dispara evento member.registered)" `
    "$memberUrl/api/members" `
    $newMember `
    201

Write-Host ""
Write-Host "Esperando a que notification-service consuma el evento..."
Start-Sleep -Seconds 2

Test-GetEndpoint `
    "GET NOTIFICATIONS (debe incluir BIENVENIDA_MIEMBRO)" `
    "$notificationUrl/api/notifications"

# ============================================================
# 17. RABBITMQ - PUB/SUB DE CAMBIOS DE HORARIO
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "16. RABBITMQ - CAMBIOS DE HORARIO (class.created/updated/cancelled)"
Write-Host "============================================================"

$updatedClass = @{
    nombre          = "CrossFit Nocturno Avanzado"
    horario         = "2026-08-10T20:00:00"
    capacidadMaxima = 15
    entrenadorId    = 3
}

Write-Host ""
Write-Host "------------------------------------------------------------"
Write-Host "PUT CLASS 3 (dispara evento class.updated)"
Write-Host "------------------------------------------------------------"
try {
    $response = Invoke-WebRequest `
        -Uri "$classUrl/api/classes/3" `
        -Method PUT `
        -ContentType "application/json; charset=utf-8" `
        -Body ($updatedClass | ConvertTo-Json -Depth 10) `
        -UseBasicParsing `
        -ErrorAction Stop
    Write-Host "HTTP: $($response.StatusCode)" -ForegroundColor Green
    $response.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "------------------------------------------------------------"
Write-Host "DELETE CLASS 3 (dispara evento class.cancelled)"
Write-Host "------------------------------------------------------------"
try {
    $response = Invoke-WebRequest -Uri "$classUrl/api/classes/3" -Method DELETE -UseBasicParsing -ErrorAction Stop
    Write-Host "HTTP: $($response.StatusCode)" -ForegroundColor Green
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "Esperando a que notification-service consuma los eventos de clase..."
Start-Sleep -Seconds 2

Test-GetEndpoint `
    "GET NOTIFICATIONS (debe incluir CLASE_PROGRAMADA / CLASE_ACTUALIZADA / CLASE_CANCELADA)" `
    "$notificationUrl/api/notifications"

# ============================================================
# 18. H2 CONSOLES
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "17. H2 CONSOLES"
Write-Host "============================================================"

Test-GetEndpoint `
    "H2 MEMBER" `
    "$memberUrl/h2-console"

Test-GetEndpoint `
    "H2 CLASS" `
    "$classUrl/h2-console"

Test-GetEndpoint `
    "H2 TRAINER" `
    "$trainerUrl/h2-console"

Test-GetEndpoint `
    "H2 EQUIPMENT" `
    "$equipmentUrl/h2-console"

Test-GetEndpoint `
    "H2 NOTIFICATION" `
    "$notificationUrl/h2-console"

# ============================================================
# RESUMEN
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "             PRUEBAS TERMINADAS"
Write-Host "============================================================"
Write-Host ""

Write-Host "Microservicios esperados:"
Write-Host "  Member       -> http://localhost:8081"
Write-Host "  Class        -> http://localhost:8082"
Write-Host "  Trainer      -> http://localhost:8083"
Write-Host "  Equipment    -> http://localhost:8084"
Write-Host "  Notification -> http://localhost:8085"
Write-Host "  RabbitMQ UI  -> http://localhost:15672 (guest/guest)"

Write-Host ""
Write-Host "Endpoints principales:"
Write-Host "  GET  /api/members"
Write-Host "  GET  /api/classes"
Write-Host "  GET  /api/trainers"
Write-Host "  GET  /api/equipment"
Write-Host "  GET  /api/notifications"
Write-Host "  PUT/DELETE /api/classes/{id}"

Write-Host ""
Write-Host "Comunicacion REST:"
Write-Host "  class-service -> trainer-service"

Write-Host ""
Write-Host "Comunicacion asincrona (RabbitMQ, exchange topic gym.events):"
Write-Host "  member-service -> notification-service (member.registered)"
Write-Host "  class-service  -> notification-service (class.created/updated/cancelled)"

Write-Host ""
Write-Host "============================================================"
Write-Host "FIN"
Write-Host "============================================================"