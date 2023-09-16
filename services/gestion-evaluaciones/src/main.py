from fastapi import FastAPI

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
