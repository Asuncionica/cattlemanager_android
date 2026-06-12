# CattleManager · Android

Aplicación móvil **Android** que actúa como **frontend** del sistema integral de gestión ganadera **CattleManager**. Permite a veterinarios, encargados y peones administrar la explotación desde el móvil: animales, eventos productivos, reproductivos y sanitarios, tareas, alertas y lotes genéticos.

> Este repositorio contiene únicamente el cliente Android. El backend REST (Spring Boot + PostgreSQL) se desarrolla y mantiene en un repositorio independiente.

---

## 🧭 Tabla de contenidos

- [Visión general](#-visión-general)
- [Arquitectura del proyecto completo](#-arquitectura-del-proyecto-completo)
- [Funcionalidades](#-funcionalidades)
- [Roles de usuario](#-roles-de-usuario)
- [Stack tecnológico](#-stack-tecnológico)
- [Estructura del código](#-estructura-del-código)
- [Requisitos](#-requisitos)
- [Configuración e instalación](#-configuración-e-instalación)
- [Conexión con el backend](#-conexión-con-el-backend)
- [Compilación y ejecución](#-compilación-y-ejecución)
- [Convenciones de ramas](#-convenciones-de-ramas)

---

## 📌 Visión general

**CattleManager** es un sistema diseñado para la gestión profesional de explotaciones ganaderas. La aplicación móvil ofrece a cada perfil de trabajador las herramientas que necesita en el campo, sincronizando la información en tiempo real con el servidor central.

---

## 🏛 Arquitectura del proyecto completo

```
┌────────────────────────┐        HTTPS/JSON         ┌─────────────────────────┐
│  CattleManager Android │  ◄────────────────────►   │   CattleManager API     │
│   (este repositorio)   │       Retrofit/Gson       │   Spring Boot + JPA     │
└────────────────────────┘                            └─────────────┬───────────┘
                                                                   │
                                                                   ▼
                                                        ┌─────────────────────┐
                                                        │     PostgreSQL      │
                                                        └─────────────────────┘
```

---

## ✨ Funcionalidades

- 🔐 **Autenticación** con sesión persistente cifrada (AndroidX Security)
- 🐄 **Animales**: alta, baja, edición, listado y detalle
- 🧬 **Lotes genéticos** asociados a animales
- 🥛 **Eventos productivos** (leche y otras producciones) con filtrado por animal o registro
- 🐂 **Eventos reproductivos** (inseminaciones, partos, etc.)
- 💉 **Eventos sanitarios** (vacunación, tratamientos)
- 🚨 **Alertas veterinarias** generadas y consultables
- ✅ **Tareas** asignadas al personal con vista de pendientes
- 🏡 **Granjas**: alta, edición y consulta
- 👥 **Gestión de usuarios** (solo encargado)

---

## 👥 Roles de usuario

| Rol           | Permisos principales |
|---------------|----------------------|
| **Encargado** | Acceso total: animales, usuarios, granjas, producción |
| **Veterinario** | Gestión sanitaria, alertas, eventos reproductivos |
| **Peón**      | Consultar animales, registrar producción, ver tareas |

---

## 🛠 Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | **Kotlin** |
| UI | View Binding + Material Components 1.9 |
| Red | **Retrofit 2.9** + Gson Converter |
| Asincronía | **Kotlin Coroutines** 1.7 |
| Seguridad | AndroidX Security Crypto (almacenamiento cifrado) |
| Ubicación | Google Play Services Location |
| Build | Gradle (Kotlin DSL) |
| SDK objetivo | `compileSdk 36` · `minSdk 24` · `targetSdk 36` |

---

## 📂 Estructura del código

```
app/src/main/java/com/example/cattlemanager/
├── activities/           # Pantallas principales y por rol
├── adapter/              # Adaptadores de RecyclerView
├── alertas/              # Alertas veterinarias
├── animal/               # API y lógica de animales
├── eventosproductivos/   # Producción (leche, etc.)
├── eventosreproductivos/ # Eventos reproductivos
├── eventossanitarios/    # Eventos sanitarios
├── granja/               # Gestión de granjas
├── model/                # Modelos de datos (DTOs)
├── network/              # Retrofit, interceptores, configuración HTTP
├── security/             # Sesión y almacenamiento cifrado
├── tareas/               # Tareas y pendientes
├── usuarios/             # Gestión de usuarios
└── util/                 # Utilidades comunes
```

---

## 📋 Requisitos

- **Android Studio** Hedgehog o superior
- **JDK 11**
- **Android SDK 36**
- Backend **CattleManager API** ejecutándose y accesible en red local
- Dispositivo físico o emulador con **Android 7.0 (API 24)** o superior

---

## ⚙️ Configuración e instalación

1. Clonar el repositorio:
   ```bash
   git clone -b main https://github.com/Asuncionica/cattlemanager_android.git
   cd cattlemanager_android
   ```
2. Abrir el proyecto en **Android Studio**.
3. Esperar a que Gradle sincronice las dependencias.
4. Configurar la IP del backend (ver siguiente sección).

---

## 🌐 Conexión con el backend

La URL del backend se define mediante `BuildConfig.BASE_URL` en [`app/build.gradle.kts`](app/build.gradle.kts):

```kotlin
buildConfigField("String", "BASE_URL", "\"http://<IP_DEL_BACKEND>:8085/\"")
```

Reemplaza `<IP_DEL_BACKEND>` por la IP del servidor:

| Escenario | Valor recomendado |
|-----------|-------------------|
| Dispositivo físico en la misma Wi-Fi | IP local del PC (ej. `192.168.1.14`) |
| Emulador con backend en el mismo PC | `10.0.2.2` |

Tras editar, sincroniza Gradle (🐘) para regenerar `BuildConfig`.

> Las IPs permitidas para HTTP en claro están definidas en
> [`app/src/main/res/xml/network_security_config.xml`](app/src/main/res/xml/network_security_config.xml).
> Añade ahí cualquier nueva IP de desarrollo.

---

## ▶️ Compilación y ejecución

Desde Android Studio: pulsa **Run ▶** con un emulador o dispositivo conectado.

Desde línea de comandos:

```bash
./gradlew installDebug   # Linux / macOS
gradlew.bat installDebug # Windows
```

---

## 🌿 Convenciones de ramas

- `main` — Versión estable de referencia
- `master` — Línea de desarrollo principal
- Ramas con nombre de miembro (ej. `Asun`, `Franklin`, `Jose+Franklin`) — Trabajo individual
- Ramas por funcionalidad (ej. `LotesGeneticos`, `Seguridad`, `Sonido`) — Features puntuales

---

## 📄 Licencia

Proyecto académico desarrollado por el equipo CattleManager.
