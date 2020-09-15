package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineRepository extends JpaRepository<Timeline, Integer> {

	// find by single parameter ==========================================

	public Timeline findByTimelineID(int timelineID);

	public List<Timeline> findAllByUserID(int userID);

	public List<Timeline> findAllByRecipeID(String recipeID);

	// find by multiple parameters =======================================

	// delete by a single parameter ======================================

	public void deleteByTimelineID(int timelineID);

	public void deleteByRecipeID(String recipeID);

	public void deleteByUserID(int userID);

}
