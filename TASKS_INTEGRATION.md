# 🐾 PetClinic — Examen de Pruebas de Integración

**Duración:** 2.5 horas | **Stack:** Spring Boot + JUnit 5 + Spring Test (`@SpringBootTest` + `MockMvc`) + H2

---

## 📌 Reglas generales

- Repositorio en GitHub o GitLab. Rama `main` protegida.
- Cada integrante trabaja en su propia rama `feature/...`.
- Toda integración a `main` se hace por merge con al menos 1 revisor del equipo.
- Cada integrante debe tener commits propios con su nombre/correo real.
- Mínimo: **una rama por integrante, un merge fusionado por integrante y 1 conflicto resuelto**.
- Al final, ejecutar `./mvnw clean test -Dspring.profiles.active=h2` debe pasar sin errores.

---

## 🎯 Objetivo

A diferencia del examen de pruebas unitarias (donde se mockean las dependencias con Mockito), aquí cada grupo debe escribir **pruebas de integración** que ejerciten los componentes reales conectados entre sí, **sin mocks**:

- **Capa de servicio** → `@SpringBootTest` + `@Autowired` del servicio real, cargando el contexto completo de Spring y la base de datos **H2 en memoria** poblada con `schema.sql` / `data.sql`.
- **Capa web (controlador)** → `@SpringBootTest` + `@AutoConfigureMockMvc` + `MockMvc`, ejerciendo los endpoints REST de extremo a extremo (controlador → servicio → repositorio → H2) y validando el JSON de respuesta con `jsonPath`.

Las pruebas se apoyan en los datos iniciales precargados. Las pruebas que crean/actualizan/borran deben ser **auto-contenidas e idempotentes**: cada prueba crea sus propios datos antes de modificarlos, para poder ejecutarse en cualquier orden.

Cada integrante tiene asignado su propio bloque de pruebas. Cada bloque debe ir en su propia rama y PR.

---

## 🧩 Estructura esperada de archivos

```
src/test/java/com/tecsup/petclinic/
├── services/        → pruebas de integración de servicio (@SpringBootTest + @Autowired)
│   ├── OwnerServiceTest.java
│   ├── VetServiceTest.java
│   ├── SpecialtyServiceTest.java
│   ├── TypeServiceTest.java
│   ├── VisitServiceTest.java
│   └── VetSpecialtyServiceTest.java
└── webs/            → pruebas de integración web (@SpringBootTest + @AutoConfigureMockMvc + MockMvc)
    ├── OwnerControllerTest.java
    ├── VetControllerTest.java
    └── ...
```

Tomar como **plantilla de referencia ya incluida en el proyecto**:
- `services/PetServiceTest.java` → patrón de prueba de integración de servicio.
- `webs/PetControllerTest.java` → patrón de prueba de integración web con `MockMvc`.

---

## 👥 Escenarios para grupos de 3 integrantes

### Grupo 1 — Pruebas de integración para OwnerService

**Integrante A — CRUD de servicio (integración con H2)**
- `testCreateOwner` — crear dueño vía servicio real y verificar que se asigna `id`.
- `testUpdateOwner` — crear, actualizar y re-leer desde H2 para confirmar el cambio.
- `testDeleteOwner` — crear, borrar y verificar que `findById` lanza `OwnerNotFoundException`.

**Integrante B — Consultas sobre datos semilla**
- `testFindOwnerById` — buscar dueño `id=1` (George Franklin) y validar nombre/apellido.
- `testFindByLastName` — `findByLastName("Davis")` debe devolver 2 registros (Betty y Harold).
- `testFindByCity` — `findByCity("Madison")` debe devolver los dueños de Madison.

**Integrante C — Pruebas de integración web (MockMvc)**
- `testFindAllOwners` — `GET /owners` → `200 OK`, `Content-Type` JSON y lista no vacía.
- `testFindOwnerById_OK` — `GET /owners/1` → `200 OK` y `jsonPath $.lastName = "Franklin"`.
- `testFindOwnerById_NotFound` — `GET /owners/666` → `404 Not Found`.

---

### Grupo 2 — Pruebas de integración para VetService

**Integrante A — CRUD de servicio (integración con H2)**
- `testCreateVet` — crear veterinario y verificar persistencia en H2.
- `testUpdateVet` — actualizar nombre/apellido y re-leer.
- `testFindVetById` — buscar `id=1` (James Carter) sobre datos semilla.

**Integrante B — Soft delete y reactivación (integración con H2)**
- `testDeactivateVet` — `active=false`; verificar que el registro **sigue existiendo** en H2.
- `testReactivateVet` — reactivar el vet 6 (Sharon Jenkins, inactivo) y confirmar `active=true`.
- `testFindActiveVets` — debe devolver solo los activos (5 de los 6 semilla).

**Integrante C — Pruebas de integración web (MockMvc)**
- `testFindAllVets` — `GET /vets` → `200 OK` y lista JSON.
- `testCreateVet_Web` — `POST /vets` con cuerpo JSON → `201 Created` y `jsonPath` del nombre creado.
- `testFindVetById_NotFound_Web` — `GET /vets/666` → `404 Not Found`.

---

### Grupo 3 — Pruebas de integración para SpecialtyService

**Integrante A — CRUD de servicio (integración con H2)**
- `testCreateSpecialty` — crear especialidad con `office`, `h_open`, `h_close`.
- `testUpdateSpecialty` — actualizar y re-leer desde H2.
- `testFindSpecialtyById` — buscar `id=1` (radiology) sobre datos semilla.

**Integrante B — Búsquedas sobre datos semilla**
- `testFindByOffice` — `findByOffice("Farewell")` devuelve radiology.
- `testFindByName` — `findByName("surgery")` devuelve la especialidad correcta.
- `testDeleteSpecialty` — crear, borrar y confirmar que ya no existe en H2.

**Integrante C — Validaciones y errores extremo a extremo**
- `testCreateSpecialty_InvalidSchedule` — crear con `h_open >= h_close` debe lanzar `InvalidScheduleException`.
- `testFindSpecialtyById_NotFound_Web` — `GET /specialties/666` → `404 Not Found`.
- `testCreateSpecialty_Web` — `POST /specialties` válido → `201 Created` y `jsonPath` del nombre.

---

### Grupo 6 — Pruebas de integración para TypeService

**Integrante A — CRUD de servicio (integración con H2)**
- `testCreateType` — crear tipo y verificar persistencia.
- `testUpdateType` — actualizar `description`/`active` y re-leer.
- `testFindTypeById` — buscar `id=1` (cat) sobre datos semilla.

**Integrante B — Reporte de mascotas por tipo (integración con H2)**
- `testGetPetCountByType` — reporte real cruzando `types` y `pets` (p. ej. cat = 4 mascotas: Leo, Samantha, Max, Sly).
- `testFindActiveTypes` — devuelve solo tipos con `active=true` (7 de los 8 semilla; excluye snake).
- `testDeleteType` — crear un tipo sin mascotas, borrarlo y confirmar que no existe.

**Integrante C — Filtros y errores extremo a extremo**
- `testGetPetCountByType_ExcludeInactive` — el reporte excluye tipos con `active=false` (snake).
- `testFindTypeById_NotFound_Web` — `GET /types/666` → `404 Not Found`.
- `testFindAllTypes_Web` — `GET /types` → `200 OK` y lista JSON.

---

## 👥 Escenarios para grupos de 4 integrantes

### Grupo 4 — Pruebas de integración para VisitService

**Integrante A — CRUD de servicio (integración con H2)**
- `testCreateVisit` — registrar visita (con `petId`, `vetId`, `visitDate`, `cost`) y verificar `id`.
- `testUpdateVisit` — actualizar descripción/costo y re-leer.
- `testFindVisitById` — buscar `id=1` (rabies shot) sobre datos semilla.

**Integrante B — Consultas por relación (integración con H2)**
- `testFindByPetId` — `findByPetId(7)` devuelve las visitas de Samantha (2 semilla).
- `testFindByVetId` — `findByVetId(2)` devuelve las visitas del vet James.
- `testDeleteVisit` — crear, borrar y confirmar que no existe.

**Integrante C — Cálculos y errores web**
- `testCalculateTotalCostByPet` — sumar costos con `BigDecimal` cruzando visitas reales de una mascota.
- `testFindVisitById_NotFound_Web` — `GET /visits/666` → `404 Not Found`.
- `testCreateVisit_Web` — `POST /visits` → `201 Created` y `jsonPath` de la descripción.

**Integrante D — Consultas por fecha (integración con H2)**
- `testFindByDateBetween` — visitas entre dos fechas (p. ej. todo 2024 → 2 visitas: ids 5 y 6).
- `testFindByVetIdAndDateBetween` — visitas de un vet dentro de un rango.
- `testCountVisitsByPet` — contar visitas de una mascota sobre datos semilla.

---

### Grupo 5 — Pruebas de integración para VetSpecialtyService

**Integrante A — Asignación (integración con H2)**
- `testAssignSpecialtyToVet` — asignar especialidad a un vet y verificar la fila en `vet_specialties`.
- `testRemoveSpecialtyFromVet` — quitar la asignación y confirmar que desaparece.
- `testAssignDuplicate_ShouldFail` — re-asignar la misma (PK compuesta `vet_id+specialty_id`) debe fallar.

**Integrante B — Consultas (integración con H2)**
- `testFindSpecialtiesByVet` — `findSpecialtiesByVet(3)` devuelve sus 2 especialidades semilla (surgery, dentistry).
- `testFindVetsBySpecialty` — `findVetsBySpecialty(1)` (radiology) devuelve los vets 2 y 5.
- `testFindSpecialtiesByVet_Empty` — un vet sin asignaciones (p. ej. vet 1) devuelve lista vacía.

**Integrante C — Especialidad principal (integración con H2)**
- `testSetPrimarySpecialty` — marcar `is_primary=true` y verificar persistencia.
- `testSetPrimarySpecialty_OnlyOnePrimary` — al marcar una nueva principal, las demás del vet quedan `false`.
- `testSetPrimarySpecialty_NotAssigned` — debe lanzar excepción si la especialidad no está asignada al vet.

**Integrante D — Experiencia y certificaciones (integración con H2)**
- `testFindVetsByMinExperience` — `findByMinExperience(1, 7)` devuelve vets de radiology con ≥7 años (vets 2 y 5).
- `testUpdateYearsExperience` — actualizar `years_experience` y re-leer desde H2.
- `testUpdateCertificationDate` — actualizar `certification_date` y verificar el cambio.

---

## 🧪 Ejemplo de referencia (servicio)

```java
@SpringBootTest
@Slf4j
public class OwnerServiceTest {

    @Autowired
    private OwnerService ownerService;   // bean REAL, sin mocks

    @Test
    public void testFindByLastName() {
        String LAST_NAME = "Davis";
        int SIZE_EXPECTED = 2;            // Betty y Harold (datos semilla)

        List<OwnerDTO> owners = this.ownerService.findByLastName(LAST_NAME);

        assertEquals(SIZE_EXPECTED, owners.size());
    }
}
```

## 🧪 Ejemplo de referencia (web / MockMvc)

```java
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testFindOwnerById_OK() throws Exception {
        this.mockMvc.perform(get("/owners/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.lastName", is("Franklin")));
    }
}
```

---

## 📏 Evaluación

| Criterio                                                                 | Puntos |
|--------------------------------------------------------------------------|--------|
| Uso de Git (ramas, merge, revisiones)                                    | 6      |
| Cada integrante completó sus pruebas de integración asignadas            | 4      |
| Resolución de conflictos                                                 | 3      |
| Calidad de la integración (sin mocks, asserts claros, datos H2 reales)   | 4      |
| Las pruebas pasan (`./mvnw clean test -Dspring.profiles.active=h2`)      | 3      |

> Penalización: cualquier prueba de este examen que use `@Mock`/`@MockBean`/`Mockito.when(...)` **no puntúa** en "calidad de la integración".

---

## 📦 Entregables

1. Enlace al repositorio.
2. Captura de `git log --all --graph --oneline`.
3. Captura de la ejecución verde de `./mvnw clean test -Dspring.profiles.active=h2`.
4. Sección al final del README del repo con: integrantes, pruebas de integración que hizo cada uno y conflictos resueltos.

