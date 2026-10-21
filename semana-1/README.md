# Semana 1 — Mi primera app en Kotlin

**Asignatura:** Desarrollo de Aplicaciones Móviles I (PMY2201)  
**Institución:** Duoc UC  
**Actividad:** Mi primera app en Kotlin

## Descripción

Aplicación de consola desarrollada en Kotlin para registrar clientes y gestionar pedidos de una pizzería. El proyecto aplica los contenidos fundamentales de la primera semana: variables y tipos de datos, operadores, entrada y salida de datos, conversiones, expresiones condicionales y manejo de errores.

## Funcionalidades

- Registro de nombre, dirección y teléfono del cliente.
- Validación del formato del teléfono móvil chileno (9 dígitos, comienza con 9).
- Menú de cuatro pizzas con precios definidos.
- Selección de pizza y cantidad mediante consola.
- Descuento automático del **10 % para pedidos de más de 3 pizzas**.
- Control independiente del inventario de **7 ingredientes**: masa, salsa, queso, pepperoni, jamón, piña y champiñones.
- Verificación de los ingredientes necesarios para cada receta antes de confirmar el pedido.
- Indicación de ingredientes faltantes y regreso al menú para elegir otra pizza o cambiar la cantidad.
- Asignación de un repartidor de ejemplo y resumen del pedido con valores expresados en pesos chilenos.
- Validación de cantidades y opciones numéricas mediante `try-catch`.

## Pizzas disponibles

| Pizza | Ingredientes | Precio |
|---|---|---:|
| Pepperoni | Masa, salsa, queso y pepperoni | $10.000 |
| Hawaiana | Masa, salsa, queso, jamón y piña | $11.000 |
| Campestre | Masa, salsa, queso, jamón y champiñones | $12.000 |
| Especial | Masa, salsa, queso, pepperoni y champiñones | $13.000 |

## Requisitos

- IntelliJ IDEA con soporte para Kotlin.
- JDK 21 (versión utilizada durante el desarrollo).

## Ejecución

1. Abrir la carpeta `MiPrimeraAppKotlin` desde IntelliJ IDEA.
2. Verificar que el JDK esté configurado.
3. Abrir `src/Main.kt`.
4. Ejecutar la función `main()` con **Run 'MainKt'**.
5. Seguir las instrucciones que aparecen en la consola.

## Pruebas sugeridas

1. Seleccionar **4 pizzas Pepperoni**: debe aplicar un descuento de $4.000 y totalizar $36.000.
2. Seleccionar **2 pizzas Hawaianas**: al no haber piña en el inventario inicial, se informa el faltante y se vuelve al menú.
3. Ingresar letras donde se solicita una cantidad: la entrada se rechaza sin cerrar el programa.
4. Ingresar un teléfono inválido: se vuelve a solicitar hasta recibir un valor con el formato correcto.

## Consideraciones

- El inventario se almacena en memoria y se reinicia cada vez que se ejecuta el programa.
- Se confirma un pedido por ejecución; si faltan ingredientes, el usuario puede volver a elegir antes de confirmar.
- Los precios, recetas, existencias iniciales y repartidor son datos de ejemplo para fines educativos.
- Se trata de una aplicación de **consola Kotlin/JVM**, no de una aplicación Android con interfaz gráfica.

## Estructura del proyecto

```text
semana-1/
├── README.md
└── MiPrimeraAppKotlin/
    └── src/
        └── Main.kt
```

Los archivos de configuración de IntelliJ pueden existir adicionalmente en la carpeta del proyecto.
