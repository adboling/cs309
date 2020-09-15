package com.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import app.ShoppingListController;
import app.ShoppingListRepo;
import app.ShoppingList;

import app.Ingredients;

public class ShoppingListTest {

	ShoppingListRepo shoppingRepo = mock(ShoppingListRepo.class);
	ShoppingListController sc = new ShoppingListController(shoppingRepo);
	ArrayList<ShoppingList> allEntries;
	
	@Before
	public void initialize() {
		allEntries = new ArrayList<ShoppingList>();
		
		ShoppingList entry1 = new ShoppingList(1 , "milk", "gallon", 3, 0);
		entry1.setEntryID(1);
		
		ShoppingList entry2 = new ShoppingList(2 , "bagels", "dozen", 1, 1);
		entry1.setEntryID(1);
		
		allEntries.add(entry1);
		allEntries.add(entry2);
		
	}
	
	@Test // test getAllByUserID
	public void testGet() {
		List<ShoppingList> userA = new ArrayList<ShoppingList>();
		userA.add(allEntries.get(0));
		when(shoppingRepo.findAllByUserID(1)).thenReturn(userA);
		
		List<ShoppingList> result = sc.getAllByUserID(1);
		
		assertTrue(result.size() == 1);
		assertTrue(result.get(0).getUserID() == 1);
		assertTrue(result.get(0).getName().equals("milk"));
		assertTrue(result.get(0).getUnit().equals("gallon"));
		assertTrue(result.get(0).getAmount() == 3);
		assertTrue(result.get(0).getComplete() == 0);

	}
	
	@Test // test /add
	public void testAdd() {
		List<Ingredients> newEntryList = new ArrayList<Ingredients>();
		Ingredients newEntry = new Ingredients();
		newEntry.setName("chips");
		newEntry.setUnit("bag");
		newEntry.setAmount(1);
		newEntryList.add(newEntry);
		
		ResponseEntity<String> result = sc.addEntry(newEntryList, 1);
		
		assertEquals("\"entries saved\"", result.getBody());
	}
	
}
