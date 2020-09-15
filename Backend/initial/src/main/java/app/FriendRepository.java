package app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FriendRepository extends JpaRepository<Friend, Integer> {

	// find by single parameter ==========================================

	public Friend findByFriendshipID(int friendshipID);

	public List<Friend> findAllBySenderID(int senderID);

	public List<Friend> findAllBySenderUsername(String senderUsername);

	public List<Friend> findAllByRecipientID(int recipientID);

	public List<Friend> findAllByRecipientUsername(String recipientUsername);

	public List<Friend> findAllByStatus(String status);

	// find by multiple parameters =======================================

	public List<Friend> findAllBySenderIDAndStatus(int senderID, String status);

	public List<Friend> findAllBySenderUsernameAndStatus(String recipientUsername, String status);

	public List<Friend> findAllByRecipientIDAndStatus(int senderID, String status);

	public List<Friend> findAllByRecipientUsernameAndStatus(String recipientUsername, String status);

	public Friend findBySenderIDAndRecipientID(int senderID, int recipientID);

	public Friend findBySenderUsernameAndRecipientUsername(String senderUsername, String recipientUsername);

	public List<Friend> findAllBySenderIDAndRecipientID(int senderID, int recipientID);

	public List<Friend> findAllBySenderUsernameAndRecipientUsername(String senderUsername, String recipientUsername);

	public List<Friend> findAllBySenderIDOrRecipientIDAndStatus(int sendnerID, int recipientID, String status);

	public List<Friend> findAllBySenderUsernameOrRecipientUsernameAndStatus(int sendnerID, int recipientID,
			String status);

	// delete by a single parameter ======================================

	public void deleteByFriendshipID(int friendshipID);

	public void deleteAllBySenderIDOrRecipientID(int userID, int user);

}
