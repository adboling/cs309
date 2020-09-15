package com.tests;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import app.Ingredients;
import app.Pantry;
import app.PantryController;
import app.PantryRepo;
import app.YummlyController;

public class PantryTest {
	public PantryRepo pantryRepo = mock(PantryRepo.class);
	public YummlyController yc = mock(YummlyController.class);
	public PantryController pc = new PantryController(pantryRepo, yc);
	Pantry pantry1;
	Pantry pantry2;

	@Before
	public void initialize() {
		pantry1 = new Pantry(1, "banana");
		pantry2 = new Pantry(1, "pasta");
	}

	@Test
	public void testAddIngredientToPantry() throws JsonParseException, JsonMappingException, IOException {
		ResponseEntity<String> result;
		Pantry p;

		when(pantryRepo.save(null)).thenThrow(new RuntimeException());
		result = pc.addIngredientToPantry(null);
		assertEquals(500, result.getStatusCodeValue());

		when(pantryRepo.save(pantry1)).thenReturn(pantry1);
		result = pc.addIngredientToPantry(pantry1);
		p = new ObjectMapper().readValue(result.getBody(), Pantry.class);
		assertEquals(200, result.getStatusCodeValue());
		assertEquals(pantry1, p);

		result = pc.addIngredientToPantry(1, "banana");
		p = new ObjectMapper().readValue(result.getBody(), Pantry.class);
		assertEquals(200, result.getStatusCodeValue());
		assertEquals(pantry1, p);

		result = pc.addIngredientToPantry(new Ingredients("banana"), 1);
		p = new ObjectMapper().readValue(result.getBody(), Pantry.class);
		assertEquals(200, result.getStatusCodeValue());
		assertEquals(pantry1, p);

	}

	@Test
	public void testSearch() throws IOException {
		List<Pantry> pantryList = new ArrayList<>();
		pantryList.add(pantry1);
		pantryList.add(pantry2);
		ResponseEntity<String> result;

		when(pantryRepo.findAllByUserID(0)).thenReturn(null);
		result = pc.search(0, null);
		assertEquals(400, result.getStatusCodeValue());
		assertEquals("\"User not found\"", result.getBody());

		when(pantryRepo.findAllByUserID(1)).thenReturn(pantryList);
		when(yc.search("&allowedIngredient[]=banana&allowedIngredient[]=pasta"))
				.thenReturn(new ResponseEntity<String>("recipes", HttpStatus.OK));
		result = pc.search(1, null);
		assertEquals(200, result.getStatusCodeValue());
		assertEquals("recipes", result.getBody());

	}

}
