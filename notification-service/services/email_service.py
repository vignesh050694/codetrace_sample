import logging

import boto3
from botocore.exceptions import ClientError
from jinja2 import Environment, FileSystemLoader, select_autoescape

from config.settings import AWS_REGION, AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, SES_SENDER_EMAIL

logger = logging.getLogger(__name__)

template_env = Environment(
    loader=FileSystemLoader("templates"),
    autoescape=select_autoescape(["html"]),
)


def _get_ses_client():
    kwargs = {"region_name": AWS_REGION}
    if AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY:
        kwargs["aws_access_key_id"] = AWS_ACCESS_KEY_ID
        kwargs["aws_secret_access_key"] = AWS_SECRET_ACCESS_KEY
    return boto3.client("ses", **kwargs)


def render_mark_email(notification: dict) -> str:
    template = template_env.get_template("mark_notification.html")
    return template.render(**notification)


def render_overall_email(notification: dict) -> str:
    template = template_env.get_template("overall_result.html")
    return template.render(**notification)


def send_email(to_email: str, subject: str, html_body: str) -> dict:
    ses_client = _get_ses_client()
    try:
        response = ses_client.send_email(
            Source=SES_SENDER_EMAIL,
            Destination={"ToAddresses": [to_email]},
            Message={
                "Subject": {"Data": subject, "Charset": "UTF-8"},
                "Body": {
                    "Html": {"Data": html_body, "Charset": "UTF-8"},
                },
            },
        )
        message_id = response["MessageId"]
        logger.info("Email sent to %s, MessageId: %s", to_email, message_id)
        return {"status": "SENT", "message_id": message_id}
    except ClientError as e:
        error_msg = e.response["Error"]["Message"]
        logger.error("Failed to send email to %s: %s", to_email, error_msg)
        return {"status": "FAILED", "error": error_msg}


def send_mark_notification_email(notification: dict) -> dict:
    event_type = notification.get("eventType", "MARK_UPDATE")
    student_name = notification.get("studentName", "Student")
    subject_name = notification.get("subject", "")
    semester = notification.get("semester", "")

    subject = f"Mark {event_type.replace('_', ' ').title()} - {subject_name} (Semester {semester}) - {student_name}"
    html_body = render_mark_email(notification)

    return send_email(notification["email"], subject, html_body)
