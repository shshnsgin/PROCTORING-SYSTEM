# Proctoring System Backend

Backend для управления онлайн-экзаменами с системой прокторинга.

## Стек технологий

| Категория | Технология |
|-----------|-----------|
| Backend   | Java 17, Spring Boot 3.2 |
| База данных | PostgreSQL 16 |
| Кэш | Redis 7 |
| Очередь | Apache Kafka |
| Безопасность | JWT, Spring Security, RBAC |
| Миграции | Liquibase |
| DevOps | Docker, Kubernetes, GitHub Actions |
| Тесты | JUnit 5, Testcontainers |

## Быстрый запуск

```bash
git clone <repo>
cd proctoring-system
docker compose up -d
```

API: `http://localhost:8080`  
Admin: `admin@proctoring.kz` / `Admin12345`

## Архитектура

```
Controller → Service → Repository → Domain
```

- **Controller** — принимает HTTP запросы, валидирует
- **Service** — бизнес-логика, аудит, Kafka
- **Repository** — работа с PostgreSQL через JPA
- **Domain** — сущности: User, Exam, ExamSession, SessionEvent

## API Endpoints

### Auth
| Метод | Путь | Описание |
|-------|------|----------|
| POST | /api/auth/register | Регистрация |
| POST | /api/auth/login | Вход |
| POST | /api/auth/refresh | Обновление токена |

### Exams
| Метод | Путь | Роль |
|-------|------|------|
| GET | /api/exams | ALL |
| POST | /api/exams | ADMIN |
| PUT | /api/exams/{id} | ADMIN |
| DELETE | /api/exams/{id} | ADMIN |

### Sessions
| Метод | Путь | Роль |
|-------|------|------|
| POST | /api/sessions/start/{examId} | STUDENT |
| POST | /api/sessions/{id}/end | STUDENT |
| POST | /api/sessions/{id}/events | STUDENT |
| GET | /api/sessions | ADMIN, PROCTOR |
| GET | /api/sessions/{id}/report | ADMIN, PROCTOR |

### Admin
| Метод | Путь | Роль |
|-------|------|------|
| GET | /api/users | ADMIN |
| PATCH | /api/users/{id}/toggle | ADMIN |
| GET | /api/audit | ADMIN |

## Роли пользователей

- **ROLE_ADMIN** — полный доступ
- **ROLE_PROCTOR** — просмотр сессий и отчетов
- **ROLE_STUDENT** — управление своими сессиями

## Типы событий

| Событие | Серьезность |
|---------|------------|
| PHONE_DETECTED | CRITICAL |
| MULTIPLE_FACES | CRITICAL |
| FACE_NOT_DETECTED | HIGH |
| BROWSER_MINIMIZE | HIGH |
| TAB_SWITCH | MEDIUM |
| COPY_PASTE | MEDIUM |
| HEAD_TURN | LOW |
| NOISE_DETECTED | LOW |

> При 5+ нарушениях HIGH/CRITICAL сессия автоматически получает статус VIOLATED.

## Запуск тестов

```bash
mvn test
```

## Kubernetes

```bash
kubectl apply -f k8s/secrets.yml
kubectl apply -f k8s/deployment.yml
```
