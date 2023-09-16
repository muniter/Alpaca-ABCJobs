import os
from fastapi import APIRouter, FastAPI, Response, status
from .config import configuration

app = FastAPI()
router = APIRouter()


def startup():
    print("Starting up")


@app.on_event("startup")
async def startup_event():
    startup()


@router.get("/")
@router.get("/ping")
def ping():
    return "pong"


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


@app.get("/health")
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


app.include_router(router, prefix="/gestion", tags=["gestion"])
