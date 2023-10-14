from typing import List
from typing import Optional
from sqlalchemy import ForeignKey
from sqlalchemy import String
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.orm import Mapped
from sqlalchemy.orm import mapped_column
from sqlalchemy.orm import relationship
from sqlalchemy.orm import DeclarativeBase

from ..api_models.gestion_candidatos import CandidatoDTO
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
    nombres: Mapped[str] = mapped_column(String(255), nullable=False)
    apellidos: Mapped[str] = mapped_column(String(255), nullable=False)
    email: Mapped[str] = mapped_column(String(255), nullable=False, unique=True)

    def build_candidato_dto(self) -> CandidatoDTO:
        return CandidatoDTO(
            id=self.id,
            nombres=self.nombres,
            apellidos=self.apellidos,
            email=self.email,
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
