## Descripción

Existen cinco clases. Cada una tiene una responsabilidad, aunque algunas tienen más de la que deberían.

__Cliente__ guarda los datos de quien hace la reserva: nombre, email y si es socio. Es una clase simple y no tiene lógica. 

__Funcion__ representa una proyección concreta de una película, con título, sala, hora y precio. También lleva la cuenta de los asientos ocupados en una lista de textos como "F5". Su método ocupar(fila, numero) hace dos cosas a la vez: comprueba si el asiento está libre, lanza una excepción si no lo está, y lo marca como ocupado si sí. Además, ocupados es una lista pública, así que cualquier clase puede modificarla directamente.

__Reserva__ agrupa los datos de una reserva: id, cliente, función, asientos, precio y si está cancelada. Tiene un método setCancelada, que cambia el estado con un setter genérico en lugar de una operación con nombre claro.

__SistemaReservas__ es la clase principal y la que concentra más problemas. Contiene la lógica del negocio:
- hacer(...) crea una reserva. Recibe los datos del cliente y los asientos como parámetros sueltos, tiene un parámetro que no usa (imprimir) y devuelve números (-1, -2, -3) para indicar errores. Además, marca los asientos uno a uno, así que si uno falla, los anteriores quedan ocupados sin reserva.
- calcularPrecio(reserva, tipo) decide el precio según un texto ("general" o "socio").
- descuentoDel15 y descuentoDel25 son dos métodos casi idénticos, y el segundo no se usa.
- cancelar(id) busca la reserva, libera sus asientos y la marca como cancelada. Para buscarla hace un casteo (Reserva) cada vez.
- buscar(id) devuelve la reserva como Object, y quien la usa debe hacer el casteo.

__Main__ es el ejemplo de uso. Crea una función y dos reservas, una para Ana y otra para Luis. Luis pide F7 y F6, pero F6 ya está ocupado. Como F7 se marca antes de que falle F6, se queda bloqueado, y la salida Ocupados: lo muestra. Después se cancela la reserva de Ana y se intenta cancelar una reserva inexistente.


## Problemas detectados 

| # | Problema | Archivo | Código actual | Técnica |
|---|---|---|---|---|
| 1 | Nombre poco claro | `SistemaReservas.java` | `public int hacer(...)` | Rename method |
| 2 | Parámetro sin uso | `SistemaReservas.java` | `boolean imprimir` | Remove parameter |
| 3 | Parámetro recibido pero ignorado | `SistemaReservas.java` | `String canal` | Add parameter |
| 4 | Datos del cliente desglosados | `SistemaReservas.java` | `String nombre, String email, boolean socio` | Preserve whole object |
| 5 | Asientos como arrays paralelos | `SistemaReservas.java` | `char[] filas, int[] numeros` | Introduce parameter object |
| 6 | Precio pasado como parámetro pudiendo calcularse | `SistemaReservas.java` | `calcularPrecio(Reserva r, String tipo)` usa `r.funcion.precio` | Replace parameter with method |
| 7 | Tarifa elegida con texto | `SistemaReservas.java` | `socio ? "socio" : "general"` y `tipo.equals("socio")` | Replace parameter with explicit method |
| 8 | Descuentos duplicados | `SistemaReservas.java` | `descuentoDel15` y `descuentoDel25` | Parameterize method |
| 9 | Consulta y modificación mezcladas | `Funcion.java` | `ocupar()` valida, modifica y lanza excepción | Separate query from modifier |
| 10 | Estado interno expuesto | `Funcion.java` | `public List<String> ocupados` | Hide method |
| 11 | Setter innecesario | `Funcion.java` | `setPrecio(double)` | Remove setting method |
| 12 | Setter innecesario | `Reserva.java` | `setCancelada(boolean)` | Remove setting method |
| 13 | Downcast repetido | `SistemaReservas.java` | `(Reserva) db.get(id)` | Encapsulate downcast |
| 14 | Códigos de error numéricos | `SistemaReservas.java` | `return -1`, `-2`, `-3` | Replace error code with exception |
| 15 | Control de flujo con excepciones | `SistemaReservas.java` | `try { f.ocupar(...) } catch (IllegalStateException e)` | Replace exception with test |


---

## Refactorización aplicada

Cada cambio está marcado en el código con `// TÉCNICA UTILIZADA: ...` y, debajo, `// REFACTOR: Al aplicar esta técnica se realizaron los siguientes cambios...`.

| # | Técnica | Dónde se aplicó | Cambio realizado |
|---|---|---|---|
| 1 | Rename method | `SistemaReservas` | `hacer` → `crearReserva`, `buscar` → `buscarReserva`, `cancelar` → `cancelarReserva` (nombres que dicen qué hacen y son consistentes entre sí) |
| 2 | Remove parameter | `SistemaReservas.crearReserva` | Se eliminó `boolean imprimir` (nunca se usaba) |
| 3 | Add parameter | `Reserva` | El constructor recibe `canal` y lo guarda (`getCanal()`); antes el dato se perdía |
| 4 | Preserve whole object | `SistemaReservas.crearReserva` | Recibe `Cliente` completo en lugar de `nombre, email, socio` |
| 5 | Introduce parameter object | Nueva clase `Asiento` | Sustituye los arrays paralelos `char[] filas, int[] numeros` por `List<Asiento>` |
| 6 | Replace parameter with method | `SistemaReservas.calcularPrecio` | Se quitó `String tipo`; el método lo obtiene con `cliente.esSocio()` |
| 7 | Replace parameter with explicit methods | `SistemaReservas` | `calcularPrecioGeneral(base)` y `calcularPrecioSocio(base)` en lugar de comparar `"general"` / `"socio"` |
| 8 | Parameterize method | `SistemaReservas.aplicarDescuento` | `descuentoDel15` y `descuentoDel25` se unen en `aplicarDescuento(precio, porcentaje)` |
| 9 | Separate query from modifier | `Funcion` | `ocupar()` se divide en `estaOcupado(asiento)` (consulta) y `ocupar(asiento)` (modificación) |
| 10 | Hide method | `Funcion`, `SistemaReservas` | `ocupados`, `db` y `contador` pasan a `private`; métodos auxiliares de precio/validación pasan a `private`; `getOcupados()` devuelve lista no modificable |
| 11 | Remove setting method | `Funcion` | Se eliminó `setPrecio(double)`; `precio` es `final` |
| 12 | Remove setting method | `Reserva` | `setCancelada(boolean)` → operación `cancelar()` |
| 13 | Encapsulate downcast | `SistemaReservas` | Mapa tipado `Map<Integer, Reserva>`; `buscarReserva(id)` devuelve `Reserva`; desaparece el casteo en `cancelarReserva` y en `Main` |
| 14 | Replace error code with exception | `SistemaReservas`, nueva clase `ReservaException` | Ya no se devuelven `-1`, `-2`, `-3` ni `null`; se lanza `ReservaException` con un mensaje descriptivo |
| 15 | Replace exception with test | `SistemaReservas.validarDisponibilidad` | Se quitó el `try/catch (IllegalStateException)`; se pregunta antes con `funcion.estaOcupado(asiento)` |

### Corrección del bug de asientos bloqueados

`crearReserva` primero valida que **todos** los asientos estén libres y solo después los ocupa. Si alguno está ocupado, no se marca ninguno. Por eso F7 ya no queda bloqueado cuando falla la reserva de Luis.

### Decisiones de diseño adicionales

- **Precio en el constructor de `Reserva`:** el precio se calcula antes de crear la reserva y se pasa al constructor, así `Reserva` no necesita setter ni campo público de precio (misma idea que *Remove setting method*). Por eso `calcularPrecio` recibe `Cliente, Funcion, List<Asiento>` en lugar de la `Reserva`.
- **`Cliente` encapsulado:** al pasar el objeto completo (*Preserve whole object*), sus campos se hicieron `private final` con getters (`esSocio()` en vez de `socio`).
- **Validación de asientos repetidos:** antes, pedir `F5, F5` fallaba por la excepción de `ocupar()`. Al separar consulta y modificación esa protección desaparecía, así que se agregó una validación explícita en `validarDatos`.
- **`ReservaException` no comprobada:** extiende `RuntimeException` para no obligar a declarar `throws` en toda la cadena; `Main` la captura y muestra un error controlado.

## Cómo compilar y ejecutar

```bash
javac *.java
java Main
```

## Verificación

`salida_antes.txt` es la salida del proyecto original y `salida_despues.txt` la del refactorizado.

| Línea | Antes | Después |
|---|---|---|
| Reserva Ana | `1` | `1` (igual) |
| Reserva Luis | `-3` | `Error - Asiento ocupado: F6` (error controlado) |
| Ocupados | `[F5, F6, F7]` | `[F5, F6]` (bug corregido, F7 libre) |
| Precio Ana (socio) | `20.4` | `20.4` (igual) |
| Cancelar 1 | `0` | `OK` |
| Cancelar 99 | `-1` | `Error - No existe la reserva con id 99` (error controlado) |

## Diagramas de clase

- Original: `diagramas/diagrama_original.png` (fuente: `diagrama_original.mmd`)
- Refactorizado: `diagramas/diagrama_refactorizado.png` (fuente: `diagrama_refactorizado.mmd`)
