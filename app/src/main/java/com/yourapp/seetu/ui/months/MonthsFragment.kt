package com.yourapp.seetu.ui.months

import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.yourapp.seetu.R
import com.yourapp.seetu.adapter.MonthCustomAdapter
import com.yourapp.seetu.databinding.FragmentMonthsBinding
import com.yourapp.seetu.model.MonthModel

class MonthsFragment : Fragment() {
    private lateinit var binding: FragmentMonthsBinding
    private lateinit var database : DatabaseReference
    private val args : MonthsFragmentArgs by navArgs()
    private lateinit var mAdView : AdView

    private val adSize: AdSize
        get() {
            val display = requireActivity().windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density
            var adWidthPixels = outMetrics.widthPixels.toFloat()

            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireActivity(), adWidth)
        }

    private fun initializeAds(){
        // Calling Ads.
        MobileAds.initialize(requireActivity())

        mAdView = AdView(requireActivity())
        mAdView.adSize = adSize
        mAdView.adUnitId = "ca-app-pub-6773446513562001/3698440075"
        binding.monthsBannerAdViewContainer.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_months, container, false)
    }

    private fun setTitle(){
        binding.seetuNameTextView.text = args.seetuName

        val seetuPendingReference = database.child("user").child(args.userPhone).child(args.organizerPhone).child(args.seetuName)
            .child("pending")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.seetuTotalPendingAmount.text = "₹" + snapshot.value.toString()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        seetuPendingReference.addValueEventListener(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMonthsBinding.bind(view)

        database = Firebase.database.reference
        setTitle()
        initializeAds()

        val shader = LinearGradient(0f, 0f, 0f, binding.seetuNameTextView.textSize, Color.RED, Color.BLUE,
            Shader.TileMode.CLAMP)
        binding.seetuNameTextView.paint.shader = shader

        val seetuMonthsReference = database.child("user").child(args.userPhone).child(args.organizerPhone).child(args.seetuName)
            .child("months")
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val monthList = mutableListOf<MonthModel>()
                for(i in snapshot.children){
                    val month = i.getValue<MonthModel>() as MonthModel
                    monthList.add(month)
                }

                val recyclerView = binding.monthsRecyclerView
                val linearLayoutManager = LinearLayoutManager(activity?.applicationContext)

                recyclerView.layoutManager = linearLayoutManager

                val monthsCustomAdapter = MonthCustomAdapter(activity?.applicationContext, monthList)
                recyclerView.adapter = monthsCustomAdapter
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        seetuMonthsReference.addValueEventListener(listener)

    }
}