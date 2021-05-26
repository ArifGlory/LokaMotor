package com.tapisdev.lokamotor.activity.pengguna

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.tapisdev.lokamotor.MainActivity
import com.tapisdev.lokamotor.R
import com.tapisdev.lokamotor.base.BaseActivity
import com.tapisdev.lokamotor.model.UserPreference
import com.tapisdev.lokamotor.util.PermissionHelper
import kotlinx.android.synthetic.main.activity_profil.*

class ProfilActivity : BaseActivity(),PermissionHelper.PermissionListener {

    var editMode = 0
    var TAG_EDIT = "editProfil"
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var  permissionHelper : PermissionHelper
    var fotoBitmap : Bitmap? = null
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        mUserPref = UserPreference(this)


        btnLogout.setOnClickListener {
            logout()
            auth.signOut()

            startActivity(Intent(this, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }

        updateUI()
    }

    fun updateUI(){
        if (editMode == 0){
            edUserName.setText(mUserPref.getName())
            edMobileNumber.setText(mUserPref.getPhone())
            if (!mUserPref.getFoto().equals("")){
                Glide.with(this)
                    .load(mUserPref.getFoto())
                    .into(ivProfile)
            }

            edUserName.isEnabled = false
            edMobileNumber.isEnabled = false
            ivGallery.isEnabled = false
        }else{
            edUserName.isEnabled = true
            edMobileNumber.isEnabled = true
            ivGallery.isEnabled = true

            edUserName.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_corner_white_10))
            edMobileNumber.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_corner_white_10))

            btnEdit.setText("Simpan Perubahan")
        }

    }

    override fun onPermissionCheckDone() {

    }
}
