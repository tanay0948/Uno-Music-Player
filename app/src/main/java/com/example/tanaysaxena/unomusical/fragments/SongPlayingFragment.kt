package com.example.tanaysaxena.unomusical.fragments

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.cleveroad.audiovisualization.AudioVisualization
import com.cleveroad.audiovisualization.DbmHandler
import com.cleveroad.audiovisualization.GLAudioVisualizationView
import com.example.tanaysaxena.unomusical.CurrentSongHelper
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.databases.EchoDatabase
import com.example.tanaysaxena.unomusical.song
import kotlinx.android.synthetic.main.fragment_song_playing.*
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SongPlayingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SongPlayingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SongPlayingFragment : Fragment() {
    var mAcceleration:Float=0f
    var mAccelerationCurrent:Float=0f
    var mAccelerationLast:Float=0f
    object statified{
        var myactivity:Activity?=null
        var mediaPlayer:MediaPlayer?=null
        var startTimeText:TextView?=null
        var endTimeText:TextView?=null
        var playPauseButton:ImageButton?=null
        var previousImageButton:ImageButton?=null
        var nextImageButton:ImageButton?=null
        var seekbar: SeekBar?=null
        var songArtistView:TextView?=null
        var songTitleView:TextView?=null
        var shuffleImageButton:ImageButton?=null
        var loopImageButton:ImageButton?=null
        var currentSongHelper:CurrentSongHelper?=null
        var currentPosition:Int=0
        var fetchSongs:ArrayList<song>?=null
        var updateSongTime=object: Runnable{
            override fun run() {
                val getCurrent=mediaPlayer?.currentPosition
                startTimeText?.setText(String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long),(TimeUnit.MILLISECONDS.toSeconds(getCurrent?.toLong() as Long)-TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getCurrent?.toLong() as Long)))%60))
                seekbar?.setProgress(getCurrent?.toInt() as  Int)

                Handler().postDelayed(this,1000)

            }

        }
        var audioVisualization:AudioVisualization?=null
        var glview:GLAudioVisualizationView?=null
        var fab:ImageButton?=null
        var favoritecontent:EchoDatabase?=null
        var mSensorManager:SensorManager?=null
        var mSensorListener:SensorEventListener?=null
        val MY_PREFS_NAME="shake feature"
        var flag:Boolean?=null
     }

    object staticated{
        var MY_PREFS_SHUFFLE="Shuffle feature"

        var MY_PREFS_LOOP="Loop feature"

        fun onSongComplete(){
            if(statified.currentSongHelper?.isShuffle as Boolean){
                PlayNext("PlayNextLikeNormalShuffle")
                statified.currentSongHelper?.isPlaying=true
            }
            else{
                if(statified.currentSongHelper?.isLoop as Boolean){
                    statified.currentSongHelper?.isPlaying=true
                    var nextSong=statified.fetchSongs?.get(statified.currentPosition)
                    statified.currentSongHelper?.songId=nextSong?.songId as Long
                    statified.currentSongHelper?.songTitle=nextSong?.songTitle
                    statified.currentSongHelper?.songArtist=nextSong?.artist
                    statified.currentSongHelper?.songPath=nextSong?.songData
                    statified.currentSongHelper?.currentPosition=statified.currentPosition

                    UpdateTextViews(statified.currentSongHelper?.songTitle as String,statified.currentSongHelper?.songArtist as String)

                    if(statified.favoritecontent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                        statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_on))
                    }
                    else{
                        statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_off))
                    }

                    statified.mediaPlayer?.reset()
                    try{
                        statified.mediaPlayer?.setDataSource(statified.myactivity, Uri.parse(statified.currentSongHelper?.songPath))
                        statified.mediaPlayer?.prepare()
                        statified.mediaPlayer?.start()

                        ProcessInformation(statified.mediaPlayer as MediaPlayer)
                    }catch(e:Exception){
                        e.printStackTrace()
                    }

                }
                else{
                    PlayNext("PlayNextNormal")
                    statified.currentSongHelper?.isPlaying=true
                }
            }

        }
        fun UpdateTextViews(songtitle:String,songartist:String){
            var stu=songtitle
            var sau=songartist
            if(songtitle.equals("<unknown>",true)){
                stu="unknown"
            }
            if(songartist.equals("<unknown>",true)){
                sau="unknown"
            }
            statified.songTitleView?.setText(stu)
            statified.songArtistView?.setText(sau)
        }
        fun ProcessInformation(mediaPlayer:MediaPlayer){
            val finalTime=mediaPlayer.duration
            val startTime=mediaPlayer.currentPosition
            statified.seekbar?.max=finalTime
            statified.startTimeText?.setText(String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long),(TimeUnit.MILLISECONDS.toSeconds(startTime?.toLong() as Long)-TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(startTime?.toLong() as Long)))%60))
            statified.endTimeText?.setText(String.format("%02d:%02d",TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long),(TimeUnit.MILLISECONDS.toSeconds(finalTime?.toLong() as Long)-TimeUnit.MILLISECONDS.toSeconds(TimeUnit.MILLISECONDS.toMinutes(finalTime?.toLong() as Long)))%60))
            statified.seekbar?.setProgress(startTime)
            Handler().postDelayed(statified.updateSongTime,1000)

        }
        fun PlayNext(check:String) {
            if (check.equals("PlayNextNormal", true)) {
                statified.currentPosition = statified.currentPosition + 1
            } else if (check.equals("PlayNextLikeNormalShuffle", true)) {
                var randomObject = Random()

                var randomPosition=randomObject.nextInt(statified.fetchSongs?.size?.plus(1) as Int)
                statified.currentPosition=randomPosition
            }
            statified.currentSongHelper?.isLoop=false
            if(statified.currentPosition==statified.fetchSongs?.size) {statified.currentPosition=0}
            var nextSong=statified.fetchSongs?.get(statified.currentPosition)
            statified.currentSongHelper?.songId=nextSong?.songId as Long
            statified.currentSongHelper?.songTitle=nextSong?.songTitle
            statified.currentSongHelper?.songArtist=nextSong?.artist
            statified.currentSongHelper?.songPath=nextSong?.songData
            statified.currentSongHelper?.currentPosition=statified.currentPosition

            UpdateTextViews(statified.currentSongHelper?.songTitle as String,statified.currentSongHelper?.songArtist as String)
            statified.mediaPlayer?.reset()
            try{
                statified.mediaPlayer?.setDataSource(statified.myactivity, Uri.parse(statified.currentSongHelper?.songPath))
                statified.mediaPlayer?.prepare()
                statified.mediaPlayer?.start()

                ProcessInformation(statified.mediaPlayer as MediaPlayer)
            }catch(e:Exception){
                e.printStackTrace()
            }

            if(statified.favoritecontent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_on))
            }
            else{
                statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_off))
            }



        }

    }


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

setHasOptionsMenu(true)
         var view=inflater!!.inflate(R.layout.fragment_song_playing, container, false)
        statified.seekbar=view?.findViewById(R.id.seekBar)
        statified.startTimeText=view?.findViewById(R.id.startTime)
        statified.endTimeText=view?.findViewById(R.id.endTime)
        statified.playPauseButton=view?.findViewById(R.id.playPauseButton)
        statified.previousImageButton=view?.findViewById(R.id.previousButton)
        statified.nextImageButton=view?.findViewById(R.id.nextButton)
        statified.songArtistView=view?.findViewById(R.id.songArtist)
        statified.songTitleView=view?.findViewById(R.id.songTitle)
        statified.shuffleImageButton=view?.findViewById(R.id.shuffleButton)
        statified.loopImageButton=view?.findViewById(R.id.loopButton)
        statified.glview=view?.findViewById(R.id.visualizer_view)
        statified.fab=view?.findViewById(R.id.favoriteButton)
        statified.fab?.alpha=0.8f
        activity.title="Now Playing"
        statified.favoritecontent= EchoDatabase(statified.myactivity)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statified.audioVisualization=statified.glview as AudioVisualization
    }

    override fun onResume() {
        super.onResume()
        statified.audioVisualization?.onResume()
        statified.mSensorManager?.registerListener(statified.mSensorListener,
                statified.mSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        statified.audioVisualization?.onPause()
        super.onPause()
        statified.mSensorManager?.unregisterListener(statified.mSensorListener)
    }

    override fun onDestroyView() {
        statified.audioVisualization?.release()
        super.onDestroyView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        statified.mSensorManager=statified.myactivity?.getSystemService(Context.SENSOR_SERVICE) as SensorManager
         mAcceleration=0.0f
        mAccelerationCurrent=SensorManager.GRAVITY_EARTH
        mAccelerationLast=SensorManager.GRAVITY_EARTH
        bindshakeFeature()
    }
    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.song_playing_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {

        super.onPrepareOptionsMenu(menu)
        val item:MenuItem?=menu?.findItem(R.id.action_redirect)
        item?.isVisible=true
        val item2:MenuItem?=menu?.findItem(R.id.action_sort)
        item2?.isVisible=false

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_redirect -> {
                statified.myactivity?.onBackPressed()
                return false
            }

        }
        return false
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        statified.myactivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        statified.myactivity=activity
    }





    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        statified.currentSongHelper=CurrentSongHelper()
        statified.currentSongHelper?.isPlaying=true
        statified.currentSongHelper?.isLoop=false
        statified.currentSongHelper?.isShuffle=false
        var path:String?=null
        var songTitle:String?=null
        var songArtist:String?=null
        var songId:Long?=null


        try{
            path=arguments.getString("path")
            songTitle=arguments.getString("songTitle")
            songArtist=arguments.getString("songArtist")
            songId=arguments.getInt("songId").toLong()
            statified.currentPosition=arguments.getInt("songPosition")
            statified.fetchSongs=arguments.getParcelableArrayList("songData")

        }catch(e:Exception){
            e.printStackTrace()
        }
        var fromMainBottomBar=arguments.get("MainBottomBar") as? String
        var fromFavBottomBar=arguments.get("favoriteBottomBar") as? String
        var fromMainPause=arguments.get("PauseDetails")
        var fromFavPause=arguments.get("fPauseDetails")
        if(fromMainBottomBar!=null){
            statified.mediaPlayer=MainScreenFragment.statified.mediaPlayer



        }
        else if(fromFavBottomBar!=null){
            statified.mediaPlayer=FavoriteFragment.statified.mediaPlayer

        }else{
        statified.mediaPlayer= MediaPlayer()
        statified.mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)

        try{
            statified.mediaPlayer?.setDataSource(statified.myactivity,Uri.parse(path))
            statified.mediaPlayer?.prepare()
        }catch(e:Exception){
            e.printStackTrace()
        }
        statified.mediaPlayer?.start()}
        staticated.ProcessInformation(statified.mediaPlayer as MediaPlayer)
        if(statified.currentSongHelper?.isPlaying as Boolean) {
            if((fromMainPause!=null && fromMainPause==true)||(fromFavPause!=null && fromFavPause==true)){
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
            }
            else{
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)}
        }
        else{

            playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        statified.currentSongHelper?.songPath=path
        statified.currentSongHelper?.songArtist=songArtist
        statified.currentSongHelper?.songTitle=songTitle
        statified.currentSongHelper?.songId=songId
        statified.currentSongHelper?.currentPosition=statified.currentPosition
        staticated.UpdateTextViews(statified.currentSongHelper?.songTitle as String,statified.currentSongHelper?.songArtist as String)

        statified.mediaPlayer?.setOnCompletionListener { staticated.onSongComplete() }
            statified.flag=true
    ClickHandler()

        var visualisationHandler=DbmHandler.Factory.newVisualizerHandler(statified.myactivity as Context,0)
        statified.audioVisualization?.linkTo(visualisationHandler)

        var prefforshuffle=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)
        var isshuffleAllowed=prefforshuffle?.getBoolean("feature",false)
        if (isshuffleAllowed as Boolean){
            statified.currentSongHelper?.isShuffle=true
            statified.currentSongHelper?.isLoop=false
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }else{
            statified.currentSongHelper?.isShuffle=false
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
        }
        var prefforloop=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)
        var isloopAllowed=prefforloop?.getBoolean("feature",false)
        if (isloopAllowed as Boolean){
            statified.currentSongHelper?.isLoop=true
            statified.currentSongHelper?.isShuffle=false
            statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
        }else{
            statified.currentSongHelper?.isLoop=false
            statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
        }

     if(statified.favoritecontent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
         statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_on))
     }
     else{
         statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_off))
     }


    }


    fun ClickHandler(){
        statified.fab?.setOnClickListener({

            if(statified.favoritecontent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
                statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_off))
                statified.favoritecontent?.deleteFavourite(statified.currentSongHelper?.songId?.toInt() as Int)
                Toast.makeText(statified.myactivity,"Removed from Favorites",Toast.LENGTH_SHORT).show()
            }
            else{
                statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_on))
                statified.favoritecontent?.storeAsFavorite(statified.currentSongHelper?.songId?.toInt() as Int,
                        statified.currentSongHelper?.songArtist,statified.currentSongHelper?.songTitle,statified.currentSongHelper?.songPath)

                Toast.makeText(statified.myactivity,"Added to Favorites",Toast.LENGTH_SHORT).show()
            }

        })
        statified.shuffleImageButton?.setOnClickListener({
          var editorShuffle=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
           var editorLoop=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
          if(statified.currentSongHelper?.isShuffle as Boolean){
              statified.currentSongHelper?.isShuffle=false
              statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
              editorShuffle?.putBoolean("feature",false)
              editorShuffle?.apply()
          }
           else{
              statified.currentSongHelper?.isShuffle =true
              statified.currentSongHelper?.isLoop=false
              statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
              statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_icon)
              editorShuffle?.putBoolean("feature",true)
              editorShuffle?.apply()
              editorLoop?.putBoolean("feature",false)
              editorLoop?.apply()
          }
       })
        statified.nextImageButton?.setOnClickListener({
            statified.currentSongHelper?.isPlaying=true
            statified.playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
           if(statified.currentSongHelper?.isShuffle as Boolean){
               staticated.PlayNext("PlayNextLikeNormalShuffle")
           }

           else{
               staticated.PlayNext("PlayNextNormal")
           }
       })
        statified.previousImageButton?.setOnClickListener({
            statified.currentSongHelper?.isPlaying=true
           if(statified.currentSongHelper?.isLoop as Boolean){
               statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
         }
         PlayPrevious()
       })
        statified.loopImageButton?.setOnClickListener({
           var editorShuffle=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_SHUFFLE,Context.MODE_PRIVATE)?.edit()
           var editorLoop=statified.myactivity?.getSharedPreferences(staticated.MY_PREFS_LOOP,Context.MODE_PRIVATE)?.edit()
           if(statified.currentSongHelper?.isLoop as Boolean){
               statified.currentSongHelper?.isLoop=false
               statified.loopImageButton?.setBackgroundResource(R.drawable.loop_white_icon)
             editorLoop?.putBoolean("feature",false)
               editorLoop?.apply()
                }
         else {
               statified.currentSongHelper?.isLoop=true;
               statified.currentSongHelper?.isShuffle=false
               statified.loopImageButton?.setBackgroundResource(R.drawable.loop_icon)
               statified.shuffleImageButton?.setBackgroundResource(R.drawable.shuffle_white_icon)
            editorLoop?.putBoolean("feature",true)
               editorLoop?.apply()
               editorShuffle?.putBoolean("feature",false)
               editorShuffle?.apply()
         }
       })
       playPauseButton?.setOnClickListener({
           if(statified.mediaPlayer?.isPlaying as Boolean)  {
               statified.mediaPlayer?.pause()
               statified.currentSongHelper?.isPlaying=false
               playPauseButton?.setBackgroundResource(R.drawable.play_icon)
           }
           else{
               statified.mediaPlayer?.start()
               statified.currentSongHelper?.isPlaying=true
               playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
           }

       })
    }



    fun PlayPrevious(){
        statified.currentPosition=statified.currentPosition-1
        if(statified.currentPosition==-1) statified.currentPosition=0
        if(statified.currentSongHelper?.isPlaying as Boolean){
            playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
        }
        else{
            playPauseButton?.setBackgroundResource(R.drawable.play_icon)
        }
        statified.currentSongHelper?.isLoop=false
        if(statified.currentPosition==statified.fetchSongs?.size) {statified.currentPosition=0}
        var nextSong=statified.fetchSongs?.get(statified.currentPosition)
        statified.currentSongHelper?.songId=nextSong?.songId as Long
        statified.currentSongHelper?.songTitle=nextSong?.songTitle
        statified.currentSongHelper?.songArtist=nextSong?.artist
        statified.currentSongHelper?.songPath=nextSong?.songData
        statified.currentSongHelper?.currentPosition=statified.currentPosition
        statified.mediaPlayer?.reset()
        try{
            statified.mediaPlayer?.setDataSource(statified.myactivity, Uri.parse(statified.currentSongHelper?.songPath))
            statified.mediaPlayer?.prepare()
            statified.mediaPlayer?.start()

            staticated.ProcessInformation(statified.mediaPlayer as MediaPlayer)
        }catch(e:Exception){
            e.printStackTrace()
        }

        staticated.UpdateTextViews(statified.currentSongHelper?.songTitle as String,statified.currentSongHelper?.songArtist as String)

        if(statified.favoritecontent?.checkIfIdExists(statified.currentSongHelper?.songId?.toInt() as Int) as Boolean){
            statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_on))
        }
        else{
            statified.fab?.setImageDrawable(ContextCompat.getDrawable(statified.myactivity,R.drawable.favorite_off))
        }

    }
    fun bindshakeFeature(){
        statified.mSensorListener=object:SensorEventListener{
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

            }

            override fun onSensorChanged(event: SensorEvent?) {
           val x= event?.values?.get(0)
                val y= event?.values?.get(1)
                val z= event?.values?.get(2)
                mAccelerationLast=mAccelerationCurrent
                if (x != null) {
                    if (y != null) {
                        if (z != null) {
                            mAccelerationCurrent=Math.sqrt((x*x+y*y+z*z).toDouble()).toFloat()
                        }
                    }
                }
                val delta=mAccelerationCurrent-mAccelerationLast
                mAcceleration=mAcceleration*0.9f+delta
                if(mAcceleration>6){
                 val prefs=statified.myactivity?.getSharedPreferences(statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
                 val isAllowed=prefs?.getBoolean("feature",false)
                    if(isAllowed as Boolean){
                        staticated.PlayNext("PlayNextNormal")
                    }
                }




            }

        }
    }

}// Required empty public constructor
