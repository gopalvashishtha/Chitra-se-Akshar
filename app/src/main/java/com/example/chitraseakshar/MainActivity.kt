package com.example.chitraseakshar

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
//import com.google.firebase.ml.vision.FirebaseVision
//import com.google.firebase.ml.vision.common.FirebaseVisionImage
//import com.google.firebase.ml.vision.text.FirebaseVisionText
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity() {
    companion object{
        private val TAG = MainActivity::class.simpleName
    }
    private lateinit var imgView: ImageView
    private lateinit var tvText: TextView
    private lateinit var image: InputImage
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imgView = findViewById(R.id.imgView)
        tvText = findViewById(R.id.tvText)
        progressBar = findViewById(R.id.progressBar)
        imgView.setOnClickListener {
            openImagePicker()
        }


    }

    private fun openImagePicker() {
        ImagePicker.with(this)
            .crop()	    			//Crop image(Optional), Check Customization for more option
            .compress(1024)			//Final image size will be less than 1 MB(Optional)
            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
            .start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val fileUri = data?.data
            Log.d(TAG, "onActivityResult:  fileUri:" +fileUri)
            imgView.setImageURI(fileUri)

            if(fileUri != null){  //Image processing
                processImage(fileUri)
            }


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImage(fileUri: Uri) {
        tvText.text = ""
        progressBar.visibility = View.VISIBLE
        try {
            image = InputImage.fromFilePath(this, fileUri)
            val recognizer = TextRecognition.getClient()

            val result = recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    // Task completed successfully
                   Log.d(TAG, "processingImage: success")

                    val resultText = visionText.text // this resultText now contains our extracted text from image
                    Log.d(TAG, "processingImage: extractedText:" +resultText)
                    if(TextUtils.isEmpty(resultText)){
                        Toast.makeText(this, "No text found in the image!", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                    }
                    else{
                        progressBar.visibility = View.GONE
                        tvText.text = resultText
                    }


//                    for (block in visionText.textBlocks) { // these loops are for individual paragraphs, words or lines
//                        val blockText = block.text
//                        val blockCornerPoints = block.cornerPoints
//                        val blockFrame = block.boundingBox
//                        for (line in block.lines) {
//                            val lineText = line.text
//                            val lineCornerPoints = line.cornerPoints
//                            val lineFrame = line.boundingBox
//                            for (element in line.elements) {
//                                val elementText = element.text
//                                val elementCornerPoints = element.cornerPoints
//                                val elementFrame = element.boundingBox
//                            }
//                        }
//                    }




                }
                .addOnFailureListener { e ->
                    // Task failed with an exception
                    Log.e(TAG, "processingImage: failure:"+e.message)
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}