package com.school.portal.faq;

import com.school.portal.faq.dto.FaqDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/search")
    public ResponseEntity<List<FaqDto>> search(@RequestParam(value = "q", required = false) String query) {
        return ResponseEntity.ok(faqService.search(query));
    }
}
