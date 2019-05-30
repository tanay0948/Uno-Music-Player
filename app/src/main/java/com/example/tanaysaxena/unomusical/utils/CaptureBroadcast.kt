package com.example.tanaysaxena.unomusical.utils

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import com.example.tanaysaxena.unomusical.Activites.MainActivity
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.fragments.SongPlayingFragment

/**
 * Created by Tanay Saxena on 12/24/2017.
 */
class CaptureBroadcast:BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1?.action==Intent.ACTION_NEW_OUTGOING_CALL){
            try{
             MainActivity.statified.notificationManger?.cancel(1978)
            }
            catch (e:Exception){
                e.printStackTrace()
            }
            try{
               if(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean){
                   SongPlayingFragment.statified.mediaPlayer?.pause()
                   SongPlayingFragment.statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
               }



            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        else{
            val tm:TelephonyManager=p0?.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when(tm?.callState){
                TelephonyManager.CALL_STATE_RINGING->{
                    try{
                        MainActivity.statified.notificationManger?.cancel(1978)
                    }
                    catch (e:Exception){
                        e.printStackTrace()
                    }

                    try{
                        if(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean){
                            SongPlayingFragment.statified.mediaPlayer?.pause()
                            SongPlayingFragment.statified.playPauseButton?.setBackgroundResource(R.drawable.play_icon)
                        }



                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
                else->{}
            }

        }
    }

}