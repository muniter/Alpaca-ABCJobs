FROM python:3.11-bookworm
WORKDIR /usr/src/app
# Postgres client dependencies
RUN apt-get update && apt-get install -y \
    libpq-dev \
    gcc
COPY requirements.txt ./
RUN pip install -r requirements.txt
COPY src src/
# Para hacer debugging interno a la imagen
#CMD ["tail", "-f", "/dev/null"]
CMD [ "uvicorn", "--host", "0.0.0.0", "--port", "80", "src:app" ]
