# Task Manager API

[![CI - Build and Test](https://github.com/cezarnovaes/Task-Manager/actions/workflows/ci.yml/badge.svg)](https://github.com/cezarnovaes/Task-Manager/actions/workflows/ci.yml)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

API RESTful completa para gerenciamento de tarefas, desenvolvida com Spring Boot. Inclui autenticação JWT, CRUD completo, paginação, filtros e documentação Swagger.

## 🚀 Demo

**API em produção:** [https://task-manager-api-2ysn.onrender.com](https://task-manager-api-2ysn.onrender.com)

## ✨ Funcionalidades

- ✅ Autenticação JWT (registro/login)
- ✅ CRUD completo de tarefas
- ✅ Paginação e ordenação
- ✅ Filtros por status e prioridade
- ✅ Validação de dados
- ✅ Tratamento global de erros
- ✅ Documentação Swagger/OpenAPI
- ✅ Testes unitários e de integração

## 🛠️ Tecnologias

### Backend
- **Java 17**
- **Spring Boot 3.2**
- **Spring Security + JWT**
- **Spring Data JPA**
- **PostgreSQL** (produção)
- **H2 Database** (desenvolvimento)
- **Maven**
- **JUnit 5 + Mockito**
- **Swagger/OpenAPI**

### DevOps
- **GitHub Actions** (CI/CD)
- **Docker**
- **Render** (deploy)

## 📁 Estrutura do Projeto

```
Task-Manager/
├── backend/           # API Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/cezar/taskapi/
│   │   │   │       ├── config/        # Configurações (Security, Swagger)
│   │   │   │       ├── controller/    # Endpoints REST
│   │   │   │       ├── dto/           # Data Transfer Objects
│   │   │   │       ├── model/         # Entidades JPA
│   │   │   │       ├── repository/    # Interfaces de persistência
│   │   │   │       └── service/       # Lógica de negócio
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/                      # Testes
│   └── pom.xml
├── docs/              # Documentação
│   └── api/
│       └── postman_collection.json
└── README.md
```

## 🏃 Como Executar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- Git

### Desenvolvimento Local

```bash
# Clone o repositório
git clone https://github.com/cezarnovaes/task-manager.git
cd task-manager

# Entre na pasta do backend
cd backend

# Execute a aplicação
./mvnw spring-boot:run

# Ou no Windows
mvnw.cmd spring-boot:run
```

A API estará disponível em: `http://localhost:8080`

### Acessar H2 Console (Desenvolvimento)
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:taskdb;DB_CLOSE_DELAY=-1`
- User: `sa`
- Password: *(vazio)*

## 📚 Documentação da API

### Swagger UI

PRODUÇÃO - Acesse: `https://task-manager-api-2ysn.onrender.com/swagger-ui.html`
DEV - Acesse: `http://localhost:8080/swagger-ui.html`

### Endpoints Principais

#### Autenticação
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/users/register` | Registrar usuário |
| POST | `/api/auth/login` | Login (retorna JWT) |

#### Tarefas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/tasks` | Listar tarefas (paginado) |
| GET | `/api/tasks/{id}` | Buscar tarefa por ID |
| POST | `/api/tasks` | Criar tarefa |
| PUT | `/api/tasks/{id}` | Atualizar tarefa |
| DELETE | `/api/tasks/{id}` | Deletar tarefa |

#### Parâmetros de Query (GET /api/tasks)
| Parâmetro | Tipo | Descrição |
|-----------|------|-----------|
| `status` | String | Filtrar por status: `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED` |
| `page` | Integer | Número da página (começa em 0) |
| `size` | Integer | Itens por página (default: 10) |
| `sortBy` | String | Campo para ordenação: `title`, `createdAt`, `priority` |
| `direction` | String | Direção: `ASC` ou `DESC` |

### Exemplos de Requisições

#### Registrar Usuário
```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "password": "senha123"
  }'
```

#### Criar Tarefa
```bash
curl -X POST "http://localhost:8080/api/tasks?userId=1" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Estudar Spring Boot",
    "description": "Completar tutorial",
    "priority": "HIGH",
    "status": "IN_PROGRESS"
  }'
```

#### Listar Tarefas com Filtros
```bash
curl "http://localhost:8080/api/tasks?userId=1&status=PENDING&page=0&size=5&sortBy=createdAt&direction=DESC"
```

### Postman Collection
Importe a collection completa: [`docs/api/postman_collection.json`](docs/api/postman_collection.json)

## 🧪 Testes

```bash
cd backend

# Executar todos os testes
./mvnw test

# Executar com cobertura
./mvnw test jacoco:report
```

Relatório de cobertura: `backend/target/site/jacoco/index.html`

## 🐳 Docker

```bash
# Build da imagem
docker build -t task-manager-api ./backend

# Executar container
docker run -p 8080:8080 task-manager-api

# Ou usar docker-compose (com PostgreSQL)
docker-compose up -d
```

## 🚀 Deploy

### Render (Recomendado - Gratuito)
1. Conecte seu repositório GitHub ao Render
2. Configure as variáveis de ambiente:
   - `SPRING_PROFILES_ACTIVE=prod`
   - `DATABASE_URL` (PostgreSQL do Render)
   - `JWT_SECRET` (sua chave secreta)
3. Deploy automático a cada push na `main`

## 📊 Modelo de Dados

```
┌─────────────────┐       ┌─────────────────────┐
│      User       │       │        Task         │
├─────────────────┤       ├─────────────────────┤
│ id              │───┐   │ id                  │
│ name            │   │   │ title               │
│ email           │   │   │ description         │
│ password        │   └──<│ user_id (FK)        │
│ created_at      │       │ status              │
└─────────────────┘       │ priority            │
                          │ due_date            │
                          │ created_at          │
                          │ updated_at          │
                          └─────────────────────┘
```

## 🗺️ Roadmap

- [x] CRUD de usuários
- [x] CRUD de tarefas
- [x] Paginação e filtros
- [x] Validações
- [x] Autenticação JWT
- [x] Documentação Swagger
- [x] Testes unitários
- [x] Testes de integração
- [x] Docker
- [x] CI/CD (GitHub Actions)
- [x] Deploy (Render)
- [ ] Frontend React

## 🤝 Contribuindo

1. Fork o projeto
2. Crie sua branch (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Add: nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

## 👤 Autor

**Cézar Novaes**

- GitHub: [@cezarnovaes](https://github.com/cezarnovaes)
- LinkedIn: [Cézar Novaes](https://linkedin.com/in/cezar-novaes-12a898193/)
- Email: cezarnovaes14@gmail.com

---

⭐ Se este projeto te ajudou, considere dar uma estrela!