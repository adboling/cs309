package app;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * This class is for making and deleting comments
 *
 */
@RestController
@RequestMapping(path = "/comments")
public class CommentController {

	@Autowired
	CommentRepo commentRepo;

	public CommentController(CommentRepo cr) {
		this.commentRepo = cr;
	}

	/**
	 * @return TEST STRING
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "\"Hello, from the CommentController!\"";
	}

	/**
	 * 
	 * @return list of every comment for debugging uses
	 */
	@GetMapping("/all")
	@ResponseBody
	public List<Comments> getAllComments() {
		return commentRepo.findAll();
	}

	/**
	 * 
	 * @param userID the ID of the user you want comments from
	 * @return a list of all comments by the user with specified id
	 */
	@GetMapping("/user")
	@ResponseBody
	public List<Comments> findByUser(@RequestParam int userID) {
		return commentRepo.findAllByUserID(userID);
	}

	/**
	 * takes in comment ID and returns one comment
	 * 
	 * @param commentID the ID of the comment you want returned
	 * @return a JSON of a comment specified by commentID
	 */
	@GetMapping("/find-by-commentID")
	@ResponseBody
	public Comments findByCommentID(@RequestParam int commentID) {
		return commentRepo.findOneByCommentID(commentID);
	}

	/**
	 * takes the id of a recipe and returns all of the comments
	 * 
	 * @param recipeID id of recipe to be returned
	 * @return a list of every comment on the given recipe
	 */
	@GetMapping
	@ResponseBody
	public List<Comments> getComments(@RequestParam String recipeID) {
		return commentRepo.findAllByRecipeID(recipeID);
	}

	/**
	 * used to add a new comment to a recipe
	 * 
	 * @param comment this is will be a comment in JSON form
	 * @return the comment in JSON from including comment ID
	 */
	@PostMapping
	public ResponseEntity<String> postComment(@RequestBody Comments comment) {
		Calendar cal = Calendar.getInstance();
		comment.setTime(new Timestamp(cal.getTimeInMillis()));
		try {
			commentRepo.save(comment);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(comment.toString(), HttpStatus.OK);

	}

	/**
	 * used to delete a comment
	 * 
	 * @param commentID the comment id that you want to delete
	 * @return the JSON of the comment that was just deleted
	 */
	@DeleteMapping
	@ResponseBody
	public Comments deleteByCommentID(@RequestParam int commentID) {
		Comments c = commentRepo.findOneByCommentID(commentID);
		commentRepo.deleteById(commentID);
		return c;
	}

	@Transactional
	public void deleteByUserID(int userID) {
		commentRepo.deleteByUserID(userID);
	}

}
