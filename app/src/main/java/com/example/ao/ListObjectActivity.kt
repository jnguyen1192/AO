package com.example.ao

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import java.util.*

class ListObjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_object)
        //@source:https://www.geeksforgeeks.org/android-listview-in-kotlin/

        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
            var data = bundle.getStringArrayList("data")

            // use arrayadapter and define an array
            val arrayAdapter: ArrayAdapter<*>

            var dataArray = arrayOfNulls<String>(data!!?.size);
            data?.toArray(dataArray)

            // access the listView from xml file
            var mListView = findViewById<ListView>(R.id.object_list_view)
            arrayAdapter = ArrayAdapter(this,
                android.R.layout.simple_list_item_1, dataArray)
            mListView.adapter = arrayAdapter

            mListView.setOnItemClickListener { parent, view, position, id ->
                //Toast.makeText(this, element.toString(), Toast.LENGTH_SHORT).show()

                val bundle = Bundle()
                bundle.putString("data", parent.getItemAtPosition(position).toString())// The item that was clicked

                val intentElement = Intent(this, MainActivity::class.java)
                intentElement.putExtras(bundle)
                startActivity(intentElement)
            }
        }
    }
}