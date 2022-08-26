package com.example.double_services

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController

class EndGameFragment : Fragment() {
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_end_game, container, false)

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

        val popped = arguments?.getInt("popped")
        val time = arguments?.getString("time")

        val sharedPreferences = requireActivity().getSharedPreferences("record", AppCompatActivity.MODE_PRIVATE)
        val recordTime : String = sharedPreferences.getString("time", "00:00")!!.toString()
        val recordPopped : Int = sharedPreferences.getString("popped", "0")!!.toInt()

        if (parseTimeInSeconds(time!!) > parseTimeInSeconds(recordTime)) {
            sharedPreferences.edit().putString("time", time).apply()
        }
        if (popped!! > recordPopped) {
            sharedPreferences.edit().putString("popped", popped.toString()).apply()
        }

        val clickTextView = rootView.findViewById<TextView>(R.id.clicks)
        clickTextView.text = "${resources.getString(R.string.clicks)}: $popped"

        val timeTextView = rootView.findViewById<TextView>(R.id.time_tv)
        timeTextView.text = "${resources.getString(R.string.time)}: $time"

        val restartButton = rootView.findViewById<Button>(R.id.restart)
        restartButton.setOnClickListener {
            findNavController().navigate(R.id.action_endGameFragment_to_playFragment)
        }

        val urlTv = rootView.findViewById<TextView>(R.id.link_end)
        urlTv.setOnClickListener {
            val uri = Uri.parse("https://www.doppiservizi.it/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return rootView
    }

    private fun parseTimeInSeconds(time: String): Int {
        val splitTime = time.split(":")
        val minutes = splitTime[0].toInt()
        val seconds = splitTime[1].toInt()
        return minutes*60 + seconds
    }
}