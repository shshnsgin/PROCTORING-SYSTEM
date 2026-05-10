package kz.proctoring.mapper;

import java.util.UUID;
import javax.annotation.processing.Generated;
import kz.proctoring.domain.entity.Exam;
import kz.proctoring.domain.entity.ExamSession;
import kz.proctoring.domain.entity.User;
import kz.proctoring.dto.response.SessionResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-11T02:56:18+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class SessionMapperImpl implements SessionMapper {

    @Override
    public SessionResponse toResponse(ExamSession session) {
        if ( session == null ) {
            return null;
        }

        SessionResponse.SessionResponseBuilder sessionResponse = SessionResponse.builder();

        sessionResponse.examId( sessionExamId( session ) );
        sessionResponse.examTitle( sessionExamTitle( session ) );
        sessionResponse.studentId( sessionStudentId( session ) );
        sessionResponse.studentName( sessionStudentFullName( session ) );
        sessionResponse.id( session.getId() );
        sessionResponse.status( session.getStatus() );
        sessionResponse.startedAt( session.getStartedAt() );
        sessionResponse.endedAt( session.getEndedAt() );
        sessionResponse.violationCount( session.getViolationCount() );
        sessionResponse.notes( session.getNotes() );
        sessionResponse.createdAt( session.getCreatedAt() );

        return sessionResponse.build();
    }

    private UUID sessionExamId(ExamSession examSession) {
        if ( examSession == null ) {
            return null;
        }
        Exam exam = examSession.getExam();
        if ( exam == null ) {
            return null;
        }
        UUID id = exam.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String sessionExamTitle(ExamSession examSession) {
        if ( examSession == null ) {
            return null;
        }
        Exam exam = examSession.getExam();
        if ( exam == null ) {
            return null;
        }
        String title = exam.getTitle();
        if ( title == null ) {
            return null;
        }
        return title;
    }

    private UUID sessionStudentId(ExamSession examSession) {
        if ( examSession == null ) {
            return null;
        }
        User student = examSession.getStudent();
        if ( student == null ) {
            return null;
        }
        UUID id = student.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private String sessionStudentFullName(ExamSession examSession) {
        if ( examSession == null ) {
            return null;
        }
        User student = examSession.getStudent();
        if ( student == null ) {
            return null;
        }
        String fullName = student.getFullName();
        if ( fullName == null ) {
            return null;
        }
        return fullName;
    }
}
