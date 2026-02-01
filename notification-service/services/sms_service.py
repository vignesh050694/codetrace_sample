import logging

import boto3
from botocore.exceptions import ClientError

from config.settings import (
    AWS_REGION,
    AWS_ACCESS_KEY_ID,
    AWS_SECRET_ACCESS_KEY,
    PINPOINT_APP_ID,
    PINPOINT_ORIGINATION_NUMBER,
)

logger = logging.getLogger(__name__)


def _get_pinpoint_client():
    kwargs = {"region_name": AWS_REGION}
    if AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY:
        kwargs["aws_access_key_id"] = AWS_ACCESS_KEY_ID
        kwargs["aws_secret_access_key"] = AWS_SECRET_ACCESS_KEY
    return boto3.client("pinpoint", **kwargs)


def build_sms_message(notification: dict) -> str:
    student_name = notification.get("studentName", "Student")
    subject = notification.get("subject", "N/A")
    semester = notification.get("semester", "N/A")
    marks = notification.get("marks", 0)
    percentage = notification.get("percentage", 0)
    status = notification.get("status", "N/A")
    overall_percentage = notification.get("overallPercentage", 0)

    return (
        f"Hi {student_name}, your marks for {subject} (Sem {semester}) "
        f"are {marks}/100 ({percentage:.1f}%) - {status}. "
        f"Overall: {overall_percentage:.1f}%. "
        f"- University Exam Portal"
    )


def send_sms(phone_number: str, message: str) -> dict:
    pinpoint_client = _get_pinpoint_client()
    try:
        response = pinpoint_client.send_messages(
            ApplicationId=PINPOINT_APP_ID,
            MessageRequest={
                "Addresses": {
                    phone_number: {"ChannelType": "SMS"}
                },
                "MessageConfiguration": {
                    "SMSMessage": {
                        "Body": message,
                        "MessageType": "TRANSACTIONAL",
                        "OriginationNumber": PINPOINT_ORIGINATION_NUMBER,
                    }
                },
            },
        )
        result = response["MessageResponse"]["Result"][phone_number]
        delivery_status = result["DeliveryStatus"]
        status_code = result["StatusCode"]

        if delivery_status == "SUCCESSFUL":
            logger.info("SMS sent to %s, status: %s", phone_number, delivery_status)
            return {"status": "SENT", "delivery_status": delivery_status}
        else:
            logger.warning("SMS to %s status: %s (code: %d)", phone_number, delivery_status, status_code)
            return {"status": "FAILED", "error": f"{delivery_status} (code: {status_code})"}
    except ClientError as e:
        error_msg = e.response["Error"]["Message"]
        logger.error("Failed to send SMS to %s: %s", phone_number, error_msg)
        return {"status": "FAILED", "error": error_msg}


def send_mark_notification_sms(phone_number: str, notification: dict) -> dict:
    message = build_sms_message(notification)
    return send_sms(phone_number, message)
