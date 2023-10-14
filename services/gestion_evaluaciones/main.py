from fastapi import APIRouter, FastAPI
from common.shared.fastapi import shared_app_setup

app = FastAPI(
    openapi_url="/evaluaciones/openapi.json",
    docs_url="/evaluaciones/docs",
)
router = APIRouter()
shared_app_setup(app, router)

app.include_router(router)  # Regiser alone so everything is at root
# Register under /evaluaciones for prod
app.include_router(router, prefix="/evaluaciones", tags=["evaluaciones"])
