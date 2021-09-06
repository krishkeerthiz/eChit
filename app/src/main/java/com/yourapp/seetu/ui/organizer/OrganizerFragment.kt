package com.yourapp.seetu.ui.organizer

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yourapp.seetu.R
import com.yourapp.seetu.adapter.OrganizerCustomAdapter
import com.yourapp.seetu.databinding.FragmentOrganizerBinding
import com.yourapp.seetu.model.OrganizerInfoModel
import com.yourapp.seetu.ui.signIn.SignInActivity

const val AD_UNIT_ID = "ca-app-pub-6773446513562001/1288926822"
const val TAG = "MainActivity"
class OrganizerFragment : Fragment() {
    private lateinit var binding : FragmentOrganizerBinding
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth
    private lateinit var phoneNumber : String
    private lateinit var mAdView : AdView

    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false

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

    private fun initializeBannerAds(){
        // Calling Ads.
        MobileAds.initialize(requireActivity())

        mAdView = AdView(requireActivity())
        mAdView.adSize = adSize
        mAdView.adUnitId = "ca-app-pub-6773446513562001/1263848422"
        binding.organizersBannerAdViewContainer.addView(mAdView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_organizer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentOrganizerBinding.bind(view)

        auth = Firebase.auth
        if(auth.currentUser == null){
            startActivity(Intent(requireActivity(), SignInActivity::class.java))
            requireActivity().finish()
            return
        }
        database = Firebase.database.reference
        phoneNumber = auth.currentUser!!.phoneNumber.toString()

        initializeBannerAds()
        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }

        val shader = LinearGradient(0f, 0f, 0f, binding.organizerHeadingTextView.textSize, Color.RED, Color.BLUE,
        Shader.TileMode.CLAMP)
        binding.organizerHeadingTextView.paint.shader = shader

        val userReference = database.child("user").child(phoneNumber)
        val listener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val organizersPhoneList = mutableListOf<String>()
                for(i in snapshot.children){
                    val key = i.key.toString()
                    organizersPhoneList.add(key)
                }

                val orgInfoReference = database.child("organizerInfo")
                val infoListener = object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val organizersList = mutableListOf<OrganizerInfoModel>()
                        for(number in organizersPhoneList){
                            val name = snapshot.child(number).child("name").value.toString()
                            val area = snapshot.child(number).child("area").value.toString()
                            organizersList.add(OrganizerInfoModel(name, area))
                        }
                        val recyclerView = binding.organizerRecyclerView
                        val linearLayoutManager = LinearLayoutManager(activity?.applicationContext)
                        recyclerView.layoutManager = linearLayoutManager
                        val organizerCustomAdapter = OrganizerCustomAdapter(organizersList){
                            position -> onListItemClicked(phoneNumber, organizersPhoneList[position], organizersList[position].name!!)
                        }

                        recyclerView.adapter = organizerCustomAdapter
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
                orgInfoReference.addListenerForSingleValueEvent(infoListener)
            }
            override fun onCancelled(error: DatabaseError) {
            }
        }
        userReference.addValueEventListener(listener)

    }

    private fun onListItemClicked(userPhone : String, organizerPhone : String, organizerName : String){
        showInterstitial()
        val action = OrganizerFragmentDirections.actionOrganizerFragmentToSeetuFragment(userPhone, organizerPhone, organizerName)
        view?.findNavController()?.navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.organizer_fragment_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.sign_out ->{
                signOut()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun signOut(){
        AuthUI.getInstance().signOut(requireActivity())
        startActivity(Intent(requireActivity(), SignInActivity::class.java))
        requireActivity().finish()
    }

    private fun loadAd() {
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            requireActivity(), AD_UNIT_ID, adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false

                }
            }
        )
    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    mInterstitialAd = null
                    loadAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                }
            }
            mInterstitialAd?.show(requireActivity())
        }
    }
}