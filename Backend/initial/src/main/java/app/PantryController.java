package app;

import java.io.IOException;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/pantry")
public class PantryController {

	@Autowired
	PantryRepo pantryRepo;

	YummlyController yc = new YummlyController();

	public PantryController() {
	}

	public PantryController(PantryRepo pantryRepo) {
		this.pantryRepo = pantryRepo;
	}

	public PantryController(PantryRepo pantryRepo, YummlyController yc) {
		this.pantryRepo = pantryRepo;
		this.yc = yc;
	}

	/**
	 * Welcome message from the PantryController for the base users path.
	 * 
	 * @return Welcome message
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "\"Hello, from the PantryController!\"";
	}

	/**
	 * Retrieve a list of all pantry items
	 * 
	 * @return List of pantry items in JSON format
	 */
	@GetMapping(path = "/all")
	@ResponseBody
	public Iterable<Pantry> getAllUsers() {
		return pantryRepo.findAll();
	}

	@GetMapping
	@ResponseBody
	public List<Pantry> findPantryByUserID(@RequestParam int userID) {
		return pantryRepo.findAllByUserID(userID);
	}

	@PostMapping
	@ResponseBody
	public ResponseEntity<String> addIngredientToPantry(@RequestBody Pantry pantry) {
		try {
			pantryRepo.save(pantry);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(pantry.toString(), HttpStatus.OK);
	}

	@PostMapping("/add")
	@ResponseBody
	public ResponseEntity<String> addIngredientToPantry(@RequestParam int userID, @RequestParam String ingredient) {
		return addIngredientToPantry(new Pantry(userID, ingredient));
	}

	@PostMapping("/addIngredient")
	@ResponseBody
	public ResponseEntity<String> addIngredientToPantry(@RequestBody Ingredients ingredient, @RequestParam int userID) {
		return addIngredientToPantry(new Pantry(userID, ingredient.getName()));
	}

	@GetMapping("/search")
	public ResponseEntity<String> search(@RequestParam int userID, @RequestParam(required = false) String q) {

		String query;
		if (q != null) {
			query = "q=" + q;
		} else {
			query = "";
		}
		List<Pantry> pantry = pantryRepo.findAllByUserID(userID);
		if (pantry == null) {
			return new ResponseEntity<String>("\"User not found\"", HttpStatus.BAD_REQUEST);
		}
		for (Pantry p : pantry) {
			query += "&allowedIngredient[]=" + p.getIngredient();
		}

//		query.substring(1);

		try {
			return yc.search(query);
		} catch (IOException e) {
			return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
		}

	}

	@DeleteMapping
	@Transactional
	public ResponseEntity<String> deletePantryItem(@RequestParam int userID, @RequestParam String ingredient) {

		pantryRepo.deleteByUserIDAndIngredient(userID, ingredient);

		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);

	}

	@DeleteMapping("/del")
	@Transactional
	public ResponseEntity<String> deletePantryItem(@RequestParam int pantryID) {

		pantryRepo.deleteById(pantryID);

		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);

	}

	@Transactional
	public ResponseEntity<String> deleteAllByUser(int userID) {

		pantryRepo.deleteAllByUserID(userID);

		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);

	}

}
