import os
import urllib.parse
import requests
import pydantic


class DBSecret(pydantic.BaseModel):
    username: str
    password: str
    host: str
    dbname: str


# Configuration error Exception
class ConfigurationError(Exception):
    pass


class AppConfiguration:
    in_aws: bool = False
    aws_metadata_uri: str | None = None
    db_uri: str = ""
    task_data: dict = {}

    def __init__(self):
        self.aws_metadata_uri = os.getenv("ECS_CONTAINER_METADATA_URI_V4")
        self.in_aws = self.aws_metadata_uri is not None
        if self.in_aws:
            self.extract_metadata()
        self.database_configuration()

    def database_configuration(self):
        if self.in_aws:
            return
            self.database_configuration_aws()
        else:
            self.database_configuration_docker()

    def database_configuration_docker(self):
        db_name = os.getenv("DB_NAME")
        if not db_name:
            raise ConfigurationError("DB_NAME is not set")
        db_user = os.getenv("DB_USER")
        if not db_user:
            raise ConfigurationError("DB_USER is not set")
        db_password = os.getenv("DB_PASSWORD")
        if not db_password:
            raise ConfigurationError("DB_PASSWORD is not set")
        db_password = urllib.parse.quote_plus(db_password)
        db_host = os.getenv("DB_HOST")
        if not db_host:
            raise ConfigurationError("DB_HOST is not set")

        self.db_uri = (
            f"postgresql+psycopg2://{db_user}:{db_password}@{db_host}/{db_name}"
        )

    def database_configuration_aws(self):
        secret_data = os.getenv("DB_SECRET")
        if not secret_data:
            raise ConfigurationError("DB_SECRET is not set")

        try:
            s = DBSecret.model_validate_json(secret_data)
            self.db_uri = f"postgresql+psycopg2://{s.username}:{s.password}@{s.host}/{s.dbname}"
        except pydantic.ValidationError as e:
            raise ConfigurationError("DB_SECRET is not valid JSON") from e

    def extract_metadata(self):
        if not self.in_aws:
            return

        response = requests.get(f"{self.aws_metadata_uri}")
        if response.status_code != 200:
            raise ConfigurationError("Unable to retrieve task metadata")
        data = response.json()
        keys = [
            "Name",
            "Image",
            "ContainerARN",
            "Labels",
            "CreatedAt",
            "StartedAt",
            "DesiredStatus",
        ]
        self.task_data = {k: data.get(k) for k in keys}


configuration = AppConfiguration()
