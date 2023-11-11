from sqlalchemy import select, text

from common.shared.api_models.gestion_candidatos import (
    CandidatoConocimientoTecnicoCreateDTO,
    CandidatoCreateDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoPersonalInformationUpdateDTO,
)
from common.shared.database.models import ExamenResultado
from gestion_evaluaciones.evaluaciones import ExamenRepository, ExamenService
from ..logger import logger

from common.shared.api_models.gestion_empresas import (
    EmpleadoCreateDTO,
    EmpresaCreateDTO,
    EquipoCreateDTO,
    VacanteCreateDTO,
)
from common.shared.api_models.gestion_proyectos import ProyectoCreateDTO
from common.shared.api_models.shared import ErrorBuilder
from gestion_candidatos.candidato import (
    CandidatoService,
    ConocimientoTecnicosRepository,
    ConocimientoTecnicosService,
    DatosAcademicosService,
    DatosLaboralesService,
    RolesHabilidadesRepository,
)
from gestion_proyectos.proyectos import ProyectoRepository, ProyectoService
from .db import get_db_session

from faker import Faker
from faker.providers import company

from gestion_empresas.empresa import (
    EmpleadoRepository,
    EmpresaRepository,
    EmpresaService,
    EquipoRepository,
    UtilsRepository,
    VacanteRepository,
)

faker = Faker()
faker.seed_instance(90909)
faker.add_provider(company)


session = get_db_session()
empresa_repository = EmpresaRepository(session=session)
utils_repository = UtilsRepository(session=session)
empleado_repository = EmpleadoRepository(session=session)
equipo_repository = EquipoRepository(session=session)
vacante_repository = VacanteRepository(session=session)
empresa_service = EmpresaService(
    repository=empresa_repository,
    utils_repository=utils_repository,
    empleado_repository=empleado_repository,
    equipo_repository=equipo_repository,
    vacante_repository=vacante_repository,
)
candidate_service = CandidatoService(session)
roles_habilidades_repository = RolesHabilidadesRepository(session)
datos_laborales_service = DatosLaboralesService(session)
datos_academicos_service = DatosAcademicosService(session)
conocimientos_tecnico_repository = ConocimientoTecnicosRepository(session)
conocimientos_tecnico_service = ConocimientoTecnicosService(session)
proyecto_repository = ProyectoRepository(session=session)
proyecto_service = ProyectoService(repository=proyecto_repository)
candidate_service = CandidatoService(session)
roles_habilidades_repository = RolesHabilidadesRepository(session)
datos_laborales_service = DatosLaboralesService(session)
datos_academicos_service = DatosAcademicosService(session)
examen_repository = ExamenRepository(session=session)
examen_service = ExamenService(repository=examen_repository)


def seed_empresas(count: int):
    for n in range(1, count + 1):
        seed_empresa(email=f"empresa{n}@email.com")


def seed_candidatos(count: int):
    for n in range(1, count + 1):
        seed_candidato(email=f"candidato{n}@email.com")


def seed_empresa(email: str):
    logger.info(f"Seeding empresa: {email} ")
    empresa = empresa_service.crear(
        EmpresaCreateDTO(
            nombre=faker.company(),
            email=email,
            password="123456789",
        )
    )
    assert not isinstance(empresa, ErrorBuilder)
    id_empresa = empresa.empresa.id
    skills = roles_habilidades_repository.get()

    # Empleados
    empleados = []
    logger.info(f"Seeding empleados")
    for _ in range(faker.random_int(min=10, max=15)):
        emp_skills = faker.random_choices(
            elements=skills, length=faker.random_int(min=2, max=5)
        )
        empleado = empresa_service.crear_empleado(
            id_empresa=id_empresa,
            data=EmpleadoCreateDTO(
                name=faker.name(),
                title=tech_job(),
                skills=[skill.id for skill in emp_skills],
                personality_id=faker.random_int(min=1, max=10),
            ),
        )
        assert not isinstance(empleado, ErrorBuilder)
        empleados.append(empleado)

    logger.info(f"Seeded {len(empleados)} empleados")

    # Equipos
    equipos = []
    logger.info(f"Seeding equipos")
    for _ in range(faker.random_int(min=3, max=6)):
        emp = unique_random_choice(
            elements=empleados, length=faker.random_int(min=2, max=5)
        )
        equipo = empresa_service.crear_equipo(
            id_empresa=id_empresa,
            data=EquipoCreateDTO(
                name=faker.color_name(),
                employees=[e.id for e in emp],
            ),
        )
        assert not isinstance(equipo, ErrorBuilder)
        equipos.append(equipo)

    logger.info(f"Seeded {len(equipos)} equipos")

    # Proyectos
    proyectos = []
    logger.info(f"Seeding proyectos")
    for _ in range(faker.random_int(min=2, max=4)):
        proyecto = proyecto_service.create_proyecto(
            id_empresa=id_empresa,
            data=ProyectoCreateDTO(
                name="Proyect: " + faker.color_name(),
                description=faker.sentence(),
                id_team=faker.random_element(elements=equipos).id,
            ),
        )
        assert not isinstance(proyecto, ErrorBuilder)
        proyectos.append(proyecto)

    logger.info(f"Seeded {len(proyectos)} proyectos")

    # Vacantes
    vacantes = []
    logger.info(f"Seeding vacantes")
    for _ in range(faker.random_int(min=2, max=4)):
        vacante = empresa_service.crear_vacante(
            id_empresa=id_empresa,
            data=VacanteCreateDTO(
                team_id=faker.random_element(elements=equipos).id,
                name=tech_job(),
                description=faker.sentence(),
            ),
        )
        assert not isinstance(vacante, ErrorBuilder)
        vacantes.append(vacante)


def seed_candidato(email: str):
    logger.info(f"Seeding candidato {email}")
    candidato = candidate_service.crear(
        CandidatoCreateDTO(
            nombres=faker.first_name(),
            apellidos=faker.last_name(),
            email=email,
            password="123456789",
        )
    )

    assert not isinstance(candidato, ErrorBuilder)

    id_candidato = candidato.candidato.id
    roles_habilidades = roles_habilidades_repository.get()

    # Datos Personales
    if faker.boolean():
        logger.debug(f"Seeding datos personales {id_candidato}")
        data = CandidatoPersonalInformationUpdateDTO(
            birth_date=faker.date_of_birth(minimum_age=18, maximum_age=100),
            country_code=4,
            city=faker.city(),
            address=faker.address(),
            phone=faker.numerify(text="#########"),
            biography=faker.text(max_nb_chars=200),
            languages=["EN", "ES"],
        )
        candidate_service.update_informacion_personal(
            id=id_candidato,
            data=data,
        )

        assert not isinstance(data, ErrorBuilder)

    # Datos Laborales
    if faker.boolean():
        logger.debug(f"Seeding datos laborales {id_candidato}")
        for _ in range(faker.random_int(min=1, max=3)):
            data = CandidatoDatosLaboralesCreateDTO(
                role=tech_job(),
                company=faker.company(),
                description=faker.text(max_nb_chars=200),
                start_year=faker.date_between(start_date="-10y", end_date="-5y").year,
                end_year=faker.date_between(start_date="-4y", end_date="-1y").year,
                skills=[
                    role.id
                    for role in unique_random_choice(
                        roles_habilidades, faker.random_int(min=1, max=5)
                    )
                ],
            )
            result = datos_laborales_service.crear(id_candidato=id_candidato, data=data)
            assert not isinstance(result, ErrorBuilder)

    # Datos Academicos
    if faker.boolean():
        logger.debug(f"Seeding datos academicos {id_candidato}")
        for _ in range(faker.random_int(min=1, max=3)):
            data = CandidatoDatosLaboralesCreateDTO(
                role=tech_job(),
                company=faker.company(),
                description=faker.text(max_nb_chars=200),
                start_year=faker.date_between(start_date="-10y", end_date="-5y").year,
                end_year=faker.date_between(start_date="-4y", end_date="-1y").year,
                skills=[
                    role.id
                    for role in unique_random_choice(
                        roles_habilidades, faker.random_int(min=1, max=5)
                    )
                ],
            )
            result = datos_laborales_service.crear(id_candidato=id_candidato, data=data)
            assert not isinstance(result, ErrorBuilder)

    conocimientos = conocimientos_tecnico_repository.get_tipos()
    # Conocimientos tecnicos
    if faker.boolean():
        logger.debug(f"Seeding conocimientos tecnicos {id_candidato}")
        for _ in range(faker.random_int(min=1, max=3)):
            data = CandidatoConocimientoTecnicoCreateDTO(
                type=faker.random_element(elements=conocimientos).id,
                description=faker.text(max_nb_chars=200),
            )
            result = conocimientos_tecnico_service.crear(
                id_candidato=id_candidato, data=data
            )
            if isinstance(result, ErrorBuilder):
                logger.error(result.serialize())
                logger.error(data.model_dump())
                raise Exception("Error seeding conocimientos tecnicos")

    # Examenes
    examenes_disponibles = examen_service.get_all_examenes(id_candidato)
    if faker.boolean():
        logger.debug(f"Seeding examenes {id_candidato}")
        for _ in range(faker.random_int(min=1, max=2)):
            examen = faker.random_element(elements=examenes_disponibles)
            resultado_int = faker.random_int(min=0, max=3)
            result = examen_repository.guardar_resultado(
                ExamenResultado(
                    id_examen_tecnico=examen.id,
                    id_candidato=id_candidato,
                    completado=True,
                    resultado=resultado_int,
                    progreso=3,
                    dificultad_actual=resultado_int if resultado_int > 0 else 1,
                )
            )

            assert not isinstance(result, ErrorBuilder)


def unique_random_choice(elements, length):
    result = []
    max_attempts = length + 10
    while len(result) < length:
        choice = faker.random_element(elements=elements)
        if choice not in result:
            result.append(choice)
        max_attempts -= 1
        if max_attempts == 0:
            raise Exception("Max attempts reached")
    return result


def tech_job():
    jobs = [
        "Software Engineer",
        "Software Developer",
        "Software Architect",
        "Python Developer",
        "Java Developer",
        "C# Developer",
        "Javascript Developer",
        "Frontend Developer",
        "Backend Developer",
        "Fullstack Developer",
        "DevOps Engineer",
        "Data Engineer",
        "Data Scientist",
        "Data Analyst",
        "Machine Learning Engineer",
        "QA Engineer",
        "QA Automation Engineer",
        "QA Manual Engineer",
        "Typescript Developer",
        "React Developer",
        "Angular Developer",
        "Vue Developer",
        "IT Support",
        "IT Manager",
        "Business Analyst",
        "Product Manager",
        "Product Owner",
        "Ux Designer",
        "Go Developer",
        "Ruby Developer",
        "Agile Coach",
        "Scrum Master",
    ]
    return faker.random_element(elements=jobs)

