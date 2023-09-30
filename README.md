# MISO - Proyecto Final - ABC Jobs - Grupo 11

| Apellido    | Nombres   | Correo @uniandes               | Usuario de GitHub |
| ----------- | -------   | ----------------               | ----------------- |
| Lugo        | Ronald    | r.lugoq@uniandes.edu.co        | [@RonaldLugo]     |
| Santamaría  | Alejandro | a.santamaria11@uniandes.edu.co | [@miso-alejosaur] |
| Tenezaca    | Hector    | h.tenezaca@uniandes.edu.co     | [@htenezaca]      |
| López Grau  | Javier    | je.lopez816@uniandes.edu.co    | [@jlopezgr]       |

<!-- links -->
[@RonaldLugo]: https://github.com/RonaldLugo
[@miso-alejosaur]: https://github.com/miso-alejosaur
[@htenezaca]: https://github.com/htenezaca
[@jlopezgr]: https://github.com/jlopezgr

## Infraestructure

Parte de la Infraestructura está definida como código usando el CDK de AWS. Esto lo encontramos en la carpeta [`infra/`](infra/).

Los servicios son definidos como ecs tacks y services, y los desplegamos usando el aws cli, pues en esta tapa requerimos de mucha iteracción y el aws cdk suele ser más útil cuando ya se ha establecido exactamente que se quiere.

### Gestión de evaluaciones

```bash
# Login to the AWS ECR
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 428011609647.dkr.ecr.us-east-1.amazonaws.com
# Build and push the image
docker build -t gestion-evaluaciones .
# Tarea
aws ecs register-task-definition --cli-input-json file://services/gestion-evaluaciones/task.json
# Servicio
aws ecs create-service --cli-input-json file://services/gestion-evaluaciones/service.json
```

# Guía de Instalación

## Desarrollo backend

El entorno de trabajo local está basado en Docker para facilitar el proceso. Cada microservicio tiene su propio Dockerfile y docker-compose.yaml para poder ser levantado localmente.

### Requisitos

Las siguientes son las dependencias necesarias para poder trabajar localmente:

- Docker
- Docker Compose
- Python 3.10

### Ejemplo de uso

Cada uno de los servicios tiene su proprio archivo docker-compose.yaml para ser levantado localmente.

```bash
cd services/gestion-evaluaciones
docker-compose up -d
# Probar que el servicio esté corriendo
curl http://localhost:3000/health
```

Algunas de las acciones comunes:

1. Reiniciar el servicio

```bash
docker-compose restart
```

2. Ver los logs

```bash
docker-compose logs -f
```

3. Detener el servicio

```bash
docker-compose down
```

4. Ejecutar un comando dentro del contenedor

```bash
docker-compose exec gestion-evaluaciones bash
```

5. Ejecutar pytest dentro del contenedor

```bash
docker-compose exec gestion-evaluaciones pytest
```

## Despliegue en producción backend

El despliegue en producción se realiza usando el CDK de AWS. Para esto, se debe tener instalado el CDK de AWS y el AWS CLI.

### Requisitos

- AWS CLI
- AWS CDK
- Python 3.10

#### Instrucciones de instalación

1. Instalar el AWS CLI

```bash
pip install awscli
```

2. Instalar el AWS CDK

```bash
npm install -g aws-cdk
```

3. Configurar el AWS CLI

```bash
aws configure
```

### Ejemplo de despliegue de toda la IAC

1. Instalar las dependencias

```bash
cd ./infra/cdk
npm install
```

2. Desplegar la infraestructura

```bash
cdk deploy --all
```

3. Ir a la consola de AWS y verificar que los servicios estén corriendo

### Despliegues continuos

Cada servicio tiene su pipeline de despliegue continuo donde se construye el contenedor, se sube a ECR, se corren pruebas unitarias y se despliega en ECS.

Un ejemplo del pipeline [se puede ver en el repositorio](./.github/workflows/gestion-evaluaciones.yaml).

## Desarrollo frontend

El desarrolla se realiza utilizando angular.

### Requisitos

- Node.js

#### Ejemplos de uso

1. Instalar las dependencias

```bash
cd ./web
npm install
```

2. Levantar la app

```bash
ng serve
```

3. Correr las pruebas

```bash
ng test
```

## Despliegue en producción frontend

Se despliega a un bucket de S3 y se sirve con CloudFront. El proceso está automatizado con el siguiente [pipeline](./.github/workflows/web.yaml).







#### Usuarios de acceso

Todos los usuarios serán generados una vez en la consola de AWS y compartido a cada uno de los integrantes.
