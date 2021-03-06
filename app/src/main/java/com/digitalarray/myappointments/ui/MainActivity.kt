package com.digitalarray.myappointments.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.digitalarray.myappointments.PreferenceHelper
import kotlinx.android.synthetic.main.activity_main.*
import com.digitalarray.myappointments.PreferenceHelper.get
import com.digitalarray.myappointments.PreferenceHelper.set
import com.digitalarray.myappointments.R
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private val snackBar by lazy {
        Snackbar.make(mainLayout,
            R.string.press_back_again, Snackbar.LENGTH_LONG)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Shared preferences
        //sqlite
        //files
        /*
        val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val session = preferences.getBoolean("session", false)
        */
        val preferences =
            PreferenceHelper.defaultPrefs(this)
        if (preferences["session", false])
            goToMenuActivity()

        btnLogin.setOnClickListener {
            //Validate
            createSessionPreference()
            goToMenuActivity()
        }

        tvGoToRegister.setOnClickListener {
            Toast.makeText(
                this,
                getString(R.string.please_fill_your_register_data),
                Toast.LENGTH_LONG
            ).show()
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createSessionPreference() {
        /*val preferences = getSharedPreferences("general", Context.MODE_PRIVATE)
        val editor = preferences.edit()
        editor.putBoolean("session", true)
        editor.apply()*/
        val preferences =
            PreferenceHelper.defaultPrefs(this)
        preferences["session"] = true

    }

    private fun goToMenuActivity() {
        val intent = Intent(this, MenuActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (snackBar.isShown)
            super.onBackPressed()
        else
            snackBar.show()

    }
}
