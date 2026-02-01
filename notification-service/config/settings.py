import os
from dotenv import load_dotenv

load_dotenv()

# RabbitMQ
RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
RABBITMQ_PORT = int(os.getenv("RABBITMQ_PORT", "5672"))
RABBITMQ_USERNAME = os.getenv("RABBITMQ_USERNAME", "guest")
RABBITMQ_PASSWORD = os.getenv("RABBITMQ_PASSWORD", "guest")
RABBITMQ_QUEUE = os.getenv("RABBITMQ_QUEUE", "notify-mark")
RABBITMQ_EXCHANGE = os.getenv("RABBITMQ_EXCHANGE", "notification-exchange")
RABBITMQ_ROUTING_KEY = os.getenv("RABBITMQ_ROUTING_KEY", "notify-mark")

# AWS General
AWS_REGION = os.getenv("AWS_REGION", "us-east-1")
AWS_ACCESS_KEY_ID = os.getenv("AWS_ACCESS_KEY_ID", "")
AWS_SECRET_ACCESS_KEY = os.getenv("AWS_SECRET_ACCESS_KEY", "")

# AWS SES
SES_SENDER_EMAIL = os.getenv("SES_SENDER_EMAIL", "noreply@university.edu")

# AWS Pinpoint
PINPOINT_APP_ID = os.getenv("PINPOINT_APP_ID", "")
PINPOINT_ORIGINATION_NUMBER = os.getenv("PINPOINT_ORIGINATION_NUMBER", "")

# Database
DATABASE_URL = os.getenv("DATABASE_URL", "sqlite:///notifications.db")
