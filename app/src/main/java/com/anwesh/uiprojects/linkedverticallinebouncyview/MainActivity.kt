package com.anwesh.uiprojects.linkedverticallinebouncyview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.verticallinebouncyview.VerticalLineBouncyView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VerticalLineBouncyView.create(this)
    }
}
