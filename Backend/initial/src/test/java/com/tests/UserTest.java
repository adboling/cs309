
package com.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import app.User;
import app.UserController;
import app.UserRepository;

public class UserTest {
	public UserRepository userRepository = mock(UserRepository.class);
	public User user = new User("admin", "testUserName", "password", "John", "Dow", "jdow@email.com");
	public UserController uc = new UserController(userRepository);
	public User newUser;

	@Before
	public void initialize() {
		user.setId(0);
	}

	void setNewUser(User user) {
		this.newUser = user;
	}

	@Test
	public void testGetAllUsers() {
		List<User> userList = new ArrayList<User>();
		userList.add(user);
		when(userRepository.findAll()).thenReturn(userList);
		Iterable<User> result = uc.getAllUsers();

		assertEquals(user, result.iterator().next());
		// TODO
		// ask about this
//		this.user.setRole("user");
//		assertNotEquals(user, result.iterator().next());

	}

	@Test
	public void testLogin() {
		when(userRepository.findByEmailIgnoreCase("jdow@email.com")).thenReturn(user);
		ResponseEntity<Integer> result = uc.login("jdow@email.com", "password");
		assertEquals(0, (int) result.getBody());

		result = uc.login("jdow@email.co", "password");
		assertEquals(-1, (int) result.getBody());
		assertEquals(400, result.getStatusCodeValue());

		result = uc.login("jdow@email.com", "passwor");
		assertEquals(-1, (int) result.getBody());
		assertEquals(401, (int) result.getStatusCodeValue());
	}

	@Test
	public void testAddNewUser() {
		when(userRepository.save(user)).thenReturn(user);
		ResponseEntity<String> result = uc.addNewUser(user);
		assertEquals(200, result.getStatusCodeValue());
		assertEquals(user.toString(), result.getBody());

		when(userRepository.save(newUser)).thenThrow(new RuntimeException());
		result = uc.addNewUser(newUser);
		assertEquals(500, result.getStatusCodeValue());

	}

}