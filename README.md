# ☁️ Projeto 01 — Deploy de Aplicação Web com Docker em Serviços de Nuvem

> Projeto da disciplina de Serviços em Nuvem — Deploy de aplicação Java Spring Boot com Docker em instâncias EC2 da AWS.

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.3-green?style=flat-square&logo=springboot)
![Docker](https://img.shields.io/badge/Docker-containerizado-blue?style=flat-square&logo=docker)
![AWS EC2](https://img.shields.io/badge/AWS-EC2-yellow?style=flat-square&logo=amazonaws)

---

## 📋 Sobre o Projeto

Aplicação web com frontend e backend separados, containerizados com Docker e hospedados em instâncias EC2 da AWS. A comunicação entre os serviços é feita via rede privada (VPC), com o backend isolado e acessível apenas pelo frontend.

**Acesso público:** `http://3.94.36.190:8080`

---

## 🏗️ Arquitetura

```
Internet
    │
    ▼
┌─────────────────────────────────────────┐
│           AWS VPC (minha-vpc)           │
│                                         │
│  ┌─────────────────┐                   │
│  │  subnet-publica  │                   │
│  │                  │                   │
│  │  ec2-frontend    │                   │
│  │  IP: 3.94.36.190 │ (Elastic IP)      │
│  │  Porta: 8080     │                   │
│  └────────┬─────────┘                   │
│           │ IP privado                  │
│           ▼                             │
│  ┌─────────────────┐                   │
│  │  subnet-privada  │                   │
│  │                  │                   │
│  │  ec2-backend     │                   │
│  │  IP: 10.0.2.149  │ (sem IP público)  │
│  │  Porta: 25000    │                   │
│  └─────────────────┘                   │
└─────────────────────────────────────────┘
```

---

## 🛠️ Tecnologias

- **Java 17** + **Spring Boot 3.2.3**
- **Thymeleaf** (template engine do frontend)
- **Docker** (multi-stage build)
- **AWS EC2** t3.micro — Ubuntu 24.04 LTS
- **AWS VPC** com subnets pública e privada
- **AWS Elastic IP** (IP fixo para o frontend)

---

## 📁 Estrutura do Projeto

```
projeto/
├── frontend/
│   ├── src/main/
│   │   ├── java/com/projeto/frontend/
│   │   │   ├── FrontendApplication.java
│   │   │   └── PageController.java
│   │   └── resources/
│   │       ├── templates/index.html
│   │       └── application.properties
│   ├── Dockerfile
│   └── pom.xml
│
└── backend/
    ├── src/main/
    │   ├── java/com/projeto/backend/
    │   │   ├── BackendApplication.java
    │   │   └── DataController.java
    │   └── resources/
    │       └── application.properties
    ├── Dockerfile
    └── pom.xml
```

---

## 🐳 Dockerfiles

### Frontend (porta 8080)

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Backend (porta 25000)

```dockerfile
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 25000
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## ☁️ Configuração da AWS

### Instâncias EC2

| Instância | IP Público | IP Privado | Subnet |
|-----------|-----------|-----------|--------|
| ec2-frontend | 3.94.36.190 (Elastic IP) | 10.0.1.153 | subnet-publica |
| ec2-backend | — | 10.0.2.149 | subnet-privada |

### Security Groups

**sg-frontend:**
- Porta 22 (SSH): `0.0.0.0/0`
- Porta 8080 (HTTP): `0.0.0.0/0`

**sg-backend:**
- Porta 22 (SSH): `10.0.1.153/32` (somente frontend)
- Porta 25000 (API): `10.0.1.153/32` (somente frontend)

---

## 🚀 Como rodar localmente

```bash
# Backend
cd projeto/backend
./mvnw clean package -DskipTests
docker build -t backend-app .
docker run -d -p 25000:25000 --name backend-container backend-app

# Frontend
cd projeto/frontend
./mvnw clean package -DskipTests
docker build -t frontend-app .
docker run -d -p 8080:8080 --name frontend-container frontend-app
```

Acesse: `http://localhost:8080`

---

## 🔁 Como subir na AWS (após End Lab)

1. Abrir AWS Academy → **Start Lab** → aguardar ponto verde
2. Acessar **EC2 → ec2-frontend → Connect → EC2 Instance Connect**
3. No terminal, rodar:

```bash
# Subir o backend
ssh -i ~/minha-chave.pem ubuntu@10.0.2.149 'sudo docker start backend-container'

# Subir o frontend
sudo docker start frontend-container
```

4. Acessar: `http://3.94.36.190:8080`

---

## 🧱 Desafios Encontrados

| Desafio | Solução |
|---------|---------|
| `Permission denied (publickey)` no SSH | Usar usuário `ubuntu` em vez de `ec2-user` (AMI Ubuntu) |
| Maven não reconhecia Java 17 | `sudo update-alternatives --set java .../java-17...` |
| Porta 25000 já alocada no Docker | `sudo docker rm -f backend` antes de recriar o container |
| Backend sem IP público | Transferir imagem via `docker save \| ssh ... docker load` |
| Maven não instalado no PC local | Compilar diretamente na EC2 após enviar o código via SCP |

---

## 👥 Integrantes

- konrado Ribeiro
- Vinicius Gomes

---

## Video

Link para o video do projeto: https://youtu.be/amJdaiGFBDA
