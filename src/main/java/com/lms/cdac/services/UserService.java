package com.lms.cdac.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lms.cdac.entities.User;

public interface UserService {

	// 🔹 Basic User CRUD Methods
	User saveUser(User user);

	User saveUserAndAssignRole(User user, String roleName);

	Optional<User> getUserById(String id);

	Optional<User> updateUser(User user);

	boolean isUserExist(String userId);

	boolean isUserExistByEmail(String email);

	List<User> getAllUsers();

	User getUserByEmail(String username);

	void deleteUser(String userId);

	void deleteUserByEmail(String email);

	void deleteUserById(String id);

	// 🔹 Role Management
	void assignRole(String userId, String roleName);

	List<String> getUserRoles(String userId);

	boolean hasRole(String userId, String roleName);

	void removeRole(String userId, String roleName);

	// 🔹 Filtering Methods
	Page<User> getAllUsers(Pageable pageable);

	List<User> getUsersByCollege(String college);

	List<User> getUsersByResourceCenter(String resourceCenter);

	User getUserByUsername(String username);

	Optional<User> findById(String userId);

	Optional<User> findByVerificationToken(String token);

	User findByEmail(String email);
	
	List<String> getAllResourceCenters();

	//
}
