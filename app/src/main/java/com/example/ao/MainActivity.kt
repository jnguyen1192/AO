package com.example.ao

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi

import java.io.File

import java.text.SimpleDateFormat
import java.util.*
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    private lateinit var layout: RelativeLayout
    fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentExcelFileName():String {
        val date = getCurrentDateTime()
        val d = date.toString("yyyy/MM/dd HH:mm:ss")
        //Log.v("Hola", d);
        // default format is DateTimeFormatter.ISO_DATE_TIME
        val a = d.toString().substring(0, 4)
        val m = d.toString().substring(5, 7)
        val j = d.toString().substring(8, 10)
        val h = d.toString().substring(11, 13).toInt()
        //println(d)
        if (h < 14) return a + m + j + "09" else return a + m + j + "14"
    }

    fun createCSV(filename: String) {
        val path = "/sdcard/donnerie"
        val root = Environment.getExternalStorageDirectory().toString()
        var file = File(root+"/"+filename)
        Log.d("Hola",root)
        //delete any file object with path and filename that already exists
        //fileOut.delete()

        //create a new file
        file.createNewFile()

        //append the header and a newline
        file.appendText("objet;poids (kg);entree;sortie;jeté\n", Charsets.UTF_8)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
        }


        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.relativeLayout)
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                Toast.makeText(this@MainActivity, "Swipe Left gesture detected",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                Toast.makeText(
                    this@MainActivity,
                    "Swipe Right gesture detected",
                    Toast.LENGTH_SHORT
                ).show()
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwipeUp() {
                super.onSwipeUp()
                Toast.makeText(this@MainActivity, "Swipe up gesture detected" + getCurrentExcelFileName(), Toast.LENGTH_SHORT)
                    .show()
                // TODO Create if not exist xslx
                val filename = getCurrentExcelFileName()
                createCSV(filename + ".csv")
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                Toast.makeText(this@MainActivity, "Swipe down gesture detected", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}