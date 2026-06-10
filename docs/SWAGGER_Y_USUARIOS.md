# Swagger y separacion de datos por usuario - Nevoa

## Objetivo de la modificacion

Esta version agrega documentacion OpenAPI/Swagger a los microservicios de Nevoa y refuerza el manejo de datos por usuario.

Cada recurso funcional se asocia a `usuarioId`, por ejemplo:

- tareas por usuario
- proyectos por usuario
- notas por usuario
- metas por usuario
- habitos por usuario
- sesiones Pomodoro por usuario

Esto permite explicar que los datos de un usuario no se mezclan con los de otro.

## Swagger centralizado por Gateway

Con el sistema levantado, abrir:

```text
http://localhost:7091/swagger-ui.html
```

En esa pantalla se muestran las APIs de:

- Usuario Service
- Tarea Service
- Proyecto Service
- Nota Service
- Meta Service
- Habito Service
- Pomodoro Service
- Dashboard Service

El Gateway obtiene los documentos OpenAPI de cada microservicio por rutas internas:

```text
/docs/usuario/v3/api-docs
/docs/tareas/v3/api-docs
/docs/proyectos/v3/api-docs
/docs/notas/v3/api-docs
/docs/metas/v3/api-docs
/docs/habitos/v3/api-docs
/docs/pomodoros/v3/api-docs
/docs/dashboard/v3/api-docs
```

## Swagger directo por microservicio

Para exposicion academica tambien se publicaron los puertos de cada microservicio:

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

## Endpoints recomendados por usuario

Ademas de los endpoints administrativos existentes, se agregaron endpoints con usuario en la ruta.

Ejemplos:

```text
POST /api/v1/tareas/usuario/{usuarioId}
GET  /api/v1/tareas/usuario/{usuarioId}
GET  /api/v1/tareas/usuario/{usuarioId}/{id}
PUT  /api/v1/tareas/usuario/{usuarioId}/{id}
DELETE /api/v1/tareas/usuario/{usuarioId}/{id}
PATCH /api/v1/tareas/usuario/{usuarioId}/{id}/completar
```

Esta misma idea se aplica a proyectos, notas, metas, habitos y pomodoros.

## Comandos para validar

```powershell
docker network create ms-net
docker compose up -d --build
```

Luego abrir:

```text
http://localhost:7081
http://localhost:7091/swagger-ui.html
```

## Pruebas sugeridas

1. Registrar usuario:

```text
POST http://localhost:7091/api/v1/auth/register
```

2. Crear tarea para usuario:

```text
POST http://localhost:7091/api/v1/tareas/usuario/1
```

3. Listar tareas del usuario:

```text
GET http://localhost:7091/api/v1/tareas/usuario/1
```

4. Verificar que cada servicio responde:

```text
GET http://localhost:7091/api/v1/tareas/instancia
GET http://localhost:7091/api/v1/usuarios/instancia
GET http://localhost:7091/api/v1/metas/instancia
```

## Explicacion para exposicion

Ningun controlador por si solo se presenta como microservicio. Cada microservicio de Nevoa es una aplicacion Spring Boot independiente, con su propio controlador, capa de servicio, repositorio, entidad, DTO, configuracion, contenedor Docker, registro en Eureka y base de datos independiente.

Los endpoints con `usuarioId` permiten evidenciar que las tareas y demas recursos pertenecen a un usuario especifico. Esto mantiene separacion de datos y evita mezclar informacion entre usuarios.
