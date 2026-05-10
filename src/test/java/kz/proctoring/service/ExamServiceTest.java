package kz.proctoring.service;

import kz.proctoring.domain.entity.Exam;
import kz.proctoring.dto.request.ExamRequest;
import kz.proctoring.dto.response.ExamResponse;
import kz.proctoring.exception.AppException;
import kz.proctoring.mapper.ExamMapper;
import kz.proctoring.repository.ExamRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private UserService userService;

    @Mock
    private AuditService auditService;

    @Mock
    private ExamMapper examMapper;

    @InjectMocks
    private ExamService examService;

    private UUID adminId;
    private ExamRequest request;

    @BeforeEach
    void setUp() {
        adminId = UUID.randomUUID();

        request = new ExamRequest();
        request.setTitle("Math Exam");
        request.setDurationMinutes(90);
        request.setScheduledAt(LocalDateTime.now().plusDays(1));
    }

    @Test
    void create_shouldSaveExamAndReturnResponse() {
        var exam = Exam.builder()
                .id(UUID.randomUUID())
                .title("Math Exam")
                .build();

        var response = ExamResponse.builder()
                .title("Math Exam")
                .build();

        when(examMapper.toEntity(request)).thenReturn(exam);
        when(examRepository.save(any(Exam.class))).thenReturn(exam);
        when(examMapper.toResponse(any(Exam.class))).thenReturn(response);

        var result = examService.create(request, adminId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Math Exam");

        verify(examMapper).toEntity(request);
        verify(examRepository).save(any(Exam.class));
        verify(examMapper).toResponse(any(Exam.class));
        verify(auditService).log(eq(adminId), any(), anyString());
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        var id = UUID.randomUUID();
        when(examRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.getById(id))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("not found");

        verify(examRepository).findById(id);
    }

    @Test
    void delete_shouldDeactivateExam() {
        var exam = Exam.builder()
                .id(UUID.randomUUID())
                .title("Test")
                .active(true)
                .build();

        when(examRepository.findById(exam.getId())).thenReturn(Optional.of(exam));
        when(examRepository.save(any(Exam.class))).thenReturn(exam);

        examService.delete(exam.getId(), adminId);

        assertThat(exam.isActive()).isFalse();
        verify(examRepository).findById(exam.getId());
        verify(examRepository).save(exam);
    }
}