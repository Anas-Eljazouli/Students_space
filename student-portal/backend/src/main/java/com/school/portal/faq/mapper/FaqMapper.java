package com.school.portal.faq.mapper;

import com.school.portal.faq.Faq;
import com.school.portal.faq.dto.FaqDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FaqMapper {
    FaqDto toDto(Faq faq);
}
