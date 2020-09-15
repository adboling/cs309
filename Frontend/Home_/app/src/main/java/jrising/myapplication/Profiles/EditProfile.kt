package jrising.myapplication.Profiles


    import android.app.Activity
    import android.content.Context
    import android.content.Intent
    import android.os.Bundle
    import android.provider.MediaStore
    import android.support.v4.app.Fragment
    import android.view.LayoutInflater
    import android.view.View
    import android.view.ViewGroup
    import android.widget.*

    import jrising.myapplication.R
    import jrising.myapplication.app.AppController
    import jrising.myapplication.net_utils.Const

    import android.graphics.Bitmap
    import android.util.Log


    import android.webkit.URLUtil
    import jrising.myapplication.app.AppController.Companion.volleyController
    import jrising.myapplication.net_utils.Userinfo
    import org.json.JSONObject


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private const val ARG_UNAME = "uname"
    private const val ARG_BIO = "bio"
    private const val ARG_FNAME = "fname"
    private const val ARG_LNAME = "lname"
    private const val ARG_avatar = "avatar"
    private const val ARG_background = "background"


    /**
     * A simple [Fragment] subclass.
     * Use the [EditProfile.newInstance] factory method to
     * create an instance of this fragment.
     *
     */
    class EditProfile : Fragment() {

        private var username: String? = null
        private var bio: String? = null
        private var firstname: String? = null
        private var lastname: String? = null
        private lateinit var avatar: String
        private lateinit var background: String

        //for layout stuff
        private lateinit var profileName: TextView
        private lateinit var bioPreview: TextView
        private lateinit var saveBio: Button
        private lateinit var changeBio: EditText
        private lateinit var submitChanges: Button
        private lateinit var pickImage: Button
        private lateinit var takeImage: Button
        private lateinit var profilePic: ImageView
        private  var photo64:String = ""
        private  var changePic: Boolean = false

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            arguments?.let {
                username = it.getString(ARG_UNAME)
                bio = it.getString(ARG_BIO)
                firstname = it.getString(ARG_FNAME)
                lastname = it.getString(ARG_LNAME)
                avatar = it.getString(ARG_avatar)!!
                background = it.getString(ARG_background)!!

            }
        }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?

        ): View? {
            // Inflate the layout for this fragment
            var basicProfileHandler = BasicProfileHandler()
            val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)
            //init layout
            profileName = view.findViewById(R.id.profile_name)
            bioPreview = view.findViewById(R.id.bio_preview)
            saveBio = view.findViewById(R.id.save_edits)
            submitChanges = view.findViewById(R.id.submitChanges)
            changeBio = view.findViewById(R.id.edit_bio)
            pickImage = view.findViewById(R.id.pickPic)
            takeImage = view.findViewById(R.id.takePic)
            profilePic = view.findViewById(R.id.user_profile_photo)

            val full_name = "Edit $firstname's Profile"
            profileName.text = full_name
            var useBio = basicProfileHandler.checkofEmptyBio(bio)
            bioPreview.text = useBio
            changeBio.setText(useBio)


            saveBio.setOnClickListener {

                changeTheBio(changeBio.text.toString())
            }

            avatar = avatar.replace("\\","")

            if( avatar.isNotEmpty()) {
                if (URLUtil.isValidUrl(avatar))
                    AppController.volleyController.requestImage(profilePic, avatar)
                else {
                    Log.d("Timeline", "Invalid image url: " + avatar)
                }
            }
                pickImage.setOnClickListener {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), Const.PICK_IMAGE)
                }
                submitChanges.setOnClickListener {
                    changeProfile(bioPreview.text as String, photo64, view.context)

                }





                return view
            }

            fun changeProfile(bio: String, photo: String, context: Context){
                submitBio(bio, Userinfo.u_id, context)
                if(changePic) changeAvatar(photo, Userinfo.u_id, context)

            }

            fun submitBio(bio: String, id: Int, context: Context){
                var url = Const.URL_VC5_CHANGEBIO+"?id="+id
                var send = JSONObject()
                send.put("message", bio)
                AppController.volleyController.createJSONRequest(url,volleyController.methodPut, "bio",send ,
                    {response -> Toast.makeText(context,"New Bio Has Been Saved", Toast.LENGTH_LONG ).show()  },
                    {error -> Log.d("bioSubmit","$error")
                })
            }
            fun changeAvatar(photo: String,id: Int, context:Context){
                var url = Const.URL_VC5_MODIFY_AVATAR+"?id="+id
                var send = JSONObject()
                send.put("message",photo)
                AppController.volleyController.createJSONRequest(url,volleyController.methodPut,"avatar", send,{response -> Toast.makeText(context,"New Bio Has Been Saved", Toast.LENGTH_LONG ).show()  },
                    {error -> Log.d("avatarSubmit","$error")
                    })
            }

            override fun onActivityResult(requestCode: Int, resultCode:Int, data: Intent?) {
                super.onActivityResult(requestCode, resultCode, data)

                if(resultCode == Activity.RESULT_OK && requestCode == Const.PICK_IMAGE){
                    var fileUri = data?.data
                    val bmp = MediaStore.Images.Media.getBitmap(context!!.contentResolver, fileUri)
                    profilePic.setImageURI(fileUri)
                    profilePic.visibility = View.VISIBLE
                    photo64 = AppController.instance.convertBitmapToBase64(bmp)
                    changePic = true


                }
                else if(requestCode == Activity.RESULT_OK && requestCode == Const.PICK_CAPTURE){

                    val bitmap = data?.extras?.get("data") as Bitmap
                    profilePic.setImageBitmap(bitmap)

                }
                else {
                    return
                }


            }

            fun changeTheBio(changeText: String) {

                if (changeText.isNotEmpty()) {
                    bioPreview.text = changeText
                }

                changeBio.setText(bioPreview.text)

            }
        }
