package com.yourapp.seetu.ui.bankDetails

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.yourapp.seetu.R
import com.yourapp.seetu.databinding.FragmentBankDetailsBinding
import java.lang.ref.ReferenceQueue

const val AD_UNIT_ID = "ca-app-pub-6773446513562001/7321172070"
const val TAG = "MainActivity"
class BankDetailsFragment : Fragment() {
    private val args : BankDetailsFragmentArgs by navArgs()
    private lateinit var binding : FragmentBankDetailsBinding
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bank_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentBankDetailsBinding.bind(view)

        val clipboard = requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        if (!mAdIsLoading && mInterstitialAd == null) {
            mAdIsLoading = true
            loadAd()
        }

        val shader = LinearGradient(0f, 0f, 0f, binding.detailsBankDetailsText.textSize, Color.RED,
        Color.BLUE, Shader.TileMode.MIRROR)
        binding.detailsBankDetailsText.paint.shader = shader

        binding.detailsName.text = args.organizerModel.name
        binding.detailsBankName.text = args.organizerModel.bankName
        binding.detailsBranch.text = args.organizerModel.branch
        binding.detailsAccountNumber.text = args.organizerModel.accountNumber
        binding.detailsIfsc.text = args.organizerModel.ifsc
        binding.detailsPhone.text = args.organizerModel.phone

        binding.copyAccountNumber.setOnClickListener{
            val accNum = binding.detailsAccountNumber.text
            if(accNum != ""){
                val clip = ClipData.newPlainText("simple text", accNum)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "Account Number Copied!", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "No Account Number Available", Toast.LENGTH_SHORT).show()
        }

        binding.copyIfsc.setOnClickListener{
            val ifsc = binding.detailsIfsc.text
            if(ifsc != ""){
                val clip = ClipData.newPlainText("simple text", ifsc)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(requireContext(), "IFSC Code Copied!", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "No IFSC Code Available", Toast.LENGTH_SHORT).show()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            showInterstitial()
            findNavController().popBackStack()
        }
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