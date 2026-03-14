# Projeto 01 - Deploy de Aplicação Web com Docker na AWS

## Estrutura do Projeto

```
projeto/
├── backend/
│   ├── src/main/java/com/projeto/backend/
│   │   ├── BackendApplication.java
│   │   └── DataController.java
│   ├── src/main/resources/
│   │   └── application.properties  (porta 25000)
│   ├── pom.xml
│   └── Dockerfile
└── frontend/
    ├── src/main/java/com/projeto/frontend/
    │   ├── FrontendApplication.java
    │   └── PageController.java
    ├── src/main/resources/
    │   ├── application.properties  (porta 8080)
    │   └── templates/index.html
    ├── pom.xml
    └── Dockerfile
```

---

## IPs da AWS (anote aqui)

- **IP Público do Frontend:** 54.224.41.162
- **IP Privado do Backend:** 10.0.2.149

---

## PASSO 1 - Antes de subir para a AWS

Verifique o arquivo `frontend/src/main/resources/application.properties`:
```properties
backend.url=http://10.0.2.149:25000
```
O IP já está configurado. Se mudar, atualize aqui.

---

## PASSO 2 - Transferir arquivos para as VMs

No seu computador (na pasta onde está o arquivo minha-chave.pem):

```bash
# Transferir backend para a VM do frontend (que repassará ao backend)
scp -i minha-chave.pem -r ./backend ubuntu@54.224.41.162:~

# Transferir frontend para a VM do frontend
scp -i minha-chave.pem -r ./frontend ubuntu@54.224.41.162:~

# Transferir a chave para a VM do frontend (para acessar o backend)
scp -i minha-chave.pem minha-chave.pem ubuntu@54.224.41.162:~
```

---

## PASSO 3 - Instalar Docker nas VMs

### Conectar na VM do Frontend:
```bash
ssh -i minha-chave.pem ubuntu@54.224.41.162
```

### Instalar Docker (rode na VM):
```bash
sudo apt update
sudo apt install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ubuntu
newgrp docker
```

### Da VM do Frontend, acessar o Backend:
```bash
chmod 400 ~/minha-chave.pem
ssh -i ~/minha-chave.pem ubuntu@10.0.2.149
```

### Instalar Docker no Backend também (mesmos comandos acima)

---

## PASSO 4 - Deploy do Backend

Da VM do frontend, copiar e subir o backend:
```bash
# Copiar para o backend
scp -i ~/minha-chave.pem -r ~/backend ubuntu@10.0.2.149:~

# Conectar no backend
ssh -i ~/minha-chave.pem ubuntu@10.0.2.149

# Build e run
cd backend
docker build -t backend-app .
docker run -d -p 25000:25000 --name backend-container backend-app

# Verificar se subiu
docker ps
curl http://localhost:25000/api/dados
```

---

## PASSO 5 - Deploy do Frontend

Na VM do frontend:
```bash
cd ~/frontend
docker build -t frontend-app .
docker run -d -p 8080:8080 --name frontend-container frontend-app

# Verificar se subiu
docker ps
curl http://localhost:8080
```

---

## PASSO 6 - Testar

No navegador do seu computador:
```
http://54.224.41.162:8080
```

A página deve carregar com os dados vindos do backend!

---

## Comandos úteis Docker

```bash
# Ver containers rodando
docker ps

# Ver logs de um container
docker logs backend-container
docker logs frontend-container

# Parar um container
docker stop backend-container

# Remover um container
docker rm backend-container

# Rebuild após mudanças
docker stop frontend-container && docker rm frontend-container
docker build -t frontend-app .
docker run -d -p 8080:8080 --name frontend-container frontend-app
```

---

## Arquitetura

```
Internet → EC2 Frontend (subnet pública, IP: 54.224.41.162)
               porta 8080 (Docker)
                   ↓
           EC2 Backend (subnet privada, IP: 10.0.2.149)
               porta 25000 (Docker)
               [isolado - só acessível pelo frontend]
```
