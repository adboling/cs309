package app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * the Controller for everything involving custom recipes
 *
 */
@RestController
@RequestMapping(path = "/recipes")
public class RecipeController {

	@Autowired
	RecipeRepo recipeRepo;

	@Autowired
	IngredientsRepo ingredientRepo;

	public RecipeController(RecipeRepo rr, IngredientsRepo ir) {
		this.ingredientRepo = ir;
		this.recipeRepo = rr;
	}

	/**
	 * 
	 * @return DEMO TEXT
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "Hello, from the RecipeController!";
	}

	/**
	 * used to get all the custom recipes
	 * 
	 * @return a JSON list all of all recipes
	 */
	@GetMapping("/all")
	@ResponseBody
	public List<Recipe> getAllRecipes() {

		List<Recipe> recipes = recipeRepo.findAll();
		for (Recipe r : recipes) {
			r.setIngredients(ingredientRepo.findAllByRecipeID(r.getRecipeID()));
		}
		return recipes;
	}

	/**
	 * is used to get a list of recipes from a specific user
	 * 
	 * @param userID is the ID of the user
	 * @return a JSON list of recipes
	 */
	@GetMapping("/user")
	@ResponseBody
	public List<Recipe> findByUser(@RequestParam int userID) {
		List<Recipe> recipes = recipeRepo.findAllByUserID(userID);
		for (Recipe r : recipes) {
			r.setIngredients(ingredientRepo.findAllByRecipeID(r.getRecipeID()));
		}
		return recipes;
	}

	/**
	 * used as a key word search for custom recipes
	 * 
	 * @param q the terms to search separate each search term with a '+'
	 * @return a JSON list of recipes that mach the search
	 */
	@GetMapping("/search")
	@ResponseBody
	public HashSet<Recipe> searchRecipes(@RequestParam String q) {
		HashSet<Recipe> recipes = new HashSet<Recipe>();
		String[] query = q.split("\\+");

		for (String s : query) {
			s = s.trim();
			recipes.addAll(recipeRepo.findAllByDescriptionContainingIgnoreCaseOrRecipeNameContainingIgnoreCase(s, s));

		}

		for (Recipe r : recipes) {
			r.setIngredients(ingredientRepo.findAllByRecipeID(r.getRecipeID()));
		}

		return recipes;
	}

	/**
	 * used to find a recipe with that ID
	 * 
	 * @param recipeID the ID of the recipe to re returned
	 * @return a JSON of the found recipe. Empty if no recipe
	 */
	@GetMapping
	@ResponseBody
	public Recipe findByRecipeID(@RequestParam String recipeID) {
		Recipe recipe = recipeRepo.findOneByRecipeID(recipeID);
		recipe.setIngredients(ingredientRepo.findAllByRecipeID(recipeID));
		return recipe;
	}

	/**
	 * used to make a new custom recipe
	 * 
	 * @param recipe a JSON of the recipe to make with the picture as a base64 .jpg
	 * @return a JSON of the new recipe with the picture as a url
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException          if incorrect param
	 */
	@PostMapping
	public ResponseEntity<String> postRecipe(@RequestBody Recipe recipe)
			throws JsonParseException, JsonMappingException, IOException {
		Calendar cal = Calendar.getInstance();
		recipe.setTime(new Timestamp(cal.getTimeInMillis()));
		String rand = String.valueOf(new Random().nextInt(2000000000));
		recipe.setRecipeID(rand + "_" + recipe.getRecipeName().replace(' ', '_'));

		// save image

		if (recipe.getPicture() != null && !recipe.getPicture().isEmpty()) {
			String base64Image = recipe.getPicture();
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			File outputfile = new File("/var/www/html/images/recipeImages/" + recipe.getRecipeID() + ".jpg");
			ImageIO.write(img, "jpg", outputfile);
			recipe.setPicture(
					"http://cs309-vc-5.misc.iastate.edu/images/recipeImages/" + recipe.getRecipeID() + ".jpg");
		}
		for (Ingredients i : recipe.getIngredients()) {
			i.setRecipeID(recipe.getRecipeID());
		}

		try {
			recipeRepo.save(recipe);
			ingredientRepo.saveAll(recipe.getIngredients());
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<String>(recipe.toString(), HttpStatus.OK);
	}

	/**
	 * used to delete a recipe
	 * 
	 * @param recipeID the ID of the recipe to be deleted
	 * @return "Success" if recipe is deleted
	 */
	@DeleteMapping
	@Transactional
	public ResponseEntity<String> DelRecipe(@RequestParam String recipeID) {
		recipeRepo.deleteByRecipeID(recipeID);
		ingredientRepo.deleteByRecipeID(recipeID);
		return new ResponseEntity<String>("\"Success\"", HttpStatus.OK);
	}

	@Transactional
	public Boolean DelRecipeByUser(int userID) {
		List<Recipe> rl = recipeRepo.findAllByUserID(userID);
		recipeRepo.deleteByUserID(userID);
		rl.forEach(r -> ingredientRepo.deleteByRecipeID(r.getRecipeID()));
		return true;
	}

}
