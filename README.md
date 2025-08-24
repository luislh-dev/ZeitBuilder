# ZeitBuilder

Plugin de IntelliJ IDEA para generar automáticamente el patrón Builder en clases Java.

## 🚀 Características

- **Generación automática**: Crea una clase interna Builder con métodos fluent
- **Dos estilos de builder**:
    - Builder tradicional (copia valores al final)
    - Builder basado en instancia (modifica directamente el objeto)
- **Selección de campos**: Elige qué campos incluir en el builder
- **Métodos generados**:
    - `builder()` - Método estático para crear el builder
    - `but()` - Método para crear una copia modificable
    - `build()` - Construye la instancia final
- **Integración IDE**: Disponible en el menú Generate (Alt+Insert)
- **Configuración persistente**: Recuerda tu preferencia de estilo de builder

## 📋 Compatibilidad

- **IntelliJ IDEA**: 2023.2+
- **Lenguajes**: Java