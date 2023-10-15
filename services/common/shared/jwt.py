from datetime import datetime, timedelta
import jwt
from .config import configuration
from .api_models.gestion_usuarios import UsuarioDTO, build_usuario_dto


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