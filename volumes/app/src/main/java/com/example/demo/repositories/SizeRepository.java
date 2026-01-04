package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.models.Size;

public interface SizeRepository extends JpaRepository<Size, Long> {
}