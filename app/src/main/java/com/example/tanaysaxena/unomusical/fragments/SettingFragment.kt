package com.example.tanaysaxena.unomusical.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import com.example.tanaysaxena.unomusical.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [SettingFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {
    var myactivity: Activity?=null
    var shakeswitch:Switch?=null
    object statified{
        var MY_PREFS_NAME="shake feature"
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view= inflater!!.inflate(R.layout.fragment_setting, container, false)
        shakeswitch=view?.findViewById(R.id.SwitchShake)
         setHasOptionsMenu(true)
        activity.title="Settings"
        return view }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        myactivity=context as Activity
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        myactivity=activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var prefs=myactivity?.getSharedPreferences(statified.MY_PREFS_NAME,Context.MODE_PRIVATE)
        var isAllowed=prefs?.getBoolean("feature",false)
        if(isAllowed as Boolean){
            shakeswitch?.isChecked=true
        }
        else{
            shakeswitch?.isChecked=false
        }
        shakeswitch?.setOnCheckedChangeListener({ buttonView: CompoundButton?, isChecked: Boolean ->
            if(isChecked){
                var editor=myactivity?.getSharedPreferences(statified.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",true)
                editor?.commit()
            }else{
                var editor=myactivity?.getSharedPreferences(statified.MY_PREFS_NAME,Context.MODE_PRIVATE)?.edit()
                editor?.putBoolean("feature",false)
                editor?.commit()
            }

        })

    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
           val item=menu?.findItem(R.id.action_sort)
            item?.isVisible=false

    }

}// Required empty public constructor
