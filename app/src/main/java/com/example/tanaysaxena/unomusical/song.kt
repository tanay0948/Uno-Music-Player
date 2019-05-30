package com.example.tanaysaxena.unomusical

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Tanay Saxena on 12/20/2017.
 */

class song(var songId:Long,var songTitle:String,var artist:String,var songData:String,var dateAdded:Long):Parcelable{
    override fun writeToParcel(dest: Parcel?, flags: Int) {
         }

    override fun describeContents(): Int {
    return 0
    }
    object statified{
        var nameComparator : Comparator<song> = Comparator<song> { song1, song2 ->
              var songone=song1.songTitle.toUpperCase()
              var songtwo=song2.songTitle.toUpperCase()
            songone.compareTo(songtwo)


        }
        var dateComparator : Comparator<song> = Comparator<song> { song1, song2 ->
            var songone=song1.dateAdded.toDouble()
            var songtwo=song2.dateAdded.toDouble()
            songone.compareTo(songtwo)


        }


    }

}