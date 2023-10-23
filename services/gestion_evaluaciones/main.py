from fastapi import APIRouter, FastAPI
from common.shared.fastapi import shared_app_setup
from common.shared.config import configuration

app = FastAPI(
    openapi_url="/evaluaciones/openapi.json",
    docs_url="/evaluaciones/docs",
)
router = APIRouter()
shared_app_setup(app, router)

if not configuration.in_aws:
    app.include_router(router)


app.include_router(router, prefix="/evaluaciones", tags=["evaluaciones"])
