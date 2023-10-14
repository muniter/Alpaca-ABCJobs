FROM python:3.11-bookworm AS base
ENV SERVICE_NAME=gestion_candidatos

WORKDIR /usr/src/app

RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update && apt-get install --no-install-recommends -y python3 python3-pip libpq-dev gcc

COPY common/requirements.txt ./setup.py .

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt && pip install -e /usr/src/app

CMD [ "uvicorn", "--host", "0.0.0.0", "--port", "80", "gestion_candidatos:app" ]
WORKDIR /usr/src/app/gestion_candidatos

FROM base AS prod

COPY common ../common
COPY gestion_candidatos/ ../gestion_candidatos


