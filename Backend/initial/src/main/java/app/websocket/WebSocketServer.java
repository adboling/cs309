package app.websocket;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 
 * websocket API
 *
 */
@ServerEndpoint("/websocket/{username}/{gameID}")
@Component
public class WebSocketServer {

	// Store all socket session and their corresponding username.
	private static Map<Session, String> sessionUsernameIDMap = new HashMap<>();
	private static Map<String, Session> usernameIDSessionMap = new HashMap<>();

	private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

	/**
	 * 
	 * When opening.joing a game will return info on the player and the game when
	 * just sending data will broadcast to everyone in the game
	 * 
	 * @param username pass in the username of the player
	 * @param gameID   pass in the name of the current game
	 * 
	 */
	@OnOpen
	public void onOpen(Session session, @PathParam("username") String username, @PathParam("gameID") String gameID)
			throws IOException {
		logger.info("Entered into Open");
		logger.info("Game ID: " + gameID);

		// checks if user has already joined that game
		if (usernameIDSessionMap.containsKey(username + "+" + gameID)) {
			String message = "{\"type\":\"FAIL\",\"player\":\"" + username + "\"}";
			broadcast(message, gameID);
			return;
		}

		boolean newGame = true;

		// checks if any other game already has that ID
		for (String sessionID : sessionUsernameIDMap.values()) {
			if (sessionID.contains(gameID)) {
				newGame = false;
				break;
			}
		}

		sessionUsernameIDMap.put(session, username + "+" + gameID);
		usernameIDSessionMap.put(username + "+" + gameID, session);

		if (newGame) {
			String message = "{\"type\":\"create\",\"player\":\"" + username + "\"}";
			broadcast(message, gameID);
		} else {
			String message = "{\"type\":\"join\",\"player\":\"" + username + "\"}";
			broadcast(message, gameID);
		}
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {
//		public void onMessage(Session session, String message, @PathParam("gameID") String gameID) throws IOException {
		// Handle new messages

		String temp = sessionUsernameIDMap.get(session);
		String username = temp.substring(0, temp.indexOf("+"));
		String gameID = temp.substring(temp.indexOf("+") + 1, temp.length());

//		if (message.startsWith("@")) // Direct message to a user using the format "@username <message>"
//		{
//			String destUsername = message.split(" ")[0].substring(1); // don't do this in your code!
//			sendMessageToPArticularUser(destUsername, "[DM] " + username + ": " + message);
//			sendMessageToPArticularUser(username, "[DM] " + username + ": " + message);
//		} else // Message to whole chat
//		{
//			broadcast(username + ": " + message);
//		}

		logger.info("Entered into Message: Got Message:" + message + "\nchannel: " + gameID);

//		broadcast(username + ": " + message, gameID);
		broadcast(message, gameID);
	}

	@OnClose
	public void onClose(Session session) throws IOException {
		logger.info("Entered into Close");

		String temp = sessionUsernameIDMap.get(session);
		String username = temp.substring(0, temp.indexOf("+"));
		String gameID = temp.substring(temp.indexOf("+") + 1, temp.length());
		sessionUsernameIDMap.remove(session);
		usernameIDSessionMap.remove(username + "+" + gameID, session);

		String message = username + " disconnected from " + gameID;
		broadcast(message, gameID);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
		logger.info("Entered into Error");
	}

//	private void sendMessageToPArticularUser(String username, String message) {
//		try {
//			usernameSessionMap.get(username).getBasicRemote().sendText(message);
//		} catch (IOException e) {
//			logger.info("Exception: " + e.getMessage().toString());
//			e.printStackTrace();
//		}
//	}

	private static void broadcast(String message, String gameID) throws IOException {
		sessionUsernameIDMap.forEach((session, username) -> {
			synchronized (session) {
				try {
					if (username.substring(username.indexOf("+") + 1, username.length()).equals(gameID)) {
						session.getBasicRemote().sendText(message);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
}
