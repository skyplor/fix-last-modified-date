package com.crafttechlabs.android.fixlastmodifieddate.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import com.crafttechlabs.android.fixlastmodifieddate.R
import com.crafttechlabs.android.fixlastmodifieddate.utils.DateUtils
import com.crafttechlabs.android.fixlastmodifieddate.utils.FileUtils
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import java.io.File


private const val PICK_IMAGE: Int = 1000
private const val EXTERNAL_STORAGE: Int = 1001

class MainActivity : AppCompatActivity() {

    private val filesSelected: ArrayList<File> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_select_images.setOnClickListener({
            if (isExternalStoragePermissionGranted()) {
                pickImage()
            }
        })
    }

    private fun isExternalStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Timber.d("Permission is granted")
                true
            } else {
                Timber.d("Permission is revoked")
                ActivityCompat.requestPermissions(this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE)
                false
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Timber.d("Permission is granted")
            true
        }
    }

    private fun pickImage() {
        // Create the ACTION_GET_CONTENT Intent
        val getContentIntent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        getContentIntent.type = "image/*"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            getContentIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        // Only return URIs that can be opened with ContentResolver
        getContentIntent.addCategory(Intent.CATEGORY_OPENABLE)

        val intent = Intent.createChooser(getContentIntent, "Select a file")
//        val intent = Intent(this, FileChooserActivity::class.java)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {

            EXTERNAL_STORAGE -> {
                if (isExternalStoragePermissionGranted()) {
                    pickImage()
                } else {
                    Timber.d("Requires permissions to be granted")
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            if (data.data != null || data.clipData != null) {
                if (data.data != null) {
                    val uri = data.data
                    if (uri != null) {
                        populateFilesSelected(uri)
                    }
                } else {
                    if (data.clipData != null) {
                        val mClipData = data.clipData
                        for (i in 0 until mClipData.itemCount) {
                            val item = mClipData.getItemAt(i)
                            Timber.d(item.toString())
                            Timber.d(item.uri.toString())
                            if (item.uri != null) {
                                populateFilesSelected(item.uri)
                            }
                        }
                    }
                }
                updateLastModifiedDateOfFiles()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun populateFilesSelected(uri: Uri?) {
        val path = FileUtils.getPath(this, uri!!)
        val file = File(path)
        Timber.d("Path: " + path)
        Timber.d(file.name)
        filesSelected.add(file)
    }

    private fun updateLastModifiedDateOfFiles() {
        for (file in filesSelected) {
            updateLastModifiedDate(file)
        }
    }

    private fun updateLastModifiedDate(file: File) {
        val filename = file.nameWithoutExtension
        Timber.d(filename)
        val fields = filename.split('-')
        val dateString = fields[1]
        val date = DateUtils.dateStringToLong("yyyyMMdd", dateString)
        Timber.d(date.toString())
        val success = file.setLastModified(date)
        Timber.d("file modified successfully? " + success)
    }
}
