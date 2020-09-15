package jrising.myapplication.app

import android.util.Log
import org.json.JSONObject

/**
 * A data class used in several packages
 * @param type String: The type or name of ingredient, IE: "Potatoes"
 * @param amount Double: The amount for the ingredient
 * @param unit String: The name of the unit for the ingredient, IE: "Cups"
 */
data class Ingredient(
    var type: String,
    var amount: Double?,
    var unit: String?) {
    constructor(source: String): this("", 0.0, "") {
        if(source.contains('=')) {
            val tokens = source.split('=')
            if(tokens.size != 3) return
            type = tokens[0]
            amount = tokens[1].toDoubleOrNull() ?: 0.0
            unit = tokens[2]
        } else {
            val obj = JSONObject(source)
            type = obj.getString(json_type)
            amount = obj.getDouble(json_amount)
            unit = obj.getString(json_unit)
        }

    }
    constructor(source: JSONObject): this("", 0.0, "") {
        type = source.getString(json_type)
        amount = source.getDouble(json_amount)
        unit = source.getString(json_unit)
    }
    override fun toString(): String {
        val amountStr = if(amount == null) "" else amount.toString()
        val unitStr = if(unit == null) "" else unit
        return "$amountStr $unitStr $type"
    }
     fun toJSONObject(): JSONObject {
        val obj = JSONObject()
         obj.put(json_type, type)
         obj.put(json_amount, amount)
         obj.put(json_unit, unit)
         return obj
    }
    fun toDataString(): String {
        return "$type=$amount=$unit"
    }
    companion object {
        const val json_type = "name"
        const val json_amount = "amount"
        const val json_unit = "unit"
    }
}

/**
 * A data class used to represent a user
 */
data class User(
    var avatar: String?,
    var background: String?,
    var bio: String?,
    var email: String?,
    var firstName: String?,
    var lastName: String?,
    var id: Int?,
    var role: String?,
    var username: String?
) {
    constructor(source: JSONObject): this(null, null, null, null, null, null, null, null, null) {
        avatar = source.optString(json_avatar)
        background = source.optString(json_background)
        bio = source.optString(json_bio)
        email = source.optString(json_email)
        firstName = source.optString(json_firstname)
        lastName = source.optString(json_lastname)
        id = source.optInt(json_id)
        role = source.optString(json_role)
        username = source.optString(json_username)
    }
    fun toJSONObject(): JSONObject {
        val result = JSONObject()
        result.put(json_avatar, avatar)
        result.put(json_background, background)
        result.put(json_bio, bio)
        result.put(json_email, email)
        result.put(json_firstname, firstName)
        result.put(json_lastname, lastName)
        result.put(json_id, id)
        result.put(json_role, role)
        result.put(json_username, username)
        return result
    }

    companion object {
        const val json_avatar = "avatar"
        const val json_background = "background"
        const val json_bio = "bio"
        const val json_email = "email"
        const val json_firstname = "firstname"
        const val json_lastname = "lastname"
        const val json_id = "id"
        const val json_role = "role"
        const val json_username = "username"
    }
}
