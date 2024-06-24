package com.portal.repository;

import org.springframework.data.repository.CrudRepository;

import com.google.common.base.Optional;
import com.portal.user.entities.UmOrgRoles;

public interface UmOrgRolesRepo extends CrudRepository<UmOrgRoles, String>{
	Optional<UmOrgRoles> findByRoleShortId(String roleShortId);

}
