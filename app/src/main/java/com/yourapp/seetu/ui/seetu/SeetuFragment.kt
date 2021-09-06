package com.yourapp.seetu.ui.seetu

import android.app.ActionBar
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.renderscript.Sampler
import android.util.DisplayMetrics
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
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
import com.yourapp.seetu.adapter.SeetuCustomAdapter
import com.yourapp.seetu.databinding.FragmentSeetuBinding
import com.yourapp.seetu.model.OrganizerModel

class SeetuFragment : Fragment() {
    private lateinit var binding: FragmentSeetuBinding
    private lateinit var database : DatabaseReference
    private val args : SeetuFragmentArgs by navArgs()
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
        mAdView.adUnitId = "ca-app-pub-6773446513562001/3176723560"
        binding.seetuBannerAdViewContainer.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_seetu, container, false)
    }

    private fun setTitle(){
        binding.organizerNameTextView.text = args.organizerName
        binding.organizerContactNumber.text = args.organizerPhone

        val orgUserInfoReference = database.child("organizerUserInfo").child(args.organizerPhone).child(args.userPhone)
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val overallPending = snapshot.child("pending").value.toString()
                binding.seetuOverallPendingAmount.text = "₹" + overallPending
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        orgUserInfoReference.addListenerForSingleValueEvent(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSeetuBinding.bind(view)

        database = Firebase.database.reference
        setTitle()
        initializeAds()

        val shader = LinearGradient(0f, 0f, 0f, binding.organizerNameTextView.textSize, Color.RED, Color.BLUE,
            Shader.TileMode.CLAMP)
        binding.organizerNameTextView.paint.shader = shader

        val userReference = database.child("user").child(args.userPhone).child(args.organizerPhone)
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val seetuNameList = mutableListOf<String>()
                val seetuPendingList = mutableListOf<String>()
                for(seetu in snapshot.children){
                    val name = seetu.child("name").value.toString()
                    val pending = seetu.child("pending").value.toString()
                    seetuNameList.add(name)
                    seetuPendingList.add(pending)
                }

                val seetuReference = database.child("seets").child(args.organizerPhone)
                val seetuListener = object : ValueEventListener{
                    val seetuAmountList = mutableListOf<String>()
                    val seetuMonthsList = mutableListOf<String>()
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(name in seetuNameList){
                            val amount = snapshot.child(name).child("amount").value.toString()
                            val months = snapshot.child(name).child("months").value.toString()
                            seetuAmountList.add(amount)
                            seetuMonthsList.add(months)
                        }

                        val recyclerView = binding.seetuRecyclerView
                        val linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
                        recyclerView.layoutManager = linearLayoutManager
                        val seetuCustomAdapter = SeetuCustomAdapter(activity?.applicationContext, seetuNameList, seetuPendingList,
                        seetuAmountList, seetuMonthsList) {position -> onListItemClicked(args.userPhone,
                            args.organizerPhone, seetuNameList[position]) }
                        recyclerView.adapter = seetuCustomAdapter
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
                seetuReference.addListenerForSingleValueEvent(seetuListener)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        userReference.addValueEventListener(listener)
    }

    private fun onListItemClicked(userPhone : String, organizerPhone : String, seetuName : String){
        val action = SeetuFragmentDirections.actionSeetuFragmentToMonthsFragment(userPhone, organizerPhone, seetuName)
        view?.findNavController()?.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.seetu_fragment_menu, menu)
    }

    private fun showBankDetails(){
        val organizerInfoRef = database.child("organizerInfo").child(args.organizerPhone)
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val orgInfo = snapshot.getValue<OrganizerModel>() as OrganizerModel

                val action = SeetuFragmentDirections.actionSeetuFragmentToBankDetailsFragment(orgInfo)
                view?.findNavController()?.navigate(action)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        organizerInfoRef.addListenerForSingleValueEvent(listener)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.bank_details -> {
                showBankDetails()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}