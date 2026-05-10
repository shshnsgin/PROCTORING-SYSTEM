package kz.proctoring.mapper;

import javax.annotation.processing.Generated;
import kz.proctoring.domain.entity.Exam;
import kz.proctoring.dto.request.ExamRequest;
import kz.proctoring.dto.response.ExamResponse;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-11T02:56:18+0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class ExamMapperImpl implements ExamMapper {

    @Override
    public Exam toEntity(ExamRequest request) {
        if ( request == null ) {
            return null;
        }

        Exam.ExamBuilder<?, ?> exam = Exam.builder();

        exam.title( request.getTitle() );
        exam.description( request.getDescription() );
        exam.scheduledAt( request.getScheduledAt() );
        if ( request.getDurationMinutes() != null ) {
            exam.durationMinutes( request.getDurationMinutes() );
        }

        return exam.build();
    }

    @Override
    public ExamResponse toResponse(Exam exam) {
        if ( exam == null ) {
            return null;
        }

        ExamResponse.ExamResponseBuilder examResponse = ExamResponse.builder();

        examResponse.id( exam.getId() );
        examResponse.title( exam.getTitle() );
        examResponse.description( exam.getDescription() );
        examResponse.scheduledAt( exam.getScheduledAt() );
        examResponse.durationMinutes( exam.getDurationMinutes() );
        examResponse.active( exam.isActive() );
        examResponse.createdAt( exam.getCreatedAt() );
        examResponse.updatedAt( exam.getUpdatedAt() );

        examResponse.createdById( exam.getCreatedBy() != null ? exam.getCreatedBy().getId() : null );

        return examResponse.build();
    }

    @Override
    public void updateEntity(ExamRequest request, Exam exam) {
        if ( request == null ) {
            return;
        }

        if ( request.getTitle() != null ) {
            exam.setTitle( request.getTitle() );
        }
        if ( request.getDescription() != null ) {
            exam.setDescription( request.getDescription() );
        }
        if ( request.getScheduledAt() != null ) {
            exam.setScheduledAt( request.getScheduledAt() );
        }
        if ( request.getDurationMinutes() != null ) {
            exam.setDurationMinutes( request.getDurationMinutes() );
        }
    }
}
