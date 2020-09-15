package app;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "timelines")
public class Timeline {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "timeline_id")
	private int timelineID;

	@Column(name = "time")
	private Timestamp time;

	@Column(name = "user_id")
	private int userID;

	@Column(name = "username")
	private String username;

	@Column(name = "recipe_id")
	private String recipeID;

	@Column(name = "recipe_name")
	private String recipeName;

	@Column(name = "picture")
	private String picture;

	@Column(name = "note_0")
	private String note0;

	@Column(name = "note_1")
	private String note1;

	@Column(name = "note_2")
	private String note2;

	@Column(name = "note_3")
	private String note3;

	@Column(name = "note_4")
	private String note4;

	@Column(name = "note_5")
	private String note5;

	@Column(name = "note_6")
	private String note6;

	@Column(name = "note_7")
	private String note7;

	@Column(name = "note_8")
	private String note8;

	@Column(name = "note_9")
	private String note9;

	public int getTimelineID() {
		return timelineID;
	}

	public void setTimelineID(int key) {
		this.timelineID = key;
	}

	public Timestamp getTime() {
		return time;
	}

	public void setTime(Timestamp time) {
		this.time = time;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getRecipeID() {
		return recipeID;
	}

	public void setRecipeID(String recipeID) {
		this.recipeID = recipeID;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getNote0() {
		return note0;
	}

	public void setNote0(String note) {
		this.note0 = note;
	}

	public String getNote1() {
		return note1;
	}

	public void setNote1(String note) {
		this.note1 = note;
	}

	public String getNote2() {
		return note2;
	}

	public void setNote2(String note) {
		this.note2 = note;
	}

	public String getNote3() {
		return note3;
	}

	public void setNote3(String note) {
		this.note3 = note;
	}

	public String getNote4() {
		return note4;
	}

	public void setNote4(String note) {
		this.note4 = note;
	}

	public String getNote5() {
		return note5;
	}

	public void setNote5(String note) {
		this.note5 = note;
	}

	public String getNote6() {
		return note6;
	}

	public void setNote6(String note) {
		this.note6 = note;
	}

	public String getNote7() {
		return note7;
	}

	public void setNote7(String note) {
		this.note7 = note;
	}

	public String getNote8() {
		return note8;
	}

	public void setNote8(String note) {
		this.note8 = note;
	}

	public String getNote9() {
		return note9;
	}

	public void setNote9(String note) {
		this.note9 = note;
	}

	@Override
	public String toString() {

		return Application.JSONify(this);
	}

}
