package com.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import app.UserRepository;
//import app.UserController;
import app.FriendRepository;
import app.FriendController;
import app.Friend;
import app.User;

public class FriendTest {
	
	public UserRepository userRepo = mock(UserRepository.class);
	public FriendRepository friendRepo = mock(FriendRepository.class);
	public FriendController fc = new FriendController(friendRepo, userRepo);
	public User user;
	public User user2;
	public Friend friend;
	
	@Before
	public void initialize() {
		user = new User("admin", "testUserName", "password", "Donald", "Trump", "dtrump@email.com");
		user2 = new User("guest", "testUserName2", "password2", "Herp", "Derpington", "derp@email.com");
		friend = new Friend(0, "testUserName", 1, "testUserName2");
		friend.setStatus("pending");
		user.setId(0);
		user2.setId(1);
		friend.setFriendshipID(0);
	}
	
	@Test //getOutgoingByUsername
	public void testOutgoing() {
		
		List<Friend> testUserNamePending = new ArrayList<>();
		testUserNamePending.add(friend);
		List<Friend> testUserName2Pending = new ArrayList<>();
		
		when(userRepo.findOneById(0)).thenReturn(user);
		when(userRepo.findOneById(1)).thenReturn(user2);
		when(friendRepo.findAllBySenderUsernameAndStatus("testUserName", "pending")).thenReturn(testUserNamePending);
		when(friendRepo.findAllBySenderUsernameAndStatus("testUserName2", "pending")).thenReturn(testUserName2Pending);
		when(userRepo.findByUsernameIgnoreCase("testUserName")).thenReturn(user);
		when(userRepo.findByUsernameIgnoreCase("testUserName2")).thenReturn(user2);

		List<User> result = fc.getOutgoingByUsername("testUserName");
		List<User> result2 = fc.getOutgoingByUsername("testUserName2");

		assertFalse(result.isEmpty());
		assertTrue(result2.isEmpty());
		
		assertEquals(result.size(), 1);
		
		assertTrue(result.get(0).getId() == 1);
		assertEquals(result.get(0).getRole(), "guest");
		assertEquals(result.get(0).getUsername(), "testUserName2");
		assertEquals(result.get(0).getPassword(), "");
		assertEquals(result.get(0).getFirstname(), "Herp");
		assertEquals(result.get(0).getLastname(), "Derpington");
		assertEquals(result.get(0).getEmail(), "");
		
	}
	
	@Test //getIncomingByUsername
	public void testIncoming() {
		
		List<Friend> testUserNamePending = new ArrayList<>();
		List<Friend> testUserName2Pending = new ArrayList<>();
		testUserName2Pending.add(friend);

		when(userRepo.findOneById(0)).thenReturn(user);
		when(userRepo.findOneById(1)).thenReturn(user2);
		when(friendRepo.findAllByRecipientUsernameAndStatus("testUserName", "pending")).thenReturn(testUserNamePending);
		when(friendRepo.findAllByRecipientUsernameAndStatus("testUserName2", "pending")).thenReturn(testUserName2Pending);
		when(userRepo.findByUsernameIgnoreCase("testUserName")).thenReturn(user);
		when(userRepo.findByUsernameIgnoreCase("testUserName2")).thenReturn(user2);

		List<User> result = fc.getIncomingByUsername("testUserName");
		List<User> result2 = fc.getIncomingByUsername("testUserName2");

		assertTrue(result.isEmpty());
		assertFalse(result2.isEmpty());
		
		assertEquals(result2.size(), 1);
		
		assertTrue(result2.get(0).getId() == 0);
		assertEquals(result2.get(0).getRole(), "admin");
		assertEquals(result2.get(0).getUsername(), "testUserName");
		assertEquals(result2.get(0).getPassword(), "");
		assertEquals(result2.get(0).getFirstname(), "Donald");
		assertEquals(result2.get(0).getLastname(), "Trump");
		assertEquals(result2.get(0).getEmail(), "");
		
	}
	
	@Test //acceptRequest
	public void testAccept() {
		
		user.setId(1);
		user2.setId(2);
		Friend friendshipFixedIds = new Friend(1, "testUserName", 2, "testUserName2");
		when(userRepo.findOneById(1)).thenReturn(user);
		when(userRepo.findOneById(2)).thenReturn(user2);
		when(userRepo.findByUsernameIgnoreCase("testUserName")).thenReturn(user);
		when(userRepo.findByUsernameIgnoreCase("testUserName2")).thenReturn(user2);
		when(friendRepo.findBySenderIDAndRecipientID(1, 2)).thenReturn(friendshipFixedIds);

		ResponseEntity<String> result = fc.acceptFriendRequest(new Friend(1, "testUserName", 2, "testUserName2"));
		
		/*
		{
		  "friendshipID" : 0,
		  "senderID" : 1,
		  "senderUsername" : "testUserName",
		  "recipientID" : 2,
		  "recipientUsername" : "testUserName2",
		  "status" : "accepted"
		}
		*/

		Scanner scan = new Scanner(result.getBody());
		assertEquals("{", scan.nextLine());
		assertEquals("  \"friendshipID\" : 0,", scan.nextLine());
		assertEquals("  \"senderID\" : 1,", scan.nextLine());
		assertEquals("  \"senderUsername\" : \"testUserName\",", scan.nextLine());
		assertEquals("  \"recipientID\" : 2,", scan.nextLine());
		assertEquals("  \"recipientUsername\" : \"testUserName2\",", scan.nextLine());
		assertEquals("  \"status\" : \"accepted\"", scan.nextLine());
		assertEquals("}", scan.nextLine());
		scan.close();
		
	}
}
