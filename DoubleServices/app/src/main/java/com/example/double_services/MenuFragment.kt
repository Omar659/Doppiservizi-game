package com.example.double_services

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController


class MenuFragment : Fragment() {


    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_menu, container, false)

        // Disable Android back button
        val doubleServices = requireActivity() as DoubleServicesActivity
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        doubleServices.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        doubleServices.actionInfo?.isVisible = true
        doubleServices.actionAbout?.isVisible = true

        val sharedPreferences = requireActivity().getSharedPreferences("record", AppCompatActivity.MODE_PRIVATE)
        val time : String = sharedPreferences.getString("time", "00:00").toString()
        val popped : String = sharedPreferences.getString("popped", "0").toString()

        val timeTv = rootView.findViewById<TextView>(R.id.time_tv_rec)
        timeTv.text = "${resources.getString(R.string.time)}: $time"
        val clickTv = rootView.findViewById<TextView>(R.id.clicks_rec)
        clickTv.text = "${resources.getString(R.string.clicks)}: $popped"

        val playButton = rootView.findViewById<Button>(R.id.play)
        playButton.setOnClickListener{
            findNavController().navigate(R.id.action_menuFragment_to_playFragment)
        }

        val urlTv = rootView.findViewById<TextView>(R.id.link_menu)
        urlTv.setOnClickListener {
            val uri = Uri.parse("https://www.doppiservizi.it/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return rootView
    }
}