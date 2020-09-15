package app;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingListRepo extends JpaRepository<ShoppingList, Integer> {

	// find
	public List<ShoppingList> findAllByUserID(int userID);
	
	public ShoppingList findByEntryID(Integer entryID);
	
	// del
	public void deleteAllByUserID(int userID);
	
	public void deleteByEntryID(int entryID);
}
