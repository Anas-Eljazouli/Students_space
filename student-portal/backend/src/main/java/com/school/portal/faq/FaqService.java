package com.school.portal.faq;

import com.school.portal.faq.dto.FaqDto;
import com.school.portal.faq.mapper.FaqMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FaqService {

    private final FaqRepository faqRepository;
    private final FaqMapper faqMapper;

    public List<FaqDto> search(String query) {
        if (query == null || query.isBlank()) {
            return faqRepository.findAll().stream().map(faqMapper::toDto).toList();
        }
        return faqRepository.searchByTerm(query).stream().map(faqMapper::toDto).toList();
    }
}
