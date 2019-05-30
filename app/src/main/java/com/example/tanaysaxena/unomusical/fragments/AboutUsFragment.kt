package com.example.tanaysaxena.unomusical.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.tanaysaxena.unomusical.R


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [AboutUsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [AboutUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutUsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
       var view= inflater!!.inflate(R.layout.fragment_about_us, container, false)

    return view
    }

}// Required empty public constructor
