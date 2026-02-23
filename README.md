# 📚 Proyecto Lectura Sana

[![Build Status](http://3.140.188.231:8080/job/pruebas-pipeline/badge/icon)](http://3.140.188.231:8080/job/pruebas-pipeline/)

Este proyecto desarrolla una plataforma innovadora para la gestión y promoción de la lectura, priorizando la experiencia del usuario y el acceso a contenidos literarios de calidad y accesibles a el publico en general.

## 👥 Integrantes

| APELLIDOS Y NOMBRES | CÓDIGO |
| :--- | :--- |
| Arias Patiño, Karina Rocío    | U23235234 |
| Ángel Abel García Valladolid  | U22229946 |
| Guzmán Támara Béquer Reynaldo | U23238030 |
| Ian Angelo Andrade Gamio      | U22235933 |


## 📖 Proyecto: Plataforma de Gestión Literaria 🚀

## 📋 Tabla de Contenidos

1. [Introducción 🌟](#introducción-)
2. [Contexto del Negocio 🏥](#contexto-del-negocio-)
3. [Planteamiento del Problema ⚠️](#planteamiento-del-problema-️)
4. [Objetivos del Proyecto 🎯](#objetivos-del-proyecto-)
    - i. [Objetivo general](#objetivo-general)
    - ii. [Objetivos específicos](#objetivos-específicos)
5. [Alcances del Proyecto 📏](#alcances-del-proyecto-️)
6. [Justificacion del proyecto💡](#justificacion-del-proyecto-️)  
7. [Metodología de Trabajo 🏃‍♂️](#metodologia-del-trabajo-️)  
8. [Planificación del Proyecto 📅](#Planificacion-del-proyecto-️)  
9. [Historias Técnicas por Sprint 🔧](#historias-tecnicas-por-sprint-️)    
10. [Historias de usuario 👥](#historias-de-usuario-️)
11. [Herramientas de Desarrollo 🛠️](#herramientas-de-desarrollo-️)
12. [Arquitectura de la Solución 🏗️](#arquitectura-de-la-solucion-️)
13. [Conclusiones 🏆](#conclusiones-️)

---

## 🌟 Introducción

El acceso a la lectura digital ha crecido exponencialmente 📈, pero muchas plataformas carecen de una interfaz intuitiva. Este proyecto desarrolla una solución moderna que optimiza la navegación y el descubrimiento de nuevos libros para fomentar el hábito de la lectura. ¡Prepárate para una experiencia literaria escalable! 📚✨

## 🏥 Contexto del negocio

Una biblioteca o librería digital en crecimiento necesita un sistema que gestione catálogos por categorías, autores y novedades. El flujo clave: **Navegación → Ver Novedades → Detalle de Noticia/Libro**, todo optimizado para reducir la tasa de abandono y aumentar la interacción. 💼

## ⚠️ Planteamiento del Problema

Problemas actuales: Catálogos desactualizados, falta de personalización en recomendaciones y procesos manuales que frenan el crecimiento. Resultado: usuarios insatisfechos y baja fidelidad. ¡Es hora de digitalizar la lectura! 🚨

## 🎯 Objetivos del Proyecto

### 🏆 Objetivo General

Crea una plataforma web de lectura intuitiva, segura y escalable que impulse el descubrimiento de libros mediante gestión en tiempo real y recomendaciones personalizadas. ¡Transformar visitantes en lectores leales! 💰

### 📑 Objetivos específicos

- Diseñar una UI/UX responsive utilizando Bootstrap 5 para móviles y escritorio 📱 💻
- Implementar un motor de búsqueda y filtrado eficiente por categorías 🔍
- Asegurar la plataforma mediante Spring Security para la gestión de roles de usuario y administrador 🔐

## 🏆 Alcances del Proyecto

El proyecto Lectura Sana comprende el desarrollo de una plataforma web funcional que abarca los siguientes puntos:

- Gestión de Catálogo Dinámico
- Sistema de Novedades Paginado
- Visualización de Contenido Detallado
- Arquitectura Robusta
- Interfaz Adaptable (Responsive)
- Seguridad y Sesiones

## 💡 Justificacion del proyecto

- Fomento a la Cultura Digital
- Optimización de la Experiencia del Usuario (UX)
- Centralización de la Información
- Escalabilidad Tecnológica
- Automatización de Procesos

## 🏃‍♂️ Metodología de Trabajo

Para el desarrollo de Lectura Sana, el equipo aplica la metodología ágil SCRUM, permitiendo una entrega continua de valor y una rápida adaptación a los cambios

## 📅 Planificación del Proyecto 

La ejecución se ha dividido en ciclos de trabajo (Sprints) para asegurar una evolución constante de la plataforma

## 🔧 Historias Técnicas por Sprint 

🔹 Sprint 1 – Control de Versiones y Colaboración
🔹 Sprint 2: Gestión Ágil y Seguimiento
🔹 Sprint 3: Integración y Entrega Continua (CI/CD)
🔹 Sprint 4: Contenedores y Despliegue

## 👥 Historias de usuarios

### 📖 Para el Lector
| ID | Historia de Usuario | Criterios de Aceptación |
| :--- | :--- | :--- |
| HU01 | **Como lector**, quiero ver una página de novedades, **para** estar al tanto de los últimos libros. | - Carga de 6 tarjetas.<br>- Paginación funcional. |
| HU02 | **Como lector**, quiero filtrar noticias, **para** leer contenido de mi interés. | - Filtro por apartado.<br>- Mensaje si no hay datos. |

### 🛠️ Para el Administrador
| ID | Historia de Usuario | Criterios de Aceptación |
| :--- | :--- | :--- |
| HU04 | **Como administrador**, quiero gestionar la BD, **para** mantener la información actualizada. | - Cambios reflejados en MySQL.<br>- data.sql estructurado. |

## 🛠️ Herramientas de Desarrollo

- Java 21 (LTS): Lenguaje de programación de última generación para una lógica de servidor eficiente.
- Spring Boot 3.5.6: Framework de alto rendimiento para la creación de la aplicación y gestión de microservicios.
- Spring Security: Módulo especializado para la autenticación y protección de rutas críticas.
- Spring Data JPA: Abstracción de persistencia para una interacción fluida con MySQL.
- MySQL Server 8.0: Motor de base de datos relacional robusto para el almacenamiento de datos.
- Bootstrap 5.3: Framework CSS líder para garantizar una interfaz 100% adaptable y moderna.
- Thymeleaf: Motor de plantillas que permite integrar la lógica de seguridad y datos en el HTML.

## 🏗️ Arquitectura de la Solución

El proyecto **Lectura Sana** implementa una arquitectura multicapa que garantiza la escalabilidad y el mantenimiento del sistema, integrando seguridad y persistencia de manera profesional.

| Capa | Componente | Función Principal |
| :--- | :--- | :--- |
| **Vista (View)** | Thymeleaf / **Bootstrap 5** | Interfaz adaptativa que consume fragmentos de seguridad para mostrar opciones según el rol del usuario. |
| **Seguridad** | **Spring Security 6** | Filtro de seguridad que intercepta peticiones, gestiona el inicio de sesión y protege las rutas administrativas. |
| **Negocio (Service)** | `NovedadService.java` | Capa intermedia donde reside la lógica de negocio y el procesamiento de los datos literarios. |
| **Acceso a Datos** | **Spring Data JPA** | Repositorios que automatizan las consultas a la base de datos mediante el mapeo objeto-relacional (ORM). |
| **Persistencia** | **MySQL Server** | Almacén de datos persistente para el catálogo de libros, noticias y credenciales de acceso. |

## 🏆 Conclusiones

* **Escalabilidad y Orden:** El uso de una arquitectura multicapa facilita el mantenimiento del código, permitiendo que nuevos integrantes se sumen al equipo sin generar conflictos en la lógica existente.
* **Optimización de Recursos:** La implementación de paginación en el servidor mediante Spring Boot asegura que la aplicación consuma menos memoria, cargando solo los datos necesarios para el lector.
* **Seguridad y Estándares:** La adopción de Spring Security y Java 21 eleva el proyecto a estándares industriales, permitiendo una gestión de identidades segura y aprovechando las últimas mejoras de rendimiento del ecosistema Java..
* **Eficiencia en la Configuración:** La integración exitosa entre **MySQL** y **VS Code** demuestra la importancia de un entorno de desarrollo bien configurado para evitar cuellos de botella técnicos.
* **Valor de la Documentación:** Contar con un **README** detallado transforma un conjunto de archivos de código en un producto profesional y comprensible para cualquier desarrollador externo.