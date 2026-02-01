from datetime import datetime, timezone

from sqlalchemy import Column, Integer, String, Float, DateTime, Text

from models.database import Base


class NotificationLog(Base):
    __tablename__ = "notification_logs"

    id = Column(Integer, primary_key=True, autoincrement=True)
    student_roll_number = Column(String(50), nullable=False, index=True)
    student_name = Column(String(200), nullable=False)
    email = Column(String(200))
    phone_number = Column(String(20))
    department = Column(String(100))
    semester = Column(Integer)
    subject = Column(String(100))
    marks = Column(Float)
    percentage = Column(Float)
    status = Column(String(10))
    overall_percentage = Column(Float)
    overall_status = Column(String(10))
    channel = Column(String(10), nullable=False)  # EMAIL or SMS
    delivery_status = Column(String(20), nullable=False, default="PENDING")  # PENDING, SENT, FAILED
    failure_reason = Column(Text)
    event_type = Column(String(50))
    created_at = Column(DateTime, default=lambda: datetime.now(timezone.utc))

    def __repr__(self):
        return (
            f"<NotificationLog(id={self.id}, student={self.student_roll_number}, "
            f"channel={self.channel}, status={self.delivery_status})>"
        )
