package com.example.tanaysaxena.unomusical.Activites

import  android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import com.example.tanaysaxena.unomusical.Adapters.NavigationDrawerAdapter
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.fragments.MainScreenFragment
import com.example.tanaysaxena.unomusical.fragments.SongPlayingFragment

class MainActivity : AppCompatActivity(){
    var navigationDrawerIconList:ArrayList<String> =arrayListOf()
    var images_for_drawer= intArrayOf(R.drawable.navigation_allsongs,R.drawable.navigation_favorites,R.drawable.navigation_settings,R.drawable.navigation_aboutus)
object statified {
    var drawerlayout: DrawerLayout? = null
    var notificationManger:NotificationManager?=null
 }
  var trackNotificationBuilder: Notification?=null
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar:Toolbar?=findViewById<Toolbar>(R.id.toolbar)
       setSupportActionBar(toolbar)
       MainActivity.statified.drawerlayout=findViewById(R.id.drawer_layout)
       var toogle=ActionBarDrawerToggle(this@MainActivity, MainActivity.statified.drawerlayout,toolbar, R.string.navigation_drawer_open,
               R.string.navigation_drawer_close)
        MainActivity.statified.drawerlayout?.setDrawerListener(toogle)
        toogle.syncState()
         navigationDrawerIconList.add("All Songs")

        navigationDrawerIconList.add("Favorite")

        navigationDrawerIconList.add("Settings")

        navigationDrawerIconList.add("About Us")

        var _navigationadapter=NavigationDrawerAdapter(navigationDrawerIconList,images_for_drawer,this)
        _navigationadapter.notifyDataSetChanged()
        val mainScreenFragment=MainScreenFragment()
        this.supportFragmentManager.beginTransaction().add(R.id.details_fragment,mainScreenFragment,"MainScreenFragment").commit()

        var navigation_recycler_view=findViewById<RecyclerView>(R.id.navigtionRecycleView)
        navigation_recycler_view.layoutManager=LinearLayoutManager(this)
        navigation_recycler_view.adapter=_navigationadapter
        navigation_recycler_view.setHasFixedSize(true)

        val intent= Intent(this@MainActivity,MainActivity::class.java)
        val pintent=PendingIntent.getActivity(this@MainActivity,System.currentTimeMillis().toInt()
                ,intent,0)
        trackNotificationBuilder=Notification.Builder(this)
                .setContentTitle("A track is playing in the background")
                .setSmallIcon(R.drawable.echo_logo).setContentIntent(pintent).setOngoing(true)
                .setAutoCancel(true).build()
        statified.notificationManger=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onStop() {
        super.onStop()
        try{
            if(SongPlayingFragment.statified.mediaPlayer?.isPlaying as Boolean){
                statified.notificationManger?.notify(1978,trackNotificationBuilder)
            }



        }catch (e:Exception){
            e.printStackTrace()
        }
    }

     override fun onStart() {
        super.onStart()
      statified.notificationManger?.cancel(1978)

    }

    override fun onResume() {
        super.onResume()

        statified.notificationManger?.cancel(1978)
    }

}