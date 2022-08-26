package com.example.double_services

import android.app.AlertDialog
import android.content.*
import android.media.MediaPlayer
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate


class DoubleServicesActivity : AppCompatActivity() {

    var actionInfo: MenuItem? = null
    var actionAbout: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_double_services)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar

        // Inflate the menu items for use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.activity_double_services_menu, menu)
        actionInfo = menu!!.findItem(R.id.action_info)
        actionAbout = menu!!.findItem(R.id.action_help)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar items
        return when (item.itemId) {
            R.id.action_info -> {
                AlertDialog.Builder(this)
                    .setTitle(R.string.info_button)
                    .setMessage(R.string.info)
                    .setPositiveButton(R.string.close_button, null)
                    .setIcon(R.drawable.baseline_info_black_48)
                    .show()
                true
            }
            R.id.action_help -> {
                val alertDialog = AlertDialog.Builder(this)
                    .setTitle(R.string.help_button)
                    .setMessage(R.string.help)
                    .setPositiveButton(R.string.close_button, null)
                    .setNeutralButton(R.string.copy_button, null)
                    .setIcon(R.drawable.baseline_help_black_48)
                    .create()
                alertDialog.setOnShowListener {
                    val button: Button =
                        (alertDialog as AlertDialog).getButton(AlertDialog.BUTTON_NEUTRAL)
                    button.setOnClickListener {
                        val clipboard: ClipboardManager? = getSystemService(
                            CLIPBOARD_SERVICE
                        ) as ClipboardManager?
                        val clip = ClipData.newPlainText("id", resources.getString(R.string.help))
                        clipboard?.setPrimaryClip(clip)
                        Toast.makeText(applicationContext, resources.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                    }
                }
                alertDialog.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}