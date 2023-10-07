import os
from fastapi import APIRouter, FastAPI, Response, status
from .shared.config import configuration
from .shared import db


app = FastAPI()
router = APIRouter()


def startup():
    print("Starting up")
    if configuration.in_aws:
        print("Running on AWS")
        print(f"Db uri: {configuration.db_uri}")
        print("Saludos alpaca")
    else:
        print("Not running on AWS")


@app.on_event("startup")
async def startup_event():
    startup()


@router.get("/")
@router.get("/ping")
def ping():
    return "pong"


@router.get("/health/bad")
@router.get("/health/bad")
def set_unhealthy():
    with open("/tmp/unhealthy", "w") as f:
        f.write("0")
    return {"message": "now unhealthy"}


@router.get("/health/ok")
def set_healthy():
    if os.path.exists("/tmp/unhealthy"):
        os.remove("/tmp/unhealthy")
    return {"message": "now healthy"}


@router.get("/health")
def health(response: Response, source: str = "unknown"):
    data_status = "healthy"

    if os.path.exists("/tmp/unhealthy"):
        data_status = "unhealthy"
        response.status_code = status.HTTP_503_SERVICE_UNAVAILABLE

    data = {
        "status": data_status,
        "source": source,
        "aws": configuration.in_aws,
        "task_data": configuration.task_data,
    }
    return data


app.include_router(router)  # Regiser alone so everything is at root
# Register under /evaluaciones for prod
app.include_router(router, prefix="/evaluaciones", tags=["evaluaciones"])
