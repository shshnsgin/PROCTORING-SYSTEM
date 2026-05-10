package kz.proctoring.mapper;

import kz.proctoring.domain.entity.Exam;
import kz.proctoring.dto.request.ExamRequest;
import kz.proctoring.dto.response.ExamResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    Exam toEntity(ExamRequest request);

    @Mapping(
        target = "createdById",
        expression = "java(exam.getCreatedBy() != null ? exam.getCreatedBy().getId() : null)"
    )
    ExamResponse toResponse(Exam exam);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntity(ExamRequest request, @MappingTarget Exam exam);
}