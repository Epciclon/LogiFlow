# ================================================
# Kong Configuration Script - LogiFlow
# ================================================

$KONG_ADMIN = "http://localhost:8001"

Write-Host "Configurando Kong Gateway para LogiFlow..." -ForegroundColor Cyan

# ================================================
# 1. AUTH SERVICE
# ================================================
Write-Host "`n[1/4] Configurando Auth Service..." -ForegroundColor Yellow

# Crear servicio
Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services" `
  -ContentType "application/json" `
  -Body (@{
    name = "auth-service"
    url = "http://auth-service:8082"
  } | ConvertTo-Json)

# Crear ruta
Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services/auth-service/routes" `
  -ContentType "application/json" `
  -Body (@{
    name = "auth-route"
    paths = @("/auth")
    strip_path = $true
  } | ConvertTo-Json)

Write-Host "✓ Auth Service configurado" -ForegroundColor Green

# ================================================
# 2. PEDIDO SERVICE
# ================================================
Write-Host "`n[2/4] Configurando Pedido Service..." -ForegroundColor Yellow

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services" `
  -ContentType "application/json" `
  -Body (@{
    name = "pedido-service"
    url = "http://pedido-service:8083"
  } | ConvertTo-Json)

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services/pedido-service/routes" `
  -ContentType "application/json" `
  -Body (@{
    name = "pedido-route"
    paths = @("/pedidos")
    strip_path = $true
  } | ConvertTo-Json)

Write-Host "✓ Pedido Service configurado" -ForegroundColor Green

# ================================================
# 3. FLEET SERVICE
# ================================================
Write-Host "`n[3/4] Configurando Fleet Service..." -ForegroundColor Yellow

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services" `
  -ContentType "application/json" `
  -Body (@{
    name = "fleet-service"
    url = "http://fleet-service:8084"
  } | ConvertTo-Json)

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services/fleet-service/routes" `
  -ContentType "application/json" `
  -Body (@{
    name = "fleet-route"
    paths = @("/fleet")
    strip_path = $true
  } | ConvertTo-Json)

Write-Host "✓ Fleet Service configurado" -ForegroundColor Green

# ================================================
# 4. BILLING SERVICE
# ================================================
Write-Host "`n[4/4] Configurando Billing Service..." -ForegroundColor Yellow

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services" `
  -ContentType "application/json" `
  -Body (@{
    name = "billing-service"
    url = "http://billing-service:8085"
  } | ConvertTo-Json)

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/services/billing-service/routes" `
  -ContentType "application/json" `
  -Body (@{
    name = "billing-route"
    paths = @("/billing")
    strip_path = $true
  } | ConvertTo-Json)

Write-Host "✓ Billing Service configurado" -ForegroundColor Green

# ================================================
# 5. CONFIGURAR CORS
# ================================================
Write-Host "`n[5/5] Configurando CORS..." -ForegroundColor Yellow

Invoke-RestMethod -Method POST -Uri "$KONG_ADMIN/plugins" `
  -ContentType "application/json" `
  -Body (@{
    name = "cors"
    config = @{
      origins = @("*")
      methods = @("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
      headers = @("Accept", "Authorization", "Content-Type")
      exposed_headers = @("X-Auth-Token")
      credentials = $true
      max_age = 3600
    }
  } | ConvertTo-Json -Depth 10)

Write-Host "✓ CORS configurado" -ForegroundColor Green

# ================================================
# VERIFICACIÓN
# ================================================
Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "Configuración completada!" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan

Write-Host "`nServicios configurados:" -ForegroundColor Yellow
$services = Invoke-RestMethod -Method GET -Uri "$KONG_ADMIN/services"
$services.data | ForEach-Object {
  Write-Host "  - $($_.name): $($_.host):$($_.port)" -ForegroundColor White
}

Write-Host "`nRutas configuradas:" -ForegroundColor Yellow
$routes = Invoke-RestMethod -Method GET -Uri "$KONG_ADMIN/routes"
$routes.data | ForEach-Object {
  Write-Host "  - $($_.name): $($_.paths -join ', ')" -ForegroundColor White
}

Write-Host "`n================================================" -ForegroundColor Cyan
Write-Host "Endpoints disponibles a través de Kong:" -ForegroundColor Green
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Auth:    http://localhost:8000/auth/api/auth/*" -ForegroundColor White
Write-Host "Pedidos: http://localhost:8000/pedidos/api/pedidos/*" -ForegroundColor White
Write-Host "Fleet:   http://localhost:8000/fleet/api/*" -ForegroundColor White
Write-Host "Billing: http://localhost:8000/billing/api/facturas/*" -ForegroundColor White
Write-Host "================================================`n" -ForegroundColor Cyan

Write-Host "Para probar los endpoints, ejecuta:" -ForegroundColor Yellow
Write-Host "  curl http://localhost:8000/auth/" -ForegroundColor White
Write-Host "  curl http://localhost:8000/pedidos/" -ForegroundColor White
Write-Host "  curl http://localhost:8000/fleet/" -ForegroundColor White
Write-Host "  curl http://localhost:8000/billing/`n" -ForegroundColor White
