from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from fastapi.encoders import jsonable_encoder
from .api_models.shared import ErrorBuilder
from .config import configuration
from fastapi import FastAPI, Request, APIRouter
from fastapi.middleware.cors import CORSMiddleware
from .logger import logger


def shared_app_setup(app: FastAPI, router: APIRouter):
    @app.exception_handler(RequestValidationError)
    async def validation_exception_handler(
        request: Request, exc: RequestValidationError
    ):
        error = ErrorBuilder()
        for pydantic_error in exc.errors():
            print("Pydantic error", pydantic_error)
            loc, msg = pydantic_error["loc"], pydantic_error["msg"]
            filtered_loc = loc[1:] if loc[0] in ("body", "query", "path") else loc
            field_string = ".".join(filtered_loc)  # nested fields with dot-notation
            error.add(field_string, msg)

        return JSONResponse(
            status_code=400,
            content=jsonable_encoder(
                {
                    "success": False,
                    "errors": error.serialize(),
                }
            ),
        )

    @router.get("/")
    @router.get("/ping")
    @app.get("/ping")
    def ping():
        return {"ping": "pong"}

    @router.get("/health")
    @app.get("/health")
    def health(source: str = "unknown"):
        data_status = "healthy"

        data = {
            "status": data_status,
            "source": source,
            "aws": configuration.in_aws,
            "task_data": configuration.task_data,
        }
        return data

    @app.on_event("startup")
    async def startup():
        if configuration.in_aws:
            logger.info("Running on AWS")
        else:
            logger.info("Running on local environment")

        logger.info(f"Startup complete")

    # CORS
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["https://jobs.abc.muniter.link"],
        allow_origin_regex="http://localhost:.*",
        allow_headers=["*"],
        allow_methods=["*"],
    )
