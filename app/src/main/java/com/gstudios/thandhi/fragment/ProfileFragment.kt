package com.gstudios.thandhi.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.firebase.ui.auth.AuthUI
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.gstudios.thandhi.LanguageConstants
import com.gstudios.thandhi.R
import com.gstudios.thandhi.SigninActivity
import com.gstudios.thandhi.glide.GlideApp
import com.gstudios.thandhi.util.FirestoreUtil
import com.gstudios.thandhi.util.StorageUtil
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_profile.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    private val rcSelectImage = 25
    private lateinit var imageBytes: ByteArray
    private var isPictureChanged = false
    private lateinit var spinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        spinner = view.findViewById(R.id.languageSpinner)

        view.apply {
            profile_picture.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(
                        Intent.EXTRA_MIME_TYPES,
                        arrayOf("image/jpeg", "image/jpg", "image/png")
                    )
                }
                startActivityForResult(
                    Intent.createChooser(intent, "Select Image"),
                    rcSelectImage
                )
            }
            btn_save.setOnClickListener {
                val language =
                    if (autoTranslateCheckBox.isChecked) {
                        spinner.selectedItemPosition.toString()
                    } else {
                        "11"
                    }

                if (::imageBytes.isInitialized) {
                    StorageUtil.uploadProfilePicture(imageBytes) { imagePath ->
                        FirestoreUtil.updateCurrentUser(
                            edit_text_name.text.toString(),
                            edit_text_bio.text.toString(),
                            imagePath,
                            autoTranslateCheckBox.isChecked,
                            language
                        )
                    }
                } else {
                    FirestoreUtil.updateCurrentUser(
                        edit_text_name.text.toString(),
                        edit_text_bio.text.toString(),
                        null,
                        autoTranslateCheckBox.isChecked,
                        language
                    )
                }
                toast("saved")
            }
            btn_sign_out.setOnClickListener {
                AuthUI.getInstance().signOut(this@ProfileFragment.context!!)
                    .addOnCompleteListener {
                        startActivity(intentFor<SigninActivity>().newTask().clearTask())
                    }
            }
            setSpinner()
        }

        return view
    }

    private fun setSpinner() {
        val allLanguage = ArrayList<String>()
        for (i in 0 until FirebaseTranslateLanguage.getAllLanguages().count())
            allLanguage.add(LanguageConstants.AllLanguage[i])
        val adapter =
            ArrayAdapter<String>(activity!!, android.R.layout.simple_spinner_item, allLanguage)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(11)

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == rcSelectImage && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imagePath = data.data!!
            lateinit var bitmap: Bitmap
            imagePath.let {
                bitmap = if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(activity!!.contentResolver, imagePath)
                } else {
                    val source =
                        ImageDecoder.createSource(activity!!.contentResolver, imagePath)
                    ImageDecoder.decodeBitmap(source)
                }
            }
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            imageBytes = outputStream.toByteArray()
            GlideApp.with(this)
                .load(imageBytes)
                .into(profile_picture)
            isPictureChanged = true
        }
    }

    override fun onStart() {
        super.onStart()
        FirestoreUtil.getCurrentUser {
            if (this@ProfileFragment.isVisible) {
                edit_text_name.setText(it.name)
                edit_text_bio.setText(it.bio)
                if (!isPictureChanged && it.profilePicturePath != null) {
                    GlideApp.with(this)
                        .load(StorageUtil.pathToReference(it.profilePicturePath))
                        .placeholder(R.drawable.ic_account_circle_black_24dp)
                        .into(profile_picture)
                }
                if (it.autoTranslate) {
                    autoTranslateCheckBox.isChecked = true
                    spinner.setSelection(it.language.toInt())
                } else {
                    autoTranslateCheckBox.isChecked = false
                    spinner.setSelection(11)
                }
            }
        }
    }
}
