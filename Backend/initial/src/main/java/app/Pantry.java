package app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "pantry")
public class Pantry {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pantry_id")
	private Integer id;

	@Column(name = "user_id")
	private int userID;

	@Column(name = "ingredient")
	private String ingredient;

	public Pantry() {

	}

	public Pantry(int userID, String ingredient) {
		this.userID = userID;
		this.ingredient = ingredient;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getIngredient() {
		return ingredient;
	}

	public void setIngredient(String ingredient) {
		this.ingredient = ingredient;
	}

	@Override
	public String toString() {
		return Application.JSONify(this);
	}

	@Override
	public boolean equals(Object obj) {
		boolean result = false;
		if (obj instanceof Pantry) {
			Pantry newPantry = (Pantry) obj;
			if (newPantry.id == this.id && newPantry.ingredient.equals(this.ingredient)
					&& newPantry.userID == this.userID) {
				return true;
			}
		}
		return result;
	}

}
