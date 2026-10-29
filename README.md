# AutoAndes API

API REST del taller AutoAndes: clientes de consulta, servicios, vehículos, órdenes cabecera-detalle e ingresos por servicio.

## Ejecutar

Requiere Java 21 o superior y Oracle disponible en `localhost:1522/FREEPDB1`, con el esquema `AUTOANDESDB` ya creado.

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux/macOS: `sh mvnw spring-boot:run`.

La API abre en `http://localhost:8080/api/v1` y Swagger en `http://localhost:8080/swagger-ui.html`. Flyway aplica `V1__esquema_y_semilla.sql` y `V2__numero_orden.sql` al primer inicio. No ejecute el script manualmente encima del esquema migrado.

El perfil predeterminado es `dev`. `application.yaml`, `application-dev.yaml` y `application-prod.yaml` siguen la separación del proyecto base. En desarrollo se usan `AUTOANDESDB` y la contraseña local `123456`. Pueden cambiarse mediante `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `SERVER_PORT`. Hibernate valida el esquema creado por Flyway; no crea tablas por su cuenta.

En `prod`, configure `SPRING_PROFILES_ACTIVE=prod`, `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` (clave Base64 de al menos 32 bytes) y `FRONTEND_ORIGINS`. Swagger está deshabilitado en producción. El JWT dura 3600 segundos por defecto.

## Usuarios de la semilla

| Usuario | Contraseña de prueba | Rol | Estado |
| --- | --- | --- | --- |
| admin | Auto2026* | ADMIN | Activo |
| asesor | Auto2026* | ASESOR | Activo |
| jefe | Auto2026* | JEFE_TALLER | Activo |
| inactivo | Auto2026* | ASESOR | Inactivo |

Las contraseñas se guardan con BCrypt al primer arranque cuando su columna está vacía. El login devuelve el mismo 401 para usuario inexistente, clave incorrecta y cuenta inactiva. Ninguna respuesta incluye el hash.

## Arquitectura

`controller` → `service/service` → `service/impl` → `repository` → Oracle. Los controladores solo reciben/devuelven DTO. `ClienteService`, `ServicioService` y `VehiculoService` heredan de `service/generic/CrudService`; Clientes conserva únicamente consultas públicas en la API. Sus escrituras se rechazan porque el examen define los clientes como semilla de solo lectura.

Los DTO ordinarios usan clases con Lombok; las conversiones de negocio están en `convertirResponse` dentro de cada implementación, como en el proyecto base. `mapper/UsuarioMapper` convierte los datos de seguridad. El reporte usa un DTO en `dto/reporte` construido por una consulta JPQL con `GROUP BY`, `SUM` y orden descendente de importe.

Los enums están en `enums`; JWT, filtro, usuario de seguridad y permisos en `security`; configuraciones en `config`; errores en `exception` y `exception/dto`.

## Reglas principales

- Código único SRV-000 y placa única ABC-123. Un vehículo con órdenes no se elimina.
- Un servicio utilizado devuelve 409 al eliminarse; `PATCH /servicios/{id}/estado` permite darlo de baja. Un servicio sin órdenes sí puede eliminarse.
- Entre 1 y 10 detalles distintos, cantidades enteras entre 1 y 5 y servicios activos.
- La orden se registra con `@Transactional`; precios y total se calculan en el servidor. La SPA envía únicamente vehículo, kilometraje, servicios y cantidades.
- El vehículo se bloquea durante el registro para validar su último kilometraje incluso con peticiones concurrentes.
- Solo se anula una orden REGISTRADA del mismo día, con motivo de 10 a 200 caracteres.
- El reporte excluye ANULADAS. Las fechas de consulta son inclusivas para el usuario; `hasta` se convierte al comienzo del día siguiente.
- La secuencia de números es segura ante concurrencia. Un rollback puede consumir un número; nunca se reutiliza.

## Seguridad y contrato

Envíe `Authorization: Bearer <token>` a los endpoints protegidos. Los permisos se verifican con `hasAuthority` y `@PreAuthorize`, y se unen cuando un usuario tiene varios roles. El filtro comprueba firma y vencimiento y recupera el usuario activo desde la base en cada petición.

| Método | Ruta bajo `/api/v1` | Permiso |
| --- | --- | --- |
| POST | `/auth/login` | Público |
| GET | `/auth/me` | Autenticado |
| GET | `/clientes` | VEHICULO_LEER |
| GET | `/servicios`, `/servicios/{id}` | SERVICIO_LEER |
| POST / PUT / DELETE | `/servicios`, `/servicios/{id}` | SERVICIO_ESCRIBIR |
| PATCH | `/servicios/{id}/estado` | SERVICIO_ESCRIBIR |
| GET | `/vehiculos?clienteId`, `/vehiculos/{id}` | VEHICULO_LEER |
| POST / PUT / DELETE | `/vehiculos`, `/vehiculos/{id}` | VEHICULO_ESCRIBIR |
| POST | `/ordenes` | ORDEN_REGISTRAR |
| GET | `/ordenes?placa&estado&desde&hasta`, `/ordenes/{id}` | ORDEN_LEER |
| PATCH | `/ordenes/{id}/anular` | ORDEN_ANULAR |
| GET | `/reportes/ingresos-por-servicio?desde&hasta` | REPORTE_VER |

Las altas devuelven 201, las consultas/ediciones 200 y las eliminaciones 204. Los errores 400, 401, 403, 404, 409 y 500 usan `timestamp`, `status`, `error`, `mensaje` y `ruta`; los de validación incluyen `campos`.

```json
{"vehiculoId":1,"kilometraje":45000,"detalles":[{"servicioId":1,"cantidad":2},{"servicioId":2,"cantidad":1}]}
```

## Verificación

```powershell
.\mvnw.cmd test
.\mvnw.cmd package
```

Las pruebas de integración requieren el mismo Oracle disponible. Las escrituras de cada prueba se revierten; las secuencias pueden avanzar. Se comprueban CRUD, validaciones, cálculos, rechazo del tercer detalle inactivo, kilometraje, anulación, reporte, JWT, CORS y permisos. La colección importable está en `postman/AutoAndes.postman_collection.json`.

## Versionado

Ramas `main`, `develop` y `examen-a-huaccha`. Los commits identifican P1, P2 y P3; el frontend registra P4 y P5. Etiqueta de entrega: `v1.0-examen-u2`.
