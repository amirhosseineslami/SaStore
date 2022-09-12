package com.mintab.sastore.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.databinding.DataBindingUtil
import androidx.documentfile.provider.DocumentFile
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.anggrayudi.storage.SimpleStorageHelper
import com.anggrayudi.storage.file.getAbsolutePath
import com.anggrayudi.storage.file.toRawFile
import com.bumptech.glide.Glide
import com.dps.custom_file_picker.activities.DocumentsActivity
import com.dps.custom_file_picker.app_helper.AppConstants.FILES_PATH
import com.dps.custom_file_picker.app_helper.CustomIntent.SELECTED_TYPES
import com.dps.custom_file_picker.app_helper.MimeTypes.IMAGE_JPEG
import com.dps.custom_file_picker.app_helper.MimeTypes.IMAGE_PNG
import com.google.android.material.navigation.NavigationView
import com.mintab.sastore.R
import com.mintab.sastore.databinding.FragmentNavBottomAccountBinding
import com.mintab.sastore.model.AccountDetailModel
import com.mintab.sastore.model.UploadPicFileStackModel
import com.mintab.sastore.model.UploadPicModel
import com.mintab.sastore.viewmodel.MainActivityViewModel
import com.mintab.sastore.worker.FillTheNullArrayString
import es.dmoral.toasty.Toasty
import okhttp3.*
import java.io.File
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine


private const val TAG = "AccountNavigationFragme"
private const val PROFILE_PIC_REQ_CODE = 123432
const val PERMISSION_READ_EXTERNAL_REQ_CODE = 100234
val permission = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

class AccountNavigationFragment : Fragment() {
    lateinit var binding: FragmentNavBottomAccountBinding
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val storageHelper = SimpleStorageHelper(this)
    private lateinit var accountProfilePictureImageView: ImageView
    private lateinit var listener: AccountNavigationFragmentEventListener
    private lateinit var menuNavDrawerLayout: DrawerLayout
    private lateinit var menuNavView: NavigationView
    private lateinit var menuNavDrawerBtn: ImageButton
    private lateinit var navViewConstraintLayout:ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_nav_bottom_account,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivityViewModel =
            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]

        // menu
        menuNavDrawerLayout = binding.fragmentAccountNavDrawerLayout
        menuNavDrawerLayout.setOnClickListener { Log.i(TAG, "onViewCreated: adfa") }
        menuNavDrawerBtn = binding.fragmentAccountActionbarButtonMenu
        menuNavView = binding.fragmentNavBottomAccountNavView
        menuNavView.setNavigationItemSelectedListener(navigationViewItemSelectedListener())
        navViewConstraintLayout = binding.fragmentNavBottomAccountNavViewConstraintLayout

        /*
        menuNavView.setOnClickListener { Log.i(TAG, "onViewCreated: ") }
        binding.fragmentNavBottomAccountNavView.menu.findItem(R.id.navigation_drawer_menu_item_settings)
            .setOnMenuItemClickListener {
                Toast.makeText(requireContext(),"sdfjl",Toast.LENGTH_SHORT).show()
                true
            }*/

        // get account details
        val sharedPreferences = requireActivity().getSharedPreferences(
            SHARED_PREFERENCES_KEY,
            Context.MODE_PRIVATE
        )
        val number = sharedPreferences.getString(SHARED_PREFERENCES_EXTRA_NUMBER_KEY, null)
        mainActivityViewModel.getAccountDetails(number!!)
            .observe(
                viewLifecycleOwner
            ) { t -> binding.model = t }


        // set Listener
        listener = AccountNavigationFragmentEventListener(
            storageHelper,
            requireContext(),
            menuNavDrawerLayout,
            menuNavView,
            navViewConstraintLayout
        )
        binding.listener = listener


        // file picker
        storageHelper.onFileSelected = onStorageHelperFileSelected()

        // views
        accountProfilePictureImageView = binding.fragmentAccountImageView
        accountProfilePictureImageView.setOnClickListener(profilePicOnClickListener())
    }

    public fun navigationViewItemSelectedListener(): NavigationView.OnNavigationItemSelectedListener {
        return NavigationView.OnNavigationItemSelectedListener {
            if (menuNavDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                menuNavDrawerLayout.closeDrawer(Gravity.RIGHT);
            } else {
                menuNavDrawerLayout.openDrawer(Gravity.RIGHT);
            }
            Toast.makeText(requireContext(),it.title,Toast.LENGTH_SHORT).show()
            when (it.itemId) {
                R.id.navigation_drawer_menu_item_settings -> {
                    Toast.makeText(requireContext(), "Clicked", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(binding.root)
                        .navigate(AccountNavigationFragmentDirections.actionAccountNavigationFragmentToSettingsNavDrawerFragment())
//                    menuNavDrawerLayout.closeDrawer(menuNavDrawerLayout)
                    menuNavDrawerLayout.closeDrawer(Gravity.RIGHT)
                }
            }
            super.onOptionsItemSelected(it)
        }
    }

    private fun profilePicOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            openSomeActivityForResult()
        }
    }

    private fun onStorageHelperFileSelected(): (requestCode: Int, files: List<DocumentFile>) -> Unit {
        return fun(requestCode, files) {
            val userAccountNumber =
                requireContext().getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                    .getString(SHARED_PREFERENCES_EXTRA_NUMBER_KEY, null)
            if (userAccountNumber != null) {

                val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2")
                sslContext.init(null, null, null)
                val engine: SSLEngine = sslContext.createSSLEngine()
                engine.enabledProtocols

                if (files[0].isFile) {
                    val filePath = files[0].getAbsolutePath(requireContext())
                    val firstFile = files[0]
                    Log.i(TAG, "first uri: ${firstFile.uri}")
                    val file: File =
                        firstFile.toRawFile(requireContext())?.takeIf { it.canRead() }!!
                    /*
                    mainActivityViewModel.uploadPicture(file)
                        .observe(viewLifecycleOwner, uploadProfilePictureObserver())*/
                } else {
                    Toasty.custom(
                        requireContext(),
                        "Select a picture!",
                        android.R.drawable.ic_menu_report_image.toDrawable(),
                        Toasty.LENGTH_SHORT,
                        true
                    )
                        .show()
                }
            } else {
                Toasty.error(requireContext(), "You haven't put your password!")
            }

        }
    }

    private fun uploadProfilePictureObserver(): Observer<in UploadPicModel> {
        return Observer<UploadPicModel> {
            if (it.status) {
                Toasty.success(requireContext(), "Your profile picture successfully Changed!")
                    .show()
                Log.i(TAG, "uploadProfilePictureObserver: ${it.data.file.url.full}")
                Glide.with(requireContext()).load(it.data.file.url)
                    .into(accountProfilePictureImageView)
            } else {
                Toasty.error(requireContext(), "Error").show()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        storageHelper.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            storageHelper.onRestoreInstanceState(savedInstanceState)
        }
        super.onViewStateRestored(savedInstanceState)
    }

    public class AccountNavigationFragmentEventListener(
        val storageHelper: SimpleStorageHelper,
        val context: Context,
        val navigationDrawerLayout: DrawerLayout,
        val menuNavDrawerView: NavigationView,
        val navViewConstraintLayout: ConstraintLayout
    ) {

        public fun navigationViewItemSelectedListener(menuItem: MenuItem): Boolean {
            Log.i(TAG, "navigationViewItemSelectedListener: ")
            Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
            when (menuItem.itemId) {
                R.id.settingsNavDrawerFragment -> {
                    Navigation.findNavController(menuItem as View)
                        .navigate(AccountNavigationFragmentDirections.actionAccountNavigationFragmentToSettingsNavDrawerFragment())
                    navigationDrawerLayout.closeDrawer(menuNavDrawerView)
                }
            }
            return true
        }

        public fun menuButtonClickListener(view: View) {
            navigationDrawerLayout.openDrawer(Gravity.RIGHT)
            //navigationDrawerLayout.open()
            /*
                menuNavDrawerView.setNavigationItemSelectedListener {
                    Log.i(TAG, "menuButtonClickListener: ")
                    false
                }*/
        }

        public fun editButtonListener(view: View, accountDetailModel: AccountDetailModel) {
            val strings: Array<String?> = arrayOf(
                accountDetailModel.getName(),
                accountDetailModel.getAddress(),
                accountDetailModel.getPostalCode(),
                accountDetailModel.getReplacementNumber(),
                accountDetailModel.getNumber()
            )
            FillTheNullArrayString.fillTheNullArrayString(strings)
            Navigation.findNavController(view).navigate(
                AccountNavigationFragmentDirections
                    .actionAccountNavigationFragmentToChangeAccountDetailsFragment(strings as Array<String>)
            )
        }

        public fun onProfilePicClickListener(view: View) {
        }

    }

    public fun openSomeActivityForResult() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission[0]
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(TAG, "onProfilePicClickListener: permission granted")
            // if it has permission open file picker
            //storageHelper.openFilePicker(PROFILE_PIC_REQ_CODE, false)
            val intent = Intent(context, DocumentsActivity::class.java)
            //intent.action = ALLOW_MULTIPLE_SELECTION // for allowing multiple selection at a time (optional)

            intent.putExtra(
                SELECTED_TYPES,
                arrayOf(IMAGE_JPEG, IMAGE_PNG)
            ) // giving array of mime types of string (Adding MimeTypes is compulsory)

            resultLauncher.launch(intent)

/* Given MimeTypes:- DOC, DOC_X, XLS, XLS_X, PPT, PPT_X, HTML, TXT, IMAGE_PNG, IMAGE_JPEG, IMAGE_BMP, IMAGE_GIF, IMAGE_SVG, VIDEO_MP4,  VIDEO_AVI, VIDEO_MPEG, VIDEO_MOV, VIDEO_3GP, AUDIO_MP3, AUDIO_OGG and AUDIO_WAV */

        } else {
            // get the Read external storage permission
            Log.i(TAG, "onProfilePicClickListener: permission requested")
            ActivityCompat.requestPermissions(
                context as Activity,
                permission,
                PERMISSION_READ_EXTERNAL_REQ_CODE
            )
        }
    }

    public var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val selectedFilesPath: ArrayList<String> =
                    result.data?.getStringArrayListExtra(FILES_PATH) as ArrayList<String>
                for (path in selectedFilesPath)  // for multiple files
                    Log.d("file_path", path)
                Toast.makeText(requireContext(), "Path: $selectedFilesPath", Toast.LENGTH_SHORT)
                    .show()
                // if only one file is selected then you can directly access by using
                val oneFile = selectedFilesPath[0] // for single file
                val firstFile = File(oneFile)
                uploadProPicFileAfterChoosing(firstFile)
            } else {
                Toast.makeText(context, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun uploadProPicFileAfterChoosing(firstFile: File) {

        val filePart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "file",
            firstFile.name,
            RequestBody.create(MediaType.parse("image/*"), firstFile)
        );

        mainActivityViewModel.uploadPicture1(firstFile)
            .observe(viewLifecycleOwner, Observer {
                Toast.makeText(requireContext(), "succeed ${it.data.file.url}", Toast.LENGTH_SHORT)
                    .show()
            })

    }

    private fun uploadProfilePictureByFileStackObserver(): Observer<in UploadPicFileStackModel> {
        return Observer {
            Log.i(TAG, "uploadProfilePictureByFileStackObserver: ${it.url}")
            Toast.makeText(requireContext(), "succeed", Toast.LENGTH_SHORT).show()
            Glide.with(requireContext())
                .load(it.url)
                .into(accountProfilePictureImageView)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_READ_EXTERNAL_REQ_CODE) {
            listener.onProfilePicClickListener(binding.fragmentAccountImageView)
        }
    }
}