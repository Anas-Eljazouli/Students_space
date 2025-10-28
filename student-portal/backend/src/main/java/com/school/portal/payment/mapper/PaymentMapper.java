package com.school.portal.payment.mapper;

import com.school.portal.payment.Payment;
import com.school.portal.payment.dto.PaymentDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    @Mapping(target = "requestId", source = "request.id")
    PaymentDto toDto(Payment entity);
}
