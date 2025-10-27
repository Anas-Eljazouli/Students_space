package com.school.portal.request;

import com.school.portal.notification.UserNotificationService;
import com.school.portal.request.dto.RequestCreateDto;
import com.school.portal.request.dto.RequestUpdateDto;
import com.school.portal.request.dto.StudentRequestDto;
import com.school.portal.request.mapper.RequestMapper;
import com.school.portal.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final StudentRequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final UserNotificationService notificationService;

    @Transactional(readOnly = true)
    public List<StudentRequestDto> findMyRequests(User student) {
        return requestRepository.findAllByStudentOrderByCreatedAtDesc(student)
                .stream().map(requestMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public StudentRequestDto findByIdForUser(Long id, User student) {
        StudentRequest request = requestRepository.findById(id).orElseThrow();
        if (!request.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        return requestMapper.toDto(request);
    }

    @Transactional
    public StudentRequestDto createRequest(User student, RequestCreateDto dto) {
        StudentRequest request = StudentRequest.builder()
                .student(student)
                .type(dto.type())
                .status(RequestStatus.SUBMITTED)
                .payloadJson(dto.payloadJson())
                .build();
        StudentRequest saved = requestRepository.save(request);
        notifyStaff(saved, "REQUEST_CREATED");
        return requestMapper.toDto(saved);
    }

    @Transactional
    public StudentRequestDto updateRequest(User student, Long id, RequestUpdateDto dto) {
        StudentRequest request = requestRepository.findById(id).orElseThrow();
        if (!request.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        if (dto.payloadJson() != null) {
            request.setPayloadJson(dto.payloadJson());
        }
        if (dto.status() != null) {
            request.setStatus(dto.status());
        }
        StudentRequest saved = requestRepository.save(request);
        notifyStaff(saved, "REQUEST_UPDATED");
        return requestMapper.toDto(saved);
    }

    @Transactional
    public StudentRequestDto transitionStatus(Long id, RequestStatus status) {
        StudentRequest request = requestRepository.findById(id).orElseThrow();
        request.setStatus(status);
        StudentRequest saved = requestRepository.save(request);
        notificationService.notifyUser(saved.getStudent(), "REQUEST_STATUS", status.name());
        return requestMapper.toDto(saved);
    }

    @Transactional
    public StudentRequestDto uploadFile(User student, Long requestId, MultipartFile file) throws IOException {
        StudentRequest request = requestRepository.findById(requestId).orElseThrow();
        if (!request.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Access denied");
        }
        Path uploads = Path.of("uploads");
        Files.createDirectories(uploads);
        String filename = Instant.now().toEpochMilli() + "_" + file.getOriginalFilename();
        Path destination = uploads.resolve(filename);
        file.transferTo(destination);
        RequestFile requestFile = RequestFile.builder()
                .request(request)
                .filename(file.getOriginalFilename())
                .mime(file.getContentType())
                .url("/uploads/" + filename)
                .build();
        request.getFiles().add(requestFile);
        requestRepository.save(request);
        return requestMapper.toDto(request);
    }

    @Transactional(readOnly = true)
    public List<StudentRequestDto> findByStatus(RequestStatus status) {
        return requestRepository.findAllByStatusOrderByCreatedAtDesc(status)
                .stream().map(requestMapper::toDto).toList();
    }

    private void notifyStaff(StudentRequest request, String type) {
        notificationService.notifyUser(request.getStudent(), type, requestMapper.toDto(request));
    }
}
