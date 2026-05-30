package com.enigmabottle.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

object AdManager {
    private const val TAG = "AdManager"

    // IDs reais do Google AdMob para produção
    private const val TEST_INTERSTITIAL_ID = "ca-app-pub-9087988028481533/2334570209"
    private const val TEST_REWARDED_ID = "ca-app-pub-9087988028481533/1274815578"

    private var mInterstitialAd: InterstitialAd? = null
    private var mRewardedAd: RewardedAd? = null
    private var isInitializing = false
    private var isInitialized = false

    fun initialize(context: Context, onInitComplete: (() -> Unit)? = null) {
        if (isInitialized || isInitializing) {
            onInitComplete?.invoke()
            return
        }
        isInitializing = true
        Log.d(TAG, "Inicializando MobileAds SDK...")
        MobileAds.initialize(context) { _ ->
            isInitialized = true
            isInitializing = false
            Log.d(TAG, "MobileAds SDK inicializado com sucesso!")
            
            // Pré-carrega os anúncios logo após a inicialização
            loadInterstitial(context)
            loadRewardedAd(context)
            
            onInitComplete?.invoke()
        }
    }

    // --- ANÚNCIO INTERSTICIAL ---

    fun loadInterstitial(context: Context) {
        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Carregando anúncio intersticial...")
        InterstitialAd.load(
            context,
            TEST_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Falha ao carregar intersticial: ${adError.message}")
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Intersticial carregado com sucesso!")
                    mInterstitialAd = interstitialAd
                }
            }
        )
    }

    fun showInterstitial(activity: Activity, onAdClosed: (() -> Unit)? = null) {
        val ad = mInterstitialAd
        if (ad != null) {
            Log.d(TAG, "Exibindo anúncio intersticial...")
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Anúncio intersticial fechado pelo usuário.")
                    mInterstitialAd = null
                    // Carrega o próximo para a próxima chamada
                    loadInterstitial(activity)
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Falha ao exibir intersticial: ${adError.message}")
                    mInterstitialAd = null
                    loadInterstitial(activity)
                    onAdClosed?.invoke()
                }
            }
            ad.show(activity)
        } else {
            Log.w(TAG, "Tentativa de exibir intersticial, mas não estava carregado.")
            loadInterstitial(activity)
            onAdClosed?.invoke()
        }
    }

    // --- ANÚNCIO PREMIADO (REWARDED) ---

    fun loadRewardedAd(context: Context) {
        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Carregando anúncio premiado...")
        RewardedAd.load(
            context,
            TEST_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.e(TAG, "Falha ao carregar anúncio premiado: ${adError.message}")
                    mRewardedAd = null
                }

                override fun onAdLoaded(rewardedAd: RewardedAd) {
                    Log.d(TAG, "Anúncio premiado carregado com sucesso!")
                    mRewardedAd = rewardedAd
                }
            }
        )
    }

    fun showRewardedAd(activity: Activity, onRewardEarned: () -> Unit, onAdClosed: (() -> Unit)? = null) {
        val ad = mRewardedAd
        if (ad != null) {
            Log.d(TAG, "Exibindo anúncio premiado...")
            var rewardGiven = false
            ad.fullScreenContentCallback = object : com.google.android.gms.ads.FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Anúncio premiado fechado.")
                    mRewardedAd = null
                    loadRewardedAd(activity)
                    if (rewardGiven) {
                        onRewardEarned()
                    }
                    onAdClosed?.invoke()
                }

                override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                    Log.e(TAG, "Falha ao exibir anúncio premiado: ${adError.message}")
                    mRewardedAd = null
                    loadRewardedAd(activity)
                    onAdClosed?.invoke()
                }
            }
            ad.show(activity) { _ ->
                Log.d(TAG, "Recompensa concedida pelo anúncio.")
                rewardGiven = true
            }
        } else {
            Log.w(TAG, "Tentativa de exibir anúncio premiado, mas não estava carregado.")
            loadRewardedAd(activity)
            onAdClosed?.invoke()
        }
    }
}
