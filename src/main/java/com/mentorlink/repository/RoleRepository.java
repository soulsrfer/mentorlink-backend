package com.mentorlink.repository;

import com.mentorlink.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    @Transactional
    @Modifying
    @Query("delete from RoleEntity r where r.name = ?1")
    int deleteByName(String name);
//    RoleEntity findByName(String name);
Optional<RoleEntity> findByName(String name);

}
