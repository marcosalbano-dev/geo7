# 🚀 Guia Completo de Atualização - Geo7

Este guia te permite atualizar todo o sistema (Frontend + Backend + Docker + EC2) sempre que fizer alterações.

## 📋 Pré-requisitos

- ✅ Acesso SSH à instância EC2
- ✅ Docker instalado na EC2
- ✅ Node.js instalado localmente
- ✅ Maven instalado localmente
- ✅ Git configurado
- ✅ **OpenSSH Client instalado no Windows** (para usar `scp` e `ssh`)

### **Instalar OpenSSH no Windows (se necessário):**

Se você receber erro "scp não é reconhecido", instale o OpenSSH:

1. **Opção 1: Via PowerShell (Recomendado)**
   ```powershell
   # Abra PowerShell como Administrador e execute:
   Add-WindowsCapability -Online -Name OpenSSH.Client~~~~0.0.1.0
   ```

2. **Opção 2: Via Configurações do Windows**
   - Abra **Configurações** > **Aplicativos** > **Recursos Opcionais**
   - Clique em **Adicionar um recurso**
   - Procure por **OpenSSH Client**
   - Clique em **Instalar**

3. **Verificar instalação:**
   ```powershell
   # No PowerShell, execute:
   Get-Command scp
   # Deve mostrar: C:\Windows\System32\OpenSSH\scp.exe
   ```

---

## 🔄 Processo Completo de Atualização

### **PASSO 1: Atualizar Backend (Local)**

```bash
# 1. Navegar para o diretório do backend
cd C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7

# 2. Compilar o projeto
mvn clean package -DskipTests

# 3. Verificar se o JAR foi gerado
ls target/geo7-1.0-SNAPSHOT.jar
```

### **PASSO 2: Atualizar Frontend (Local)**

```bash
# 1. Navegar para o diretório do frontend
cd C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app

# 2. Garantir que está no branch correto
git checkout feat_campos_obrigatorios_front
git pull origin feat_campos_obrigatorios_front

# 3. Instalar dependências (se necessário)
npm install

# 4. Limpar build anterior
rm -rf dist/

# 5. Compilar para produção
npm run build

# 6. Verificar se os arquivos foram gerados
# O Angular gera em dist/geo7-app/browser/
ls dist/geo7-app/browser/
ls dist/geo7-app/browser/index.html

# 7. Verificar data de modificação dos arquivos
# (Os arquivos devem ter data/hora recente)
```

### **PASSO 3: Preparar Arquivos para Upload**

```bash
# 1. Criar diretório temporário para upload
mkdir C:\temp\geo7-update

# 2. Copiar JAR do backend
copy target\geo7-1.0-SNAPSHOT.jar C:\temp\geo7-update\

# 3. Copiar arquivos do frontend
# IMPORTANTE: O Angular gera em dist/geo7-app/browser/
# Copiamos o conteúdo de browser/ para frontend/
xcopy dist\geo7-app\browser\* C:\temp\geo7-update\frontend\ /E /I /Y

# 4. Verificar se o index.html foi copiado
dir C:\temp\geo7-update\frontend\index.html
```

### **PASSO 4: Upload para EC2**

```bash
# 1. Upload do backend
scp -i geo7-key.pem C:\temp\geo7-update\geo7-1.0-SNAPSHOT.jar ubuntu@18-228-94-6.sslip.io:/home/ubuntu/

# 2. Upload do frontend
scp -i geo7-key.pem -r C:\temp\geo7-update\frontend ubuntu@18-228-94-6.sslip.io:/home/ubuntu/
```

### **PASSO 5: Atualizar na EC2**

```bash
# 1. Conectar via SSH
ssh -i geo7-key.pem ubuntu@18-228-94-6.sslip.io

# 2. Parar containers Docker
sudo docker-compose down

# 3. Atualizar JAR do backend
sudo cp geo7-1.0-SNAPSHOT.jar /opt/geo7/

# 4. Atualizar frontend (FORÇAR atualização completa)
sudo rm -rf /var/www/html/*

# Detectar estrutura (Angular pode gerar em frontend/browser/)
if [ -f "frontend/browser/index.html" ]; then
    # Estrutura Angular com subdiretório browser/
    sudo cp -r frontend/browser/* /var/www/html/
else
    # Estrutura padrão
    sudo cp -r frontend/* /var/www/html/
fi

sudo chown -R www-data:www-data /var/www/html
sudo chmod -R 755 /var/www/html

# 5. Verificar arquivos atualizados
ls -la /var/www/html/index.html
head -5 /var/www/html/index.html

# 6. Reiniciar containers (força reload do nginx)
sudo docker-compose up -d

# 7. Aguardar inicialização
sleep 10

# 8. Verificar se está funcionando
sudo docker ps
curl http://localhost:8080/api/healthz

# 9. Limpar cache do navegador (instruir usuário a fazer Ctrl+Shift+R)
```

**⚠️ IMPORTANTE:** Após atualizar, o usuário deve limpar o cache do navegador:
- **Chrome/Edge:** `Ctrl+Shift+R` (Windows) ou `Cmd+Shift+R` (Mac)
- **Firefox:** `Ctrl+F5` (Windows) ou `Cmd+Shift+R` (Mac)

---

## 🛠️ Scripts Automatizados

### **Script 1: Atualização Local (Windows)**

Crie o arquivo `atualizar-local.ps1`:

```powershell
# Script para atualizar backend e frontend localmente
Write-Host "=== ATUALIZANDO SISTEMA LOCAL ===" -ForegroundColor Green

# 1. Atualizar Backend
Write-Host "`n1. Atualizando Backend..." -ForegroundColor Yellow
Set-Location "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7"
mvn clean package -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend compilado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro na compilação do backend" -ForegroundColor Red
    exit 1
}

# 2. Atualizar Frontend
Write-Host "`n2. Atualizando Frontend..." -ForegroundColor Yellow
Set-Location "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-app"
npm run build

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Frontend compilado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro na compilação do frontend" -ForegroundColor Red
    exit 1
}

# 3. Preparar arquivos
Write-Host "`n3. Preparando arquivos..." -ForegroundColor Yellow
Remove-Item -Path "C:\temp\geo7-update" -Recurse -Force -ErrorAction SilentlyContinue
New-Item -Path "C:\temp\geo7-update" -ItemType Directory -Force
New-Item -Path "C:\temp\geo7-update\frontend" -ItemType Directory -Force

# Copiar JAR
Copy-Item "target\geo7-1.0-SNAPSHOT.jar" "C:\temp\geo7-update\"

# Copiar frontend (IMPORTANTE: do diretório browser/)
if (Test-Path "dist\geo7-app\browser") {
    Copy-Item "dist\geo7-app\browser\*" "C:\temp\geo7-update\frontend\" -Recurse -Force
    Write-Host "✅ Frontend copiado de dist/geo7-app/browser/" -ForegroundColor Green
} else {
    # Fallback
    Copy-Item "dist\geo7-app\*" "C:\temp\geo7-update\frontend\" -Recurse -Force
    Write-Host "⚠️ Copiado de dist/geo7-app/ (browser/ não encontrado)" -ForegroundColor Yellow
}

# Verificar se index.html foi copiado
if (Test-Path "C:\temp\geo7-update\frontend\index.html") {
    Write-Host "✅ index.html encontrado no diretório de upload" -ForegroundColor Green
} else {
    Write-Host "❌ ERRO: index.html não encontrado!" -ForegroundColor Red
    exit 1
}

Write-Host "✅ Arquivos preparados em C:\temp\geo7-update" -ForegroundColor Green
Write-Host "`nPróximo passo: Execute o script de upload para EC2" -ForegroundColor Cyan
```

### **Script 2: Upload para EC2 (Windows)**

Crie o arquivo `upload-ec2.ps1`:

```powershell
# Script para fazer upload para EC2
Write-Host "=== FAZENDO UPLOAD PARA EC2 ===" -ForegroundColor Green

$keyPath = "C:\Users\marco\OneDrive\Documentos\projeto-geo7\geo7-key.pem"
$ec2Host = "ubuntu@18-228-94-6.sslip.io"
$localPath = "C:\temp\geo7-update"

# 1. Upload do backend
Write-Host "`n1. Enviando backend..." -ForegroundColor Yellow
scp -i $keyPath "$localPath\geo7-1.0-SNAPSHOT.jar" "$ec2Host:/home/ubuntu/"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Backend enviado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro no upload do backend" -ForegroundColor Red
    exit 1
}

# 2. Upload do frontend
Write-Host "`n2. Enviando frontend..." -ForegroundColor Yellow
scp -i $keyPath -r "$localPath\frontend" "$ec2Host:/home/ubuntu/"

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Frontend enviado com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro no upload do frontend" -ForegroundColor Red
    exit 1
}

Write-Host "`nPróximo passo: Execute o script de atualização na EC2" -ForegroundColor Cyan
```

### **Script 3: Atualização na EC2 (Linux)**

Crie o arquivo `atualizar-ec2.sh`:

```bash
#!/bin/bash

# Script para atualizar sistema na EC2
echo "=== ATUALIZANDO SISTEMA NA EC2 ==="

# 1. Parar containers
echo ""
echo "1. Parando containers..."
sudo docker-compose down

# 2. Atualizar backend
echo ""
echo "2. Atualizando backend..."
sudo cp geo7-1.0-SNAPSHOT.jar /opt/geo7/

# 3. Atualizar frontend
echo ""
echo "3. Atualizando frontend..."
sudo rm -rf /var/www/html/*
sudo cp -r frontend/* /var/www/html/

# 4. Reiniciar containers
echo ""
echo "4. Reiniciando containers..."
sudo docker-compose up -d

# 5. Aguardar inicialização
echo ""
echo "5. Aguardando inicialização..."
sleep 10

# 6. Verificar status
echo ""
echo "6. Verificando status..."
sudo docker ps
curl -s http://localhost:8080/api/healthz

if [ $? -eq 0 ]; then
    echo "✅ Sistema atualizado com sucesso!"
else
    echo "❌ Erro na atualização"
    exit 1
fi
```

---

## 🚀 Processo Rápido (3 Comandos)

### **Opção 1: Manual**
```bash
# 1. Local
.\atualizar-local.ps1

# 2. Upload
.\upload-ec2.ps1

# 3. EC2 (via SSH)
ssh -i geo7-key.pem ubuntu@18-228-94-6.sslip.io
chmod +x atualizar-ec2.sh
./atualizar-ec2.sh
```

### **Opção 2: Script Único**

Crie o arquivo `atualizar-tudo.ps1`:

```powershell
# Script completo de atualização
Write-Host "=== ATUALIZAÇÃO COMPLETA DO SISTEMA ===" -ForegroundColor Green

# 1. Atualizar local
Write-Host "`n1. Atualizando localmente..." -ForegroundColor Yellow
& ".\atualizar-local.ps1"

# 2. Upload para EC2
Write-Host "`n2. Fazendo upload para EC2..." -ForegroundColor Yellow
& ".\upload-ec2.ps1"

# 3. Atualizar na EC2
Write-Host "`n3. Atualizando na EC2..." -ForegroundColor Yellow
ssh -i geo7-key.pem ubuntu@18-228-94-6.sslip.io "chmod +x atualizar-ec2.sh && ./atualizar-ec2.sh"

Write-Host "`n✅ ATUALIZAÇÃO COMPLETA FINALIZADA!" -ForegroundColor Green
```

---

## 🔧 Configurações Importantes

### **Docker Compose (EC2)**
```yaml
# docker-compose.yml
version: '3.8'
services:
  app:
    image: openjdk:17-jre-slim
    container_name: geo7-app
    ports:
      - "8080:8080"
    volumes:
      - /opt/geo7/geo7-1.0-SNAPSHOT.jar:/app.jar
    command: java -jar /app.jar
    restart: unless-stopped

  nginx:
    image: nginx:alpine
    container_name: geo7-web
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - /var/www/html:/usr/share/nginx/html
      - ./nginx.conf:/etc/nginx/nginx.conf
    restart: unless-stopped
```

### **Nginx Config (EC2)**
```nginx
# nginx.conf
events {
    worker_connections 1024;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    server {
        listen 80;
        server_name 18-228-94-6.sslip.io;

        # Frontend
        location / {
            root /usr/share/nginx/html;
            index index.html;
            try_files $uri $uri/ /index.html;
        }

        # API Backend
        location /api/ {
            proxy_pass http://app:8080/api/;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}
```

---

## 📝 Checklist de Atualização

### **Antes de Atualizar:**
- [ ] Fazer backup dos arquivos atuais
- [ ] Verificar se não há usuários ativos
- [ ] Testar localmente primeiro

### **Durante a Atualização:**
- [ ] Backend compilado sem erros
- [ ] Frontend compilado sem erros
- [ ] Upload realizado com sucesso
- [ ] Containers reiniciados
- [ ] Health check passou

### **Após a Atualização:**
- [ ] Testar login
- [ ] Testar edição de lotes
- [ ] Testar exportação XML
- [ ] Verificar logs de erro
- [ ] Monitorar por alguns minutos

---

## 🆘 Troubleshooting

### **Problemas Comuns:**

1. **Erro de compilação:**
   ```bash
   # Limpar cache
   mvn clean
   npm cache clean --force
   ```

2. **Erro de upload:**
   ```bash
   # Verificar permissões da chave
   chmod 400 geo7-key.pem
   ```

3. **Container não inicia:**
   ```bash
   # Verificar logs
   sudo docker logs geo7-app
   sudo docker logs geo7-web
   ```

4. **Frontend não atualiza (problema de cache):**
   ```bash
   # Na EC2, executar script de atualização forçada
   chmod +x forcar-atualizacao-frontend.sh
   ./forcar-atualizacao-frontend.sh
   
   # OU fazer manualmente:
   sudo docker-compose down
   sudo rm -rf /var/www/html/*
   sudo cp -r frontend/* /var/www/html/
   sudo docker-compose up -d
   sudo docker exec geo7-web nginx -s reload
   ```
   
   **No navegador:**
   - Limpar cache: `Ctrl+Shift+R` (Windows) ou `Cmd+Shift+R` (Mac)
   - Ou abrir em modo anônimo/privado

5. **Endpoints retornando 401 (não autorizado):**
   ```bash
   # Verificar se o token JWT está sendo enviado
   # Fazer login novamente no frontend
   # Verificar logs do backend:
   sudo docker logs geo7-app | grep -i "401\|unauthorized"
   ```
   
   **Solução:** Fazer logout e login novamente no frontend para obter novo token JWT.

6. **Frontend não atualiza mesmo após atualização (cache do navegador):**
   ```bash
   # Na EC2, corrigir cache do nginx
   chmod +x fix-nginx-cache.sh
   ./fix-nginx-cache.sh
   ```
   
   **No navegador:**
   - Limpar cache: `Ctrl+Shift+R` (Windows) ou `Cmd+Shift+R` (Mac)
   - Ou abrir em modo anônimo/privado
   - Ou limpar cache completamente nas configurações do navegador

7. **Frontend não carrega:**
   ```bash
   # Verificar nginx
   sudo docker exec geo7-web nginx -t
   sudo docker exec geo7-web nginx -s reload
   
   # Verificar arquivos
   ls -la /var/www/html/
   
   # Debug completo
   chmod +x debug-frontend.sh
   ./debug-frontend.sh
   ```

---

## 🎯 Resumo do Processo

1. **Desenvolvimento** → Alterar código
2. **Compilação** → `mvn build` + `npm build`
3. **Upload** → `scp` para EC2
4. **Deploy** → Parar containers, atualizar arquivos, reiniciar
5. **Teste** → Verificar funcionamento

**Tempo total estimado: 5-10 minutos**

---

## 📞 Suporte

Se encontrar problemas:
1. Verificar logs: `sudo docker logs [container]`
2. Verificar status: `sudo docker ps`
3. Verificar conectividade: `curl http://localhost:8080/api/healthz`
4. Verificar arquivos: `ls -la /opt/geo7/` e `ls -la /var/www/html/`
