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
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.tanaysaxena.unomusical.Adapters.FavoriteAdapter
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.databases.EchoDatabase
import com.example.tanaysaxena.unomusical.song


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [FavoriteFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment() {
      object statified{
         var mediaPlayer: MediaPlayer?=null
          var pauseflag:Boolean=true
      }
      var myactivity:Activity?=null
    var noFavorites:TextView?=null
    var nowPlayingBottomBar:RelativeLayout?=null
    var playPauseButton:ImageButton?=null
    var songTitle:TextView?=null
    var recyclerView:RecyclerView?=null
    var favoriteAdapter:FavoriteAdapter?=null
    var favoritecontent:EchoDatabase?=null
    var trackPosition:Int=0
    var refreshList:ArrayList<song>?=null
    var getListFromDatabase:ArrayList<song>?=null
    var fetchListfromDevice:ArrayList<song>?=null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

    var view=inflater!!.inflate(R.layout.fragment_favorite, container, false)
    noFavorites=view?.findViewById(R.id.noFavorites)
    nowPlayingBottomBar=view?.findViewById(R.id.hiddenBarFavScreen)
    playPauseButton=view?.findViewById(R.id.playPauseButton)
    songTitle=view?.findViewById(R.id.songTitleFavScreen)
    recyclerView=view?.findViewById(R.id.favoriteRecycler)
      favoritecontent= EchoDatabase(myactivity)
        setHasOptionsMenu(true)

        activity.title="My Favorites"
    return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity=activity
    }

    override  fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val item=menu?.findItem(R.id.action_sort)
        item?.isVisible=false
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
      display_by_searching()
        bottomBarSetup()

    }
    fun getSongsFromPhone():ArrayList<song>{
        var arrayList=ArrayList<song>();
        var contentResolver=myactivity?.contentResolver
        var songuri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
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
               nowPlayingBottomBar?.visibility=View.VISIBLE
            }else{
                nowPlayingBottomBar?.visibility=View.INVISIBLE
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
        nowPlayingBottomBar?.setOnClickListener({
            statified.mediaPlayer=SongPlayingFragment.statified.mediaPlayer
            var songplayingfragment= SongPlayingFragment()
            var args= Bundle()
            args.putString("songArtist",SongPlayingFragment.statified.currentSongHelper?.songArtist)
            args.putString("path",SongPlayingFragment.statified.currentSongHelper?.songPath)
            args.putString("songTitle",SongPlayingFragment.statified.currentSongHelper?.songTitle)
            args.putInt("songId",SongPlayingFragment.statified.currentSongHelper?.songId?.toInt() as Int)
            args.putInt("songPosition",SongPlayingFragment.statified.currentSongHelper?.currentPosition?.toInt() as Int)
            args.putParcelableArrayList("songData",SongPlayingFragment.statified.fetchSongs)
            args.putString("favoriteBottomBar","success")
            args.putBoolean("fPauseDetails",statified.pauseflag)
            songplayingfragment.arguments=args
            fragmentManager.beginTransaction()
                    .replace(R.id.details_fragment,songplayingfragment).addToBackStack("SongPlayingFragmentfavoriteplay").commit()


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
    fun display_by_searching(){
        if(favoritecontent?.checkSize() as Int >0){
            refreshList= ArrayList<song>()
            getListFromDatabase=favoritecontent?.queryDBlist()
            fetchListfromDevice=getSongsFromPhone()
            if(fetchListfromDevice!=null){
                 for(i in 0..fetchListfromDevice?.size as Int-1){
                     for(j in 0..getListFromDatabase?.size as Int -1){
                         if((getListFromDatabase?.get(j)?.songId)===(fetchListfromDevice?.get(i)?.songId)){
                             refreshList?.add((getListFromDatabase?.get(j) as song))
                         }
                     }
                 }
            }
            else{
            }
            if(getListFromDatabase==null){
                recyclerView?.visibility=View.INVISIBLE
                noFavorites?.visibility=View.VISIBLE
            }else {

                favoriteAdapter = FavoriteAdapter(getListFromDatabase as ArrayList<song>, myactivity as Context)
                recyclerView?.layoutManager = LinearLayoutManager(myactivity)
                recyclerView?.itemAnimator = DefaultItemAnimator()
                recyclerView?.adapter = favoriteAdapter
                recyclerView?.setHasFixedSize(true)
            }

        }else{
            recyclerView?.visibility=View.INVISIBLE
            noFavorites?.visibility=View.VISIBLE
        }

    }
}// Required empty public constructor
