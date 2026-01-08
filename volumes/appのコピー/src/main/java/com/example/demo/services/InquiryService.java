package com.example.demo.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.models.forms.InquiryForm;
import com.example.demo.repositories.InquiryRepository;
import com.example.demo.models.entity.Inquiry;

@Service
@Transactional
public class InquiryService {

    private final InquiryRepository repository;

    public InquiryService(InquiryRepository repository) {
        this.repository = repository;
    }

    public void saveInquiry(InquiryForm form) {

        Inquiry inquiry = new Inquiry();
        inquiry.setName(form.getName());
        inquiry.setMail(form.getMail());
        inquiry.setContent(form.getContent());

        repository.save(inquiry);
    }
    
}
