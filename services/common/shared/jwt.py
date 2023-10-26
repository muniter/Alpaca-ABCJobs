from datetime import datetime
from fastapi import Depends, HTTPException
import jwt
from jwt.exceptions import PyJWTError
from .config import configuration
from .api_models.gestion_usuarios import (
    UsuarioCandidatoDTO,
    UsuarioDTO,
    build_usuario_dto,
)
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from .logger import logger

security = HTTPBearer()


def create_token(usuario_data: dict, duration: int = 86400 * 30) -> str:
    data = {}
    data["exp"] = datetime.utcnow().timestamp() + duration
    data["usuario"] = usuario_data
    return jwt.encode(
        data, configuration.jwt_secret_key, algorithm=configuration.jwt_algorithm
    )


def decode_token(token: str) -> dict:
    result = jwt.decode(
        token,
        configuration.jwt_secret_key,
        algorithms=[configuration.jwt_algorithm],
        verify=True,
    )
    return result


def get_usuario_from_token(token: str) -> UsuarioDTO:
    result = decode_token(token)
    usuario_data = result.get("usuario", None)
    return build_usuario_dto(usuario_data)


def get_request_user(
    authorization: HTTPAuthorizationCredentials = Depends(security),
) -> UsuarioDTO:
    try:
        return get_usuario_from_token(authorization.credentials)
    except PyJWTError as e:
        logger.error(f"Failed getting user from token: {e}")
        raise HTTPException(status_code=401, detail="Invalid authorization code")


def get_request_user_candidato(
    authorization: HTTPAuthorizationCredentials = Depends(security),
) -> UsuarioCandidatoDTO:
    try:
        usuario = get_usuario_from_token(authorization.credentials)
        if isinstance(usuario, UsuarioCandidatoDTO):
            return usuario
        raise HTTPException(
            status_code=401, detail="Endpoint only for candidates, not company users"
        )
    except PyJWTError as e:
        logger.error(f"Failed getting user from token: {e}")
        raise HTTPException(status_code=401, detail="Invalid authorization code")
