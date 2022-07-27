package com.tadelle.dscatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tadelle.dscatalog.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

}
