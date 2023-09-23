from typing import Literal
import requests
import csv
from datetime import datetime
import logging

logger = logging.getLogger(__name__)
# Log to stdout
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(message)s")

events = []
special_events = {}

BASE_URL = "https://api.abc.muniter.link/gestion"
OriginalContainerARN: str | None = None

# Define a type for the events
EventName = Literal["start", "set_unhealthy", "healthy", "unhealthy", "unresponsive"]


# Type of name should be one of the string literals in event_names
def append_event(name: EventName):
    data = {"name": name, "timestamp": datetime.now().isoformat()}
    if len(events) and events[-1]["name"] != name:
        special_events[name] = data
    events.append(data)


def get_container_arn() -> str:
    url = f"{BASE_URL}/health"
    response = requests.get(url)
    response.raise_for_status()
    data = response.json()
    assert data["status"] == "healthy"
    arn = data["task_data"]["ContainerARN"]
    logger.info("Container ARN is %s", arn)
    return arn


def make_service_unhealthy():
    url = f"{BASE_URL}/health/bad"
    response = requests.get(url)
    response.raise_for_status()
    logger.info("Service is now unhealthy")
    append_event("set_unhealthy")


def monitor_health():
    assert OriginalContainerARN is not None
    url = f"{BASE_URL}/health"
    while True:
        response = requests.get(url, timeout=5)
        if response.ok:
            data = response.json()
            status = data["status"]
            container_arn = data["task_data"]["ContainerARN"]

            if status == "healthy" and container_arn != OriginalContainerARN:
                logger.info(
                    "Service is now healthy, container ARN is %s", container_arn
                )
                append_event("healthy")
                return
            else:
                raise Exception("Unexpected status or container ARN")
        else:
            try:
                data = response.json()
                logger.info("Service is responding but unhealthy")
                append_event("unhealthy")
            except requests.exceptions.JSONDecodeError:
                logger.info("Service is not responding")
                append_event("unresponsive")


def save_events():
    time = datetime.now().strftime("%Y%m%d_%H%M%S")
    with open(f"{time}_experimento_recuperacion.csv", "w") as f:
        writer = csv.DictWriter(f, fieldnames=["timestamp", "name"])
        writer.writeheader()
        writer.writerows(events)
    print(f"Saved events to {time}_experimento_recuperacion")


def summaryze():
    unhealthy = datetime.fromisoformat(special_events["set_unhealthy"]["timestamp"])
    healthy = datetime.fromisoformat(special_events["healthy"]["timestamp"])
    unresponsive = datetime.fromisoformat(special_events["unresponsive"]["timestamp"])
    time_to_recovery = (healthy - unhealthy).seconds
    time_to_removal = (unresponsive - unhealthy).seconds
    time_of_replacement = (healthy - unresponsive).seconds
    print(f"Tiempo de recuperación: {time_to_recovery} segundos")
    print(f"Tiempo de remoción: {time_to_removal} segundos")
    print(
        f"Tiempo de reemplazo (entre unresponsive a healthy): {time_of_replacement} segundos"
    )


def main():
    global OriginalContainerARN
    append_event("start")
    OriginalContainerARN = get_container_arn()
    make_service_unhealthy()
    monitor_health()
    save_events()
    summaryze()


if __name__ == "__main__":
    main()
