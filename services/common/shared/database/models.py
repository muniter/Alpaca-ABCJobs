from typing import List
from typing import Optional
from sqlalchemy import ForeignKey
from sqlalchemy import String, JSON, Date, Column, Table, PrimaryKeyConstraint
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy.orm import relationship
from sqlalchemy.orm import DeclarativeBase
from datetime import date

from ..api_models.gestion_candidatos import (
    CandidatoDTO,
    CandidatoPersonalInformationDTO,
    LenguajeDTO,
)
from ..api_models.gestion_empresas import EmpresaDTO

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

    id: Mapped[int] = mapped_column(primary_key=True)
    nombre: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False, unique=True)

    def build_empresa_dto(self) -> EmpresaDTO:
        return EmpresaDTO(
            id=self.id,
            nombre=self.nombre,
            email=self.email,
        )


class Candidato(Base):
    __tablename__ = "candidato"

    id: Mapped[int] = mapped_column(primary_key=True)
    id_persona: Mapped[int] = mapped_column(
        ForeignKey("persona.id"), nullable=False, unique=True
    )
    persona: Mapped["Persona"] = relationship("Persona", backref="candidato")

    def build_candidato_dto(self) -> CandidatoDTO:
        return CandidatoDTO(
            id=self.id,
            nombres=self.persona.nombres,
            apellidos=self.persona.apellidos,
            email=self.persona.email,
        )


class Country(Base):
    __tablename__ = "countries"

    num_code: Mapped[int] = mapped_column(primary_key=True)
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

    id: Mapped[int] = mapped_column(primary_key=True)
    nombres: Mapped[str] = mapped_column(String(255), nullable=False)
    apellidos: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False, unique=True)
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

    def build_informacion_personal_dto(self) -> CandidatoPersonalInformationDTO:
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


class Usuario(Base):
    __tablename__ = "usuario"

    id: Mapped[int] = mapped_column(primary_key=True)
    email: Mapped[str] = mapped_column(String(255), nullable=False)
    password: Mapped[str] = mapped_column(String(255), nullable=False)
    id_empresa: Mapped[Optional[int]] = mapped_column(
        ForeignKey("empresa.id"), nullable=True, unique=True
    )
    id_candidato: Mapped[Optional[int]] = mapped_column(
        ForeignKey("candidato.id"), nullable=True, unique=True
    )
    # JSON config column
    config: Mapped[dict] = mapped_column(JSON, nullable=False, default={})

    def build_usuario_dto(self) -> UsuarioDTO:
        if self.id_candidato:
            return UsuarioCandidatoDTO(
                id=self.id,
                email=self.email,
                id_candidato=self.id_candidato,
            )
        elif self.id_empresa:
            return UsuarioEmpresaDTO(
                id=self.id,
                email=self.email,
                id_empresa=self.id_empresa,
            )
        else:
            raise ValueError("Usuario data must have id_candidato or id_empresa")
