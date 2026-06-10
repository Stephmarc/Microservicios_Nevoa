# Comandos de prueba para Névoa

## Infraestructura

```bash
cp .env.example .env
docker network create ms-net
docker compose up -d --build
```

## Config Server

```bash
curl http://localhost:7071/tarea-service/prod
curl http://localhost:7071/gateway-server/prod
```

## Eureka

Abrir en navegador:

```text
http://localhost:7081
```

## Gateway

```bash
curl http://localhost:7091/api/v1/usuarios/instancia
curl http://localhost:7091/api/v1/tareas/instancia
curl http://localhost:7091/api/v1/proyectos/instancia
curl http://localhost:7091/api/v1/notas/instancia
curl http://localhost:7091/api/v1/metas/instancia
curl http://localhost:7091/api/v1/habitos/instancia
curl http://localhost:7091/api/v1/pomodoros/instancia
curl http://localhost:7091/api/v1/dashboard/instancia
```

## Flujos funcionales

Ver `README.md` para ejemplos de creación de usuario, tareas, proyectos, notas, metas, hábitos y sesiones Pomodoro.
