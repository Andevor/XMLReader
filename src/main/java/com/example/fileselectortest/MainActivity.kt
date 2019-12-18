package com.example.fileselectortest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import javax.xml.parsers.DocumentBuilderFactory


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun performFileSearch(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            resultData?.data?.also { uri ->
                x = uri
                super.onActivityResult(requestCode, resultCode, resultData)
            }
        }
    }


    @Throws(IOException::class)
    private fun readTextFromUri(uri: Uri): String {
        val stringBuilder = StringBuilder()
        contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                var line: String? = reader.readLine()
                while (line != null) {
                    stringBuilder.append(line)
                    line = reader.readLine()
                }
            }
        }
        return stringBuilder.toString()
    }

    fun read(view: View) {
        var doc: Document? = null
        contentResolver.openInputStream(x)?.use { inputStream ->
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
        }
        doc!!.documentElement.normalize()
        val nlist = doc!!.documentElement.getElementsByTagName("person")
        var names = ""
        for (i in 0..nlist.length - 1) {
            val elem = nlist.item(i) as Element
            names += elem.getElementsByTagName("name").item(0).textContent
            names += "\n"
        }
        tv_file_path.text = names
    }

    companion object {
        private const val READ_REQUEST_CODE: Int = 42
        var x: Uri = Uri.EMPTY
    }

}
