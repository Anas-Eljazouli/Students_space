package com.school.portal.faq;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    @Query("select f from Faq f where lower(f.question) like lower(concat('%', :term, '%')) or lower(f.answer) like lower(concat('%', :term, '%'))")
    List<Faq> searchByTerm(@Param("term") String term);
}
