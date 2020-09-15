package com.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.CommentController;
import app.CommentRepo;
import app.Comments;

public class CommentTest {

	public Comments comment1;
	public Comments comment2;
	public CommentRepo commentRepo = mock(CommentRepo.class);
	public CommentController cc = new CommentController(commentRepo);

	@Before
	public void initialize() {
		comment1 = new Comments(0, 1, "my_recipe", new Timestamp(0), "comment", 0);
		comment2 = new Comments(2, 1, "my_recipe2", new Timestamp(0), "comment", 0);

	}

	@Test
	public void testGetAll() {
		List<Comments> result;

		when(commentRepo.findAll()).thenReturn(new ArrayList<Comments>(Arrays.asList(comment1)));

		result = cc.getAllComments();
		assertSame(comment1, result.get(0));

		when(commentRepo.findAll()).thenReturn(null);
		result = cc.getAllComments();
		assertNull(result);
	}

	@Test
	public void testPostComment() throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<String> result;

		when(commentRepo.save(new Comments())).thenThrow(new RuntimeException());
		result = cc.postComment(new Comments());
		assertEquals(200, result.getStatusCodeValue());

		when(commentRepo.save(new Comments())).thenReturn(null);
		result = cc.postComment(new Comments());
		Comments c = new ObjectMapper().readValue(result.getBody(), Comments.class);
		assertTrue(result.getBody().contains("null"));
		assertEquals(200, result.getStatusCodeValue());

		assertNotEquals(0, c.getTime());

		when(commentRepo.save(comment1)).thenReturn(comment1);
		result = cc.postComment(comment1);
		c = new ObjectMapper().readValue(result.getBody(), Comments.class);
		assertEquals(200, result.getStatusCodeValue());
//		assertFalse(result.getBody().contains("null"));
		assertEquals("my_recipe", c.getRecipeID());
		assertEquals(1, c.getUserID());

	}

}
