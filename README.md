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
# Crear o actualizar el stack
aws ecs register-task-definition --cli-input-json file://infra/gestion-evaluaciones-task-definition.json
# Crear el servicio
aws ecs create-service --cli-input-json file://infra/gestion-evaluaciones-service.json
