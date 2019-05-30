package com.example.tanaysaxena.unomusical.fragments

import android.app.Activity
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.tanaysaxena.unomusical.Adapters.MainScreenAdapter
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.song
import java.util.*


class MainScreenFragment : Fragment() {
    var songList:ArrayList<song>?=null
    var nowplayingBottomBar:RelativeLayout?=null
      var playPauseButton:ImageButton?=null
      var songTitle:TextView?=null
      var visibleLayout:RelativeLayout?=null
     var recyclerView:RecyclerView?=null
     var myactivity:Activity?=null
     var noSongs:RelativeLayout?=null
    var mainscreenAdapter:MainScreenAdapter?=null
    object statified{
        var mediaPlayer: MediaPlayer?=null
        var pauseflag:Boolean=true
    }
    var trackPosition:Int=0


     override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

       val view=inflater!!.inflate(R.layout.fragment_main_screen, container, false)
       visibleLayout=view?.findViewById<RelativeLayout>(R.id.visibleLayout)
       noSongs=view?.findViewById<RelativeLayout>(R.id.noSongs)
       nowplayingBottomBar=view?.findViewById<RelativeLayout>(R.id.hiddenBarMainScreen)
       songTitle=view?.findViewById<TextView>(R.id.songTitleMainScreen)
       playPauseButton=view?.findViewById<ImageButton>(R.id.playPauseButton)
       recyclerView=view?.findViewById<RecyclerView>(R.id.contentMain)
        setHasOptionsMenu(true)

         activity.title="All Songs"
     return view
     }

    override  fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        songList=getSongsFromPhone()
        if(songList==null){
            recyclerView?.visibility=View.INVISIBLE
            noSongs?.visibility=View.VISIBLE
        }else {
            mainscreenAdapter = MainScreenAdapter(songList as ArrayList<song>, myactivity as Context)
            recyclerView?.layoutManager = LinearLayoutManager(myactivity)
            recyclerView?.itemAnimator = DefaultItemAnimator()
            recyclerView?.adapter = mainscreenAdapter
        }
     var prefs=myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)
        val action_sort_names=prefs?.getString("action_sort_names","true")
        val action_sort_dates=prefs?.getString("sction_sort_dates","false")
      if(songList!=null){
          if(action_sort_names.equals("true")){
              Collections.sort(songList,song.statified.nameComparator)
              mainscreenAdapter?.notifyDataSetChanged()
          }
          else if(action_sort_dates.equals("true")){
              Collections.sort(songList,song.statified.dateComparator)
              mainscreenAdapter?.notifyDataSetChanged()

          }
      }
        bottomBarSetup()





    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity=activity
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.main,menu)
        return
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val switcher=item?.itemId
        if(switcher==R.id.action_sort_names){
              val editor=myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
              editor?.putString("action_sort_names","true")
            editor?.putString("action_sort_dates","false")
            if(songList!=null){
                Collections.sort(songList,song.statified.nameComparator)
            }
         mainscreenAdapter?.notifyDataSetChanged()

        }
        else if(switcher==R.id.action_sort_recent){
            val editor=myactivity?.getSharedPreferences("action_sort",Context.MODE_PRIVATE)?.edit()
            editor?.putString("action_sort_names","false")
            editor?.putString("action_sort_dates","true")
            if(songList!=null){
                Collections.sort(songList,song.statified.dateComparator)
            }
            mainscreenAdapter?.notifyDataSetChanged()


        }
return false
    }
    fun getSongsFromPhone():ArrayList<song>{
        var arrayList=ArrayList<song>();
        var contentResolver=myactivity?.contentResolver
        var songuri=MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        var songCursor=contentResolver?.query(songuri,null,null,null,null)
        if(songCursor!=null && songCursor.moveToFirst()) {
            val songId = (songCursor.getColumnIndex(MediaStore.Audio.Media._ID))

            val songTitle = (songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE))

            val songArtist = (songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

            val songData = (songCursor.getColumnIndex(MediaStore.Audio.Media.DATA))

            val songDate = (songCursor.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED))

            while (songCursor.moveToNext()) {
                var currentId = songCursor.getLong(songId)
                var currentTitle=songCursor.getString(songTitle)
                var currentArtist=songCursor.getString(songArtist)
                var currentData=songCursor.getString(songData)
                var currentDate=songCursor.getLong(songDate)
                arrayList.add(song(currentId,currentTitle,currentArtist,currentData,currentDate))
            }
        }
        return arrayList

    }
    fun bottomBarSetup(){
        try{
            bottombarClickHandler()
            songTitle?.setText(SongPlayingFragment.statified.currentSongHelper?.songTitle)
            SongPlayingFragment.statified.mediaPlayer?.setOnCompletionListener {
                songTitle?.setText(SongPlayingFragment.statified.currentSongHelper?.songTitle)
                SongPlayingFragment.staticated.onSongComplete()
            }
            if(SongPlayingFragment.statified.flag as Boolean){
                nowplayingBottomBar?.visibility=View.VISIBLE
            }else{
                nowplayingBottomBar?.visibility=View.INVISIBLE
            }
            if(!(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean)){
               playPauseButton?.setBackgroundResource(R.drawable.play_icon)
               statified.mediaPlayer=SongPlayingFragment.statified.mediaPlayer
              statified.pauseflag=true
            }
            else{
                statified.pauseflag=false
            }

        }catch (e:Exception){e.printStackTrace()}
    }
    fun bottombarClickHandler(){
        nowplayingBottomBar?.setOnClickListener({
            MainScreenFragment.statified.mediaPlayer=SongPlayingFragment.statified.mediaPlayer
            var songplayingfragment= SongPlayingFragment()
            var args= Bundle()
            args.putString("songArtist",SongPlayingFragment.statified.currentSongHelper?.songArtist)
            args.putString("path",SongPlayingFragment.statified.currentSongHelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.statified.currentSongHelper?.songTitle)
            args.putInt("songId",SongPlayingFragment.statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.statified.fetchSongs)
            args.putString("MainBottomBar","success")
            args.putBoolean("PauseDetails",statified.pauseflag)
            songplayingfragment.arguments=args
            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment,songplayingfragment).addToBackStack("SongPlayingFragmentmainscreen").commit()


        })
        playPauseButton?.setOnClickListener({
            if(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean){
                SongPlayingFragment.statified.mediaPlayer?.pause()
                statified.mediaPlayer=SongPlayingFragment.statified.mediaPlayer
                playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                statified.pauseflag=true
            }
            else{
                SongPlayingFragment.statified.mediaPlayer=statified.mediaPlayer
                SongPlayingFragment.statified.mediaPlayer?.start()
                playPauseButton?.setBackgroundResource(R.drawable.pause_icon)
               statified.pauseflag=false
            }

        })
    }
}
