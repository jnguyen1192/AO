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
import android.widget.EditText
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Paths
import android.content.Intent
import android.text.Editable
import androidx.constraintlayout.widget.ConstraintLayout


class MainActivity : AppCompatActivity() {
    private lateinit var layout: ConstraintLayout
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun createCSV(filename: String) {
        val root = Environment.getExternalStorageDirectory().toString()
        var file = File(root+"/Donnerie/"+filename)
        //Log.d("Hola",root)

        //Log.d("Hola",file.length().toString())
        //delete any file object with path and filename that already exists
        //fileOut.delete()
        if(file.length().toInt() == 0) {
            var dir = File(root+"/Donnerie/")
            dir.mkdirs()
            //create a new file

            file.createNewFile()

            //append the header and a newline
            file.appendText("objet;poids (kg);entree;sortie;jet??\n", Charsets.UTF_8)
            Log.d("Hola","Created")
        }

    }
    fun addLineCSV(filename: String, line: String) {
        val root = Environment.getExternalStorageDirectory().toString()
        var file = File(root+"/Donnerie/"+filename)

        Log.d("Hola",file.length().toString())

        //append the header and a newline
        file.appendText(line, Charsets.UTF_8)

        Log.d("Hola","Appended")

    }
    fun readCSV(filename: String): ArrayList<String> {
        val root = Environment.getExternalStorageDirectory().toString()
        var file = File(root+"/Donnerie/"+filename)

        Log.d("Hola",file.length().toString())
        var arrayObjectIn = arrayListOf<String>()
        var arrayObjectInFinal = arrayListOf<String>()
        var firstLine = true;
        //append the header and a newline
        file.forEachLine {
            if (firstLine) {
                firstLine = false
            }
            else {
                var split_res = it.split(";")
                var objectName = split_res[0]
                var objectWeight = split_res[1]
                var objectIn = split_res[2]
                if(objectIn == "1") {
                    arrayObjectIn.add(objectName+";"+objectWeight)
                }
            }
        }
        firstLine = true;
        for(objectIn in arrayObjectIn) {
            var isIn = false
            file.forEachLine {
                if (firstLine) {
                    firstLine = false
                }
                else {
                    var split_res = it.split(";")
                    var objectName = split_res[0]
                    var objectWeight = split_res[1]
                    var objectOut = split_res[3]
                    var objectDrop = split_res[4]
                    if((objectOut == "1" ||objectDrop == "1") && objectName+";"+objectWeight == objectIn) {
                        isIn = true
                    }
                }
            }
            if(isIn == false) {
                arrayObjectInFinal.add(objectIn)
            }
        }
        Log.d("Hola","arrayObjectInFinal")
        return arrayObjectInFinal


    }

    fun checkInput(data: String):Boolean {
        if(data == "") {
            return false
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            this.supportActionBar!!.hide()
        } catch (e: NullPointerException) {
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1);
        }


        setContentView(R.layout.activity_main)
        layout = findViewById(R.id.constraintLayout)
        // Create if not exist csv
        val filename = getCurrentExcelFileName()
        createCSV(filename + ".csv")

        val bundle :Bundle ?=intent.extras
        if (bundle!=null) {
            var data_str = bundle.getString("data","Debug")
            if(data_str != "Debug") {
                var data = data_str?.split(";")?.toTypedArray() // 1

                val oName = findViewById(R.id.ObjectName) as EditText
                val oWeight = findViewById(R.id.ObjectWeight) as EditText
                oName.text = Editable.Factory.getInstance().newEditable(data?.get(0))
                oWeight.text = Editable.Factory.getInstance().newEditable(data?.get(1))
            }
        }
        layout.setOnTouchListener(object : OnSwipeTouchListener(this@MainActivity) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                /*Toast.makeText(this@MainActivity, readCSV(filename+".csv").joinToString("\n"),
                    Toast.LENGTH_SHORT)
                    .show()*/
                val intent = Intent(applicationContext, ListObjectActivity::class.java)
                val bundle = Bundle()
                bundle.putStringArrayList("data", readCSV(filename+".csv"))
                intent.putExtras(bundle)
                startActivity(intent)
            }
            override fun onSwipeRight() {
                super.onSwipeRight()
                val oName = findViewById(R.id.ObjectName) as EditText
                val oWeight = findViewById(R.id.ObjectWeight) as EditText
                if(checkInput(oName.text.toString()) && checkInput(oWeight.text.toString())) {
                    addLineCSV(filename + ".csv",
                        oName.text.toString() + ";" + oWeight.text.toString()
                            .replace(".", ",") + ";0;0;1\n"
                    )
                    oName.getText().clear()
                    oWeight.getText().clear()
                    Toast.makeText(
                        this@MainActivity,
                        "RIP",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    Toast.makeText(
                        this@MainActivity,
                        "Objet incomplet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onSwipeUp() {
                super.onSwipeUp()
                val oName = findViewById(R.id.ObjectName) as EditText
                val oWeight = findViewById(R.id.ObjectWeight) as EditText
                if(checkInput(oName.text.toString()) && checkInput(oWeight.text.toString())) {
                    addLineCSV(filename + ".csv", oName.text.toString()+";"+oWeight.text.toString().replace(".", ",")+";0;1;0\n")
                    oName.getText().clear()
                    oWeight.getText().clear()
                    Toast.makeText(this@MainActivity, "Merci pour la reprise", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(
                        this@MainActivity,
                        "Objet incomplet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            override fun onSwipeDown() {
                super.onSwipeDown()
                val oName = findViewById(R.id.ObjectName) as EditText
                val oWeight = findViewById(R.id.ObjectWeight) as EditText
                if(checkInput(oName.text.toString()) && checkInput(oWeight.text.toString())) {
                    addLineCSV(filename + ".csv", oName.text.toString()+";"+oWeight.text.toString().replace(".", ",")+";1;0;0\n")
                    oName.getText().clear()
                    oWeight.getText().clear()
                    Toast.makeText(this@MainActivity, "Merci pour votre don", Toast.LENGTH_SHORT)
                        .show()
                }
                else {
                    Toast.makeText(
                        this@MainActivity,
                        "Objet incomplet",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })
    }
}