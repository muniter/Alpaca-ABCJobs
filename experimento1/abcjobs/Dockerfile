FROM python:3.11-bookworm AS builder

WORKDIR /usr/src/app

RUN apt-get update && apt-get install -y --no-install-recommends git

COPY requirements.txt ./

RUN pip install -r requirements.txt

COPY ./src ./src

CMD [ "uvicorn", "--host", "0.0.0.0", "--port", "8000", "src:app" ]