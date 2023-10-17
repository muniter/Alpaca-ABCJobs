from typing import Union
from fastapi import APIRouter, FastAPI, Response, status, Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateDTO,
    CandidatoCreateResponseDTO,
)
from common.shared.logger import logger
from common.shared.fastapi import shared_app_setup
from common.shared.api_models.shared import (
    ErrorBuilder,
    SuccessResponse,
    ErrorResponse,
)
from .candidato import CandidatoService, get_candidato_service

app = FastAPI(
    openapi_url="/candidatos/openapi.json",
    docs_url="/candidatos/docs",
)
router = APIRouter()
shared_app_setup(app, router)


@router.post(
    "/crear",
    response_model=Union[SuccessResponse[CandidatoCreateResponseDTO], ErrorResponse],
    status_code=status.HTTP_201_CREATED,
)
def crear(
    data: CandidatoCreateDTO,
    response: Response,
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.crear(data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


app.include_router(router)
app.include_router(router, prefix="/candidatos", tags=["candidatos"])
