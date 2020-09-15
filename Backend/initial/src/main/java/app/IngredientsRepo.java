package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepo extends JpaRepository<Ingredients, Integer> {

	public List<Ingredients> findAllByRecipeID(String recipeID);

	// delete ====================================================

	public void deleteByRecipeID(String recipeID);

}
