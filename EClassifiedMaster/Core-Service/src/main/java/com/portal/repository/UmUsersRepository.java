package com.portal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.portal.user.entities.UmUsers;

public interface UmUsersRepository extends CrudRepository<UmUsers, Integer>{

	@Query("from UmUsers us where upper(us.logonId)= upper( ?1 ) and us.markAsDelete = ?2")      
	List<UmUsers> getUserLoginId(String logonId, boolean markAsDelete);
	
	@Query("from UmUsers us where upper(us.logonId)= upper( ?1 ) and us.markAsDelete=?2")      
	List<UmUsers> getUserbyLoginId(String logonId, boolean markAsDelete);
	
	@Query("from UmUsers us where us.userId=?1 and us.markAsDelete = ?2")      
	List<UmUsers> getUserByUserId(Integer userId, boolean markAsDelete);
	
	@Query("select um.userId, um.email from UmUsers um where um.userId in (?1) and um.markAsDelete = ?2")      
	List<Object[]> getUsersByUserIds(List<Integer> userIds, boolean markAsDelete);
	
	Boolean existsByMobile(String mobile);
	
	Boolean existsByLogonId(String logonId);
	
	@Query("from UmUsers us where us.userId in(?1) and us.markAsDelete = ?2")      
	List<UmUsers> getUsersByCreatedId(List<Integer> userId, boolean markAsDelete);
}
