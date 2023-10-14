from typing import Union
from common.shared.api_models.shared import ErrorResponse, SuccessResponse
from ..api_models.gestion_usuarios import (
    UsuarioLoginResponseDTO,
    UsuarioRegisterDTO,
)
from ..config import configuration
import requests


class UsuarioClient:
    def __init__(self):
        if configuration.in_aws:
            self.url = "https://api.abc.muniter.link/usuarios"
        else:
            self.url = "http://gestion_usuarios/usuarios"

    def crear_login(
        self, data: UsuarioRegisterDTO
    ) -> Union[SuccessResponse[UsuarioLoginResponseDTO], ErrorResponse]:
        response = requests.post(f"{self.url}/crear", json=data.model_dump())
        response_data = response.json()
        if response.status_code != 201:
            validated = ErrorResponse.model_validate(response_data)
            return validated

        validated = SuccessResponse.model_validate(response_data)
        return validated
