===================================

**LevelApp**

**Descripción del Proyecto:**

El proyecto consiste en un ecosistema completo (Cliente Móvil + Servidor Backend) que simula una tienda de videojuegos.
* **Para el Cliente:** Ofrece catálogo de productos, carrito de compras, perfil de usuario y solicitud de servicio técnico con geolocalización.
* **Para el Administrador:** Provee un panel de gestión (CRUD) para productos, con capacidad de importar datos reales desde la base de datos mundial de videojuegos (RAWG) y sincronización con el servidor central.

---

## Estudiantes:

* Matías Medina
* Ignacio Farmer

---

## Arquitectura y Tecnologías

El proyecto sigue el patrón de diseño **MVVM (Model-View-ViewModel)** para asegurar la separación de responsabilidades y la escalabilidad.

### Frontend (Android)
* **Lenguaje:** Kotlin.
* **UI:** Jetpack Compose (Material Design 3).
* **Navegación:** Navigation Compose.
* **Inyección de Dependencias:** ViewModel nativo.
* **Persistencia Local:** SQLite (SQLiteOpenHelper).
* **Red:** Retrofit + Gson.
* **Imágenes:** Coil.
* **Recursos Nativos:** Cámara, Galería, GPS (Location Services).

### Backend (Microservicios)
* **Framework:** Spring Boot 3.
* **Base de Datos:** MySQL.
* **ORM:** Spring Data JPA (Hibernate).
* **API:** RESTful Services.

---

## Funcionalidades Clave

### Autenticación y Perfil
* **Login/Registro:** Validación de formularios en tiempo real (correo, rut, contraseñas).
* **Perfil de Usuario:** Edición de foto de perfil utilizando **Cámara** o **Galería**. Las imágenes se guardan localmente en el dispositivo.

### Experiencia de Compra (Home & Cart)
* **Catálogo:** Visualización de productos con imágenes y precios.
* **Buscador Local:** Filtrado de productos en tiempo real.
* **Carrito de Compras:** Agregar/Quitar items, cálculo automático del total y persistencia en base de datos local.

### Servicio Técnico
* **Formulario:** Solicitud de reparación de consolas.
* **Geolocalización (GPS):** Detección automática de la dirección real del usuario mediante Google Location Services (sin mapas pesados, solo coordenadas y geocodificación inversa).

### Panel de Administrador
* **CRUD Completo:** Crear, Leer, Actualizar y Eliminar productos.
* **Sincronización Híbrida:** Los cambios se guardan en SQLite (para funcionamiento offline) y se replican en el servidor MySQL (Spring Boot).
* **Integración API Externa:** Buscador integrado con la API de **RAWG.io** para autocompletar datos de videojuegos (Nombre, Imagen, Rating, Descripción).

---

## API y Endpoints

### 1. Microservicios Propios (Spring Boot)
Base URL: `http://10.0.2.2:8080/` (Desde emulador) o IP Local.

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/products` | Obtiene todos los productos de la BD MySQL. |
| `POST` | `/api/products` | Crea un nuevo producto. |
| `PUT` | `/api/products/{id}` | Actualiza un producto existente. |
| `DELETE` | `/api/products/{id}` | Elimina un producto. |
| `POST` | `/api/auth/login` | Autenticación de usuarios. |

### 2. API Externa (RAWG Video Games Database)
Base URL: `https://api.rawg.io/api/`

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/games` | Búsqueda de videojuegos por nombre (requiere API Key). |

---

## ⚙️ Configuración y Ejecución

### Paso 1: Base de Datos
1.  Tener instalado **MySQL**.
2.  Crear una base de datos vacía llamada `levelupdb`.
    ```sql
    CREATE DATABASE levelupdb;
    ```

### Paso 2: Backend (Spring Boot)
1.  Abrir el proyecto `backend` en IntelliJ IDEA.
2.  Configurar `src/main/resources/application.properties` con tus credenciales de MySQL.
3.  Ejecutar la clase principal `LevelupBackendApplication`.
4.  Verificar que corra en el puerto `8080`.

### Paso 3: Aplicación Móvil (Android)
1.  Abrir el proyecto en **Android Studio**.
2.  Verificar la IP en `data/network/RetrofitClient.kt`:
    * Si usas Emulador: `http://10.0.2.2:8080/`
3.  Sincronizar Gradle y Ejecutar (`Run`).

---

## Pruebas Unitarias
Se han implementado pruebas unitarias con **JUnit** y **MockK** cubriendo la lógica de negocio crítica:
* **AuthViewModelTest:** Validación de reglas de negocio en registro y login.
* **ProductViewModelTest:** Lógica de filtrado y validación de inventario.
* **CartViewModelTest:** Cálculo matemático de totales.


---

## Entregables
* **Código Fuente:** Disponible en este repositorio.
* **APK Firmado:** Ubicado en la carpeta `/release` (`app-release.apk`).
* **Llave Keystore:** Archivo `keystore.jks` incluido en la raíz para fines de evaluación.

   ===================================
