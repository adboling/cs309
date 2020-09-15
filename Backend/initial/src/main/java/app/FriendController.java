package app;

import java.util.ArrayList;
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
 * The Friend Controller which controls the table in the database with friend
 * "relationship" objects. A Friend objects contains the sender's id and
 * username, the recipient's id and username, and the status of the friendship.
 * (accepted, pending, declined)
 * 
 * @author Team 27 (VC_5)
 */
@RestController
@RequestMapping(path = "/friends")
public class FriendController {
	
	public FriendController(FriendRepository fr, UserRepository ur) {
		this.friendRepo = fr;
		this.userRepo = ur;
	}

	@Autowired
	FriendRepository friendRepo;

	@Autowired
	UserRepository userRepo;

	@Autowired
	RecipeRepo recipeRepo;

	@Autowired
	IngredientsRepo ingredientRepo;

	/**
	 * Welcome message from the FriendController for the base friends path.
	 * 
	 * @return Welcome message
	 */
	@GetMapping("/")
	public String welcome() {
		return "Hello, from the FriendController!";
	}

	/**
	 * Gets a list of all friendships in the database
	 * 
	 * @return A list of all friendships in the database
	 */
	@GetMapping("/allFriendships")
	@ResponseBody
	public List<Friend> getAllFriendships() {
		return friendRepo.findAll();
	}

	/**
	 * Send a friend request to a certain user. Adds a new friend object to the
	 * database with the status "pending".
	 * 
	 * @param friend The friendship trying to be made. Must contain the sender's id
	 *               and/or username, and the recipients's id and/or username.
	 * @return If no sender information supplied, return "no sender supplied" and
	 *         HttpStatus BAD_REQUEST. If no recipient information supplied, return
	 *         "no recipient supplied" and HttpStatus BAD_REQUEST. If the sender
	 *         does not exist returns "sender does not exist" and HttpStatus
	 *         NOT_FOUND. If the recipient does not exist returns "recipient does
	 *         not exist" and HttpStatus NOT_FOUND. If successful, returns the new
	 *         completed friendship object with HttpStatus OK.
	 * @throws DataIntegrityViolationException
	 * @throws Exception
	 */
	@PostMapping("/sendRequest")
	@ResponseBody
	public ResponseEntity<String> sendFriendRequest(@RequestBody Friend friend) {

		int sid = friend.getSenderID();
		int rid = friend.getRecipientID();
		String su = friend.getSenderUsername();
		String ru = friend.getRecipientUsername();

		if(sid == 0 && su == null) {
			return new ResponseEntity<String>("\"no sender supplied\"", HttpStatus.BAD_REQUEST);
		}
		if(rid == 0 && ru == null) {
			return new ResponseEntity<String>("\"no recipient supplied\"", HttpStatus.BAD_REQUEST);
		}
		
		if(su != null) {
			sid = userRepo.findByUsernameIgnoreCase(su).getId();
		} else {
			su = userRepo.findOneById(sid).getUsername();
		}
		
		if(ru != null) {
			rid = userRepo.findByUsernameIgnoreCase(ru).getId();
		} else {
			ru = userRepo.findOneById(rid).getUsername();
		}
		
		if(sid == 0 || su == null) {
			return new ResponseEntity<String>("\"sender does not exist\"", HttpStatus.NOT_FOUND);
		}
		if(rid == 0 || ru == null) {
			return new ResponseEntity<String>("\"recipient does not exist\"", HttpStatus.NOT_FOUND);
		}

		List<Friend> checkForSpam = friendRepo.findAllBySenderIDAndRecipientID(sid, rid);
		List<Friend> checkForSpam2 = friendRepo.findAllBySenderIDAndRecipientID(rid, sid); //reverse friendship prevention
		if (!checkForSpam.isEmpty() || !checkForSpam2.isEmpty()) {
			return new ResponseEntity<String>("a request has already been sent to or recieved from this user",
					HttpStatus.BAD_REQUEST);
		}

		friend.setStatus("pending");

		try {
			friendRepo.save(friend);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<String>(friend.toString(), HttpStatus.OK);
	}

	/**
	 * Gets a list of all Users the specified user is friends with (status =
	 * accepted)
	 * 
	 * @param userID The user id of the user to get friends of
	 * @return A list of users as user objects the specified user is friends with
	 *         (status = accepted) in JSON format. The returned user objects in the
	 *         list have their password and email removed.
	 */
	@GetMapping("/getFriendsByUserID")
	@ResponseBody
	public List<User> getFriendsByUserID(@RequestParam int userID) {
		if (userRepo.findOneById(userID) == null) {
			return new ArrayList<User>();
		}

		List<Friend> friendList_1 = friendRepo.findAllBySenderIDAndStatus(userID, "accepted");
		List<Friend> friendList_2 = friendRepo.findAllByRecipientIDAndStatus(userID, "accepted");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		int otherUserID;

		for (Friend f : friendList_1) {
			otherUserID = f.getRecipientID();
			otherUser = userRepo.findOneById(otherUserID);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}
		for (Friend f : friendList_2) {
			otherUserID = f.getSenderID();
			otherUser = userRepo.findOneById(otherUserID);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Gets a list of all Users the specified user is friends with (status =
	 * accepted)
	 * 
	 * @param username The username of the user to get friends of
	 * @return A list of users as user objects the specified user is friends with
	 *         (status = accepted) in JSON format. The returned user objects in the
	 *         list have their password and email removed.
	 */
	@GetMapping("/getFriendsByUsername")
	@ResponseBody
	public List<User> getFriendsByUsername(@RequestParam String username) {
		if (userRepo.findByUsernameIgnoreCase(username) == null) {
			return new ArrayList<User>();
		}

		List<Friend> friendList_1 = friendRepo.findAllBySenderUsernameAndStatus(username, "accepted");
		List<Friend> friendList_2 = friendRepo.findAllByRecipientUsernameAndStatus(username, "accepted");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		String otherUserUsername;

		for (Friend f : friendList_1) {
			otherUserUsername = f.getRecipientUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}
		for (Friend f : friendList_2) {
			otherUserUsername = f.getSenderUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Gets a list of all Users the specified user has send a friend request to and
	 * the request is still pending.
	 * 
	 * @param userID The user id of the user who has sent requests
	 * @return A list of users as user objects the specified user has send a friend
	 *         request to (status = pending) in JSON format. The returned user
	 *         objects in the list have their password and email removed.
	 */
	@GetMapping("/getOutgoingByUserID")
	@ResponseBody
	public List<User> getOutgoingByUserID(@RequestParam int userID) {
		if (userRepo.findOneById(userID) == null) {
			return new ArrayList<User>();
		}

		List<Friend> outgoingFriendList = friendRepo.findAllBySenderIDAndStatus(userID, "pending");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		int otherUserID;

		for (Friend f : outgoingFriendList) {
			otherUserID = f.getRecipientID();
			otherUser = userRepo.findOneById(otherUserID);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Gets a list of all Users the specified user has send a friend request to and
	 * the request is still pending.
	 * 
	 * @param username The username of the user who has sent requests
	 * @return A list of users as user objects the specified user has send a friend
	 *         request to (status = pending) in JSON format. The returned user
	 *         objects in the list have their password and email removed.
	 */
	@GetMapping("/getOutgoingByUsername")
	@ResponseBody
	public List<User> getOutgoingByUsername(@RequestParam String username) {
		if (userRepo.findByUsernameIgnoreCase(username) == null) {
			return new ArrayList<User>();
		}

		List<Friend> outgoingFriendList = friendRepo.findAllBySenderUsernameAndStatus(username, "pending");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		String otherUserUsername;

		for (Friend f : outgoingFriendList) {
			otherUserUsername = f.getRecipientUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Gets a list of all Users who have sent a request to the specified user and
	 * the request is still pending.
	 * 
	 * @param userID The user id of the user who has recieved requests
	 * @return A list of users as user objects the specified user has recieved a
	 *         friend request from (status = pending) in JSON format. The returned
	 *         user objects in the list have their password and email removed.
	 */
	@GetMapping("/getIncomingByUserID")
	@ResponseBody
	public List<User> getIncomingByUserID(@RequestParam int userID) {
		if (userRepo.findOneById(userID) == null) {
			return new ArrayList<User>();
		}

		List<Friend> incomingFriendList = friendRepo.findAllByRecipientIDAndStatus(userID, "pending");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		int otherUserID;

		for (Friend f : incomingFriendList) {
			otherUserID = f.getSenderID();
			otherUser = userRepo.findOneById(otherUserID);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Gets a list of all Users who have sent a request to the specified user and
	 * the request is still pending.
	 * 
	 * @param username The username of the user who has recieved requests
	 * @return A list of users as user objects the specified user has recieved a
	 *         friend request from (status = pending) in JSON format. The returned
	 *         user objects in the list have their password and email removed.
	 */
	@GetMapping("/getIncomingByUsername")
	@ResponseBody
	public List<User> getIncomingByUsername(@RequestParam String username) {
		if (userRepo.findByUsernameIgnoreCase(username) == null) {
			return new ArrayList<User>();
		}

		List<Friend> incomingFriendList = friendRepo.findAllByRecipientUsernameAndStatus(username, "pending");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		String otherUserUsername;

		for (Friend f : incomingFriendList) {
			otherUserUsername = f.getSenderUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}
	
	/**
	 * Gets the list of people which the specified user has blocked. You may supply
	 * the user's id and/or username.
	 * @param userID
	 * The id of the user
	 * @param username
	 * The username of the user
	 * @return A list which contains all users which the specified user has blocked. In
	 * other words, all relationships which have a status of "declined".
	 */
	@GetMapping("/getBlockedUsers")
	@ResponseBody
	public List<User> viewBlockedUsers(@RequestParam(required = false) Integer userID,
			@RequestParam(required = false) String username) {
		
		if(userID == null && username == null) {
			return new ArrayList<User>();
		}

		User user;

		if (username != null) {
			user = userRepo.findByUsernameIgnoreCase(username);
		} else {
			user = userRepo.findOneById(userID);
		}

		if (user == null) {
			return new ArrayList<User>();
		}
		
		//List<Friend> blockedList_1 = friendRepo.findAllBySenderUsernameAndStatus(user.getUsername(), "declined");
		List<Friend> blockedList_2 = friendRepo.findAllByRecipientUsernameAndStatus(user.getUsername(), "declined");

		ArrayList<User> userList = new ArrayList<>();
		User otherUser;
		String otherUserUsername;

		/*
		for (Friend f : blockedList_1) {
			otherUserUsername = f.getRecipientUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}
		*/
		for (Friend f : blockedList_2) {
			otherUserUsername = f.getSenderUsername();
			otherUser = userRepo.findByUsernameIgnoreCase(otherUserUsername);
			otherUser.setPassword("");
			otherUser.setEmail("");
			userList.add(otherUser);
		}

		return userList;
	}

	/**
	 * Accept a friend request from a certain user. Modifies the friend object in
	 * the database by changing the status to "accepted".
	 * 
	 * @param friend The friendship being accepted. Must contain the sender's id
	 *               and/or username, and the recipients's id and/or username (and
	 *               identical to the object stored in the database. Ex: user "a"
	 *               sends a request to user "b". In both the act of sending and
	 *               accepting, the sender must be "a" and the recipient must be
	 *               "b").
	 * @return If no sender information supplied, return "no sender supplied" and
	 *         HttpStatus BAD_REQUEST. If no recipient information supplied, return
	 *         "no recipient supplied" and HttpStatus BAD_REQUEST. If the sender
	 *         does not exist returns "sender does not exist" and HttpStatus
	 *         NOT_FOUND. If the recipient does not exist returns "recipient does
	 *         not exist" and HttpStatus NOT_FOUND. If successful, returns the
	 *         modified friendship object with HttpStatus OK.
	 * @throws DataIntegrityViolationException
	 * @throws Exception
	 */
	@PostMapping("/acceptRequest")
	@ResponseBody
	@Transactional
	public ResponseEntity<String> acceptFriendRequest(@RequestBody Friend friend) {

		Integer sid = friend.getSenderID();
		Integer rid = friend.getRecipientID();
		String su = friend.getSenderUsername();
		String ru = friend.getRecipientUsername();

		if(sid == 0 && su == null) {
			return new ResponseEntity<String>("\"no sender supplied\"", HttpStatus.BAD_REQUEST);
		}
		if(rid == 0 && ru == null) {
			return new ResponseEntity<String>("\"no recipient supplied\"", HttpStatus.BAD_REQUEST);
		}
		
		if(su != null) {
			sid = userRepo.findByUsernameIgnoreCase(su).getId();
		} else {
			su = userRepo.findOneById(sid).getUsername();
		}
		
		if(ru != null) {
			rid = userRepo.findByUsernameIgnoreCase(ru).getId();
		} else {
			ru = userRepo.findOneById(rid).getUsername();
		}
		
		if(sid == 0 || su == null) {
			return new ResponseEntity<String>("\"sender does not exist\"", HttpStatus.NOT_FOUND);
		}
		if(rid == 0 || ru == null) {
			return new ResponseEntity<String>("\"recipient does not exist\"", HttpStatus.NOT_FOUND);
		}

		//Friend oldFriendship = friendRepo.findBySenderIDAndRecipientID(sid, rid);
		//friend.setStatus("accepted");
		Friend updatedFriendship = friendRepo.findBySenderIDAndRecipientID(sid, rid);
		updatedFriendship.setStatus("accepted");

		try {
			friendRepo.save(updatedFriendship);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		//friendRepo.deleteByFriendshipID(oldFriendship.getFriendshipID());
		return new ResponseEntity<String>(updatedFriendship.toString(), HttpStatus.OK);
	}

	/**
	 * Decline a friend request from a certain user. Modifies the friend object in
	 * the database by changing the status to "declined".
	 * 
	 * @param friend The friendship being declined. Must contain the sender's id
	 *               and/or username, and the recipients's id and/or username (and
	 *               identical to the object stored in the database. Ex: user "a"
	 *               sends a request to user "b". In both the act of sending and
	 *               declining, the sender must be "a" and the recipient must be
	 *               "b").
	 * @return If no sender information supplied, return "no sender supplied" and
	 *         HttpStatus BAD_REQUEST. If no recipient information supplied, return
	 *         "no recipient supplied" and HttpStatus BAD_REQUEST. If the sender
	 *         does not exist returns "sender does not exist" and HttpStatus
	 *         NOT_FOUND. If the recipient does not exist returns "recipient does
	 *         not exist" and HttpStatus NOT_FOUND. If successful, returns the
	 *         modified friendship object with HttpStatus OK.
	 * @throws DataIntegrityViolationException
	 * @throws Exception
	 */
	@PostMapping("/declineRequest")
	@ResponseBody
	@Transactional
	public ResponseEntity<String> declineFriendRequest(@RequestBody Friend friend) {

		Integer sid = friend.getSenderID();
		Integer rid = friend.getRecipientID();
		String su = friend.getSenderUsername();
		String ru = friend.getRecipientUsername();

		if(sid == 0 && su == null) {
			return new ResponseEntity<String>("\"no sender supplied\"", HttpStatus.BAD_REQUEST);
		}
		if(rid == 0 && ru == null) {
			return new ResponseEntity<String>("\"no recipient supplied\"", HttpStatus.BAD_REQUEST);
		}
				
		if(su != null) {
			sid = userRepo.findByUsernameIgnoreCase(su).getId();
		} else {
			su = userRepo.findOneById(sid).getUsername();
		}
		
		if(ru != null) {
			rid = userRepo.findByUsernameIgnoreCase(ru).getId();
		} else {
			ru = userRepo.findOneById(rid).getUsername();
		}
		
		if(sid == 0 || su == null) {
			return new ResponseEntity<String>("\"sender does not exist\"", HttpStatus.NOT_FOUND);
		}
		if(rid == 0 || ru == null) {
			return new ResponseEntity<String>("\"recipient does not exist\"", HttpStatus.NOT_FOUND);
		}

		//Friend oldFriendship = friendRepo.findBySenderIDAndRecipientID(sid, rid);
		//friend.setStatus("declined");
		
		Friend updatedFriendship = friendRepo.findBySenderIDAndRecipientID(sid, rid);
		updatedFriendship.setStatus("declined");
		
		//Friend toDelete = friendRepo.findBySenderIDAndRecipientID(sid, rid);
		//friendRepo.deleteByFriendshipID(toDelete.getFriendshipID());

		
		try {
			friendRepo.save(updatedFriendship);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		//friendRepo.deleteByFriendshipID(oldFriendship.getFriendshipID());
		return new ResponseEntity<String>(updatedFriendship.toString(), HttpStatus.OK);
		//return new ResponseEntity<String>("\"Successfully declined/deleted friendship\"", HttpStatus.OK);
	}

	/**
	 * Deletes one specified friend of a specified user
	 * @param userID
	 * The user id of the user to have a friend deleted
	 * @param friendID
	 * The user id of the friend to be deleted
	 * @return ResponseEntity with message "deleted" and HttpStatus OK
	 */
	@DeleteMapping("/deleteFriend")
	@Transactional
	public ResponseEntity<String> DeleteFriendByUserId(@RequestParam int userID, @RequestParam int friendID) {
		Friend toDelete = friendRepo.findBySenderIDAndRecipientID(userID, friendID);
		if(toDelete == null) {
			toDelete = friendRepo.findBySenderIDAndRecipientID(friendID, userID);
		}
		if(toDelete == null) {
			return new ResponseEntity<String>("\"The friendship was not found or does not exist\"", HttpStatus.BAD_REQUEST);
		}
		friendRepo.deleteByFriendshipID(toDelete.getFriendshipID());
		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);
	}
	
	/**
	 * Deletes all friends of a specified user
	 * @param userID
	 * The user to have all friends deleted
	 * @return ResponseEntity with message "deleted" and HttpStatus OK
	 */
	@DeleteMapping("/delAllFriends")
	@Transactional
	public ResponseEntity<String> DeleteAllFriendsByUserID(@RequestParam int userID) {
		friendRepo.deleteAllBySenderIDOrRecipientID(userID, userID);
		return new ResponseEntity<String>("\"deleted\"", HttpStatus.OK);
	}

	/**
	 * Get the recipies of the specified user
	 * @param userID
	 * The user to get recipes from
	 * @return A List of recipes in JSON format
	 */
	@GetMapping("/recipes")
	@ResponseBody
	public List<Recipe> getFriendsRecipes(@RequestParam int userID) {
		List<User> friends = getFriendsByUserID(userID);
		List<Recipe> result = new ArrayList<Recipe>();

		for (User f : friends) {
			result.addAll(new RecipeController(recipeRepo, ingredientRepo).findByUser(f.getId()));
		}

		return result;
	}

	// Todo list

	// add get all friendships DONE
	// add send a friend request DONE
	// add get all of a user's friends DONE
	// add get all of a user's outgoing requests DONE
	// add get all of a user's incoming requests DONE
	// add accept request DONE
	// add decline request DONE
	// add request spam prevention DONE
	// change spam prevention? 1 decline = never friends (can't send another request)
	// prevent users from sending requests to self
	// fix the error checks where it checks ints against null DONE
	// add ability to remove friend DONE

	// do something like if friend.getid is null then set up id by username and vice
	// versa
//	@PostMapping("/add")
//	public ResponseEntity<String> postTimeline(@RequestBody Friend friend) {
//		User foundUser = userRepo.findOneById(timeline.getUserID());
//		if(foundUser == null) {
//			return new ResponseEntity<String>("user id not found", HttpStatus.NOT_FOUND);
//		}
//		timeline.setUsername(foundUser.getUsername());
//			
//		try {
//			timeRepo.save(timeline);
//		} catch (DataIntegrityViolationException ex) {
//			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
//		} catch (Exception ex) {
//			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		return new ResponseEntity<String>(timeline.toString(), HttpStatus.OK);
//	}
	
	//Old system if needed for some reason:
	/*
	if (sid == 0) {
		return new ResponseEntity<String>("no sender id supplied", HttpStatus.NOT_FOUND);
	}
	if (su == null) {
		return new ResponseEntity<String>("no sender username supplied", HttpStatus.NOT_FOUND);
	}
	if (rid == 0) {
		return new ResponseEntity<String>("no recipient id supplied", HttpStatus.NOT_FOUND);
	}
	if (ru == null) {
		return new ResponseEntity<String>("no recipient username supplied", HttpStatus.NOT_FOUND);
	}

	User foundUser;

	foundUser = userRepo.findOneById(sid);
	if (foundUser == null) {
		return new ResponseEntity<String>("sender id does not exist", HttpStatus.NOT_FOUND);
	}
	foundUser = userRepo.findByUsernameIgnoreCase(su);
	if (foundUser == null) {
		return new ResponseEntity<String>("sender username does not exist", HttpStatus.NOT_FOUND);
	}
	foundUser = userRepo.findOneById(rid);
	if (foundUser == null) {
		return new ResponseEntity<String>("recipient id does not exist", HttpStatus.NOT_FOUND);
	}
	foundUser = userRepo.findByUsernameIgnoreCase(ru);
	if (foundUser == null) {
		return new ResponseEntity<String>("recipient username does not exist", HttpStatus.NOT_FOUND);
	}

	foundUser = userRepo.findOneById(sid);
	if (foundUser.getUsername().compareToIgnoreCase(su) != 0) {
		return new ResponseEntity<String>("sender id and username not associated", HttpStatus.NOT_FOUND);
	}
	foundUser = userRepo.findOneById(rid);
	if (foundUser.getUsername().compareToIgnoreCase(ru) != 0) {
		return new ResponseEntity<String>("recipient id and username not associated", HttpStatus.NOT_FOUND);
	}
	*/

}
