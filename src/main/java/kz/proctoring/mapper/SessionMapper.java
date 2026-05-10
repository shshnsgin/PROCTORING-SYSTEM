package kz.proctoring.mapper;

import kz.proctoring.domain.entity.ExamSession;
import kz.proctoring.dto.response.SessionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SessionMapper {

    @Mapping(target = "examId", source = "exam.id")
    @Mapping(target = "examTitle", source = "exam.title")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    SessionResponse toResponse(ExamSession session);
}
