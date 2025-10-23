package com.tech.sid

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.github.dhaval2404.imagepicker.util.FileUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException

class ImagePickerHelper(
    private val fragment: Fragment,
    private val onImageSelected: (MultipartBody.Part?, Uri?) -> Unit
) {

    private var photoFile: File? = null
    private var photoUri: Uri? = null
    private var action: ImagePickerAction? = null

    // Only request CAMERA permission now
    private val permissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            action?.let {
                when (it) {
                    ImagePickerAction.Camera -> launchCamera()
                    ImagePickerAction.Gallery -> pickImageFromGallery()
                }
            }
        } else {
            showToast("Camera permission denied")
        }
    }

    // Android Photo Picker for gallery (no storage permission required)
    private val pickVisualMediaLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            val multipartPart = convertMultipartPart(it)
            onImageSelected(multipartPart, it)
        } ?: showToast("No media selected")
    }

    // Camera capture launcher
    private val cameraLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && photoFile?.exists() == true) {
                photoUri?.let {
                    val multipartPart = convertMultipartPartCamera(it)
                    onImageSelected(multipartPart, it)
                }
            }
        }


    // Public function to pick image/video
    fun pickImage(action: ImagePickerAction) {
        this.action = action
        if (action is ImagePickerAction.Camera) {
            // Request only CAMERA permission
            permissionLauncher.launch(android.Manifest.permission.CAMERA)
        } else {
            // Gallery action: no permissions required
            pickImageFromGallery()
        }
    }

    private fun pickImageFromGallery() {
        pickVisualMediaLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            try {
                photoFile = AppUtils.createImageFile1(fragment.requireContext())
                photoUri = photoFile?.let {
                    FileProvider.getUriForFile(
                        fragment.requireContext(),
                        "${fragment.requireContext().packageName}.fileProvider",
                        it
                    )
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                cameraLauncher.launch(intent)
            } catch (ex: IOException) {
                ex.printStackTrace()
                showToast("Error creating image file")
            }
        } else {
            showToast("No camera app available")
        }
    }

    private fun convertMultipartPartCamera(imageUri: Uri): MultipartBody.Part? {
        val file = File(photoFile?.absolutePath ?: return null)
        if (!file.exists()) return null
        return MultipartBody.Part.createFormData(
            "profileImage",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun convertMultipartPart(imageUri: Uri): MultipartBody.Part? {
        val file = FileUtil.getTempFile(fragment.requireActivity(), imageUri) ?: return null
        val fileName =
            "${file.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
        val newFile = File(file.parent, fileName)
        file.renameTo(newFile)
        return MultipartBody.Part.createFormData(
            "profileImage",
            newFile.name,
            newFile.asRequestBody("image/*".toMediaTypeOrNull())
        )
    }

    private fun showToast(message: String) {
        Toast.makeText(fragment.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    sealed class ImagePickerAction {
        object Gallery : ImagePickerAction()
        object Camera : ImagePickerAction()
    }
}
