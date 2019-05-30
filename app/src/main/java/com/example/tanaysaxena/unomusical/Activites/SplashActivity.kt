package com.example.tanaysaxena.unomusical.Activites

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.example.tanaysaxena.unomusical.R

class SplashActivity : AppCompatActivity() {
    var permissionString=arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.PROCESS_OUTGOING_CALLS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if(!hasPermission(this@SplashActivity,permissionString)){
              ActivityCompat.requestPermissions(this@SplashActivity,permissionString,121)
         }
        else{
            Handler().postDelayed({
                var startAct= Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(startAct)
                this.finish()

            },1000)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            121->{
                if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED
                        && grantResults[1]==PackageManager.PERMISSION_GRANTED
                        && grantResults[2]==PackageManager.PERMISSION_GRANTED
                        && grantResults[3]==PackageManager.PERMISSION_GRANTED
                        && grantResults[4]==PackageManager.PERMISSION_GRANTED
                        ){
                    Handler().postDelayed({
                        var startAct= Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(startAct)
                        this.finish()

                    },1000)


                 } else{
                    Toast.makeText(this@SplashActivity,"Please give all Permissions",Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }
            else->{
    Toast.makeText(this@SplashActivity,"Something went Wrong",Toast.LENGTH_LONG).show()
        this.finish()
            }
        }
    }
    fun hasPermission(context: Context,permissions:Array<String>):Boolean{
        var hasAllpermission=true
        for(permission in permissions){
            val res=context.checkCallingOrSelfPermission(permission)
            if(res!=PackageManager.PERMISSION_GRANTED){
                hasAllpermission=false
            }
        }
        return hasAllpermission

    }
}
