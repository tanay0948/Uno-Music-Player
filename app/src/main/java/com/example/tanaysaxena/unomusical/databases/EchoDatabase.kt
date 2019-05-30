package com.example.tanaysaxena.unomusical.databases

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.tanaysaxena.unomusical.song

class EchoDatabase:SQLiteOpenHelper{
    var  songList=ArrayList<song>()
    object staticated{
        val DB_NAME="FavouriteDatabase"
        val TABLE_NAME="favoriteTable"
        val COLUMN_ID="songId"
        val COLUMN_SONG_TITLE="songTitle"
        val COLUMN_SONG_ARTIST="songArtist"
        val COLUMN_SONG_PATH="songPath"
        val DB_VERSION=1
    }
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE "+staticated.TABLE_NAME+"( "+staticated.COLUMN_ID+" INT, "+staticated.COLUMN_SONG_ARTIST+" String, " +
                staticated.COLUMN_SONG_TITLE+" String, "+staticated.COLUMN_SONG_PATH+" String);")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        }

    constructor(context: Context?, name: String?, factory: SQLiteDatabase.CursorFactory?, version: Int) : super(context, name, factory, version)
    constructor(context: Context?) : super(context, staticated.DB_NAME, null, staticated.DB_VERSION)

    fun storeAsFavorite(id:Int?,artist:String?,title:String?,path:String?){
        val db=this.writableDatabase
        var contentvalues=ContentValues();
        contentvalues.put(staticated.COLUMN_ID,id)
        contentvalues.put(staticated.COLUMN_SONG_ARTIST,artist)
        contentvalues.put(staticated.COLUMN_SONG_TITLE,title)
        contentvalues.put(staticated.COLUMN_SONG_PATH,path)
        db.insert(staticated.TABLE_NAME,null,contentvalues)
        db.close()
    }
    fun queryDBlist():ArrayList<song>?{
        try{
            val db=this.readableDatabase
            val query="SELECT * FROM "+staticated.TABLE_NAME
            var csor=db.rawQuery(query,null)
            if(csor.moveToFirst()){
                do{
                    var _id=csor.getInt(csor.getColumnIndexOrThrow(staticated.COLUMN_ID))
                    var _artist=csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONG_ARTIST))
                    var _title=csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONG_TITLE))
                    var _path=csor.getString(csor.getColumnIndexOrThrow(staticated.COLUMN_SONG_PATH))
                    songList.add(song(_id.toLong(),_title,_artist,_path,0))
                }while(csor.moveToNext())
            }else{
                return null
            }

        }catch(e:Exception){
            e.printStackTrace()
        }
        return songList
    }
    fun checkIfIdExists(id:Int?):Boolean{
        var storeId=-1090
        val db=this.readableDatabase
        var query="SELECT * FROM "+staticated.TABLE_NAME+" WHERE " +staticated.COLUMN_ID+" = $id"
        var csor=db.rawQuery(query,null)
        if(csor.moveToFirst()){
            do{
                storeId=csor.getInt(csor.getColumnIndexOrThrow(staticated.COLUMN_ID))
            }while(csor.moveToNext())
        }
        else{
            return false
        }
        return storeId!=-1090
    }
    fun deleteFavourite(_id:Int){
        val db=this.writableDatabase
        db.delete(staticated.TABLE_NAME,staticated.COLUMN_ID+" = $_id",null)
        db.close()
    }
    fun checkSize():Int{
        var counter=0
        val db=this.readableDatabase
        var query="SELECT * FROM "+staticated.TABLE_NAME
        var csor=db.rawQuery(query,null)
        if(csor.moveToFirst()){
            do{
               counter=counter+1
            }while(csor.moveToNext())
        }
        else{
            return 0
        }
        return counter

    }
}