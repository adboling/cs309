package jrising.myapplication.MyTimeline

import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.*
import android.widget.AdapterView.OnItemLongClickListener
import com.android.volley.Response
import jrising.myapplication.R
import jrising.myapplication.RecipeView.recipeView
import jrising.myapplication.app.AppController
import jrising.myapplication.app.AppController.Companion.volleyController
import jrising.myapplication.net_utils.Const
import org.json.JSONArray
import org.json.JSONObject


/**
 * Recycler view adaptor for the [MyTImelLineFragment] that holds
 * all of the logs taking in a JSON array from the server holding all of the users logs
 * @param logdata: holds all of the users timeline objects or recipe logs to be populate eadh item in the timeline
 * @author Justin Kuennen
 */
class MytimeLineRecycleAdaptor(private val logdata: JSONArray,private val isOwner:Boolean) : RecyclerView.Adapter<MytimeLineRecycleAdaptor.logHolder>( ) {

    private lateinit var recipeID: String

    inner class logHolder(v: View) : RecyclerView.ViewHolder(v) {

        var logPhoto: ImageView = v.findViewById(R.id.Recipe_image)
        var recipeName: TextView = v.findViewById(R.id.recipeName)
        var date: TextView = v.findViewById(R.id.Log_date)
        var notes: ListView = v.findViewById(R.id.notesList)
        private var noteArray: ArrayList<String> = ArrayList()
        var adapter: ArrayAdapter<String>
        var recipeButton: Button = v.findViewById(R.id.openRecipe_btn)
        var deleteBtn: ImageButton = v.findViewById(R.id.time_delete_btn)
        var Lview = v
        var recipeID: String? = null

        init {
            if (!isOwner) {
                deleteBtn.visibility = View.GONE
            }
            adapter = ArrayAdapter(v.context, android.R.layout.simple_expandable_list_item_1, noteArray)
            //bonds with arraylist to hold data
            notes.adapter = adapter

        }

    }


        /**
         * populates each timeline object with adding notes and pictures and so on
         * @param holder takes in the logHolder that is essential the layout we will be populating
         * @param position takes in the positions of the current layout object currently being populated
         */
        override fun onBindViewHolder(holder: logHolder, position: Int) {

            var jlogdata = logdata.getJSONObject(position)
            Log.d("Timeline", jlogdata.toString())

            var Udate = jlogdata.getString("time")
            Udate = Udate.substring(0..9)
            recipeID = jlogdata.getString("recipeID")
            var dishName = jlogdata.getString("recipeName")
            var photo = jlogdata.optString("picture")

            //grab the photo and apply it from the server
            photo = photo.replace("\\", "")
            if (photo.isNotEmpty()) {
                if (URLUtil.isValidUrl(photo))
                    AppController.volleyController.requestImage(holder.logPhoto, photo)
                else {
                    Log.d("Timeline", "Invalid image url: $photo")
                }
            }
            holder.recipeID = recipeID


            holder.adapter.clear()
            for (i in 0..9) {
                var stringNote = "note$i"
                var note = jlogdata.getString(stringNote)
                if (note.isNotEmpty())
                    holder.adapter.add(note)

                note = ""
            }
            holder.date.text = Udate
            holder.recipeName.text = dishName


        }

        private fun setupClickables(holder: logHolder) {
            holder.recipeButton.setOnClickListener {
                val fragment = recipeView()
                fragment.recipeID = holder.recipeID
                AppController.eventBus.switchMainFragment(fragment)
            }

            /**
             * allows deletion of listview item
             */
            holder.notes.onItemLongClickListener = OnItemLongClickListener { arg0, arg1, pos, id ->
                // Dialog/Popup will appears here
                holder.adapter.remove(holder.adapter.getItem(pos))
                holder.adapter.notifyDataSetChanged()


                true
            }
            //will open a dialog to delete a post
            holder.deleteBtn.setOnClickListener {
                removeTimelineLogDialog(holder)
            }


        }

        /**
         * gets the total number of objects in the recycler view
         */



        override fun getItemCount(): Int {


            return logdata.length()
        }

        /**
         * Inflates the layout the will be used with logHolder an the rest of the recyclerView
         */
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): logHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_log, parent, false)
            val logHolder = logHolder(v)
            setupClickables(logHolder)

            return logHolder

        }

        /**
         * creates a dialog box for the user to have the option to delete a timeline post
         */
       private fun removeTimelineLogDialog(holder: logHolder) {
            val alertDialog: AlertDialog? = holder.Lview.let {
                val builder = AlertDialog.Builder(holder.Lview.context)
                builder.apply {
                    setMessage("Delete Item ?\nDeleting this cannot be undone")


                    setPositiveButton(
                        R.string.delete
                    ) { _, _ ->
                        //if you choose yes we will start the proccess of deleting that timeline object
                        var jlogdata = logdata.getJSONObject(holder.adapterPosition)
                        var ObjectId = jlogdata.getInt("timelineID")
                        deleteFromServer(ObjectId, holder)

                    }
                    setNegativeButton(
                        R.string.cancel
                    ) { _, _ ->
                        // User cancelled the dialog
                    }
                }


                // Create the AlertDialog
                builder.show()
            }
        }
  private  fun deleteFromServer(ObjectId:Int,  holder: logHolder){
        var url = Const.URL_VC5_TIMELINES+"?timelineID="+ObjectId
        AppController.volleyController.createJSONRequest(url, volleyController.methodDelete, "deleteTimelineID", null, {response-> handleRemove(response, holder)},
            {error-> Log.e("error", "$error")})
    }

    private fun handleRemove(response: JSONObject, holder:logHolder ){
        logdata.remove(holder.adapterPosition)
        notifyItemRemoved(holder.adapterPosition)
        notifyItemRangeChanged(holder.adapterPosition, logdata.length())
    }
    }




