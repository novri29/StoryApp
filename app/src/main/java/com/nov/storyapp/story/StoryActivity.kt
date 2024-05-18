package com.nov.storyapp.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.nov.storyapp.camera.CameraActivity

import com.nov.storyapp.databinding.ActivityStoryBinding
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.helper.reduceFileImage
import com.nov.storyapp.helper.uriToFile
import com.nov.storyapp.home.HomeActivity
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoryBinding
    private var currentImageUri: Uri? = null

    private val viewModelFactory = ViewModelFactory.getInstance(this@StoryActivity)
    private val viewModel: StoryViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonGallery.setOnClickListener {
            galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.buttonCamera.setOnClickListener {
            if (!cameraPermission()) {
                permissionLauncher.launch(REQUIRED_PERMISSION)
            } else {
                val intent = Intent(this, CameraActivity::class.java)
                cameraXLauncher.launch(intent)
            }
        }

        binding.buttonSubmit.setOnClickListener {
            viewModel.getDataLogin().observe(this) { loginData ->
                if (loginData.isLogin) {
                    postStory()
                }
            }
        }
    }

    private fun postStory() {
        currentImageUri?.let {
            val imageFile = uriToFile(it, this@StoryActivity).reduceFileImage()
            val description = binding.edDescription.text.toString()

            val requestBody = description.toRequestBody("text/plain".toMediaType())
            val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())

            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            viewModel.postStory(multipartBody, requestBody).observe(this) {
                when (it) {
                    is ResultState.Error -> {
                        Toast.makeText(
                            this@StoryActivity, "Error Post Story", Toast.LENGTH_SHORT
                        ).show()
                        showLoading(false)
                    }
                    ResultState.Loading -> {
                        showLoading(true)
                    }
                    is ResultState.Success -> {
                        showLoading(false)
                        Toast.makeText(
                            this@StoryActivity, it.data.message, Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    private val cropImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK && it.data != null) {
            val result = UCrop.getOutput(it.data!!)
            if (result != null && result.encodedSchemeSpecificPart != null) {
                val bitmap = BitmapFactory.decodeFile(result.encodedSchemeSpecificPart)
                binding.ivStory.setImageBitmap(bitmap)
                currentImageUri = result
            }
        } else if (it.resultCode == UCrop.RESULT_ERROR) {
            val error = UCrop.getError(it.data!!)
            Log.e("StoryActivity", error.toString())
        }
    }

    private fun ucropImage(imageUri: Uri) {
        val destinationUri = Uri.fromFile(uriToFile(imageUri, this@StoryActivity))
        val options = UCrop.Options().apply {
            setFreeStyleCropEnabled(true)
            setCompressionQuality(80)
        }
        val intent = UCrop.of(imageUri, destinationUri)
            .withOptions(options)
            .getIntent(this)

        cropImageLauncher.launch(intent)
    }

    private fun cameraPermission() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            Toast.makeText(this, "Permission Berhasil", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CameraActivity::class.java)
            cameraXLauncher.launch(intent)
        } else {
            Toast.makeText(this, "Permission Gagal", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraXLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CameraActivity.CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        it?.let {
            currentImageUri = it
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            ucropImage(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) android.view.View.VISIBLE else android.view.View.GONE
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
