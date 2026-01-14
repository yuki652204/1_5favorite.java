package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.entity.Inquiry;

public interface InquiryRepository
        extends JpaRepository<Inquiry, Long> {
}
