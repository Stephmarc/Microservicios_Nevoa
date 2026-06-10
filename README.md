# Névoa - Sistema distribuido de productividad personal

Névoa es una plataforma distribuida para organizar productividad personal mediante usuarios, tareas, proyectos, notas, metas, hábitos, sesiones Pomodoro y un dashboard resumen. La solución está preparada para la Unidad 1 de Desarrollo de Aplicaciones Distribuidas: configuración externa, descubrimiento de servicios, API Gateway, bases de datos independientes, Docker y preparación para múltiples instancias.

## Arquitectura

- `config-server` centraliza la configuración de todos los servicios.
- `registry-server` registra y descubre servicios mediante Eureka.
- `gateway-server` funciona como punto único de entrada por rutas `lb://`.
- Cada microservicio de dominio tiene su propia base de datos MySQL.
- `dashboard-service` queda preparado para integraciones posteriores; en U1 expone resumen operativo sin Feign.

## Microservicios

| Servicio | Puerto | Responsabilidad |
|---|---:|---|
| config-server | 7071 | Configuración centralizada |
| registry-server | 7081 | Eureka / descubrimiento |
| gateway-server | 7091 | Entrada única |
| usuario-service | 8081 | Registro, login y usuarios |
| tarea-service | 8082 | Tareas y prioridades |
| proyecto-service | 8083 | Proyectos y progreso |
| nota-service | 8084 | Notas y categorías |
| meta-service | 8085 | Metas y avance |
| habito-service | 8086 | Hábitos y rachas |
| pomodoro-service | 8087 | Sesiones Pomodoro |
| dashboard-service | 8088 | Resumen general |

## Requisitos

- Docker Desktop
- Docker Compose
- Java 17 y Maven 3.9+ solo si se ejecuta localmente sin Docker

## Ejecución completa con Docker

```bash
cp .env.example .env
docker network create ms-net
docker compose up -d --build
```

Verificar infraestructura:

```bash
curl http://localhost:7071/usuario-service/prod
curl http://localhost:7081
curl http://localhost:7091/api/v1/usuarios/instancia
```

## Pruebas rápidas por Gateway

Registrar usuario:

```bash
curl -X POST http://localhost:7091/api/v1/auth/register   -H "Content-Type: application/json"   -d '{"nombreCompleto":"Midwar Coila","email":"midwar@nevoa.com","password":"123456"}'
```

Login:

```bash
curl -X POST http://localhost:7091/api/v1/auth/login   -H "Content-Type: application/json"   -d '{"email":"midwar@nevoa.com","password":"123456"}'
```

Crear tarea:

```bash
curl -X POST http://localhost:7091/api/v1/tareas   -H "Content-Type: application/json"   -d '{"usuarioId":1,"proyectoId":null,"titulo":"Terminar documentación","descripcion":"Completar entregable de Névoa","prioridad":"MEDIA","fechaLimite":"2026-04-30"}'
```

Crear proyecto:

```bash
curl -X POST http://localhost:7091/api/v1/proyectos   -H "Content-Type: application/json"   -d '{"usuarioId":1,"nombre":"Diseño de App","descripcion":"Prototipo y backend distribuido de Névoa"}'
```

Crear nota:

```bash
curl -X POST http://localhost:7091/api/v1/notas   -H "Content-Type: application/json"   -d '{"usuarioId":1,"titulo":"Ideas para presentación","categoria":"Proyecto","contenido":"Explicar arquitectura, Eureka, Gateway y Docker."}'
```

Crear meta:

```bash
curl -X POST http://localhost:7091/api/v1/metas   -H "Content-Type: application/json"   -d '{"usuarioId":1,"titulo":"Aprender Spring Cloud","descripcion":"Dominar microservicios","categoria":"APRENDIZAJE","fechaLimite":"2026-05-30"}'
```

Crear hábito:

```bash
curl -X POST http://localhost:7091/api/v1/habitos   -H "Content-Type: application/json"   -d '{"usuarioId":1,"nombre":"Leer 30 minutos"}'
```

Crear Pomodoro:

```bash
curl -X POST http://localhost:7091/api/v1/pomodoros   -H "Content-Type: application/json"   -d '{"usuarioId":1,"minutosTrabajo":25,"minutosDescanso":5}'
```

Dashboard:

```bash
curl http://localhost:7091/api/v1/dashboard/resumen/1
```

## Preparación para múltiples instancias

El gateway usa `lb://nombre-servicio` y Eureka resuelve las instancias activas. Los microservicios no publican puertos fijos al host en `docker-compose.yml`; se exponen solo dentro de la red `ms-net` para permitir escalado sin conflicto de puertos. Para simular más instancias:

```bash
docker compose up -d --scale tarea-service=2
curl http://localhost:7091/api/v1/tareas/instancia
```

Para ver los contenedores activos:

```bash
docker compose ps
```

## Estructura del repositorio

```text
nevoa-microservicios/
├── docker-compose.yml
├── .env.example
├── infra/
│   ├── config-repo/
│   ├── config-server/
│   ├── registry-server/
│   └── gateway-server/
└── services/
    ├── usuario-service/
    ├── tarea-service/
    ├── proyecto-service/
    ├── nota-service/
    ├── meta-service/
    ├── habito-service/
    ├── pomodoro-service/
    └── dashboard-service/
```

## Swagger / OpenAPI

Esta version incluye documentacion Swagger para los microservicios de Nevoa.

Swagger centralizado por API Gateway:

```text
http://localhost:7091/swagger-ui.html
```

Swagger directo por microservicio:

```text
http://localhost:8081/swagger-ui.html  usuario-service
http://localhost:8082/swagger-ui.html  tarea-service
http://localhost:8083/swagger-ui.html  proyecto-service
http://localhost:8084/swagger-ui.html  nota-service
http://localhost:8085/swagger-ui.html  meta-service
http://localhost:8086/swagger-ui.html  habito-service
http://localhost:8087/swagger-ui.html  pomodoro-service
http://localhost:8088/swagger-ui.html  dashboard-service
```

Los recursos funcionales se manejan por usuario mediante rutas recomendadas como:

```text
/api/v1/tareas/usuario/{usuarioId}
/api/v1/proyectos/usuario/{usuarioId}
/api/v1/notas/usuario/{usuarioId}
/api/v1/metas/usuario/{usuarioId}
/api/v1/habitos/usuario/{usuarioId}
/api/v1/pomodoros/usuario/{usuarioId}
```

Ver mas detalles en `docs/SWAGGER_Y_USUARIOS.md`.
