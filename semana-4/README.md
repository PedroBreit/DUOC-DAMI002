\# Semana 4 — PizzaApp Android



\## Asignatura



\*\*Desarrollo de Aplicaciones Móviles I (PMY2201)\*\*

\*\*Institución:\*\* Duoc UC

\*\*Actividad:\*\* Configurando Android Studio

\*\*Estudiante:\*\* Pedro Breit



\## Descripción



PizzaApp es una aplicación móvil desarrollada en Kotlin con Android Studio y Jetpack Compose para gestionar pedidos de una pizzería.



El proyecto adapta a Android las clases y reglas de negocio desarrolladas durante las semanas anteriores de la asignatura, incorporando una interfaz gráfica y un flujo de compra y despacho.



\## Funcionalidades



\- Pantalla de bienvenida y navegación entre etapas.

\- Catálogo de pizzas con precios e ingredientes.

\- Selección de productos mediante controles de cantidad.

\- Validación de disponibilidad de ingredientes.

\- Registro y selección de clientes.

\- Validación de teléfonos y correos electrónicos mediante Regex.

\- Cálculo de subtotal, descuentos e IVA del 19 %.

\- Promociones por cantidad y período de vigencia.

\- Confirmación de pedidos y actualización del inventario.

\- Reposición de ingredientes mediante cantidades ingresadas.

\- Seguimiento de pedidos desde preparación hasta despacho.

\- Asignación automática del repartidor disponible más cercano.

\- Liberación de repartidores al completar entregas.



\## Tecnologías utilizadas



\- Kotlin

\- Android Studio

\- Jetpack Compose

\- Material 3

\- Gradle con Kotlin DSL

\- Android SDK

\- Emulador Pixel 4 con Android 16 (API 36)



\*\*SDK mínimo:\*\* API 23 (Android 6.0).



Se utiliza \*core library desugaring\* para permitir el uso de determinadas API modernas de Java en versiones anteriores de Android.



\## Arquitectura del proyecto



```text

semana-4/

├── README.md

├── evidencias/

│   ├── 01\_SDK\_Platforms.png

│   ├── ...

│   └── 20\_Stock\_Actualizado.png

└── pizzeria-app/

&#x20;   ├── app/

&#x20;   │   └── src/

&#x20;   │       └── main/

&#x20;   │           ├── java/com/example/pizzeriaapp/

&#x20;   │           │   ├── MainActivity.kt

&#x20;   │           │   ├── InventarioScreen.kt

&#x20;   │           │   ├── domain/

&#x20;   │           │   ├── logic/

&#x20;   │           │   └── ui/theme/

&#x20;   │           └── AndroidManifest.xml

&#x20;   ├── gradle/

&#x20;   ├── build.gradle.kts

&#x20;   └── settings.gradle.kts

```



\### Organización



\*\*domain:\*\* clases y entidades relacionadas con clientes, usuarios, productos, pedidos, repartidores e inventario.



\*\*logic:\*\* validaciones, cálculos de pedidos y operaciones de gestión de la pizzería.



\*\*ui/theme:\*\* colores, tipografía y configuración visual.



\*\*MainActivity.kt:\*\* navegación y flujo principal de la aplicación.



\*\*InventarioScreen.kt:\*\* pantalla para consultar y reponer ingredientes.



\## Reglas de negocio



\- Se permite seleccionar entre 1 y 100 productos.

\- Se aplica un descuento del 10 % desde cuatro pizzas.

\- Los productos promocionables pueden recibir un descuento adicional del 5 % durante noviembre de 2026.

\- El IVA del 19 % se calcula después de aplicar los descuentos.

\- Un pedido solo puede confirmarse si existen ingredientes suficientes.

\- Al confirmar un pedido se descuentan los ingredientes utilizados.

\- Los pedidos avanzan por los estados En preparación → Listo → En camino → Entregado. Al completar una entrega, el repartidor asignado vuelve a estar disponible.

\- Se asigna el repartidor disponible con menor distancia.

\- Al finalizar una entrega, el repartidor vuelve a estar disponible.



\## Ejecución



1\. Abrir Android Studio.

2\. Seleccionar la carpeta `semana-4/pizzeria-app`.

3\. Sincronizar Gradle.

4\. Seleccionar un dispositivo Android compatible.

5\. Ejecutar la configuración `app`.

6\. Ingresar desde la pantalla de bienvenida y seguir el proceso de compra.



\## Pruebas funcionales



Se realizaron pruebas de:



\- Ejecución de la aplicación en el emulador.

\- Selección de pizzas y control de cantidades.

\- Bloqueo de productos por falta de stock.

\- Registro y selección de clientes.

\- Cálculo de descuentos, impuestos y total.

\- Confirmación y seguimiento del pedido.

\- Asignación y liberación de repartidores.

\- Actualización y reposición del inventario.



Las capturas correspondientes están organizadas en la carpeta `evidencias`, con archivos numerados del 01 al 20.



\## Relación con semanas anteriores



La Semana 1 introdujo los fundamentos de Kotlin y el sistema inicial de pedidos.



La Semana 2 amplió la aplicación mediante programación orientada a objetos, inventario y estados de pedidos.



La Semana 3 incorporó conceptos avanzados como validaciones Regex, rangos, anotaciones, Reflection, comparación de objetos, desestructuración y sobrecarga de operadores.



La Semana 4 traslada la lógica a Android Studio y agrega una interfaz gráfica desarrollada con Jetpack Compose.



\## Consideraciones



\- La información se administra principalmente en memoria.

\- Los datos iniciales de clientes, productos, ingredientes y repartidores son ejemplos académicos.

\- La aplicación no utiliza una base de datos ni servicios externos.

\- Los precios se expresan en pesos chilenos.

\- El proyecto corresponde a una implementación educativa.



\## Evidencias



Las 20 capturas de pantalla de la actividad se encuentran en:



`semana-4/evidencias/`
