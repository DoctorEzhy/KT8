## Проект: Task Manager (Kotlin + Spring Boot)

REST API для управления пользователями и их задачами. У задачи есть статус
(TODO, IN_PROGRESS, DONE) и дедлайн. База данных H2 (в памяти).

### Запуск
```bash
./gradlew bootRun
```
Сервер стартует на http://localhost:8080 (Windows: `gradlew.bat bootRun`).

### Эндпоинты
| Метод | Путь | Описание |
|---|---|---|
| POST | /api/users | создать пользователя |
| GET | /api/users/{id} | получить пользователя |
| GET | /api/users/{id}/dashboard | сводка: пользователь + статистика по статусам (async) |
| POST | /api/tasks | создать задачу (статус TODO) |
| GET | /api/tasks/{id} | получить задачу |
| PATCH | /api/tasks/{id}/start | перевести в IN_PROGRESS |
| PATCH | /api/tasks/{id}/complete | отметить выполненной (DONE) |
| DELETE | /api/tasks/{id} | удалить задачу |
| GET | /api/tasks/stream | стрим всех задач (Flow, SSE) |
| GET | /api/tasks/user/{userId}/stream | стрим задач пользователя (Flow, SSE) |
| GET | /api/tasks/user/{userId}/dashboard | то же, что /api/users/{id}/dashboard |

### Статусы задачи
TODO -> IN_PROGRESS -> DONE. Из TODO можно сразу в DONE. Из DONE перехода нет.
Недопустимый переход возвращает 409.

### Формат ошибок
```json
{"message": "Ошибка валидации", "errors": {"title": "Название обязательно"}}
```

### Что использовано из Kotlin
- data class для DTO: CreateUserRequest, CreateTaskRequest, UserResponse, TaskResponse, DashboardResponse, ErrorResponse
- enum class: TaskStatus (с правилами переходов canMoveTo)
- sealed class: UserResult, TaskResult (when без else в ResponseMappers.kt)
- Extension functions: User.toResponse(), Task.toResponse(), UserResult.toResponseEntity(), TaskResult.toResponseEntity()
- Null-safety: ?., ?:, findByIdOrNull, без оператора !!
- Корутины: suspend эндпоинты, async/await в UserService.getDashboard(), Flow в TaskService.streamTasks()
- Блокирующие вызовы JPA вынесены в withContext(Dispatchers.IO)
- Валидация: @field:NotBlank, @field:Positive, @field:Size, @field:Email; единый @RestControllerAdvic