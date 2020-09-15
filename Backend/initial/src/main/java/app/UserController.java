package app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The User Controller which controls the table in the database with user
 * objects. A user objects holds a user's information.
 * 
 * @author Team 27 (VC_5)
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

	public UserController(UserRepository userRepository2) {
		this.userRepo = userRepository2;
	}

	@Autowired
	UserRepository userRepo;

	@Autowired
	CommentRepo commentRepo;

	@Autowired
	FriendRepository friendRepo;

	@Autowired
	RecipeRepo recipeRepo;

	@Autowired
	IngredientsRepo ingredientRepo;

	@Autowired
	TimelineRepository timeRepo;

	@Autowired
	PantryRepo pantryRepo;

	/**
	 * Welcome message from the UserController for the base users path.
	 * 
	 * @return Welcome message
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "\"Hello, from the UserController!\"";
	}

	/**
	 * Retrieve a list of all users
	 * 
	 * @return List of users as user objects in JSON format
	 */
	@GetMapping(path = "/all")
	@ResponseBody
	public Iterable<User> getAllUsers() {
		return userRepo.findAll();
	}

	/**
	 * Finds the user with the supplied id (id supplied in path)
	 * 
	 * @param id id of user to be found, supplied in path
	 * @return User with the supplied id as user object in JSON format
	 */
	@GetMapping(path = "/findbyid")
	@ResponseBody
	public User findUser(@RequestParam Integer id) {
		User u = userRepo.findOneById(id);
		return u;
	}

	/**
	 * Finds the user with the supplied username (username supplied in path)
	 * 
	 * @param username username of the user to be found
	 * @return User with the supplied username as user object in JSON format
	 */
	@GetMapping(path = "/findbyusername")
	@ResponseBody
	public User findUser(@RequestParam String username) {
		User user = userRepo.findByUsernameIgnoreCase(username);

		// TODO
		// if you want to return a null user then there should be a null user in the...
		// user.java file
//		if (user == null) {
//			User notFound = new User("null", "null", "null", "null", "null", "null");
//			notFound.setId(-1);
//			return notFound;
//		}

		return user;
	}

	/**
	 * Logs in the user with the supplied credentials if the credentials match to an
	 * existing user (email and password supplied in path)
	 * 
	 * @param email    email of the user logging in
	 * @param password password of the user logging in
	 * @return The id of the user logging in
	 */
	@GetMapping(path = "/login")
	public ResponseEntity<Integer> login(@RequestParam String email, @RequestParam String password) {

		// get user
		User user = userRepo.findByEmailIgnoreCase(email);

		// check if user exists
		if (user == null) {
			return new ResponseEntity<Integer>(-1, HttpStatus.BAD_REQUEST);

		}
		// check if password is correct
		if (user.getPassword().compareTo(password) != 0) {
			return new ResponseEntity<Integer>(-1, HttpStatus.UNAUTHORIZED);
		}

		return new ResponseEntity<Integer>(user.getId(), HttpStatus.OK);
	}

	/**
	 * Adds a new user (user object supplied in body in JSON format)
	 * 
	 * @param user the user to be created
	 * @return If successful, returns the new user in JSON format with HttpStatus
	 *         OK. Else if the new user conflicts with an existing user, returns a
	 *         DataIntegrityViolationException and HttpStatus CONFLICT. If another
	 *         exception occured, returns "unknown error" and HttpStatus
	 *         INTERNAL_SERVER_ERROR.
	 */
	@PostMapping
	public ResponseEntity<String> addNewUser(@RequestBody User user) {
		try {
			user.setId(null);
			userRepo.save(user);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(user.toString(), HttpStatus.OK);
	}

	/**
	 * Get a list of all users with a specific role (role supplied in path)
	 * 
	 * @param role The role to filter users by
	 * @return The list of all users with the specified role.
	 */
	@GetMapping(path = "/find-by-role/{role}")
	@ResponseBody
	public List<User> findByRole(@PathVariable(value = "role") String role) {
		return userRepo.findAllByRole(role);
	}

	/**
	 * Modifies the bio of the specified user by replacing the old bio with the
	 * supplied new bio. Must supply the user's id and/or username.
	 * 
	 * @param id       The id of the user (id supplied in path).
	 * @param username The username of the user (username supplied in path).
	 * @param message   The new bio of the supplied user (bio supplied in body as
	 *                 plain text).
	 * @return If id is null and username is null, return "no user provided" with
	 *         HttpStatus BAD_REQUEST. If the user is not found, returns "user not
	 *         found" with HttpStatus BAD_REQUEST. If the user is found, the user's
	 *         bio is replaced with the new bio and returns "Bio modified
	 *         successfully" with HttpStatus OK. If the user is found and the bio is
	 *         null or empty, returns "Bio cleared successfully" with HttpStatus OK.
	 */
	@PutMapping(path = "/modifyBio")
	public ResponseEntity<String> modifyBio(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String username, @RequestBody(required = false) Message message) {

		if (id == null && username == null) {
			return new ResponseEntity<String>("\"no user provided\"", HttpStatus.BAD_REQUEST);
		}

		User user;

		if (username != null) {
			user = new UserController(userRepo).findUser(username);
		} else {
			UserController uc = new UserController(userRepo);
			user = uc.findUser(id);
		}

		if (user == null) {
			return new ResponseEntity<String>("\"user not found\"", HttpStatus.BAD_REQUEST);
		}

		String newBio = message.getMessage();
		String result = "Bio modified successfully";

		if (newBio == null || newBio.compareTo("") == 0) {
			newBio = "";
			result = "Bio cleared successfully";
		}

		user.setBio(newBio);
		userRepo.save(user);

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	/**
	 * Replaces the avatar of the specified user with the supplied image (jpg format).
	 * May supply the id and/or username of the user
	 * @param message
	 * The image in base64 encoding
	 * @param id
	 * The id of the user
	 * @param username
	 * The username of the user
	 * @return A string with a message and an http status relevant to how the call went.
	 * @throws IOException
	 */
	@PutMapping("/modifyAvatar")
	public ResponseEntity<String> modifyAvatar(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String username, @RequestBody(required = false) Message message)
			throws IOException {

		if (id == null && username == null) {
			return new ResponseEntity<String>("\"no user provided\"", HttpStatus.BAD_REQUEST);
		}

		User user;

		if (username != null) {
			user = new UserController(userRepo).findUser(username);
		} else {
			UserController uc = new UserController(userRepo);
			user = uc.findUser(id);
		}

		if (user == null) {
			return new ResponseEntity<String>("\"user not found\"", HttpStatus.BAD_REQUEST);
		}

		String newAvatar = message.getMessage();
		String result = "Avatar modified successfully";

		if (newAvatar == null || newAvatar.compareTo("") == 0) {
			newAvatar = "";
			result = "Avatar cleared successfully";
		}

		String avatarPath = "http://cs309-vc-5.misc.iastate.edu/images/defaultAvatar.jpg";

		if (newAvatar != null && !newAvatar.isEmpty()) {
			String base64Image = newAvatar;
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			String filename = user.getUsername();
			File outputfile = new File("/var/www/html/images/avatars/" + filename + ".jpg");
			ImageIO.write(img, "jpg", outputfile);
			avatarPath = "http://cs309-vc-5.misc.iastate.edu/images/avatars/" + filename + ".jpg";
		}

		user.setAvatar(avatarPath);
		userRepo.save(user);

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}

	/**
	 * Replaces the background of the specified user with the supplied image (jpg format).
	 * May supply the id and/or username of the user
	 * @param id
	 * The id of the user
	 * @param username
	 * The username of the user
	 * @param message
	 * The image in base64 encoding
	 * @return A string with a message and an http status relevant to how the call went.
	 * @throws IOException
	 */
	@PutMapping(path = "/modifyBackground")
	public ResponseEntity<String> modifyBackground(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String username, @RequestBody(required = false) Message message)
			throws IOException {

		if (id == null && username == null) {
			return new ResponseEntity<String>("\"no user provided\"", HttpStatus.BAD_REQUEST);
		}

		User user;

		if (username != null) {
			user = new UserController(userRepo).findUser(username);
		} else {
			UserController uc = new UserController(userRepo);
			user = uc.findUser(id);
		}

		if (user == null) {
			return new ResponseEntity<String>("\"user not found\"", HttpStatus.BAD_REQUEST);
		}

		String newBackground = message.getMessage();
		String result = "Background modified successfully";

		if (newBackground == null || newBackground.compareTo("") == 0) {
			newBackground = "";
			result = "Background cleared successfully";
		}

		String backgroundPath = "";

		if (newBackground != null && !newBackground.isEmpty()) {
			String base64Image = newBackground;
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			String filename = user.getUsername();
			File outputfile = new File("/var/www/html/images/backgroundImages/" + filename + ".jpg");
			ImageIO.write(img, "jpg", outputfile);
			backgroundPath = "http://cs309-vc-5.misc.iastate.edu/images/backgroundImages/" + filename + ".jpg";
		}

		user.setBackground(backgroundPath);
		userRepo.save(user);

		return new ResponseEntity<String>(result, HttpStatus.OK);
	}
	
	/**
	 * Changes the role of the specified user to the specified role. Valid roles are admin,
	 * curator, and user.
	 * @param username
	 * Specifies the user to have their role changed
	 * @param role
	 * The role to change to
	 * @return If no valid user type entered, responds with "Not a valid user type" with
	 * HttpStatus BAD_REQUEST.
	 * If the user entered is not found or does not exist, responds with "User was not
	 * found or does not exist" with HttpStatus BAD_REQUEST.
	 * If both fields are valid responds with "Role modified successfully" with HttpStatus
	 * OK.
	 */
	@PutMapping("/modifyRole")
	public ResponseEntity<String> modifyRole(@RequestParam String username, @RequestParam String role) {
		
		if(!role.equals("admin") && !role.equals("curator") && !role.equals("user")) {
			return new ResponseEntity<String>("\"Not a valid user type\"", HttpStatus.BAD_REQUEST);
		}
		
		User user = userRepo.findByUsernameIgnoreCase(username);
		
		if(user == null) {
			return new ResponseEntity<String>("\"User was not found or does not exist\"", HttpStatus.BAD_REQUEST);
		}
		
		user.setRole(role);
		userRepo.save(user);
		
		return new ResponseEntity<String>("\"Role modified successfully\"", HttpStatus.OK);
	}
	

	/**
	 * Deletes a user
	 * 
	 * @param userID The id of the user to be deleted
	 * @return If the user does not exist, throws DataIntegrityViolationException.
	 *         If another exception occurred, returns "unknown error" and the
	 *         message of the exception with HttpStatus INTERNAL_SERVER_ERROR. If
	 *         the user was not found, returns "user not found" with HttpStatus
	 *         BAD_REQUEST. Otherwise, the user is deleted and returns "deleted"
	 *         with HttpStatus OK.
	 */
	@DeleteMapping
	@Transactional
	public ResponseEntity<String> DeleteUser(@RequestParam int userID) {

		userRepo.deleteById(userID);
		new CommentController(commentRepo).deleteByUserID(userID);
		new FriendController(friendRepo, userRepo).DeleteAllFriendsByUserID(userID);
		new RecipeController(recipeRepo, ingredientRepo).DelRecipeByUser(userID);
		new TimelineController(timeRepo, userRepo).deleteByUserID(userID);
		new PantryController(pantryRepo).deleteAllByUser(userID);

		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);
	}

	/**
	 * used to search for a user by there username
	 * 
	 * @param username the keyword for their user name
	 * @return a list of users with matching usernames
	 */
	@GetMapping(path = "/search")
	@ResponseBody
	public List<User> UserSearch(@RequestParam String username) {
		return userRepo.findAllByUsernameContainingIgnoreCase(username);
	}

	/**
	 * used to get all the info on a user
	 * 
	 * @param userID
	 * @return IDK
	 */
	@GetMapping(path = "/allInfo")
	public ResponseEntity<String> GetAllInfo(@RequestParam int userID) {

		ArrayList<Object> objs = new ArrayList<Object>();

		objs.add(findUser(userID));
		objs.add(new CommentController(commentRepo).findByUser(userID));
		objs.add(new FriendController(friendRepo, userRepo).getFriendsByUserID(userID));
		objs.add(new RecipeController(recipeRepo, ingredientRepo).findByUser(userID));
		objs.add(new TimelineController(timeRepo, userRepo).findByUserID(userID));
		objs.add(new PantryController(pantryRepo).findPantryByUserID(userID));

		return new ResponseEntity<String>(Application.JSONify(objs), HttpStatus.OK);
	}
}
