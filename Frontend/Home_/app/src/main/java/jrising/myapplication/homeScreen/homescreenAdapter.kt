package jrising.myapplication.homeScreen

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import jrising.myapplication.R
import jrising.myapplication.RecipeView.recipeView
import jrising.myapplication.app.AppController
import kotlinx.android.synthetic.main.homscreen_cards.view.*
import org.json.JSONArray


class homescreenAdaptor(private val homeInfo: JSONArray) : RecyclerView.Adapter<homescreenAdaptor.cardHolder>(){
    private lateinit var recipeID : String



    /**
     * sets the content for each card taking in from the JSONArray at the possition taken at
     * @param holder is a cardHolder that is used to populate data from
     */
    override fun onBindViewHolder(holder: cardHolder, position: Int) {
        var jhomedata = homeInfo.getJSONObject(homeInfo.length() - position-1)

        var Uname = jhomedata.getString("username")
        var dishName = jhomedata.getString("recipeName")
        recipeID = jhomedata.getString("recipeID")
        var url = ""
        url = jhomedata.optString("picture")
        url = url.replace("\\","")
        holder.userName.text = Uname
        holder.dishName.text = dishName
        holder.recipeID = recipeID
        if(URLUtil.isValidUrl(url))
            AppController.volleyController.requestImage(holder.dishimage, url)
        else
            Log.d("Homescreen", "Invalid image url: " + url)
    }



    /**
     * controls how manycards there are in the recyclerView
     */
    override fun getItemCount(): Int {
        return homeInfo.length()
    }

    /**
     * inits the recyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cardHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.homscreen_cards, parent,false)
        val cardHolder = cardHolder(v)
        setupClickables(cardHolder)
        return cardHolder
    }

    /**
     * grabs the starting variables from homescreen_cards layout
     */
    inner class cardHolder(v: View):RecyclerView.ViewHolder(v){
        var userName : TextView = v.findViewById(R.id.postUser)
        var dishName : TextView = v.findViewById(R.id.cardDishName)
        // var commentChange: TextSwitcher
        var dishimage: ImageView = v.findViewById(R.id.dishImageCenter)
        var commentBtn: ImageButton = v.findViewById(R.id.btnComments)
        var recipeButton: Button = v.findViewById(R.id.cardRecipeButton)

        var recipeID: String? = null


        init {
            //commentChange = v.findViewById(R.id.commentCounter)


        }

    }

    private fun setupClickables(holder : cardHolder) {
       holder.recipeButton.setOnClickListener {
           var jhomedata = homeInfo.getJSONObject(holder.adapterPosition)
           var args = Bundle()

           val fragment = recipeView()
           fragment.recipeID = holder.recipeID
           AppController.eventBus.switchMainFragment(fragment)
       }
    }


}
