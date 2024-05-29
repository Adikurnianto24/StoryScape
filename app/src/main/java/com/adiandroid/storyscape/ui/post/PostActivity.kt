package com.adiandroid.storyscape.ui.post

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.adiandroid.storyscape.R
import com.adiandroid.storyscape.databinding.ActivityPostBinding
import com.adiandroid.storyscape.ui.home.MainActivity
import com.adiandroid.storyscape.utility.createCustomTempFile
import com.adiandroid.storyscape.utility.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding
    private val postViewModel: PostViewModel by viewModels()
    private lateinit var currentPhotoPath: String
    private var getFile: File? = null

    private var storyLatitude: Double = 0.0
    private var storyLongitude: Double = 0.0

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        postViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        postViewModel.isError.observe(this) {
            Log.d("test", "Gagal upload")
        }

        checkPermission()
        fetchLocation()

        startGallery()
        startCamera()
        postStoryButton()
        backButton()
    }

    private fun fetchLocation() {
        val task = fusedLocationProviderClient.lastLocation

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }
        task.addOnSuccessListener {
            if (it != null) {
                storyLongitude = it.longitude
                storyLatitude = it.latitude
                Log.d("location tracker : ", "${it.longitude} , ${it.latitude}")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_PERMISSIONS) {
            if (!permissionGranted()) {
                Toast.makeText(this@PostActivity, R.string.no_access, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun permissionGranted() = PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission() {
        if (!permissionGranted()) {
            ActivityCompat.requestPermissions(
                this@PostActivity,
                PERMISSIONS,
                CODE_PERMISSIONS
            )
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            getFile = myFile
            val result = BitmapFactory.decodeFile(myFile.path)

            binding.storyPictPreview.setImageBitmap(result)
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun startCamera() {
        binding.camBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.resolveActivity(packageManager)

            createCustomTempFile(applicationContext).also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this@PostActivity,
                    "com.adiandroid.storyscape.mycamera",
                    it
                )
                currentPhotoPath = it.absolutePath
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                launcherIntentCamera.launch(intent)
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@PostActivity)
            getFile = myFile
            binding.storyPictPreview.setImageURI(selectedImg)
        }
    }

    private fun startGallery() {
        binding.galleryBtn.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            val picImage = Intent.createChooser(intent, "Select An Image")
            launcherIntentGallery.launch(picImage)
        }
    }

    private fun postStoryButton() {
        binding.postBtn.setOnClickListener {
            val description = binding.descEt.text.toString()
            if (!TextUtils.isEmpty(description) && getFile != null) {
                lifecycleScope.launch {
                    showProgressBar()
                    try {
                        postViewModel.postStory(
                            getFile!!,
                            description,
                            storyLatitude,
                            storyLongitude
                        )
                        Log.d("testPost", "isi postingan : $description , $storyLatitude, $storyLongitude")
                    } finally {
                        hideProgressBar()
                        showSuccessDialog()
                        delay(SPACE_TIME)
                        goToMain()
                    }
                }
            } else {
                Toast.makeText(this, "Description is empty or file is not selected", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun goToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Upload Successful")
            .setMessage("Your post has been successfully uploaded.")
            .setPositiveButton("OK") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }
    private fun showProgressBar() {
        binding.root.visibility = View.INVISIBLE
        binding.loadingLayout.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.loadingLayout.visibility = View.INVISIBLE
    }

    private fun backButton() {
        binding.backHome.setOnClickListener {
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        val PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        const val CODE_PERMISSIONS = 10
        const val SPACE_TIME = 1000L
    }
}