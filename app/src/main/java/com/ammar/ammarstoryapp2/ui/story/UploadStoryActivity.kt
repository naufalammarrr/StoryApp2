package com.ammar.ammarstoryapp2.ui.story

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ammar.ammarstoryapp2.response.Result
import com.ammar.ammarstoryapp2.R
import com.ammar.ammarstoryapp2.databinding.ActivityUploadStoryBinding
import com.ammar.ammarstoryapp2.ui.main.MainActivity
import com.ammar.ammarstoryapp2.utils.ViewModelFactory
import com.ammar.ammarstoryapp2.utils.getImageUri
import com.ammar.ammarstoryapp2.utils.reduceFileImage
import com.ammar.ammarstoryapp2.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class UploadStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadStoryBinding

    private var currentImageUri: Uri? = null

    private val viewModel by viewModels<UploadStoryViewModel> {
        ViewModelFactory.getInstance(application)
    }

    private var locationSwitcher: Boolean = false
    private var currentLocation: Location? = null

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions: Map<String, Boolean> ->
        val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] ?: false
        val fineLocationPermissionGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        when {
            cameraPermissionGranted -> Toast.makeText(
                this,
                "Camera permission granted",
                Toast.LENGTH_LONG
            ).show()

            fineLocationPermissionGranted -> Toast.makeText(
                this,
                "Location permission granted",
                Toast.LENGTH_LONG
            ).show()

            !cameraPermissionGranted -> Toast.makeText(
                this,
                "Camera permission is required",
                Toast.LENGTH_LONG
            ).show()

            !fineLocationPermissionGranted -> Toast.makeText(
                this,
                "Fine location permission is required",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun allPermissionsGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.upload_story)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.btnUpload.setOnClickListener { uploadImage() }

        val locationSwitch = binding.switchGetLocation

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationSwitch.setOnCheckedChangeListener { _, isChecked ->
            locationSwitcher = isChecked
            if (isChecked) {
                requestLocationUpdates()
            } else {
                binding.tvLocation.text = null
            }

        }
    }

    private fun requestLocationUpdates() {
        if (locationSwitcher) {
            if (allPermissionsGranted()) {
                try {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { location: Location? ->
                            location?.let {
                                currentLocation = it
                                val latitude = it.latitude
                                val longitude = it.longitude
                                Log.d("Location", "Latitude: $latitude, Longitude: $longitude")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("Location", "Error getting location: ${e.message}")
                        }
                } catch (e: SecurityException) {
                    Log.e("Location", "Location permission denied: ${e.message}")
                }
            } else {
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        Log.d("Camera", "startCamera() called")
        currentImageUri = getImageUri(this)
        Log.d("Camera", "Image URI: $currentImageUri")
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.imageView.setImageURI(it)
        }
    }

    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.edRegisterName.text.toString()
            var lon: Double? = null
            var lat: Double? = null


            if (locationSwitcher) {
                lon = currentLocation?.longitude
                lat = currentLocation?.latitude
            }

            showLoading(true)

            viewModel.uploadImage(imageFile, description, lat, lon).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            showLoading(true)
                        }

                        is Result.Success -> {
                            showToast(result.data.message)
                            showLoading(false)
                            val intent =
                                Intent(this@UploadStoryActivity, MainActivity::class.java).apply {
                                    flags =
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                            startActivity(intent)
                        }

                        is Result.Error -> {
                            showToast(result.error)
                            showLoading(false)
                        }
                    }
                }
            }
        } ?: showToast(getString(R.string.empty_image_warning))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}