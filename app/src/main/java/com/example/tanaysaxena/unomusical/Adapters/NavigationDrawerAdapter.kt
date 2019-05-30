package com.example.tanaysaxena.unomusical.Adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.tanaysaxena.unomusical.Activites.MainActivity
import com.example.tanaysaxena.unomusical.R
import com.example.tanaysaxena.unomusical.fragments.AboutUsFragment
import com.example.tanaysaxena.unomusical.fragments.FavoriteFragment
import com.example.tanaysaxena.unomusical.fragments.MainScreenFragment
import com.example.tanaysaxena.unomusical.fragments.SettingFragment

class NavigationDrawerAdapter(_contentList:ArrayList<String>,_getImages:IntArray,context:Context): RecyclerView.Adapter<NavigationDrawerAdapter.NavViewHolder>() {
    var contentList:ArrayList<String>?=null
    var getImages:IntArray?=null
    var mcontext:Context?=null
    init{
        contentList=_contentList
        getImages=_getImages
        mcontext=context
    }
    class NavViewHolder(itemView: View?):RecyclerView.ViewHolder(itemView){
       var icon_get: ImageView?=null
        var text_get: TextView?=null
        var contentHolder:RelativeLayout?=null
        init{
            icon_get=itemView?.findViewById(R.id.icon_navdrawer)
            text_get=itemView?.findViewById(R.id.text_navdrawer)
            contentHolder=itemView?.findViewById(R.id.navdrawer_item_contentHolder)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavViewHolder {
        var itemview=LayoutInflater.from(parent?.context).inflate(R.layout.row_custom_navigationdrawer,parent,false)
        var returnthis=NavViewHolder(itemview)
        return returnthis

    }

    override fun onBindViewHolder(holder: NavViewHolder?, position: Int) {
      holder?.icon_get?.setBackgroundResource(getImages?.get(position) as Int)
        holder?.text_get?.setText(contentList?.get(position) as String)

       holder?.contentHolder?.setOnClickListener({
           if(position==0){
               var mainscreenfragment=MainScreenFragment()
                 (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.details_fragment, mainscreenfragment,"MainScreenFragment").addToBackStack("mainscreen").commit()


           }
           else if(position==1){
               var favorite= FavoriteFragment()
               (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.details_fragment, favorite,"FavouriteFragment").addToBackStack("favorite").commit()

           }
           else if(position==2){
               var setting= SettingFragment()
               (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.details_fragment, setting,"SettingFragment").addToBackStack("setting").commit()

           }
           else{
               var about= AboutUsFragment()
               (mcontext as MainActivity).supportFragmentManager.beginTransaction().replace(R.id.details_fragment, about,"AboutUs").addToBackStack("aboutus").commit()

           }
           MainActivity.statified.drawerlayout?.closeDrawers()
       })

    }

    override fun getItemCount(): Int {
            return contentList?.size as Int
    }

}