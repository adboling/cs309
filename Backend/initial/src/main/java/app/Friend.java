package app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "friends")
public class Friend {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "friendship_id")
	private int friendshipID;
	
	@Column(name = "sender_id")
	private int senderID;
	
	@Column(name = "sender_username")
	private String senderUsername;
	
	@Column(name = "recipient_id")
	private int recipientID;
	
	@Column(name = "recipient_username")
	private String recipientUsername;
	
	@Column(name = "status")
	private String status;
	
	Friend() {
		
	}
	
	public Friend(int sid, String su, int rid, String ru) {
		senderID = sid;
		senderUsername = su;
		recipientID = rid;
		recipientUsername = ru;
	}
	
	public int getFriendshipID() {
		return friendshipID;
	}
	
	public void setFriendshipID(int friendshipID) {
		this.friendshipID = friendshipID;
	}
	
	public int getSenderID() {
		return senderID;
	}
	
	public void setSenderID(int id) {
		this.senderID = id;
	}
	
	public String getSenderUsername() {
		return senderUsername;
	}
	
	public void setSenderUsername(String username) {
		this.senderUsername = username;
	}
	
	public int getRecipientID() {
		return recipientID;
	}
	
	public void setRecipientID(int id) {
		this.recipientID = id;
	}
	
	public String getRecipientUsername() {
		return recipientUsername;
	}
	
	public void setRecipientUsername(String username) {
		this.recipientUsername = username;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	@Override
	public String toString() {
		return Application.JSONify(this);
	}
	
}
