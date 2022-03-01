package com.thanhtam.backend.repository;

import com.sun.org.apache.xpath.internal.objects.XBoolean;
import com.thanhtam.backend.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

   User findByUsername(String username);
   User findByEmail(String email);

   List<User> findByIntakeId(Long intakeId);


}
