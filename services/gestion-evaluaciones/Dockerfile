FROM python:3.11-bookworm AS builder

WORKDIR /usr/src/app

RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt \
    apt-get update && apt-get install --no-install-recommends -y python3 python3-pip libpq-dev gcc

COPY gestion-evaluaciones/requirements.txt ./

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt

COPY gestion-evaluaciones/src ./src
COPY common/shared ./src/shared

CMD [ "uvicorn", "--host", "0.0.0.0", "--port", "80", "src:app" ]
