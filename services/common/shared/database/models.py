from typing import List
from typing import Optional
from sqlalchemy import ForeignKey
from sqlalchemy import (
    String,
    Identity,
    JSON,
    Date,
    Column,
    Table,
    PrimaryKeyConstraint,
    UniqueConstraint,
)
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy.orm import relationship
from sqlalchemy.orm import DeclarativeBase
from datetime import date

from common.shared.api_models.gestion_proyectos import ProyectoDTO

from ..api_models.gestion_evaluaciones import (
    ExamenDTO,
    ExamenPreguntaDTO,
    ExamenRespuestaDTO,
    ExamenResultadoDTO,
)

from ..api_models.gestion_candidatos import (
    CandidatoConocimientoTecnicoDTO,
    CandidatoConocimientoTecnicoTipoDTO,
    CandidatoDTO,
    CandidatoDatosAcademicosDTO,
    CandidatoDatosLaboralesDTO,
    CandidatoDatosLaboralesTipoDTO,
    CandidatoPersonalInformationDTO,
    LenguajeDTO,
    RolHabilidadDTO,
)
from ..api_models.gestion_empresas import (
    EmpleadoDTO,
    EmpleadoPersonalityDTO,
    EmpresaDTO,
    EquipoDTO,
)

from ..api_models.gestion_usuarios import (
    UsuarioCandidatoDTO,
    UsuarioDTO,
    UsuarioEmpresaDTO,
)


class Base(DeclarativeBase):
    __abstract__ = True

    def __repr__(self):
        return f"<{self.__class__.__name__}>"


class Empresa(Base):
    __tablename__ = "empresa"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False, unique=True)

    proyectos: Mapped[List["Proyecto"]] = relationship("Proyecto", back_populates="empresa")
    equipos: Mapped[List["Equipo"]] = relationship("Equipo", back_populates="empresa")

    def build_dto(self) -> EmpresaDTO:
        return EmpresaDTO(
            id=self.id,
            nombre=self.nombre,
            email=self.email,
        )


class Candidato(Base):
    __tablename__ = "candidato"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_persona: Mapped[int] = mapped_column(
        ForeignKey("persona.id"), nullable=False, unique=True
    )
    persona: Mapped["Persona"] = relationship("Persona", backref="candidato")
    usuario: Mapped["Usuario"] = relationship(back_populates="candidato")

    def build_dto(self) -> CandidatoDTO:
        return CandidatoDTO(
            id=self.id,
            nombres=self.persona.nombres,
            apellidos=self.persona.apellidos,
            email=self.persona.email,
        )


class Country(Base):
    __tablename__ = "countries"

    num_code: Mapped[int] = mapped_column(Identity(), primary_key=True)
    alpha_2_code: Mapped[str] = mapped_column(String(2), nullable=False, unique=True)
    alpha_3_code: Mapped[str] = mapped_column(String(3), nullable=False, unique=True)
    en_short_name: Mapped[str] = mapped_column(String(255), nullable=False)
    nationality: Mapped[str] = mapped_column(String(255), nullable=False)


person_language = Table(
    "persona_languaje",
    Base.metadata,
    Column("id_persona", ForeignKey("persona.id")),
    Column("id_lenguaje", ForeignKey("lenguaje.id")),
    PrimaryKeyConstraint("id_persona", "id_lenguaje"),
)


class Lenguaje(Base):
    __tablename__ = "lenguaje"

    id: Mapped[str] = mapped_column(primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)


class Persona(Base):
    __tablename__ = "persona"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombres: Mapped[str] = mapped_column(String(255), nullable=False)
    apellidos: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=True, unique=True)
    fecha_nacimiento: Mapped[Optional[date]] = mapped_column(Date, nullable=True)
    celular: Mapped[Optional[str]] = mapped_column(String(20), nullable=True)
    lenguajes: Mapped[List[Lenguaje]] = relationship(secondary=person_language)
    country_code: Mapped[Optional[int]] = mapped_column(
        ForeignKey("countries.num_code"), nullable=True
    )
    pais: Mapped[Optional[Country]] = relationship("Country", backref="personas")
    ciudad: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    direccion: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    biografia: Mapped[Optional[str]] = mapped_column(String(500), nullable=True)
    fecha_creacion: Mapped[str] = mapped_column(Date, nullable=False, default="now()")
    fecha_actualizacion: Mapped[str] = mapped_column(
        Date, nullable=False, default="now()", onupdate="now()"
    )

    def build_dto(self) -> CandidatoPersonalInformationDTO:
        lenguajes = [
            LenguajeDTO(id=lang.id, name=lang.nombre) for lang in self.lenguajes
        ]
        return CandidatoPersonalInformationDTO(
            names=self.nombres,
            last_names=self.apellidos,
            full_name=f"{self.nombres} {self.apellidos}",
            email=self.email,
            birth_date=self.fecha_nacimiento,
            country_code=self.country_code,
            country=self.pais.en_short_name if self.pais else None,
            city=self.ciudad,
            address=self.direccion,
            phone=self.celular,
            biography=self.biografia,
            languages=lenguajes if lenguajes else None,
        )


datos_laborales_roles = Table(
    "datos_laborales_roles",
    Base.metadata,
    Column("id_datos_laborales", ForeignKey("datos_laborales.id")),
    Column("id_rol", ForeignKey("roles_habilidades.id")),
    PrimaryKeyConstraint("id_datos_laborales", "id_rol"),
)


class RolesHabilidades(Base):
    __tablename__ = "roles_habilidades"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)

    def build_dto(self) -> RolHabilidadDTO:
        return RolHabilidadDTO(
            id=self.id,
            name=self.nombre,
        )


class DatosLaborales(Base):
    __tablename__ = "datos_laborales"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_persona: Mapped[int] = mapped_column(ForeignKey("persona.id"), nullable=False)
    persona: Mapped["Persona"] = relationship("Persona", backref="datos_laborales")
    cargo: Mapped[str] = mapped_column(String(255), nullable=False)
    empresa: Mapped[str] = mapped_column(String(255), nullable=False)
    descripcion: Mapped[str] = mapped_column(String(500), nullable=False)
    start_year: Mapped[int] = mapped_column(nullable=False)
    end_year: Mapped[Optional[int]] = mapped_column(nullable=True)
    roles_habilidades: Mapped[List[RolesHabilidades]] = relationship(
        secondary=datos_laborales_roles
    )

    def build_dto(self) -> CandidatoDatosLaboralesDTO:
        return CandidatoDatosLaboralesDTO(
            id=self.id,
            id_persona=self.id_persona,
            role=self.cargo,
            company=self.empresa,
            description=self.descripcion,
            start_year=self.start_year,
            end_year=self.end_year,
            skills=[r.build_dto() for r in self.roles_habilidades],
        )


class DatosAcademicosTipo(Base):
    __tablename__ = "datos_academicos_tipo"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)

    def build_dto(self) -> CandidatoDatosLaboralesTipoDTO:
        return CandidatoDatosLaboralesTipoDTO(
            id=self.id,
            name=self.nombre,
        )


class DatosAcademicos(Base):
    __tablename__ = "datos_academicos"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_persona: Mapped[int] = mapped_column(
        ForeignKey("persona.id"), nullable=False, unique=False
    )
    persona: Mapped["Persona"] = relationship("Persona", backref="datos_academicos")

    id_tipo: Mapped[int] = mapped_column(
        ForeignKey("datos_academicos_tipo.id"), nullable=False, unique=False
    )
    tipo: Mapped["DatosAcademicosTipo"] = relationship(
        "DatosAcademicosTipo", backref="datos_academicos"
    )

    titulo: Mapped[str] = mapped_column(String(255), nullable=False)
    institucion: Mapped[str] = mapped_column(String(255), nullable=False)
    start_year: Mapped[int] = mapped_column(nullable=False)
    end_year: Mapped[Optional[int]] = mapped_column(nullable=True)
    logro: Mapped[str] = mapped_column(String(500), nullable=True)

    def build_dto(self) -> CandidatoDatosAcademicosDTO:
        return CandidatoDatosAcademicosDTO(
            id=self.id,
            id_persona=self.id_persona,
            type=self.tipo.build_dto(),
            title=self.titulo,
            institution=self.institucion,
            start_year=self.start_year,
            end_year=self.end_year,
            achievement=self.logro,
        )


class ConocimientoTecnicoTipo(Base):
    __tablename__ = "conocimiento_tecnico_tipo"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)

    def build_dto(self) -> CandidatoConocimientoTecnicoTipoDTO:
        return CandidatoConocimientoTecnicoTipoDTO(
            id=self.id,
            name=self.nombre,
        )


class ConocimientoTecnicos(Base):
    __tablename__ = "conocimiento_tecnicos"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_persona: Mapped[int] = mapped_column(
        ForeignKey("persona.id"), nullable=False, unique=False
    )
    persona: Mapped["Persona"] = relationship(
        "Persona", backref="conocimiento_tecnicos"
    )

    id_tipo: Mapped[int] = mapped_column(
        ForeignKey("conocimiento_tecnico_tipo.id"), nullable=False, unique=False
    )
    tipo: Mapped["ConocimientoTecnicoTipo"] = relationship(
        "ConocimientoTecnicoTipo", backref="conocimiento_tecnicos"
    )
    descripcion: Mapped[str] = mapped_column(String(500), nullable=True)

    def build_dto(self) -> CandidatoConocimientoTecnicoDTO:
        return CandidatoConocimientoTecnicoDTO(
            id=self.id,
            id_persona=self.id_persona,
            type=self.tipo.build_dto(),
            description=self.descripcion,
        )


class Usuario(Base):
    __tablename__ = "usuario"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    email: Mapped[str] = mapped_column(String(255), nullable=False)
    password: Mapped[str] = mapped_column(String(255), nullable=False)
    id_empresa: Mapped[Optional[int]] = mapped_column(
        ForeignKey("empresa.id"), nullable=True, unique=True
    )
    id_candidato: Mapped[Optional[int]] = mapped_column(
        ForeignKey("candidato.id"), nullable=True, unique=True
    )

    candidato: Mapped[Optional[Candidato]] = relationship(back_populates="usuario")
    # JSON config column
    config: Mapped[dict] = mapped_column(JSON, nullable=False, default={})

    def build_dto(self) -> UsuarioDTO:
        if self.id_candidato:
            assert self.candidato
            return UsuarioCandidatoDTO(
                id=self.id,
                email=self.email,
                id_candidato=self.id_candidato,
                id_persona=self.candidato.id_persona,
            )
        elif self.id_empresa:
            return UsuarioEmpresaDTO(
                id=self.id,
                email=self.email,
                id_empresa=self.id_empresa,
            )
        else:
            raise ValueError("Usuario data must have id_candidato or id_empresa")


class Personalidad(Base):
    __tablename__ = "personality_types"
    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)

    def build_dto(self) -> EmpleadoPersonalityDTO:
        return EmpleadoPersonalityDTO(
            id=self.id,
            name=self.nombre,
        )


empleado_roles = Table(
    "empleado_roles",
    Base.metadata,
    Column("id_empleado", ForeignKey("empleado.id")),
    Column("id_rol", ForeignKey("roles_habilidades.id")),
    PrimaryKeyConstraint("id_empleado", "id_rol"),
)


class Empleado(Base):
    __tablename__ = "empleado"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_persona: Mapped[int] = mapped_column(
        ForeignKey("persona.id"), nullable=False, unique=True
    )
    persona: Mapped["Persona"] = relationship("Persona", backref="empleado")
    cargo: Mapped[str] = mapped_column(String(255), nullable=False)
    id_empresa: Mapped[int] = mapped_column(ForeignKey("empresa.id"), nullable=False)
    empresa: Mapped["Empresa"] = relationship("Empresa", backref="empleados")
    personalidad_id: Mapped[int] = mapped_column(
        ForeignKey("personality_types.id"), nullable=False
    )
    personalidad: Mapped["Personalidad"] = relationship(
        "Personalidad", backref="empleado"
    )

    roles_habilidades: Mapped[List[RolesHabilidades]] = relationship(
        secondary=empleado_roles
    )

    fecha_creacion: Mapped[Date] = mapped_column(Date, nullable=False, default="now()")

    def build_dto(self) -> EmpleadoDTO:
        return EmpleadoDTO(
            id=self.id,
            name=(" ".join([self.persona.nombres, self.persona.apellidos])).strip(),
            title=self.cargo,
            company=self.empresa.build_dto(),
            personality=self.personalidad.build_dto(),
            skills=[r.build_dto() for r in self.roles_habilidades],
        )


empleado_equipo = Table(
    "empleado_equipo",
    Base.metadata,
    Column("id_empleado", ForeignKey("empleado.id")),
    Column("id_equipo", ForeignKey("equipo.id")),
    PrimaryKeyConstraint("id_empleado", "id_equipo"),
)


class Equipo(Base):
    __tablename__ = "equipo"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_empresa: Mapped[int] = mapped_column(ForeignKey("empresa.id"), nullable=False)
    empresa: Mapped["Empresa"] = relationship("Empresa", back_populates="equipos")
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)
    empleados: Mapped[List[Empleado]] = relationship(secondary=empleado_equipo)

    def build_dto(self) -> EquipoDTO:
        return EquipoDTO(
            id=self.id,
            name=self.nombre,
            company=self.empresa.build_dto(),
            employees=[e.build_dto() for e in self.empleados],
        )


class ExamenTecnico(Base):
    __tablename__ = "examen_tecnico"
    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_rol_habilidad: Mapped[int] = mapped_column(
        ForeignKey("roles_habilidades.id"), nullable=False
    )

    rol_habilidad: Mapped["RolesHabilidades"] = relationship(
        "RolesHabilidades", backref="examen_tecnico"
    )

    preguntas: Mapped[List["ExamenPregunta"]] = relationship(
        "ExamenPregunta", back_populates="examen_tecnico"
    )

    def build_dto(self, completed: bool = False) -> ExamenDTO:
        return ExamenDTO(
            id=self.id,
            completed=completed,
            skill=self.rol_habilidad.build_dto(),
            number_of_questions=3,
        )


class ExamenPregunta(Base):
    __tablename__ = "examen_pregunta"
    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_examen_tecnico: Mapped[int] = mapped_column(
        ForeignKey("examen_tecnico.id"), nullable=False
    )
    examen_tecnico: Mapped["ExamenTecnico"] = relationship(back_populates="preguntas")
    pregunta: Mapped[str] = mapped_column(String(255), nullable=False)
    dificultad: Mapped[int] = mapped_column(nullable=False)
    respuestas: Mapped[List["ExamenRespuesta"]] = relationship(
        "ExamenRespuesta", backref="examen_pregunta"
    )

    def build_dto(self) -> ExamenPreguntaDTO:
        # Sort the answers so the correct one is always first
        respuestas = sorted(self.respuestas, key=lambda x: x.correcta, reverse=True)
        return ExamenPreguntaDTO(
            id=self.id,
            id_exam=self.id_examen_tecnico,
            question=self.pregunta,
            difficulty=self.dificultad,
            answers=[r.build_dto() for r in respuestas],
        )


class ExamenRespuesta(Base):
    __tablename__ = "examen_respuesta"
    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    respuesta: Mapped[str] = mapped_column(String(255), nullable=False)
    id_examen_pregunta: Mapped[int] = mapped_column(
        ForeignKey("examen_pregunta.id"), nullable=False
    )
    correcta: Mapped[bool] = mapped_column(nullable=False)

    def build_dto(self) -> ExamenRespuestaDTO:
        return ExamenRespuestaDTO(
            id=self.id,
            id_question=self.id_examen_pregunta,
            answer=self.respuesta,
        )


class ExamenResultado(Base):
    __tablename__ = "examen_resultado"
    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    id_examen_tecnico: Mapped[int] = mapped_column(
        ForeignKey("examen_tecnico.id"), nullable=False
    )
    examen_tecnico: Mapped["ExamenTecnico"] = relationship("ExamenTecnico")
    id_candidato: Mapped[int] = mapped_column(
        ForeignKey("candidato.id"), nullable=False
    )
    resultado: Mapped[int] = mapped_column(nullable=True)
    progreso: Mapped[int] = mapped_column(nullable=False, default=0)
    completado: Mapped[bool] = mapped_column(nullable=False, default=False)

    # Column where the progress of the exam is stored
    dificultad_actual: Mapped[int] = mapped_column(nullable=False)
    data: Mapped[List] = mapped_column(JSON, nullable=False, default=[])

    def build_dto(self) -> ExamenResultadoDTO:
        return ExamenResultadoDTO(
            id=self.id,
            exam=self.examen_tecnico.build_dto(),
            id_candidato=self.id_candidato,
            result=self.resultado,
            completed=self.completado,
        )


class Proyecto(Base):
    __tablename__ = "proyecto"

    id: Mapped[int] = mapped_column(Identity(), primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)
    descripcion: Mapped[str] = mapped_column(String(500), nullable=True)
    id_equipo: Mapped[int] = mapped_column(ForeignKey("equipo.id"), nullable=False)
    equipo: Mapped["Equipo"] = relationship("Equipo", backref="proyectos")
    id_empresa: Mapped[int] = mapped_column(ForeignKey("empresa.id"), nullable=False)
    empresa: Mapped["Empresa"] = relationship("Empresa", back_populates="proyectos")

    __table_args__ = (
        UniqueConstraint("id_empresa", "nombre", name="unique_empresa_nombre"),
    )

    def build_dto(self) -> ProyectoDTO:
        return ProyectoDTO(
            id=self.id,
            name=self.nombre,
            description=self.descripcion,
            team=self.equipo.build_dto(),
        )
