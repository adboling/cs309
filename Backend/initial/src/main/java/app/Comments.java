package app;

//import java.sql.Time;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comments")
public class Comments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id")
	private int commentID;
	@Column(name = "user_id")
	private int userID;
	@Column(name = "recipe_id")
	private String recipeID;
	@Column(name = "date")
	private Timestamp time;
	@Column(name = "comment")
	private String comment;
	@Column(name = "likes")
	private int likes;

	public Comments() {

	}

	public Comments(int commentID, int userID, String recipeID, Timestamp time, String comment, int likes) {
		super();
		this.commentID = commentID;
		this.userID = userID;
		this.recipeID = recipeID;
		this.time = time;
		this.comment = comment;
		this.likes = likes;
	}

	public int getCommenID() {
		return commentID;
	}

	public void setCommenID(int commenID) {
		this.commentID = commenID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getRecipeID() {
		return recipeID;
	}

	public void setRecipeID(String recipeID) {
		this.recipeID = recipeID;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getLikes() {
		return likes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	@Override
	public String toString() {

		return Application.JSONify(this);
	}

}
