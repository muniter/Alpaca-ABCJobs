import os
import urllib.parse


# Configuration error Exception
class ConfigurationError(Exception):
    pass


class AppConfiguration:
    in_aws: bool
    in_aws: bool
    db_uri: str

    def __init__(self):
        self.in_aws = os.getenv("ECS_CONTAINER_METADATA_URI_V4") is not None
        self.database_configuration()

    def database_configuration(self):
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

        self.db_uri = f"postgresql+psycopg2://{db_user}:{db_password}@{db_host}/{db_name}"


configuration = AppConfiguration()
