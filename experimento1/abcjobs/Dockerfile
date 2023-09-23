FROM python:3.11-bookworm AS builder
#FROM python:3.10.12 AS builder

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y python3 python3-pip --no-install-recommends git

COPY requirements.txt ./

RUN pip install -r requirements.txt

COPY ./src ./src

CMD [ "uvicorn", "--app-dir", "src", "--host", "0.0.0.0", "--port", "8000", "main:app" ]