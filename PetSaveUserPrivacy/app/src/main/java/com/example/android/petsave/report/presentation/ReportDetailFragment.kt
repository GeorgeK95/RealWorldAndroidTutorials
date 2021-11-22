package com.example.android.petsave.report.presentation

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.android.petsave.core.data.preferences.PetSavePreferences
import com.example.android.petsave.databinding.FragmentReportDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_report_detail.*
import java.io.File
import java.net.URL
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import javax.net.ssl.HttpsURLConnection

@AndroidEntryPoint
class ReportDetailFragment : Fragment() {

    companion object {
        private const val API_URL = "https://example.com/?send_report"
        private const val PIC_FROM_GALLERY = 2
        private const val REPORT_APP_ID = 46341L
        private const val REPORT_PROVIDER_ID = 46341L
        private const val REPORT_SESSION_KEY = "session_key_in_next_chapter"
    }

    object ReportTracker {
        var reportNumber = AtomicInteger()
    }

    @Volatile
    private var isSendingReport = false

    private val binding get() = _binding!!
    private var _binding: FragmentReportDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportDetailBinding.inflate(inflater, container, false)

        binding.sendButton.setOnClickListener {
            sendReportPressed()
        }

        binding.uploadPhotoButton.setOnClickListener {
            uploadPhotoPressed()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PIC_FROM_GALLERY -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    val galleryIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                    startActivityForResult(galleryIntent, PIC_FROM_GALLERY)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onPause() {
        requireContext().cacheDir?.deleteRecursively()
        requireContext().externalCacheDir?.deleteRecursively()
        PetSavePreferences(requireContext()).clearPrefs()
        super.onPause()
    }

    private fun setupUI() {
        details_edtxtview.imeOptions = EditorInfo.IME_ACTION_DONE
        details_edtxtview.setRawInputType(InputType.TYPE_CLASS_TEXT)
    }

    private fun sendReportPressed() {
        if (!isSendingReport) {
            isSendingReport = true

            //1. Save report
            var reportString = category_edtxtview.text.toString()
            reportString += " : "
            reportString += details_edtxtview.text.toString()
            val reportID = UUID.randomUUID().toString()

            context?.let { theContext ->
                val file = File(theContext.filesDir?.absolutePath, "$reportID.txt")
                file.bufferedWriter().use {
                    it.write(reportString)
                }
            }

            ReportTracker.reportNumber.incrementAndGet()

            //2. Send report
            val postParameters = mapOf(
                "application_id" to REPORT_APP_ID * REPORT_PROVIDER_ID,
                "report_id" to reportID,
                "report" to reportString
            )
            if (postParameters.isNotEmpty()) {
                //send report
                val connection = URL(API_URL).openConnection() as HttpsURLConnection
                connection.setRequestProperty("Cache-Control", "no-cache")
                connection.defaultUseCaches = false
                connection.useCaches = false
//                webview.clearCache(true)
            }

            isSendingReport = false
            context?.let {
                val report = "Report: ${ReportTracker.reportNumber.get()}"
                val toast = Toast.makeText(
                    it, "Thank you for your report.$report", Toast
                        .LENGTH_LONG
                )
                toast.show()
            }

            val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as
                    InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
        }
    }

    private fun uploadPhotoPressed() {
        context?.let {
            if (ContextCompat.checkSelfPermission(
                    it,
                    READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        READ_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE
                    ), PIC_FROM_GALLERY
                )
            } else {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent, PIC_FROM_GALLERY)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            PIC_FROM_GALLERY ->

                if (resultCode == Activity.RESULT_OK) {

                    //image from gallery
                    val selectedImage = data?.data
                    selectedImage?.let {
                        showUploadedFile(selectedImage)
                    }
                }
            else -> println("Didn't select picture option")
        }
    }

    private fun showUploadedFile(selectedImage: Uri) {
        //get filename
        val fileNameColumn = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)
        val nameCursor = activity?.contentResolver?.query(
            selectedImage, fileNameColumn,
            null, null, null
        )
        nameCursor?.moveToFirst()
        val nameIndex = nameCursor?.getColumnIndex(fileNameColumn[0])
        var filename = ""
        nameIndex?.let {
            filename = nameCursor.getString(it)
        }
        nameCursor?.close()

        //update UI with filename
        upload_status_textview?.text = filename
    }
}
