import logging

from models.database import get_session
from models.notification import NotificationLog
from services.email_service import send_mark_notification_email
from services.sms_service import send_mark_notification_sms

logger = logging.getLogger(__name__)


def _save_notification_log(notification: dict, channel: str, delivery_status: str,
                           failure_reason: str = None, phone_number: str = None):
    session = get_session()
    try:
        log = NotificationLog(
            student_roll_number=notification.get("studentRollNumber", ""),
            student_name=notification.get("studentName", ""),
            email=notification.get("email"),
            phone_number=phone_number,
            department=notification.get("department"),
            semester=notification.get("semester"),
            subject=notification.get("subject"),
            marks=notification.get("marks"),
            percentage=notification.get("percentage"),
            status=notification.get("status"),
            overall_percentage=notification.get("overallPercentage"),
            overall_status=notification.get("overallStatus"),
            channel=channel,
            delivery_status=delivery_status,
            failure_reason=failure_reason,
            event_type=notification.get("eventType"),
        )
        session.add(log)
        session.commit()
        logger.info("Notification log saved: student=%s channel=%s status=%s",
                     log.student_roll_number, channel, delivery_status)
    except Exception as e:
        session.rollback()
        logger.error("Failed to save notification log: %s", str(e))
    finally:
        session.close()


def handle_notification(notification: dict):
    logger.info("Processing notification for student: %s, subject: %s",
                notification.get("studentRollNumber"), notification.get("subject"))

    # Send email via AWS SES
    email = notification.get("email")
    if email:
        email_result = send_mark_notification_email(notification)
        _save_notification_log(
            notification=notification,
            channel="EMAIL",
            delivery_status=email_result["status"],
            failure_reason=email_result.get("error"),
        )
    else:
        logger.warning("No email found for student %s, skipping email notification",
                       notification.get("studentRollNumber"))
        _save_notification_log(
            notification=notification,
            channel="EMAIL",
            delivery_status="FAILED",
            failure_reason="No email address available",
        )

    # Send SMS via AWS Pinpoint
    phone_number = notification.get("phoneNumber")
    if phone_number:
        sms_result = send_mark_notification_sms(phone_number, notification)
        _save_notification_log(
            notification=notification,
            channel="SMS",
            delivery_status=sms_result["status"],
            failure_reason=sms_result.get("error"),
            phone_number=phone_number,
        )
    else:
        logger.info("No phone number for student %s, skipping SMS notification",
                    notification.get("studentRollNumber"))
