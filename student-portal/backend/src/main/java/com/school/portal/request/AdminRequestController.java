package com.school.portal.request;

import com.school.portal.request.dto.StudentRequestDto;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/requests")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class AdminRequestController {

    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<StudentRequestDto>> list(@RequestParam("status") RequestStatus status) {
        return ResponseEntity.ok(requestService.findByStatus(status));
    }

    @PostMapping("/{id}/transition")
    public ResponseEntity<StudentRequestDto> transition(@PathVariable Long id,
                                                         @RequestParam("status") RequestStatus status) {
        return ResponseEntity.ok(requestService.transitionStatus(id, status));
    }
}
