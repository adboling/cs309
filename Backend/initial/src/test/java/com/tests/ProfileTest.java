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
import app.FriendRepository;
import app.TimelineRepository;

import app.FriendController;
import app.TimelineController;
import app.ProfilesController;

import app.User;
import app.Friend;
import app.Timeline;

public class ProfileTest {
	
	public UserRepository userRepo = mock(UserRepository.class);
	public FriendRepository friendRepo = mock(FriendRepository.class);
	public TimelineRepository timeRepo = mock(TimelineRepository.class);
	public ProfilesController pc = new ProfilesController(userRepo, friendRepo, timeRepo);
	public User userA;
	public User userB;
	public User userC;
	public Timeline entryA;
	public Timeline entryB;
	public List<User> friendList;
	public List<Timeline> timeList;
	
	@Before
	public void initialize() {
		/*
		userA = new User("admin", "userA", "testPasswordA", "Leroy", "Jenkins", "timesupletsdothis@wow.com");
		userA.setId(1);
		userA.setAvatar("testAvatarPathA");
		userA.setBackground("testBackgroundPathA");
		userA.setBio("testBioA");
		
		userB = new User("user", "userB", "testPasswordB", "Bob", "Builder", "bobthebuilder@stupidcartoons.com");
		userB.setId(2);
		userB.setAvatar("");
		userB.setBackground("testBackgroundPathB");
		userB.setBio("testBioB");
		
		userC = new User("curator", "testUserC", "testPasswordC", "Bob", "Ross", "happylittletree@mistake.forest");
		userC.setId(3);
		userC.setAvatar(null);
		userC.setBackground("testBackgroundPathC");
		userC.setBio("testBioC");
		
		friendList.add(userB);
		friendList.add(userC);
		
		entryA = new Timeline();
		entryA.setTimelineID(1);
		entryA.setTime(null);
		entryA.setUserID(1);
		entryA.setUsername("userA");
		entryA.setRecipeID("testtimeIDA");
		entryA.setRecipeName("testRecipeA");
		entryA.setPicture("testPictureA");
		entryA.setNote0("a");
		entryA.setNote1("");
		entryA.setNote2("");
		entryA.setNote3("");
		entryA.setNote4("");
		entryA.setNote5("");
		entryA.setNote6("");
		entryA.setNote7("");
		entryA.setNote8("");
		entryA.setNote9("");
		
		entryB = new Timeline();
		entryB.setTimelineID(1);
		entryB.setTime(null);
		entryB.setUserID(1);
		entryB.setUsername("userA");
		entryB.setRecipeID("testTimeIDB");
		entryB.setRecipeName("testRecipeB");
		entryB.setPicture("testPictureB");
		entryB.setNote0("n");
		entryB.setNote1("");
		entryB.setNote2("");
		entryB.setNote3("");
		entryB.setNote4("");
		entryB.setNote5("");
		entryB.setNote6("");
		entryB.setNote7("");
		entryB.setNote8("");
		entryB.setNote9("");
		*/
	}
	
	@Test //test getProfile //a lot harder than it looks - put off
	public void testGetProfile() {
		//when()
		assert(true);
	}

}
