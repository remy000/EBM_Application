package com.rra.ebm.EBMApplication.repository;

import com.rra.ebm.EBMApplication.domain.Requests;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestRepo extends JpaRepository<Requests, Integer> {
}
