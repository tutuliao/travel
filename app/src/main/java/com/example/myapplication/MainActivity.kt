package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    /*因为android app有生命周期
    所以入口点是onCreate，而不是main函数*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    var sum = 0;

    fun increment(view: View){
       sum++;
       showScores(sum);
    }

    fun decrement(view: View){
        sum--;
        showScores(sum);
    }

    private fun showScores(sum: Int) {
      val textView: TextView = findViewById(R.id.textView1);
        textView.text = sum.toString();
    }
}