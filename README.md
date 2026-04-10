# Sudoku Program (Java + Swing)

Aplicación de Sudoku escrita en **Java** con interfaz gráfica **Swing**, motor de validación, análisis de candidatos y resolución paso a paso con explicación.

## ✨ ¿Qué hace este proyecto?

- Permite capturar y editar un tablero de Sudoku 9x9.
- Muestra candidatos posibles por:
  - celda,
  - fila,
  - columna,
  - bloque 3x3.
- Resuelve **el siguiente movimiento** y explica:
  - el método usado,
  - tipo de razonamiento,
  - celdas patrón,
  - celdas afectadas,
  - candidatos eliminados,
  - narrativa paso a paso.

## 🧩 Técnicas implementadas

### Lógica básica
- Único candidato en celda.
- Único lugar en fila.
- Único lugar en columna.
- Único lugar en bloque 3x3.

### Lógica intermedia
- Pares desnudos.
- Pares ocultos.
- Pointing pairs.

### Lógica avanzada
- X-Wing.
- Swordfish (filas y columnas).
- XY-Wing.
- Rectángulo único (tipo 1, validación estricta).

### Hipótesis / contradicción
- Cadenas forzadas (prueba de candidatos bivalue y descarte por contradicción).

> Nota: algunas técnicas avanzadas son de eliminación de candidatos. En este proyecto, un paso se aplica cuando la eliminación fuerza inmediatamente un valor en una celda.

## 🏗️ Estructura del proyecto

- `Main`: punto de entrada de la app.
- `Vista`: interfaz gráfica Swing.
- `TableroSudoku`: estado del tablero, celdas fijas y operaciones base.
- `ValidadorSudoku`: reglas de validación del Sudoku.
- `AnalizadorCandidatos`: cálculo de candidatos.
- `ResolutorSudoku`: motor de técnicas y resolución del siguiente paso.
- `PasoResolucion`: modelo de explicación enriquecida.
- `Coordenada`: modelo de coordenadas.
- `SudokuTest`: pruebas unitarias JUnit.

## ▶️ Cómo ejecutar

### Requisitos
- Java 8 o superior.
- Maven 3.x (opcional, para pruebas/build).

### Ejecutar desde IDE
1. Abre el proyecto Maven.
2. Ejecuta `com.flavio.Main`.

### Ejecutar por línea de comandos (sin Maven)
```bash
javac $(find src/main/java -name '*.java')
java -cp src/main/java com.flavio.Main
```

## ✅ Pruebas

Pruebas unitarias en:
- `src/test/java/com/flavio/SudokuTest.java`

Comando:
```bash
mvn test
```

Si tu entorno bloquea acceso a Maven Central, puede fallar la descarga de plugins/dependencias.

## 🧠 Enfoque didáctico

La app está pensada para explicar el razonamiento de cada jugada, no solo dar el resultado.

La salida de explicación incluye:
- técnica,
- tipo de razonamiento,
- unidad clave,
- celdas de patrón,
- celdas afectadas,
- candidatos eliminados,
- pasos narrados.

## 🚧 Mejoras futuras sugeridas

- Modo “Mostrar siguiente deducción” (eliminaciones sin forzar número).
- Resaltado visual avanzado por rol (patrón, eliminaciones, resultado).
- Historial de pasos con navegación adelante/atrás.
- Exportar/importar tableros (JSON/CSV).
