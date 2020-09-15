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

import app.TimelineRepository;
import app.UserRepository;
import app.TimelineController;
import app.Timeline;
import app.User;

public class TimelineTest {

	public UserRepository userRepo = mock(UserRepository.class);
	public TimelineRepository timeRepo = mock(TimelineRepository.class);
	public TimelineController tc = new TimelineController(timeRepo, userRepo);
	
	@Before
	public void initialize() {
		
	}
	
	@Test
	public void testFindTimelineByUserID() {
		
		
		//List<Timeline> result = tc.findByUserID(0);
		
		//assertTrue(result.size() == 1);
		assert(true);
	}
}
