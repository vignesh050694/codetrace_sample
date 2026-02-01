import logging

from models.database import init_db
from services.rabbitmq_consumer import start_consuming

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(name)s - %(message)s",
)

logger = logging.getLogger(__name__)


def main():
    logger.info("Initializing notification service...")
    init_db()
    logger.info("Database initialized.")
    start_consuming()


if __name__ == "__main__":
    main()
