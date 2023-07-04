package com.example.wordtopdf

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.wordtopdf.ui.theme.WordtopdfTheme
import com.pdftron.common.PDFNetException
import com.pdftron.pdf.Convert
import com.pdftron.pdf.PDFDoc
import com.pdftron.pdf.config.ViewerConfig
import com.pdftron.pdf.controls.DocumentActivity
import com.pdftron.sdf.SDFDoc
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordtopdfTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")

                    val config = ViewerConfig.Builder()
                        .openUrlCachePath(this.cacheDir.absolutePath).build()

                    // from assets
                    val uri = Uri.parse("file:///android_asset/chinese-zh.doc")

                    // intent builder
                    val intent: Intent = DocumentActivity.IntentBuilder.fromActivityClass(
                        this,
                        DocumentActivity::class.java
                    )
                        .withUri(uri)
                        .usingConfig(config)
                        .usingTheme(R.style.Theme_WordToPdf)
                        .build()
                    startActivity(intent)

                    val outputFilename = "chinese-zh.pdf"
                    simpleDocxConvert("chinese-zh.doc", outputFilename)
                }
            }
        }
    }

    private fun simpleDocxConvert(inputFilename: String, outputFilename: String) {
        try {
            val pdfDoc = PDFDoc()
            // Get the AssetManager from the current context (e.g., MainActivity)
            val assetManager = applicationContext.assets

            // Open the file using the AssetManager
            val inputStream = assetManager.open(inputFilename)

            // Save the file to a temporary location in the device's file system
            val temporaryFile = File(cacheDir, inputFilename)
            val outputStream = FileOutputStream(temporaryFile)
            inputStream.copyTo(outputStream)

            // Convert the document using the temporary file as input
            Convert.officeToPdf(pdfDoc, temporaryFile.absolutePath, null)
            // Get the external storage directory for saving the output file
            val externalFilesDir = getExternalFilesDir(null)
            val outputFilePath = File(externalFilesDir, outputFilename).absolutePath

            pdfDoc.save(outputFilePath, SDFDoc.SaveMode.INCREMENTAL, null)
            Log.d("simpleDocConvert", "Done conversion: $outputFilePath")
        } catch (e: PDFNetException) {
            val errorMessage = StringBuilder()
            errorMessage.append("Error converting document:\n")
            errorMessage.append(e.message).append("\n")

            for (element in e.stackTrace) {
                errorMessage.append(element.toString()).append("\n")
            }
            Log.d("simpleDocConvert", errorMessage.toString())
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WordtopdfTheme {
        Greeting("Android")
    }
}