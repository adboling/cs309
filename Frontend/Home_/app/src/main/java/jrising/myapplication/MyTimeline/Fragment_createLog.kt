package jrising.myapplication.MyTimeline

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import jrising.myapplication.R
import jrising.myapplication.app.AppController
import jrising.myapplication.app.createLogsHandler
import jrising.myapplication.net_utils.Const
import jrising.myapplication.net_utils.Userinfo
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [Fragment_createLog.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [Fragment_createLog.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class Fragment_createLog : Fragment(), createLogsHandler {

    override fun handleLogSubmition() {
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager?.beginTransaction()
        //opens myTimeLineFragement from menu
        val myTimeLine = MyTImelLineFragment()
        var args = Bundle()
        args.putString("uName", Userinfo.username)
        myTimeLine.arguments = args
        fragmentTransaction?.replace(R.id.fragment_container, myTimeLine)
        fragmentTransaction?.commit()
    }

    // TODO: Rename and change types of parameters
    private var recipeID: String? = ""
    private var dishName: String? = ""
    private lateinit var dishImage: ImageView
    private lateinit var listView: ListView
    private lateinit var noteText: EditText
    private lateinit var addNoteBtn: Button
    private lateinit var  itemModelList: ArrayList<String>
    private lateinit var  customAdapter: ArrayAdapter<String>
    private var listNum: Int = 0
    private lateinit var dishTextName: TextView

    private var photo64: String = ""
    private var firstNote:Boolean = true



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

    }

    /**
     * Inflates the view and initializes the layout widgets to be
     * used creating the and initialing the listview that is used ot hold notes
     * on [addNoteBtn] click  a note will be added to the listview from the text of [noteText]
     * [submitBtn] click will call [submitLog] starting the log creation process
     * @return the inflated view for [create_log]
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?):View{

        //init section
        var view = inflater.inflate(R.layout.fragment_fragment_create_log, container, false) // Inflate the layout for this fragment
        //to get image from roll or camera app
        var PickimageBtn = view.findViewById<Button>(R.id.pickPic)

        dishTextName= view.findViewById(R.id.profile_name)
        var submitBtn = view.findViewById<Button>(R.id.SubmitButton)
        submitBtn.isEnabled = true
        //grabs the argurments from the recipe from before start up of fragment
        var args = arguments
        recipeID = args?.getString("recipeID")
        dishName= args?.getString("recipeName")
        dishTextName.text = dishName //sets the recipe name

        dishImage = view.findViewById(R.id.user_profile_photo)
        //for making the list
        listView = view.findViewById(R.id.notes_list)
        noteText = view.findViewById((R.id.input_Notes))
        itemModelList = ArrayList()

        addNoteBtn = view.findViewById(R.id.takeNoteButton)
        //make adapter for listView
        customAdapter = ArrayAdapter(context,android.R.layout.simple_expandable_list_item_1,itemModelList)
        //bonds with arraylist to hold data
        listView.adapter = customAdapter

        //button gets image from library
        PickimageBtn.setOnClickListener{

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.PICK_IMAGE)
        }


        //adds to the list
        addNoteBtn.setOnClickListener {
            if(firstNote){
                firstNote = false
                Toast.makeText(context,"Important: Long Press an Item to Delete it",Toast.LENGTH_LONG).show()
            }
            addListItem() //goes to add an item to the list
        }
        //submits the log puts everything in a jsonBody and sends it to the server will go to timeline on success
        submitBtn.setOnClickListener {
            submitBtn.isEnabled = false
           submitLog()
        }
        /**
         * allows deletion of listview item to adjust notes of the
         */
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { arg0, arg1, pos, id ->
            // Dialog/Popup will appears here
            customAdapter.remove(customAdapter.getItem(pos))
            customAdapter.notifyDataSetChanged()

            true
        }



        return view
    }

   private fun getBundle(){
        var args = arguments
        recipeID = args?.getString("recipeID")
        dishName= args?.getString("recipeName")
    }

    private  fun submitLog(){
        var submitLog = JSONObject() //create a json

        submitLog.put("userID", Userinfo.u_id)
        submitLog.put("recipeID", recipeID)
        submitLog.put("picture", photo64)
        submitLog.put("recipeName", dishName)
        for(i in 0..9){
            if(i < itemModelList.size){
                submitLog.put("note$i", itemModelList[i])
            }
            else{
                submitLog.put("note$i", "")
            }
        }
        Log.d("ignore me", submitLog.toString())
        AppController.volleyController.postLog(submitLog, this)

    }

    /**
     * adds new notes to a listView using noteText
     * loads into a ArrayList itemModeList
     */
   private fun addListItem(){

        var testNoteText = noteText.text.toString() //grabs note text

        //checks if the note is empty
        if(testNoteText.isEmpty()){
            noteText.error = "Enter your note first"

        }

        else{

            if(listNum <= 10) { //only add the first 10 notes for now
                itemModelList.add(testNoteText)
                customAdapter.notifyDataSetChanged()
                listNum++
            }
            else{
                noteText.error = "too many note for this current version"
            }
            noteText.setText("") //clear the notes
        }



    }

    /**
     * used to handle the photo part of the log
     * goes to this function after picking an image from the gallery and puts it into the imageView
     */
    override fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

            if(resultCode == Activity.RESULT_OK && requestCode == Const.PICK_IMAGE){
               var fileUri = data?.data
                val bmp = MediaStore.Images.Media.getBitmap(context!!.contentResolver, fileUri)
                dishImage.setImageURI(fileUri)
                dishImage.visibility = View.VISIBLE
                 photo64 = AppController.instance.convertBitmapToBase64(bmp)


            }
        else if(requestCode == Activity.RESULT_OK && requestCode == Const.PICK_CAPTURE){
               // dishImage.setImageBitmap(image)
              //  dishImage.visibility = View.VISIBLE

            }
        else {
              return
            }


    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment_createLog.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment_createLog().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
