from typing import List
from fastapi import Depends
from sqlalchemy import select, func
from sqlalchemy.orm import Session
from common.shared.api_models.gestion_evaluaciones import (
    ExamenDTO,
    ExamenRespuestaDTO,
    ExamenResultadoDTO,
    ExamenStepResponseDTO,
)
from common.shared.api_models.shared import ErrorBuilder
from common.shared.database.db import get_db_session_dependency

from common.shared.database.models import (
    ExamenPregunta,
    ExamenRespuesta,
    ExamenResultado,
    ExamenTecnico,
)


class ExamenRepository:
    session: Session

    def __init__(self, session: Session):
        self.session = session

    def get_examen(self, id: int) -> ExamenTecnico | None:
        query = select(ExamenTecnico).where(ExamenTecnico.id == id)
        return self.session.execute(query).scalar_one_or_none()

    def get_all_examenes(self) -> List[ExamenTecnico]:
        query = select(ExamenTecnico)
        return list(self.session.execute(query).scalars().all())

    def get_next_pregunta(
        self, id_examen: int, dificultad: int, not_in: List[int] = []
    ) -> ExamenPregunta:
        query = (
            select(ExamenPregunta)
            .where(
                ExamenPregunta.id_examen_tecnico == id_examen,
                ExamenPregunta.dificultad == dificultad,
                ~ExamenPregunta.id.in_(not_in),
            )
            .order_by(func.random())
            .limit(1)
        )
        response = self.session.execute(query).scalar_one_or_none()
        assert response is not None
        return response

    def get_pregunta(self, id: int, id_examen: int) -> ExamenPregunta | None:
        query = (
            select(ExamenPregunta)
            .where(ExamenPregunta.id == id)
            .where(ExamenPregunta.id_examen_tecnico == id_examen)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_respuesta(self, id: int, id_pregunta: int) -> ExamenRespuesta | None:
        query = (
            select(ExamenRespuesta)
            .where(ExamenRespuesta.id == id)
            .where(ExamenRespuesta.id_examen_pregunta == id_pregunta)
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_resultado(
        self, id_result: int, id_candidato: int
    ) -> ExamenResultado | None:
        query = select(ExamenResultado).where(
            ExamenResultado.id == id_result,
            ExamenResultado.id_candidato == id_candidato,
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_resultado_for_examen(
        self, id_candidato: int, id_examen: int, completado: bool = False
    ) -> ExamenResultado | None:
        query = select(ExamenResultado).where(
            ExamenResultado.id_candidato == id_candidato,
            ExamenResultado.id_examen_tecnico == id_examen,
            ExamenResultado.completado == completado,
        )
        return self.session.execute(query).scalar_one_or_none()

    def get_all_resultados_completados(
        self, id_candidato: int
    ) -> List[ExamenResultado]:
        query = select(ExamenResultado).where(
            ExamenResultado.id_candidato == id_candidato,
            ExamenResultado.completado.is_(True),
        )
        return list(self.session.execute(query).scalars().all())

    def create_resultado(self, resultado: ExamenResultado) -> ExamenResultado:
        self.session.add(resultado)
        self.session.commit()
        return resultado

    def guardar_resultado(self, resultado: ExamenResultado) -> ExamenResultado:
        self.session.add(resultado)
        self.session.commit()
        return resultado


class ExamenService:
    repository: ExamenRepository

    def __init__(self, repository: ExamenRepository):
        self.repository = repository

    def get_examen(self, id_examen: int, id_candidato: int) -> ExamenDTO | ErrorBuilder:
        examen = self.repository.get_examen(id=id_examen)
        resultado = self.repository.get_resultado_for_examen(
            id_candidato=id_candidato, id_examen=id_examen, completado=True
        )
        if not examen:
            error = ErrorBuilder()
            error.add("global", "Exam not found")
            return error

        return examen.build_dto(completed=resultado is not None)

    def get_all_examenes(self, id_candidato: int) -> List[ExamenDTO]:
        examenes = self.repository.get_all_examenes()
        respuestas = self.repository.get_all_resultados_completados(
            id_candidato=id_candidato
        )
        respuestas_dict = {r.id_examen_tecnico: r for r in respuestas}
        return [
            examen.build_dto(completed=examen.id in respuestas_dict)
            for examen in examenes
        ]

    def get_resultado(
        self, id_result: int, id_candidato: int
    ) -> ExamenResultadoDTO | ErrorBuilder:
        resultado = self.repository.get_resultado(
            id_result=id_result, id_candidato=id_candidato
        )
        if not resultado:
            error = ErrorBuilder()
            error.add("global", "Result not found")
            return error

        return resultado.build_dto()

    def get_all_results(self, id_candidato: int) -> List[ExamenResultadoDTO]:
        return [
            r.build_dto()
            for r in self.repository.get_all_resultados_completados(
                id_candidato=id_candidato
            )
        ]

    def start_examen(
        self, id: int, id_candidato: int
    ) -> ExamenStepResponseDTO | ErrorBuilder:
        examen = self.get_examen(id_examen=id, id_candidato=id_candidato)
        error = ErrorBuilder()
        if not examen:
            error.add("global", "Exam not found")
            return error

        resultado = ExamenResultado(
            id_examen_tecnico=id,
            id_candidato=id_candidato,
            completado=False,
            resultado=0,
            progreso=0,
            data=[],
            dificultad_actual=1,
        )
        self.repository.create_resultado(resultado=resultado)

        return self.next_step(resultado=resultado)

    def process_answer(
        self, id_result: int, id_candidato: int, resp: ExamenRespuestaDTO
    ) -> ExamenStepResponseDTO | ErrorBuilder:
        resultado = self.repository.get_resultado(
            id_result=id_result, id_candidato=id_candidato
        )
        error = ErrorBuilder()
        if not resultado:
            error.add("global", "Exam not found")
            return error

        # Si ya se completó el examen, no se puede seguir
        if resultado.completado:
            error = ErrorBuilder()
            error.add("global", "Exam already completed")
            return error

        return self.next_step(resultado=resultado, resp=resp)

    def next_step(
        self, resultado: ExamenResultado, resp: ExamenRespuestaDTO | None = None
    ) -> ExamenStepResponseDTO | ErrorBuilder:
        examen = resultado.examen_tecnico

        # Inicio del examen (primer pregunta)
        if not resp:
            pregunta = self.repository.get_next_pregunta(
                id_examen=examen.id, dificultad=resultado.dificultad_actual
            )
            assert (
                pregunta
            ), f"No se encontró pregunta con criterios {examen.id} {resultado.dificultad_actual}"

            return ExamenStepResponseDTO(
                id_result=resultado.id,
                id_exam=examen.id,
                next_question=pregunta.build_dto(),
                result=None,
            )

        pregunta = self.repository.get_pregunta(
            id=resp.id_question, id_examen=examen.id
        )
        if not pregunta:
            error = ErrorBuilder()
            error.add("global", "Question not found")
            return error

        filtered = list(filter(lambda x: x.id == resp.id, pregunta.respuestas))
        if len(filtered) == 0:
            error = ErrorBuilder()
            error.add("global", "Answer not found")
            return error

        respuesta = filtered[0]

        resultado.progreso += 1
        resultado.data.append(respuesta.id_examen_pregunta)
        resultado.completado = True if resultado.progreso == 3 else False

        if respuesta.correcta:
            resultado.resultado += 1
            resultado.dificultad_actual += 1

        short_finish = resultado.progreso == 2 and resultado.dificultad_actual == 1
        if short_finish:
            resultado.completado = True

        self.repository.guardar_resultado(resultado=resultado)

        if resultado.completado:
            return ExamenStepResponseDTO(
                id_result=resultado.id,
                id_exam=examen.id,
                next_question=None,
                result=resultado.build_dto(),
            )

        pregunta = self.repository.get_next_pregunta(
            id_examen=examen.id,
            dificultad=resultado.dificultad_actual,
            not_in=resultado.data,
        )

        assert (
            pregunta
        ), f"No se encontró pregunta con criterios {examen.id} {resultado.dificultad_actual}"

        return ExamenStepResponseDTO(
            id_result=resultado.id,
            id_exam=examen.id,
            next_question=pregunta.build_dto(),
            result=None,
        )


def get_examen_repository(
    session: Session = Depends(get_db_session_dependency),
) -> ExamenRepository:
    return ExamenRepository(session=session)


def get_examen_service(
    repository: ExamenRepository = Depends(get_examen_repository),
) -> ExamenService:
    return ExamenService(repository=repository)
