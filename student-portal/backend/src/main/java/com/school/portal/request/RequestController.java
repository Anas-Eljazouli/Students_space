package com.school.portal.request;

import com.school.portal.request.dto.RequestCreateDto;
import com.school.portal.request.dto.RequestUpdateDto;
import com.school.portal.request.dto.StudentRequestDto;
import com.school.portal.user.CurrentUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class RequestController {

    private final RequestService requestService;
    private final CurrentUserService currentUserService;

    @PostMapping
    public ResponseEntity<StudentRequestDto> create(@AuthenticationPrincipal UserDetails principal,
                                                    @RequestBody @Valid RequestCreateDto dto) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(requestService.createRequest(user, dto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<StudentRequestDto>> myRequests(@AuthenticationPrincipal UserDetails principal) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(requestService.findMyRequests(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentRequestDto> get(@AuthenticationPrincipal UserDetails principal,
                                                 @PathVariable Long id) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(requestService.findByIdForUser(id, user));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudentRequestDto> update(@AuthenticationPrincipal UserDetails principal,
                                                    @PathVariable Long id,
                                                    @RequestBody @Valid RequestUpdateDto dto) {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(requestService.updateRequest(user, id, dto));
    }

    @PostMapping(value = "/{id}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StudentRequestDto> upload(@AuthenticationPrincipal UserDetails principal,
                                                    @PathVariable Long id,
                                                    @RequestPart("file") MultipartFile file) throws IOException {
        var user = currentUserService.requireUser(principal);
        return ResponseEntity.ok(requestService.uploadFile(user, id, file));
    }
}
