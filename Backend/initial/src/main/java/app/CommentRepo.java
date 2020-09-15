package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepo extends JpaRepository<Comments, Integer> {
	
	// find ======================================================
	
	public List<Comments> findAllByRecipeID(String recipeID);
	public List<Comments> findAllByUserID(int userID);
	public Comments findOneByCommentID(int commentID);
	
	// delete ====================================================

	public void deleteByCommentID(int commentID);
	
	public void deleteByUserID(int userID);
}
