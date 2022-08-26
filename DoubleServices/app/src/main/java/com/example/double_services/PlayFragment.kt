package com.example.double_services

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlin.properties.Delegates

class PlayFragment : Fragment() {

    var pauseTime by Delegates.notNull<Long>()
    private lateinit var myView: MyView
    var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        myView = MyView(requireContext())
        myView.activity = requireActivity()

        // Disable Android back button
        val doubleServices = requireActivity() as DoubleServicesActivity
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {

                }
            }
        doubleServices.onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        doubleServices.actionInfo?.isVisible = false
        doubleServices.actionAbout?.isVisible = false
        pauseTime = System.currentTimeMillis()

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.game_music)
        mediaPlayer?.setVolume(0.1f, 0.1f)
        mediaPlayer?.start()
        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.start()
        }
        return myView
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer!!.pause()

        pauseTime = System.currentTimeMillis()
        myView.pause = true
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer!!.start()

        val resumeTime = System.currentTimeMillis()
        val timeInPause = resumeTime-pauseTime
        myView.start += timeInPause
        myView.pause = false
        myView.invalidate()
    }

    override fun onDestroy() {
        mediaPlayer!!.stop()
        mediaPlayer!!.release()
        super.onDestroy()
    }
}