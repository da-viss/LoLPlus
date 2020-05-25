package net.azarquiel.lolplus.views

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.android.synthetic.main.fragment_inicio.*

import net.azarquiel.lolplus.R


class InicioFragment : Fragment() {

    private val videos = arrayListOf ("3Eu7NzzHC84","aR-KAldshAE","zF5Ddo9JdpY","vzHrjOMfHPY","ZjvDFvzfxsQ")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab= requireActivity().findViewById(R.id.fab) as FloatingActionButton
        fab.show()
        loadvideo()
        btnenlace.setOnClickListener{onClickEnlace()}
    }

    private fun loadvideo() {
        val ytVideo: YouTubePlayerView = requireActivity().findViewById (R.id.youtube_view)
        lifecycle.addObserver (ytVideo)
        ytVideo.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val n= (Math.random()*5).toInt()
                val videoId = videos[n]
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })
    }

    private fun onClickEnlace() {
        val url = "https://euw.leagueoflegends.com/es-es/"
        val myintent = Intent(Intent.ACTION_VIEW)
        myintent.data = Uri.parse(url)
        startActivity(myintent)
    }
}
