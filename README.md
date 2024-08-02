# Stock Stats
![Compilar y Desplegar a GKE](https://github.com/adralpz/stocks-stats/actions/workflows/gke-deploy.yml/badge.svg?branch=main)

## Descripción

Stock Stats Analyzer es un proyecto de práctica diseñado para profundizar en el uso de Spring Boot, PostgreSQL, Kubernetes, concurrencia con Java y tecnologías de cloud computing.  

La aplicación permite analizar estadísticas básicas de acciones bursátiles, demostrando la implementación de una arquitectura de microservicios moderna y prácticas de DevOps.

## Objetivo del Proyecto

El principal objetivo de este proyecto es servir como plataforma de aprendizaje y práctica para:

- Desarrollo backend con Spring Boot
- Despliegue de aplicaciones en Kubernetes
- Implementación de CI/CD con GitHub Actions
- Integración con servicios cloud (Google Cloud Platform)
- Prácticas de containerización con Docker

**Nota**: El frontend es minimalista y sirve principalmente para demostrar la integración completa del stack tecnológico.

## Tecnologías Utilizadas

- **Backend**: Spring Boot 3.3, Java 21
- **Frontend**: Next.js (React)
- **Base de Datos**: PostgreSQL (SQL GCP)
- **Containerización**: Docker
- **Orquestación**: Kubernetes (GKE)
- **CI/CD**: GitHub Actions
- **Cloud**: Google Cloud Platform

## Estructura del Proyecto

```
stocks-stats/
├── src/                    # Código fuente del backend
├── front/                  # Código fuente del frontend (Next.js)
├── .github/
    └── workflows/          # Configuraciones de GitHub Actions
    └──kubernetes/          # Archivos de configuración de Kubernetes
├── Dockerfile              # Dockerfile para el backend
└── pom.xml                 # Configuración de Maven
```

## Instalación y Ejecución Local

1. Clonar el repositorio:
   ```
   git clone https://github.com/tu-usuario/stocks-stats.git
   cd stocks-stats
   ```

2. Construir el backend:
   ```
   mvn clean package
   ```
   
3. Ejecutar el backend:
   ```
   java -jar target/stocks-stats-{version}.jar
   ```
   
4. Construir y ejecutar el frontend:
   ```
   cd front
   npm install
   npm run dev
   ```

## Despliegue

El proyecto está configurado para desplegarse automáticamente en Google Kubernetes Engine (GKE) mediante GitHub Actions. Cada push a la rama principal desencadena el proceso de CI/CD.

### Requisitos para Despliegue

- Cuenta de Google Cloud Platform con GKE habilitado
- Secretos configurados en GitHub:
  - `GKE_PROJECT`: ID del proyecto de GCP
  - `GKE_SA_KEY`: Clave de cuenta de servicio de GCP codificada en base64
  - `NEXT_PUBLIC_API_URL`: IP de la API Rest
    
## Contribuciones

No tengo pensado aceptar contribuciones, aunque si deseas hacerlo de todas formas abre un issue para discutir cambios mayores antes de crear un pull request.

## Licencia

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
