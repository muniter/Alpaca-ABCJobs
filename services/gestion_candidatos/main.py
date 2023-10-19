from typing import List, Union
from fastapi import APIRouter, FastAPI, Response, status, Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoCreateDTO,
    CandidatoCreateResponseDTO,
    CandidatoPersonalInformationDTO,
    CandidatoPersonalInformationUpdateDTO,
    CountryDTO,
    LenguajeDTO,
)
from common.shared.config import configuration
from common.shared.api_models.gestion_usuarios import UsuarioDTO
from common.shared.logger import logger
from common.shared.fastapi import shared_app_setup
from common.shared.jwt import get_request_user
from common.shared.api_models.shared import (
    ErrorBuilder,
    SuccessResponse,
    ErrorResponse,
)
from .candidato import (
    CandidatoService,
    CountryRepository,
    LenguajeRepository,
    get_candidato_service,
    get_country_repository,
    get_lenguaje_repository,
)

app = FastAPI(
    openapi_url="/candidatos/openapi.json",
    docs_url="/candidatos/docs",
)
router = APIRouter()
utils_router = APIRouter()
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


@router.post(
    "/personal-info",
    response_model=Union[
        SuccessResponse[CandidatoPersonalInformationDTO], ErrorResponse
    ],
    status_code=status.HTTP_200_OK,
)
def personal_info(
    data: CandidatoPersonalInformationUpdateDTO,
    response: Response,
    user: UsuarioDTO = Depends(get_request_user),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.update_informacion_personal(user.id, data)
    if isinstance(result, ErrorBuilder):
        response.status_code = 400
        return ErrorResponse(errors=result)

    return SuccessResponse(data=result)


@router.get(
    "/personal-info",
    response_model=Union[
        SuccessResponse[CandidatoPersonalInformationDTO], ErrorResponse
    ],
    status_code=status.HTTP_200_OK,
)
def get_personal_info(
    user: UsuarioDTO = Depends(get_request_user),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.get_informacion_personal(user.id)
    return SuccessResponse(data=result)


@utils_router.get(
    "/utils/countries",
    response_model=SuccessResponse[List[CountryDTO]],
    status_code=status.HTTP_200_OK,
)
def get_countries(
    repository: CountryRepository = Depends(get_country_repository),
):
    result = repository.get_all()
    return SuccessResponse(data=result)


@utils_router.get(
    "/utils/languages",
    response_model=SuccessResponse[List[LenguajeDTO]],
    status_code=status.HTTP_200_OK,
)
def get_languages(repository: LenguajeRepository = Depends(get_lenguaje_repository)):
    languages = repository.get_all()
    result = []
    for language in languages:
        result.append(LenguajeDTO(id=language.id, name=language.nombre))

    return SuccessResponse(data=result)


if not configuration.in_aws:
    app.include_router(router)
    app.include_router(utils_router)

app.include_router(router, prefix="/candidatos", tags=["candidatos"])
app.include_router(utils_router, prefix="/candidatos", tags=["utils"])
