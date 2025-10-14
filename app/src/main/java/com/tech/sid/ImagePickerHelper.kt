package com.tech.sid

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
    private var action: ImagePickerAction? = null // Store the action

    private val permissionLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            when (action) { // Use stored action
                ImagePickerAction.Gallery -> launchGallery()
                ImagePickerAction.Camera -> launchCamera()
                null -> showToast("No action specified")
            }
        } else {
            showToast("Permission Denied")
        }
    }

    private val galleryLauncher = fragment.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                val multipartPart = convertMultipartPart(it, isFromGallery = true)
                onImageSelected(multipartPart, it)
            }
        }
    }

    //    private val cameraLauncher = fragment.registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK && photoFile?.exists() == true) {
//            photoUri?.let {
//                val multipartPart = convertMultipartPart(it, isFromGallery = false)
//                onImageSelected(multipartPart, it)
//            }
//        }
//    }
    private fun convertMultipartPartCamera(imageUri: Uri): MultipartBody.Part? {
        val filePath = imageUri.path ?: return null
        val file = File(filePath)
        if (!file.exists()) {
            return null
        }
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("profileImage", file.name, requestFile)
    }
    private val cameraLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                if (photoFile?.exists() == true) {
                    val imagePath = photoFile?.absolutePath.toString()
                    val imageUri = imagePath.toUri()
                    imageUri.let {
                        val multipartImage = convertMultipartPartCamera(it)
                        onImageSelected(multipartImage, it)
                    }
                }
            }
        }


    // Generic function to initiate image picking
    fun pickImage(action: ImagePickerAction, permissions: Array<String>) {
        this.action = action // Store the action
        permissionLauncher.launch(permissions)
    }

    private fun launchGallery() {
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply {
                type = "image/*"
            }
        galleryLauncher.launch(intent)
    }

    private fun launchCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(fragment.requireContext().packageManager) != null) {
            try {
                photoFile = AppUtils.createImageFile1(fragment.requireContext())
                photoUri = photoFile?.let {
                    FileProvider.getUriForFile(
                        fragment.requireContext(),
//                        "com.tech.sid.fileProvider",
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

    private fun convertMultipartPart(imageUri: Uri, isFromGallery: Boolean): MultipartBody.Part? {
        return if (isFromGallery) {
            val file = FileUtil.getTempFile(fragment.requireActivity(), imageUri) ?: return null
            val fileName =
                "${file.nameWithoutExtension}_${System.currentTimeMillis()}.${file.extension}"
            val newFile = File(file.parent, fileName)
            file.renameTo(newFile)
            MultipartBody.Part.createFormData(
                "profileImage", newFile.name, newFile.asRequestBody("image/*".toMediaTypeOrNull())
            )
        } else {
            val filePath = imageUri.path ?: return null
            val file = File(filePath)
            if (!file.exists()) return null
            MultipartBody.Part.createFormData(
                "profileImage", file.name, file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(fragment.requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    // Sealed class to define image picking actions
    sealed class ImagePickerAction {
        object Gallery : ImagePickerAction()
        object Camera : ImagePickerAction()
    }
}