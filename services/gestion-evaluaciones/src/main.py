from fastapi import FastAPI
from .config import configuration

app = FastAPI()


def startup():
    print("Starting up")


@app.on_event("startup")
async def startup_event():
    startup()


@app.get("/")
@app.get("/ping")
def ping():
    return "pong"


@app.get("/health")
def health(source: str = "unknown"):
    data = {"status": "healthy", "source": source, "aws": configuration.in_aws}
    return data
