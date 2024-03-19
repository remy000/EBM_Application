package com.rra.ebm.EBMApplication.repository;

import com.rra.ebm.EBMApplication.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepo extends JpaRepository<Users,Integer> {
}
