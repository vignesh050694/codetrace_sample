import json
import logging
import time

import pika

from config.settings import (
    RABBITMQ_HOST,
    RABBITMQ_PORT,
    RABBITMQ_USERNAME,
    RABBITMQ_PASSWORD,
    RABBITMQ_QUEUE,
    RABBITMQ_EXCHANGE,
    RABBITMQ_ROUTING_KEY,
)
from services.notification_handler import handle_notification

logger = logging.getLogger(__name__)

MAX_RETRIES = 5
RETRY_DELAY_SECONDS = 5


def _get_connection():
    credentials = pika.PlainCredentials(RABBITMQ_USERNAME, RABBITMQ_PASSWORD)
    parameters = pika.ConnectionParameters(
        host=RABBITMQ_HOST,
        port=RABBITMQ_PORT,
        credentials=credentials,
        heartbeat=600,
        blocked_connection_timeout=300,
    )
    return pika.BlockingConnection(parameters)


def _setup_channel(connection):
    channel = connection.channel()
    channel.exchange_declare(exchange=RABBITMQ_EXCHANGE, exchange_type="topic", durable=True)
    channel.queue_declare(queue=RABBITMQ_QUEUE, durable=True)
    channel.queue_bind(exchange=RABBITMQ_EXCHANGE, queue=RABBITMQ_QUEUE, routing_key=RABBITMQ_ROUTING_KEY)
    channel.basic_qos(prefetch_count=1)
    return channel


def _on_message(channel, method, properties, body):
    try:
        notification = json.loads(body)
        logger.info("Received notification for student: %s", notification.get("studentRollNumber"))
        handle_notification(notification)
        channel.basic_ack(delivery_tag=method.delivery_tag)
    except json.JSONDecodeError as e:
        logger.error("Failed to decode message: %s", str(e))
        channel.basic_nack(delivery_tag=method.delivery_tag, requeue=False)
    except Exception as e:
        logger.error("Error processing notification: %s", str(e))
        channel.basic_nack(delivery_tag=method.delivery_tag, requeue=True)


def start_consuming():
    retries = 0
    while True:
        try:
            connection = _get_connection()
            channel = _setup_channel(connection)
            channel.basic_consume(queue=RABBITMQ_QUEUE, on_message_callback=_on_message)

            logger.info("Notification service started. Waiting for messages on queue: %s", RABBITMQ_QUEUE)
            retries = 0
            channel.start_consuming()
        except pika.exceptions.AMQPConnectionError as e:
            retries += 1
            if retries > MAX_RETRIES:
                logger.error("Max retries exceeded. Exiting.")
                raise
            wait = RETRY_DELAY_SECONDS * retries
            logger.warning("Connection lost (%s). Retrying in %ds (attempt %d/%d)...",
                          str(e), wait, retries, MAX_RETRIES)
            time.sleep(wait)
        except KeyboardInterrupt:
            logger.info("Shutting down notification service...")
            break
