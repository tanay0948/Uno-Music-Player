package com.example.tanaysaxena.unomusical.Adapters

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.fragments.SongPlayingFragment
import com.example.tanaysaxena.unomusical.song
import java.util.ArrayList

/**
 * Created by Tanay Saxena on 12/22/2017.
 */
class FavoriteAdapter(_songDetails: ArrayList<song>, context: Context): RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>() {
    var songDetails: ArrayList<song>?=null
    var mcontext: Context?=null
    init{
        songDetails=_songDetails
        mcontext=context
    }
    override fun getItemCount(): Int {
        if(songDetails==null){
            return 0
        }else{
            return (songDetails as ArrayList<song>).size
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyViewHolder {
        val itemview= LayoutInflater.from(parent?.context)
                .inflate(R.layout.row_custom_mainview_adapter,parent,false)
        return MyViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: MyViewHolder?, position: Int) {
        var songObject=songDetails?.get(position)
        holder?.trackTitle?.text=songObject?.songTitle
        holder?.trackArtist?.text=songObject?.artist
        holder?.contentHolder?.setOnClickListener({
            var songplayingfragment= SongPlayingFragment()
            var args= Bundle()
            args.putString("songArtist",songObject?.artist)
            args.putString("path",songObject?.songData)
            args.putString("songTitle",songObject?.songTitle)
            args.putInt("songId",songObject?.songId?.toInt() as Int)
            args.putInt("songPosition",position)
            args.putParcelableArrayList("songData",songDetails)
            songplayingfragment.arguments=args
            if(SongPlayingFragment.statified.mediaPlayer!=null){
                if(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean){
                    SongPlayingFragment.statified.mediaPlayer?.stop()
                }}
            (mcontext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.details_fragment,songplayingfragment).addToBackStack("SongPlayingFragmentfavorite").commit()


        })



    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var trackTitle: TextView?=null
        var trackArtist: TextView?=null
        var contentHolder: RelativeLayout?=null
        init{
            trackTitle=view?.findViewById(R.id.trackTitle)
            trackArtist=view?.findViewById(R.id.trackArtist)
            contentHolder=view?.findViewById(R.id.contentRow)
        }
    }
}