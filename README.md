===================================

**LevelApp**

**Descripción del Proyecto:**

Level-App es una aplicación móvil de comercio para la tienda Level-Up Gaming. La aplicación simula una tienda de productos electrónicos y permite una gestión de productos tanto para clientes como para administradores. El proyecto está construido siguiendo la arquitectura MVVM y utiliza SQLite para el almacenamiento de datos local.

===================================

## Estudiantes:

* Ignacio Farmer
* Matias Medina

===================================

## Funcionalidades Implementadas

**Autenticación de Usuarios**:
    * Registro de nuevos clientes.
    * Inicio de sesión para clientes y un rol de administrador (`admin:admin`).
**Panel de Administrador**:
    * Formulario para agregar nuevos productos a la tienda, incluyendo nombre, descripción, stock, precio, categoría e imagen.
    * Visualización en tiempo real de los productos agregados.
    * Opción para cerrar sesión.
**Tienda para Clientes (Home Screen)**:
    * Catálogo de productos organizazo.
    * Botón para añadir productos al carrito de compras.
    * Opción para cerrar sesión.
**Carrito de Compras**:
    * Visualización de los productos agregados.
    * Cálculo del total de la compra.
    * Opción para eliminar productos del carrito.
**Persistencia de Datos**: Toda la información de usuarios, productos y carrito se guarda localmente en una base de datos **SQLite**.
**Recursos Nativos**:
    * Acceso a la **galería** del dispositivo para seleccionar imágenes de productos.
    * Uso del **almacenamiento interno** para la base de datos.

    ===================================
