package com.enigmabottle.data

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BillingManager(
    private val context: Context,
    private val coroutineScope: CoroutineScope,
    private val onPremiumPurchased: (Boolean) -> Unit,
    private val onHintsPurchased: (Int) -> Unit,
    private val onXRayPurchased: (Int) -> Unit,
    private val onRevealPurchased: (Int) -> Unit,
    private val onFreezePurchased: (Int) -> Unit,
    private val onCoinsPurchased: (Int) -> Unit,
    private val onComboPurchased: () -> Unit
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_LIFETIME_PREMIUM = "enigma_bottles_premium_lifetime"
        const val PRODUCT_PACK_HINTS = "enigma_bottles_pack_hints"
        const val PRODUCT_PACK_XRAY = "enigma_bottles_pack_xray"
        const val PRODUCT_PACK_REVEAL = "enigma_bottles_pack_reveal"
        const val PRODUCT_PACK_FREEZE = "enigma_bottles_pack_freeze"
        const val PRODUCT_COINS_500 = "enigma_bottles_coins_500"
        const val PRODUCT_COINS_1000 = "enigma_bottles_coins_1000"
        const val PRODUCT_COINS_5000 = "enigma_bottles_coins_5000"
        const val PRODUCT_COMBO_PACK = "enigma_bottles_combo_pack"
    }

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    private val _premiumPurchasedState = MutableStateFlow(false)
    val premiumPurchasedState = _premiumPurchasedState.asStateFlow()

    private val _productsDetailsMapState = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val productsDetailsMapState = _productsDetailsMapState.asStateFlow()

    private var isConnected = false

    init {
        startConnection()
    }

    fun startConnection() {
        Log.d(TAG, "Iniciando conexão com o Google Play Billing...")
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Google Play Billing conectado com sucesso!")
                    isConnected = true
                    queryPurchases()
                    queryProductDetails()
                } else {
                    Log.e(TAG, "Falha ao conectar com o Billing: ${billingResult.debugMessage}")
                    isConnected = false
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Serviço de faturamento desconectado. Tentando reconectar...")
                isConnected = false
            }
        })
    }

    fun queryProductDetails() {
        if (!isConnected) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_LIFETIME_PREMIUM)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PACK_HINTS)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PACK_XRAY)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PACK_REVEAL)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_PACK_FREEZE)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_COINS_500)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_COINS_1000)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_COINS_5000)
                .setProductType(BillingClient.ProductType.INAPP)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_COMBO_PACK)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val safeList = queryProductDetailsResult.productDetailsList ?: emptyList()
                val detailsMap = safeList.associateBy { it.productId }
                Log.d(TAG, "Detalhes dos produtos carregados: ${detailsMap.keys}")
                _productsDetailsMapState.value = detailsMap
            } else {
                Log.e(TAG, "Erro ao buscar detalhes do produto: ${billingResult.debugMessage}")
            }
        }
    }

    fun queryPurchases() {
        if (!isConnected) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult: BillingResult, purchaseList: List<Purchase>? ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var premiumFound = false
                val safePurchases = purchaseList ?: emptyList()
                for (purchase in safePurchases) {
                    handlePurchase(purchase)
                    
                    if (purchase.products.contains(PRODUCT_LIFETIME_PREMIUM) && 
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        premiumFound = true
                    }
                }
                _premiumPurchasedState.value = premiumFound
                onPremiumPurchased(premiumFound)
            } else {
                Log.e(TAG, "Erro ao buscar compras do usuário: ${billingResult.debugMessage}")
            }
        }
    }

    fun launchBillingFlow(activity: Activity, productId: String): Boolean {
        val productDetails = _productsDetailsMapState.value[productId]
        if (!isConnected || productDetails == null) {
            Log.w(TAG, "BillingClient não está conectado ou detalhes do produto ($productId) não foram carregados.")
            return false
        }

        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        return billingResult.responseCode == BillingClient.BillingResponseCode.OK
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "Compra cancelada pelo usuário.")
        } else {
            Log.e(TAG, "Erro na atualização da compra: ${billingResult.debugMessage} (código: ${billingResult.responseCode})")
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        for (productId in purchase.products) {
            if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                when (productId) {
                    PRODUCT_LIFETIME_PREMIUM -> {
                        Log.d(TAG, "Processando compra premium bem-sucedida...")
                        if (!purchase.isAcknowledged) {
                            acknowledgePurchase(purchase)
                        } else {
                            _premiumPurchasedState.value = true
                            onPremiumPurchased(true)
                        }
                    }
                    PRODUCT_PACK_HINTS -> {
                        Log.d(TAG, "Processando compra de pacote de dicas consumível...")
                        consumePurchaseLocally(purchase) {
                            onHintsPurchased(10)
                        }
                    }
                    PRODUCT_PACK_XRAY -> {
                        Log.d(TAG, "Processando compra de pacote de raio-x consumível...")
                        consumePurchaseLocally(purchase) {
                            onXRayPurchased(10)
                        }
                    }
                    PRODUCT_PACK_REVEAL -> {
                        Log.d(TAG, "Processando compra de pacote de revelar consumível...")
                        consumePurchaseLocally(purchase) {
                            onRevealPurchased(10)
                        }
                    }
                    PRODUCT_PACK_FREEZE -> {
                        Log.d(TAG, "Processando compra de pacote de congelar consumível...")
                        consumePurchaseLocally(purchase) {
                            onFreezePurchased(10)
                        }
                    }
                    PRODUCT_COINS_500 -> {
                        Log.d(TAG, "Processando compra de 500 moedas...")
                        consumePurchaseLocally(purchase) {
                            onCoinsPurchased(500)
                        }
                    }
                    PRODUCT_COINS_1000 -> {
                        Log.d(TAG, "Processando compra de 1000 moedas...")
                        consumePurchaseLocally(purchase) {
                            onCoinsPurchased(1000)
                        }
                    }
                    PRODUCT_COINS_5000 -> {
                        Log.d(TAG, "Processando compra de 5000 moedas...")
                        consumePurchaseLocally(purchase) {
                            onCoinsPurchased(5000)
                        }
                    }
                    PRODUCT_COMBO_PACK -> {
                        Log.d(TAG, "Processando compra do combo pack...")
                        consumePurchaseLocally(purchase) {
                            onComboPurchased()
                        }
                    }
                }
            }
        }
    }

    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Compra confirmada com sucesso na Google Play!")
                        _premiumPurchasedState.value = true
                        onPremiumPurchased(true)
                    } else {
                        Log.e(TAG, "Falha ao confirmar compra: ${billingResult.debugMessage}")
                    }
                }
            }
        }
    }

    private fun consumePurchaseLocally(purchase: Purchase, onConsumedSuccess: () -> Unit) {
        val consumeParams = ConsumeParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                billingClient.consumeAsync(consumeParams) { billingResult, purchaseToken ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Compra consumida com sucesso no Google Play!")
                        coroutineScope.launch(Dispatchers.Main) {
                            onConsumedSuccess()
                        }
                    } else {
                        Log.e(TAG, "Erro ao consumir compra: ${billingResult.debugMessage}")
                    }
                }
            }
        }
    }
}
