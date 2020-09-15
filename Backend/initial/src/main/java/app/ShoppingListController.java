package app;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller for the shopping list
 * @author Team 27 (VC_5)
 *
 */
@RestController
@RequestMapping(path = "/shoppinglist")
public class ShoppingListController {

	@Autowired
	ShoppingListRepo shoppingRepo;
	
	public ShoppingListController(ShoppingListRepo slr) {
		this.shoppingRepo = slr;
	}
	
	/**
	 * Welcome message from the ShoppingListController for the base /shoppinglist path.
	 * @return Welcome message
	 */
	@GetMapping("/")
	public String welcome() {
		return "Hello, from the ShoppingListController!";
	}
	
	/**
	 * Retrieve a list of all shopping list entries.
	 * A shopping list entry consists of an int entryID, int userID,
	 * String name (ingredient name), String unit, and int amount.
	 * It is in essence an ingredient object with a different id, a userID,
	 * and no recipe id.
	 * @return List of shopping list entries in JSON format
	 */
	@GetMapping("/all")
	@ResponseBody
	public List<ShoppingList> getAllEntries() {
		return shoppingRepo.findAll();
	}
	
	/**
	 * Retrieve a list of shopping list entries for a single user.
	 * @param userID
	 * The id of the user.
	 * @return A list of shopping list entries in JSON format
	 */
	@GetMapping("/get")
	@ResponseBody
	public List<ShoppingList> getAllByUserID(@RequestParam int userID) {
		return shoppingRepo.findAllByUserID(userID);
	}
	
	/**
	 * Adds entries to the specified user's shopping list
	 * @param ingredients
	 * The list of ingredients to add to the user's shopping list
	 * @param userID
	 * The id of the user
	 * @return If an error occurred while saving, "unknown error" with
	 * HttpStatus INTERNAL_SERVER_ERROR. Otherwise, saved the ingredients
	 * and returns "entries saved" with HttpStatus OK.
	 */
	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<String> addEntry(@RequestBody List<Ingredients> ingredients, @RequestParam int userID) {
		
		for(Ingredients i : ingredients) {
			String name = i.getName();
			String unit = i.getUnit();
			int amount = i.getAmount();
			int complete = 0;
		
			ShoppingList newEntry = new ShoppingList(userID, name, unit, amount, complete);
		
			try {
				shoppingRepo.save(newEntry);
			} catch (DataIntegrityViolationException ex) {
				return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
			} catch (Exception ex) {
				return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		
		}
		
		return new ResponseEntity<String>("\"entries saved\"", HttpStatus.OK);
	}
	
	/**
	 * Modifies the completeness of the specified shopping list entry.
	 * @param entryID
	 * The id of the shopping list entry
	 * @param complete
	 * The new completeness of the entry. Must be 0 (incomplete) or 1 (complete).
	 * @return A message relevant to the result of the call
	 */
	@PutMapping("/modifyComplete")
	public ResponseEntity<String> modifyCompleteness(@RequestParam int entryID, @RequestParam int complete) {
		
		if(complete != 0 && complete != 1) {
			return new ResponseEntity<String>("\"complete must be 0 for incomplete, or 1 for complete\"", HttpStatus.BAD_REQUEST);			
		}
		
		ShoppingList entry = shoppingRepo.findByEntryID(entryID);
		if(entry == null) {
			return new ResponseEntity<String>("\"entry was not found or does not exist\"", HttpStatus.BAD_REQUEST);
		}
		
		entry.setComplete(complete);
		shoppingRepo.save(entry);
		
		return new ResponseEntity<String>("\"Completeness successfully modified\"", HttpStatus.OK);
	}
	
	/**
	 * Deletes the specified shopping list entry
	 * @param entryID
	 * The id of the entry to be deleted
	 * @return If entry wasn't found or does not exist, returns "That entry
	 * was not found or does not exist" with HttpStatus BAD_REQUEST.
	 * Otherwise, deletes the entry and returns "deleted" with HttpStatus OK.
	 */
	@DeleteMapping("/delete")
	@Transactional
	@ResponseBody
	public ResponseEntity<String> deleteEntry(@RequestParam int entryID) {
		
		ShoppingList temp = shoppingRepo.findByEntryID(entryID);
		if(temp == null) {
			return new ResponseEntity<String>("\"That entry was not found or does not exist\"", HttpStatus.BAD_REQUEST);
		}
		shoppingRepo.deleteByEntryID(entryID);
		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);
	}
	
	/**
	 * Deletes all shopping list entries associated with a specified user id
	 * @param userID
	 * The id of the user to delete all entries for
	 * @return If no entries were found or that user does not exist, returns 
	 * "No entries found or that user does not exist" with HttpStatus BAD_REQUEST.
	 * Otherwise, deletes the entries and returns "deleted" with HttpStatus OK.
	 */
	@DeleteMapping("/deleteAll")
	@Transactional
	@ResponseBody
	public ResponseEntity<String> deleteAllByUserID(@RequestParam int userID) {
		
		List<ShoppingList> temp = shoppingRepo.findAllByUserID(userID);
		if(temp.isEmpty()) {
			return new ResponseEntity<String>("\"No entries found or that user does not exist\"", HttpStatus.BAD_REQUEST);
		}
		shoppingRepo.deleteAllByUserID(userID);
		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);
	}
}
