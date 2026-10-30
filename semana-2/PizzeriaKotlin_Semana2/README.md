# Aplicando utilidades de Kotlin — Semana 2 (PMY2201)

## Descripción
Sistema de consola Kotlin/JVM para simular la gestión de una pizzería. Se creó conforme al caso de negocio y la pauta de evaluación de Duoc UC. No es una aplicación Android con interfaz gráfica; desarrolla la lógica del negocio solicitada con IntelliJ IDEA.

## Instrucciones para ejecutar en IntelliJ IDEA
1. Descomprime el ZIP.
2. Abre IntelliJ IDEA y crea un proyecto nuevo **Kotlin** con JDK 17 o superior, usando sistema de compilación IntelliJ (Kotlin/JVM).
3. Copia `src/Main.kt` en la carpeta `src` del proyecto (reemplazando cualquier `Main.kt` de ejemplo).
4. Abre `Main.kt`, haz clic en el botón verde al lado de `fun main()` y elige **Run 'MainKt'**.
5. Observa el catálogo, inventario, procesamiento de pedidos, reportes, errores controlados y mensajes simulados en la consola.

**Otra opción:** Con Java instalado, ejecutar `java -jar evidencias/Pizzeria.jar` desde la carpeta del proyecto. El JAR está incluido y fue compilado en el entorno de elaboración.

## Estructura
- `src/Main.kt`: código fuente completo.
- `evidencias/Pizzeria.jar`: programa ejecutable compilado.
- `evidencias/salida_ejecucion.txt`: salida de prueba.
- `README.md`: explicación de requisitos y ejecución.

## Relación con la pauta
1. **Flujos, arreglos y strings:** `Inventario.prepararLote()` aplica `when`, las existencias se revisan antes de aceptarse el pedido, `pedidosDelDia` es `Array<Pedido>`, `for` imprime el reporte y `filter` agrupa según despacho. `validarTelefono` y `validarCorreo` aplican reglas con Regex y entregan valores alternativos.
2. **Funciones y colecciones:** `calcularTotales` calcula subtotal, descuento e IVA; `cantidadesParaLote` suma ingredientes y `prepararLote` descuenta inventario de manera conjunta. Se usan `Map`, `listOf`, `setOf`, `find` y `sortedBy`.
3. **POO:** Clases `Producto`, `Pedido`, `Usuario`, `Cliente`, `Repartidor`, con métodos `detallarProducto`, `calcularTotal`, `cambiarEstado` y `entregarPedido`. `Usuario` es `open`, y `Cliente` sobrescribe `descripcion`.
4. **Errores y nulos:** `try-catch` en ingreso y validación de precios y existencias; `?.`, `?:` en notas; `let` en asignación de repartidores y notificaciones simuladas.
5. **Resumen:** `Pedido.resumen()` incluye cliente, contacto, productos, cantidades, subtotal, descuento, IVA, total, estado y notas.
6. **Entrega:** todo el trabajo se incluye en ZIP.

## Casos de prueba incluidos
- **Pedido #1:** 4 pizzas, aplica descuento del 10%, sale a reparto con el repartidor disponible más cercano.
- **Pedido #2:** falta pepperoni, el sistema rechaza el intento, informa faltantes, reabastece y permite la preparación.
- **Datos inválidos:** correo y teléfono incorrectos se reemplazan por valores de ejemplo; una cantidad negativa, entrada no numérica, precio negativo y reabastecimiento negativo generan mensajes controlados.
- **Null safety:** notas ausentes se imprimen como `Sin notas` y solo se simula notificación a correos válidos.

## Decisiones de implementación
- Los precios son enteros en CLP. Se redondean impuestos y descuentos al peso más cercano.
- IVA de ejemplo: 19%; descuento de ejemplo: 10% sobre el subtotal para 4 o más pizzas. El impuesto se calcula sobre el monto después del descuento.
- Los ingredientes se miden en unidades simplificadas por pizza.
- Las distancias de repartidores se suministran como datos ficticios, no se calculan con GPS.
- Los estados del enunciado son `En preparación`, `Listo` y `En camino`. El reporte cuenta como **en camino** los pedidos despachados, no necesariamente entregados, y considera los demás pendientes de despacho.
- Los pedidos y datos del inventario son simulaciones en memoria; se reinician en cada ejecución.

## Cómo explicar la actividad
El programa primero registra productos y disponibilidad. Cada pedido verifica el inventario antes de reservar ingredientes, evitando existencias negativas y pedidos sin stock. Luego calcula descuentos e impuestos, actualiza el estado y puede asignar el repartidor libre más cercano. Finalmente imprime un reporte general y comprobantes individuales. Las validaciones controlan los datos erróneos y los operadores seguros evitan fallos por valores nulos.
