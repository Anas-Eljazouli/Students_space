package com.school.portal.faq.dto;

import java.util.List;

public record FaqDto(Long id,
                     String question,
                     String answer,
                     List<String> tags) {}
