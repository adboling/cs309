package app;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.imageio.ImageIO;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * The Timeline Controller which controls the the table in the database with
 * timeline objects. A Timeline object is a "journal entry" that users create
 * for what recipe they made and with up to 10 comments.
 * 
 * @author Team 27 (VC_5)
 */
@RestController
@RequestMapping(path = "/timelines")
public class TimelineController {

	@Autowired
	TimelineRepository timeRepo;

	@Autowired
	UserRepository userRepo;

	public TimelineController(TimelineRepository timeRepo, UserRepository userRepo) {
		this.timeRepo = timeRepo;
		this.userRepo = userRepo;
	}

	/**
	 * Welcome message from the TimelineController for the base timelines path.
	 * 
	 * @return Welcome message
	 */
	@GetMapping(path = "/")
	public String welcome() {
		return "Hello, from the TimelineController!";
	}

	/**
	 * Retrieve a list of all timelines of all users
	 * 
	 * @return A list of all timelines of all users
	 */
	@GetMapping("/all")
	@ResponseBody
	public List<Timeline> getAllTimelines() {
		return timeRepo.findAll();
	}

	/**
	 * Retrieves a list of timeline objects in chronological order, representing a
	 * user's timeline.
	 * 
	 * @param userID The id of the user (supplied in path)
	 * @return A list of timeline objects in JSON format
	 */
	@GetMapping("/findTimelineByUserID")
	@ResponseBody
	public List<Timeline> findByUserID(@RequestParam int userID) {
		return timeRepo.findAllByUserID(userID);
	}

	/**
	 * Retrieves a list of timeline objects in chronological order, representing a
	 * user's timeline.
	 * 
	 * @param username The username of the user (supplied in path)
	 * @return A list of timeline objects in JSON format
	 */
	@GetMapping("/findTimelineByUsername")
	@ResponseBody
	public List<Timeline> findByUsername(@RequestParam String username) {
		User foundUser = userRepo.findByUsernameIgnoreCase(username);
		if (foundUser != null) {
			int foundUserID = foundUser.getId();
			return timeRepo.findAllByUserID(foundUserID);
		}
		return new ArrayList<Timeline>();
	}

	// add find by recipe name
	// add find by recipe id

	/**
	 * Stores a timeline object in the database
	 * 
	 * @param timeline The timeline object to be stored (supplied in body in JSON
	 *                 format)
	 * @return The timeline stored with HttpStatus OK.
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@PostMapping("/create")
	public ResponseEntity<String> postTimeline(@RequestBody Timeline timeline)
			throws JsonParseException, JsonMappingException, IOException {

		Calendar cal = Calendar.getInstance();
		timeline.setTime(new Timestamp(cal.getTimeInMillis()));

		if(timeline.getUserID() == 0 && timeline.getUsername() == null) {
			return new ResponseEntity<String>("\"no user provided\"", HttpStatus.BAD_REQUEST);
		}

		/*
		User foundUser = userRepo.findOneById(timeline.getUserID());
		
		if (foundUser == null) {
			return new ResponseEntity<String>("user id not found", HttpStatus.NOT_FOUND);
		}
		timeline.setUsername(foundUser.getUsername());
		*/
		
		User user;
		
		if(timeline.getUsername() == null) {
			user = userRepo.findOneById(timeline.getUserID());
		} else {
			user = userRepo.findByUsernameIgnoreCase(timeline.getUsername());
		}
		
		if (user == null) {
			return new ResponseEntity<String>("\"user not found\"", HttpStatus.BAD_REQUEST);
		}
		
		timeline.setUsername(user.getUsername());
		timeline.setUserID(user.getId());

		// add statement like above to check that recipe is inputed correctly
		// find a way to check against both our own recipes and recipes in yummly api

		// save image//

		if (timeline.getPicture() != null && !timeline.getPicture().isEmpty()) {
			String base64Image = timeline.getPicture();
			byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(imageBytes));
			String filename = timeline.getUsername() + timeline.getTime().hashCode();
			File outputfile = new File("/var/www/html/images/timelineImages/" + filename + ".jpg");
			ImageIO.write(img, "jpg", outputfile);
			timeline.setPicture("http://cs309-vc-5.misc.iastate.edu/images/timelineImages/" + filename + ".jpg");
		}

		try {
			timeRepo.save(timeline);
		} catch (DataIntegrityViolationException ex) {
			return new ResponseEntity<String>(ex.getCause().getCause().getMessage().toString(), HttpStatus.CONFLICT);
		} catch (Exception ex) {
			return new ResponseEntity<String>("unknown error\n" + ex, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>(timeline.toString(), HttpStatus.OK);
	}

	/**
	 * Deletes a timeline object from the database
	 * 
	 * @param timelineID The id of the timeline object to be deleted (supplied in
	 *                   path)
	 * @return The deleted timeline object
	 */
	@DeleteMapping
	@ResponseBody
	@Transactional
	public Timeline deleteByTimelineID(@RequestParam int timelineID) {
		Timeline t = timeRepo.findByTimelineID(timelineID);
		timeRepo.deleteByTimelineID(timelineID);
		return t;
	}

	/**
	 * Deletes all timeline objects associated with a certain user id
	 * 
	 * @param userID The id of the user (supplied in path)
	 * @return A list of all timeline objects which were deleted in JSON format
	 */
	@DeleteMapping("/user")
	@ResponseBody
	@Transactional
	public List<Timeline> deleteByUserID(@RequestParam int userID) {
		List<Timeline> t = timeRepo.findAllByUserID(userID);
		timeRepo.deleteByUserID(userID);
		return t;
	}

}
