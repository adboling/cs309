package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PantryRepo extends JpaRepository<Pantry, Integer> {

	// find
	public List<Pantry> findAllByUserID(int userID);

	public Pantry findByUserIDAndIngredient(int userID, String ingredient);

	// del
	public void deleteAllByUserID(int userID);

	public void deleteByUserIDAndIngredient(int userID, String ingredient);

}
