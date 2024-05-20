package com.nov.storyapp.view.story

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.util.Pair
import com.nov.storyapp.R
import com.nov.storyapp.view.camera.CameraActivity

import com.nov.storyapp.databinding.ActivityStoryBinding
import com.nov.storyapp.helper.ResultState
import com.nov.storyapp.helper.ViewModelFactory
import com.nov.storyapp.helper.reduceFileImage
import com.nov.storyapp.helper.uriToFile
import com.nov.storyapp.view.home.HomeActivity
import com.nov.storyapp.view.main.MainActivity
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
        enableEdgeToEdge()
        supportActionBar?.apply {
            title = getString(R.string.back)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
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
            if (currentImageUri == null || binding.edDescription.text.toString().isEmpty()) {
                Toast.makeText(
                    this@StoryActivity,
                    getString(R.string.empty_photo_or_description),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.getDataLogin().observe(this) { loginData ->
                    if (loginData.isLogin) {
                        postStory()
                    } else {
                        postStoryGuest()
                    }
                }
            }
        }
    }

    private fun postStory() {
        currentImageUri?.let {
            val imageFile = uriToFile(it, this@StoryActivity)
            try {
                val reducedFile = imageFile.reduceFileImage()
                val description = binding.edDescription.text.toString()

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())

                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    reducedFile.name,
                    requestImageFile
                )

                viewModel.postStory(multipartBody, requestBody).observe(this) {
                    when (it) {
                        is ResultState.Error -> {
                            Toast.makeText(
                                this@StoryActivity, getString(R.string.error_post_story), Toast.LENGTH_SHORT
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
                            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@StoryActivity,
                                Pair(binding.ivStory, "logo"),
                                Pair(binding.buttonGallery, "button1"),
                                Pair(binding.buttonCamera, "button2"),
                                Pair(binding.edDescription, "text"),
                                Pair(binding.buttonSubmit, "button3")
                            )
                            startActivity(intent, optionsCompat.toBundle())
                            finish()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@StoryActivity, getString(R.string.error_reduce_image), Toast.LENGTH_SHORT
                ).show()
                Log.e("StoryActivity", "Error reducing image file", e)
            }
        }
    }

    private fun postStoryGuest() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this@StoryActivity)
            try {
                val reducedFile = imageFile.reduceFileImage()
                val descriptionText = binding.edDescription.text.toString()

                val requestBody = descriptionText.toRequestBody("text/plain".toMediaType())
                val requestImageFile = reducedFile.asRequestBody("image/jpeg".toMediaType())

                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    reducedFile.name,
                    requestImageFile
                )

                viewModel.postStoryGuest(multipartBody, requestBody).observe(this) { result ->
                    when (result) {
                        is ResultState.Error -> {
                            showLoading(false)
                            Toast.makeText(
                                this@StoryActivity,
                                getString(R.string.error_post_story),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        is ResultState.Loading -> {
                            showLoading(true)
                        }
                        is ResultState.Success -> {
                            showLoading(false)
                            val intent = Intent(this@StoryActivity, MainActivity::class.java)
                            val optionsCompat: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this@StoryActivity,
                                Pair(binding.ivStory, "logo")
                            )
                            startActivity(intent, optionsCompat.toBundle())
                            finish()
                            Toast.makeText(
                                this@StoryActivity,
                                result.data.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@StoryActivity, getString(R.string.error_reduce_image), Toast.LENGTH_SHORT
                ).show()
                Log.e("StoryActivity", "Error reducing image file", e)
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
            Toast.makeText(this, getString(R.string.permission_success), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, CameraActivity::class.java)
            cameraXLauncher.launch(intent)
        } else {
            Toast.makeText(this, getString(R.string.permission_failed), Toast.LENGTH_SHORT).show()
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
