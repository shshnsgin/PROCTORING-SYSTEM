package kz.proctoring.domain.enums;

public enum AuditAction {
    USER_CREATED,
    USER_LOGIN,
    USER_UPDATED,
    USER_DELETED,

    EXAM_CREATED,
    EXAM_UPDATED,
    EXAM_DELETED,

    SESSION_STARTED,
    SESSION_ENDED,

    EVENT_RECORDED,
    REPORT_VIEWED
}