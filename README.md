# ZeitBuilder

Plugin de IntelliJ IDEA para generar automáticamente el patrón Builder en clases Java.

## 🚀 Características

- **Generación automática**: Crea una clase interna Builder con métodos fluent
- **Selección de campos**: Elige qué campos incluir en el builder
- **Métodos generados**:
    - `builder()` - Método estático para crear el builder
    - `toBuilder()` - Método para crear una copia modificable
    - `build()` - Construye la instancia final
- **Integración IDE**: Disponible en el menú Generate (Alt+Insert)
- **Configuración persistente**: Recuerda tu preferencia de estilo de builder
- **Eliminación limpia**: Remueve artefactos de builders existentes

## 📖 Uso

1. Coloca el cursor dentro de una clase Java
2. Presiona **Alt+Insert** o ve a **Code** → **Generate**
3. Selecciona "Builder"
4. Elige los campos para incluir en el builder
5. Haz clic en "OK" para generar el código

## 📋 Compatibilidad

- **IntelliJ IDEA**: 2023.2+
- **Lenguajes**: Java