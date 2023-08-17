package com.example.submissionstoryapp.ui.addstory

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import androidx.paging.ExperimentalPagingApi
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.PreferencesHelper
import com.example.submissionstoryapp.databinding.ActivityAddStoryBinding
import com.example.submissionstoryapp.utils.BaseActivity
import com.example.submissionstoryapp.utils.MediaUtility
import com.example.submissionstoryapp.utils.MediaUtility.reduceFileImage
import com.example.submissionstoryapp.utils.MediaUtility.uriToFile
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.inject.Inject

enum class ImageType { LANDSCAPE, PORTRAIT }

@ExperimentalPagingApi
@AndroidEntryPoint
class AddStoryActivity : BaseActivity<ActivityAddStoryBinding>() {

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile: File? = null
    private var token: String = ""
    private var location: Location? = null

    private val viewModel: AddStoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        token = preferencesHelper.token

        clickButton()
    }

    override fun inflateBinding(layoutInflater: LayoutInflater): ActivityAddStoryBinding {
        return ActivityAddStoryBinding.inflate(layoutInflater)
    }

    private fun clickButton() {
        binding.apply {
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
            btnCamera.setOnClickListener { startIntentCamera() }
            btnGallery.setOnClickListener { startIntentGallery() }
            btnUpload.setOnClickListener { uploadStory() }
            btnImagePicker.setOnClickListener { startImagePicker(ImageType.LANDSCAPE) }
            btnImagePickerPt.setOnClickListener { startImagePicker(ImageType.PORTRAIT) }
            checkBoxLocation.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    getUserLocation()
                } else {
                    location = null
                }
            }
        }
    }

    private fun startImagePicker(imageType: ImageType) {
        ImagePicker.with(this)
            .galleryOnly()
            .galleryMimeTypes( // Exclude gif images
                mimeTypes = arrayOf(
                    "image/png",
                    "image/jpg",
                    "image/jpeg",
                ),
            )
            .apply {
                if (imageType == ImageType.LANDSCAPE) crop(16f, 9f) else crop(9f, 16f)
            }
            .compress(1024) // Final image size will be less than 1 MB(Optional)
//            .maxResultSize(1080, 1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            when (resultCode) {
                Activity.RESULT_OK -> {
                    // Image Uri will not be null for RESULT_OK
                    val fileUri = data?.data!!

                    uriToFile(fileUri, this).also { getFile = it }
                    binding.ivStory.setImageURI(fileUri)
                    Toast.makeText(this, fileUri.toString(), Toast.LENGTH_SHORT).show()
                }
                ImagePicker.RESULT_ERROR -> {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }

    private fun getUserLocation() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    this.location = location
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.minta_permission),
                        Toast.LENGTH_SHORT,
                    ).show()

                    binding.checkBoxLocation.isChecked = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ),
            )
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getUserLocation()
            }
            else -> {
                Snackbar
                    .make(
                        binding.root,
                        getString(R.string.izin_lokasi_ditolak),
                        Snackbar.LENGTH_SHORT,
                    )
                    .setActionTextColor(getColor(R.color.white))
                    .setAction(getString(R.string.ubah_izin_lokasi)) {

                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).also { intent ->
                            val uri = Uri.fromParts("package", packageName, null)
                            intent.data = uri

                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    .show()

                binding.checkBoxLocation.isChecked = false
            }
        }
    }

    private fun uploadStory() {
        showLoading(true)

        val etDescription = binding.etDesc
        var isValid = true

        if (etDescription.text.toString().isBlank()) {
            showSnackbar(getString(R.string.empty_desc))
            isValid = false
        }

        if (getFile == null) {
            showSnackbar(getString(R.string.empty_story))
            isValid = false
        }

        if (isValid) {
            lifecycleScope.launchWhenStarted {
                launch {
                    val description =
                        etDescription.text.toString().toRequestBody("text/plain".toMediaType())
                    val file = reduceFileImage(getFile as File)
                    val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                        "photo",
                        file.name,
                        requestImageFile,
                    )

                    var lat: RequestBody? = null
                    var lon: RequestBody? = null
                    if (location != null) {
                        lat = location?.latitude.toString().toRequestBody("text/plain".toMediaType())
                        lon = location?.longitude.toString().toRequestBody("text/plain".toMediaType())
                    }
                    viewModel.uploadImage(imageMultipart, description, lat, lon).collect { response ->
                        response.onSuccess {
                            showSnackbar(getString(R.string.upload_success))
                            finish()
                        }

                        response.onFailure {
                            showLoading(false)
                            showSnackbar(getString(R.string.upload_fail))
                        }
                    }
                }
            }
        } else {
            showLoading(false)
        }
    }

    private fun showLoading(isShow: Boolean) {
        binding.progressBar.isVisible = isShow
    }

    private fun startIntentGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.pilih_gambar))
        launcherIntentGallery.launch(chooser)
    }

    private fun startIntentCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        MediaUtility.createTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this,
                "com.example.submissionstoryapp",
                it,
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_SHORT,
        ).show()
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val file = File(currentPhotoPath).also { getFile = it }
            val os: OutputStream

            val bitmap = BitmapFactory.decodeFile(getFile?.path)
            val exif = ExifInterface(currentPhotoPath)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED,
            )

            val rotatedBitmap: Bitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> TransformationUtils.rotateImage(bitmap, 90)
                ExifInterface.ORIENTATION_ROTATE_180 -> TransformationUtils.rotateImage(bitmap, 180)
                ExifInterface.ORIENTATION_ROTATE_270 -> TransformationUtils.rotateImage(bitmap, 270)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }

            run {
                try {
                    os = FileOutputStream(file)
                    rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, os)
                    os.flush()
                    os.close()

                    getFile = file
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            binding.ivStory.setImageBitmap(rotatedBitmap)
            binding.ivStory.scaleType = ImageView.ScaleType.FIT_XY
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            uriToFile(selectedImg, this).also { getFile = it }

            binding.ivStory.setImageURI(selectedImg)
            binding.ivStory.scaleType = ImageView.ScaleType.FIT_XY
        }
    }
}
