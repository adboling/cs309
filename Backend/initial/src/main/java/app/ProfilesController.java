package app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Profile Controller which collects information from the user repository,
 * friend repository, and timeline repository to form a profile for a user.
 * @author Team 27 (VC_5)
 */
@RestController
@RequestMapping(path = "/profile")
public class ProfilesController {

	public ProfilesController(UserRepository ur, FriendRepository fr, TimelineRepository tr) {
		this.userRepo = ur;
		this.friendRepo = fr;
		this.timeRepo = tr;
	}
	
	@Autowired
	UserRepository userRepo;

	@Autowired
	FriendRepository friendRepo;

	@Autowired
	TimelineRepository timeRepo;

	/**
	 * Welcome message from the TimelineController for the base profile path.
	 * @return Welcome message
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "\"Hello, from the ProfileController!\"";
	}

	/**
	 * Gets the profile of the specified user. The id and/or username may be supplied.
	 * @param id
	 * The id of the user
	 * @param userName
	 * The username of the user
	 * @return A JSON object which contains the user's id, username, email, first name,
	 * role, bio, friends list, and timeline.
	 */
	@GetMapping()
	// @ResponseBody
	public ResponseEntity<String> getProfile(@RequestParam(required = false) Integer id,
			@RequestParam(required = false) String userName) {

		if (id == null && userName == null) {
			return new ResponseEntity<String>("\"no input provided\"", HttpStatus.BAD_REQUEST);
		}

		User user;

		if (userName != null) {
			user = new UserController(userRepo).findUser(userName);
		} else {
			UserController uc = new UserController(userRepo);
			user = uc.findUser(id);
		}

		if (user == null) {
			return new ResponseEntity<String>("\"user not found\"", HttpStatus.BAD_REQUEST);
		}

		class Result {
			public int id;
			public String username;
			public String email;
			public String firstname;
			public String role;
			public String bio;
			public String avatar;
			public String background;
			public List<User> friendsList;
			public List<Timeline> timeline;

			public Result(int userID, String username, String email, String firstname, String role, String bio,
					String avatar, String background, List<User> friendsList, List<Timeline> timeline) {
				super();
				this.id = userID;
				this.username = username;
				this.email = email;
				this.firstname = firstname;
				this.role = role;
				this.bio = bio;
				this.avatar = avatar;
				this.background = background;
				this.friendsList = friendsList;
				this.timeline = timeline;
			}

		}

		Result returnObj = new Result(user.getId(), user.getUsername(), user.getEmail(), user.getFirstname(),
				user.getRole(), user.getBio(), user.getAvatar(), user.getBackground(),
				new FriendController(friendRepo, userRepo).getFriendsByUserID(user.getId()),
				new TimelineController(timeRepo, userRepo).findByUserID(user.getId()));
		
		if(returnObj.avatar == null || returnObj.avatar.compareTo("") == 0) {
			returnObj.avatar = "http://cs309-vc-5.misc.iastate.edu/images/defaultAvatar.jpg";
		}

		return new ResponseEntity<String>(Application.JSONify(returnObj), HttpStatus.OK);

	}

}
