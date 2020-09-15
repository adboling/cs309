package app;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "shopping_list")
public class ShoppingList {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shopping_entry_id")
	private int entryID;
	
	@Column(name = "user_id")
	private int userID;
	
	@Column(name = "ingredient_name")
	private String name;
	
	@Column(name = "ingredient_unit")
	private String unit;
	
	@Column(name = "ingredient_amount")
	private int amount;
	
	@Column(name = "complete")
	private int complete;
	
	public ShoppingList() {
		
	}
	
	public ShoppingList(int userID, String name, String unit, int amount, int complete) {
		this.userID = userID;
		this.name = name;
		this.unit = unit;
		this.amount = amount;
		this.complete = complete;
	}

	public Integer getEntryID() {
		return entryID;
	}

	public void setEntryID(Integer entryID) {
		this.entryID = entryID;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getComplete() {
		return complete;
	}

	public void setComplete(int complete) {
		this.complete = complete;
	}
	
	@Override
	public String toString() {
		return Application.JSONify(this);
	}
	
}
