# 🧾 Contabix — Sistema Contable

**Contabix** es una aplicación web desarrollada en **Spring Boot** que permite la gestión contable de usuarios, roles y operaciones financieras como **Libro Diario** y **Libro Mayor**.  
Incluye autenticación de usuarios, control de roles y un panel administrativo para gestionar cuentas y permisos.

---

# 🚀 Tecnologías Utilizadas
- **Java 17+**  
- **Spring Boot 3.x**  
- **Spring Data JPA**  
- **Spring Security**  
- **Thymeleaf**  
- **Bootstrap / CSS personalizado**  
- **PostgreSQL o MySQL (según configuración)**

---

# 🧰 Requisitos Previos

| Requisito | Versión mínima | Descripción |
|------------|----------------|--------------|
| **Java JDK** | 17 | Lenguaje base del proyecto |
| **Maven** | 3.8+ | Gestión de dependencias |
| **PostgreSQL / MySQL** | 13+ / 8+ | Base de datos |
| **IDE recomendado** | IntelliJ IDEA / Eclipse / VS Code | Para desarrollo |
| **Git** | Última | Para clonar el repositorio |

---

# ⚙️ Configuración del Proyecto

### 1. Clonar el repositorio
git clone https://github.com/alissonhernandez/ContaBix.git <br>
cd ContaBix

### 2. Crear la base de datos
CREATE DATABASE sistema_contable;

### 3. Variables del entorno

## Nombre de la aplicación y perfil activo
spring.application.name=contabix
spring.profiles.active=dev

## Configuración de base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/sistema_contable
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

## Configuración JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

## Puerto del servidor
server.port=8080

# 🧮 Funcionalidades Principales
🔐 Autenticación de usuarios
👥 Gestión de roles (Admin, Auditor, Contador)
📘 Libro Diario
📗 Libro Mayor
⚙️ Panel de administración
🧾 Edición y eliminación de usuarios
💾 Registro de auditorías y cambios de roles

# ▶️ Comandos Útiles

## Compilar proyecto
mvn clean install

## Ejecutar el proyecto
mvn spring-boot:run

## Acceder a la web
http://localhost:8080/login

# 📝 Notas de Desarrollo

Spring Boot carga la configuración según el perfil activo (spring.profiles.active=dev).

La contraseña de usuarios se cifra automáticamente usando PasswordEncoder.

Solo usuarios con rol Admin pueden gestionar otros usuarios y roles.

Las vistas están en Thymeleaf y el estilo es Bootstrap + CSS personalizado.
