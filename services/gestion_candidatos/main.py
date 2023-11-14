from typing import List
from fastapi import APIRouter, FastAPI, Response, status, Depends
from common.shared.api_models.gestion_candidatos import (
    CandidatoConocimientoTecnicoBatchSetDTO,
    CandidatoConocimientoTecnicoCreateDTO,
    CandidatoConocimientoTecnicoDTO,
    CandidatoConocimientoTecnicoTipoDTO,
    CandidatoCreateDTO,
    CandidatoCreateResponseDTO,
    CandidatoDatosAcademicosCreateDTO,
    CandidatoDatosAcademicosDTO,
    CandidatoDatosLaboralesCreateDTO,
    CandidatoDatosLaboralesDTO,
    CandidatoDatosLaboralesTipoDTO,
    CandidatoPersonalInformationDTO,
    CandidatoPersonalInformationUpdateDTO,
    CandidatoSearchDTO,
    CandidatoSearchResultDTO,
    CountryDTO,
    LenguajeDTO,
    RolHabilidadDTO,
)
from common.shared.config import configuration
from common.shared.api_models.gestion_usuarios import (
    UsuarioCandidatoDTO,
    UsuarioEmpresaDTO,
)
from common.shared.logger import logger
from common.shared.fastapi import shared_app_setup
from common.shared.jwt import get_request_user_candidato, get_request_user_empresa
from common.shared.api_models.shared import (
    APIResponse,
    APIResponseModel,
)
from .candidato import (
    CandidatoSearchService,
    CandidatoService,
    ConocimientoTecnicosRepository,
    ConocimientoTecnicosService,
    CountryRepository,
    DatosAcademicosRepository,
    DatosAcademicosService,
    DatosLaboralesService,
    LenguajeRepository,
    RolesHabilidadesRepository,
    get_candidato_service,
    get_conocimientos_tecnicos_repository,
    get_conocimientos_tecnicos_service,
    get_country_repository,
    get_datos_academicos_repository,
    get_datos_academicos_service,
    get_datos_laborales_service,
    get_lenguaje_repository,
    get_roles_habilidades_repository,
    get_candidato_search_service,
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
    response_model=APIResponseModel(CandidatoCreateResponseDTO),
    status_code=status.HTTP_201_CREATED,
)
def crear(
    data: CandidatoCreateDTO,
    service: CandidatoService = Depends(get_candidato_service),
):
    return APIResponse(result=service.crear(data))


@router.post(
    "/search",
    response_model=APIResponseModel(List[CandidatoSearchResultDTO]),
    status_code=status.HTTP_200_OK,
)
def search(
    data: CandidatoSearchDTO,
    service: CandidatoSearchService = Depends(get_candidato_search_service),
    _: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.search(data)
    return APIResponse(result)


@router.post(
    "/personal-info",
    response_model=APIResponseModel(CandidatoPersonalInformationDTO),
    status_code=status.HTTP_200_OK,
)
def personal_info(
    data: CandidatoPersonalInformationUpdateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.update_informacion_personal(user.id_candidato, data)
    return APIResponse(result=result)


@router.get(
    "/personal-info",
    response_model=APIResponseModel(CandidatoPersonalInformationDTO),
    status_code=status.HTTP_200_OK,
)
def get_personal_info(
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: CandidatoService = Depends(get_candidato_service),
):
    result = service.get_informacion_personal(user.id_candidato)
    return APIResponse(result=result)


@router.get(
    "/personal-info/{id}",
    description="Get personal information by id (Only for Companies)",
    response_model=APIResponseModel(CandidatoPersonalInformationDTO),
    status_code=status.HTTP_200_OK,
)
def get_personal_info_by_id(
    id: int,
    service: CandidatoService = Depends(get_candidato_service),
    _: UsuarioEmpresaDTO = Depends(get_request_user_empresa),
):
    result = service.get_informacion_personal(id)
    return APIResponse(result=result)


@router.get(
    "/work-info",
    response_model=APIResponseModel(List[CandidatoDatosLaboralesDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_work_info(
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.get_all(user.id_candidato)
    return APIResponse(result=result)


@router.get(
    "/work-info/{id}",
    response_model=APIResponseModel(CandidatoDatosLaboralesDTO),
    status_code=status.HTTP_200_OK,
)
def get_work_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.get_by_id(id, user.id_candidato)
    return APIResponse(result=result)


@router.post(
    "/work-info",
    response_model=APIResponseModel(CandidatoDatosLaboralesDTO),
    status_code=status.HTTP_201_CREATED,
)
def create_work_info(
    data: CandidatoDatosLaboralesCreateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.crear(user.id_candidato, data)
    return APIResponse(result=result)


@router.post(
    "/work-info/{id}",
    response_model=APIResponseModel(CandidatoDatosLaboralesDTO),
    status_code=status.HTTP_200_OK,
)
def update_work_info(
    id: int,
    data: CandidatoDatosLaboralesCreateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.update(id, user.id_candidato, data)
    return APIResponse(result=result)


@router.delete(
    "/work-info/{id}",
    response_model=APIResponseModel(dict),
    status_code=status.HTTP_200_OK,
)
def delete_work_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosLaboralesService = Depends(get_datos_laborales_service),
):
    result = service.delete(id, user.id_candidato)
    return APIResponse(result=result or {})


@router.get(
    "/technical-info",
    response_model=APIResponseModel(List[CandidatoConocimientoTecnicoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_technical_info(
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.get_all(user.id_candidato)
    return APIResponse(result=result)


@router.get(
    "/technical-info/{id}",
    response_model=APIResponseModel(CandidatoConocimientoTecnicoDTO),
    status_code=status.HTTP_200_OK,
)
def get_technical_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.get_by_id(id, user.id_candidato)
    return APIResponse(result)


@router.post(
    "/technical-info",
    response_model=APIResponseModel(CandidatoConocimientoTecnicoDTO),
    status_code=status.HTTP_201_CREATED,
)
def create_technical_info(
    data: CandidatoConocimientoTecnicoCreateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.crear(user.id_candidato, data)
    return APIResponse(result)


@router.post(
    "/technical-info/batch-set",
    response_model=APIResponseModel(List[CandidatoConocimientoTecnicoDTO]),
    status_code=status.HTTP_201_CREATED,
)
def create_technical_info_batch(
    data: CandidatoConocimientoTecnicoBatchSetDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.batch_set(user.id_candidato, data)
    return APIResponse(result)


@router.post(
    "/technical-info/{id}",
    response_model=APIResponseModel(CandidatoConocimientoTecnicoDTO),
    status_code=status.HTTP_200_OK,
)
def update_technical_info(
    id: int,
    data: CandidatoConocimientoTecnicoCreateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.update(id, user.id_candidato, data)
    return APIResponse(result)


@router.delete(
    "/technical-info/{id}",
    response_model=APIResponseModel(dict),
    status_code=status.HTTP_200_OK,
)
def delete_technical_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: ConocimientoTecnicosService = Depends(get_conocimientos_tecnicos_service),
):
    result = service.delete(id, user.id_candidato)
    return APIResponse(result or {})


@router.get(
    "/academic-info",
    response_model=APIResponseModel(List[CandidatoDatosAcademicosDTO]),
    status_code=status.HTTP_200_OK,
)
def get_all_academic_info(
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.get_all(user.id_candidato)
    return APIResponse(result)


@router.get(
    "/academic-info/{id}",
    response_model=APIResponseModel(CandidatoDatosAcademicosDTO),
    status_code=status.HTTP_200_OK,
)
def get_academic_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.get_by_id(id, user.id_candidato)
    return APIResponse(result)


@router.post(
    "/academic-info",
    response_model=APIResponseModel(CandidatoDatosAcademicosDTO),
    status_code=status.HTTP_201_CREATED,
)
def crate_academic_info(
    data: CandidatoDatosAcademicosCreateDTO,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.crear(user.id_candidato, data)
    return APIResponse(result)


@router.post(
    "/academic-info/{id}",
    response_model=APIResponseModel(CandidatoDatosAcademicosDTO),
    status_code=status.HTTP_200_OK,
)
def update_academic_info(
    data: CandidatoDatosAcademicosCreateDTO,
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.update(id, user.id_candidato, data)
    return APIResponse(result)


@router.delete(
    "/academic-info/{id}",
    response_model=APIResponseModel(dict),
    status_code=status.HTTP_200_OK,
)
def delete_academic_info(
    id: int,
    user: UsuarioCandidatoDTO = Depends(get_request_user_candidato),
    service: DatosAcademicosService = Depends(get_datos_academicos_service),
):
    result = service.delete(id, user.id_candidato)
    return APIResponse(result or {})


@utils_router.get(
    "/utils/countries",
    response_model=APIResponseModel(List[CountryDTO]),
    status_code=status.HTTP_200_OK,
)
def get_countries(
    repository: CountryRepository = Depends(get_country_repository),
):
    result = repository.get_all()
    return APIResponse(result)


@utils_router.get(
    "/utils/languages",
    response_model=APIResponseModel(List[LenguajeDTO]),
    status_code=status.HTTP_200_OK,
)
def get_languages(repository: LenguajeRepository = Depends(get_lenguaje_repository)):
    languages = repository.get_all()
    result = []
    for language in languages:
        result.append(LenguajeDTO(id=language.id, name=language.nombre))

    return APIResponse(result)


@utils_router.get(
    "/utils/skills",
    response_model=APIResponseModel(List[RolHabilidadDTO]),
    status_code=status.HTTP_200_OK,
)
def get_skills(
    repository: RolesHabilidadesRepository = Depends(get_roles_habilidades_repository),
):
    skills = repository.get()
    result = [skill.build_dto() for skill in skills]
    return APIResponse(result)


@utils_router.get(
    "/utils/title-types",
    response_model=APIResponseModel(List[CandidatoDatosLaboralesTipoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_title_types(
    repository: DatosAcademicosRepository = Depends(get_datos_academicos_repository),
):
    tipos = repository.get_tipos()
    result = [tipo.build_dto() for tipo in tipos]
    return APIResponse(result)


@utils_router.get(
    "/utils/technical-info-types",
    response_model=APIResponseModel(List[CandidatoConocimientoTecnicoTipoDTO]),
    status_code=status.HTTP_200_OK,
)
def get_technical_types(
    repository: ConocimientoTecnicosRepository = Depends(
        get_conocimientos_tecnicos_repository
    ),
):
    tipos = repository.get_tipos()
    result = [tipo.build_dto() for tipo in tipos]
    return APIResponse(result)


if not configuration.in_aws:
    app.include_router(router)
    app.include_router(utils_router)

app.include_router(router, prefix="/candidatos", tags=["candidatos"])
app.include_router(utils_router, prefix="/candidatos", tags=["utils"])
