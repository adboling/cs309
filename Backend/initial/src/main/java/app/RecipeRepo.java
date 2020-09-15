package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepo extends JpaRepository<Recipe, String> {

	// find ======================================================

	// public List<Recipe> findAllByRecipeID(String recipeID);

	public List<Recipe> findAllByUserID(int userID);

	public List<Recipe> findAllByDescriptionContainingIgnoreCaseOrRecipeNameContainingIgnoreCase(String keyword,
			String keyword2);

	public Recipe findOneByRecipeID(String recipeID);

	// delete ====================================================

	public void deleteByRecipeID(String recipeID);

	public void deleteByUserID(int userID);
}
