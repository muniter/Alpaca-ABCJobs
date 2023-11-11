FROM python:3.11-bookworm AS builder

WORKDIR /usr/src/app

RUN --mount=type=cache,target=/var/cache/apt \
    --mount=type=cache,target=/var/lib/apt,sharing=locked \
    apt-get update && apt-get install --no-install-recommends -y python3 python3-pip libpq-dev gcc

COPY common/requirements.txt ./setup.py .

RUN --mount=type=cache,target=/root/.cache/pip \
    pip install -r requirements.txt

COPY . .
