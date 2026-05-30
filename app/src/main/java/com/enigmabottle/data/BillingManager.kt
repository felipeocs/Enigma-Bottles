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
    private val onPremiumPurchased: (Boolean) -> Unit
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_LIFETIME_PREMIUM = "enigma_bottles_premium_lifetime"
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

    private val _productDetailsState = MutableStateFlow<ProductDetails?>(null)
    val productDetailsState = _productDetailsState.asStateFlow()

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

    // Consulta os detalhes do produto cadastrado na Google Play Store
    fun queryProductDetails() {
        if (!isConnected) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(PRODUCT_LIFETIME_PREMIUM)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val premiumProduct = productDetailsList.find { it.productId == PRODUCT_LIFETIME_PREMIUM }
                if (premiumProduct != null) {
                    Log.d(TAG, "Produto premium encontrado: ${premiumProduct.name} - ${premiumProduct.oneTimePurchaseOfferDetails?.formattedPrice}")
                    _productDetailsState.value = premiumProduct
                } else {
                    Log.w(TAG, "Produto premium não foi encontrado no Google Play Console.")
                }
            } else {
                Log.e(TAG, "Erro ao buscar detalhes do produto: ${billingResult.debugMessage}")
            }
        }
    }

    // Consulta as compras já efetuadas pelo usuário
    fun queryPurchases() {
        if (!isConnected) return

        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchaseList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var premiumFound = false
                for (purchase in purchaseList) {
                    if (purchase.products.contains(PRODUCT_LIFETIME_PREMIUM) && 
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        
                        Log.d(TAG, "Compra premium ativa encontrada!")
                        premiumFound = true
                        
                        // Verifica se a compra já foi confirmada (acknowledged)
                        if (!purchase.isAcknowledged) {
                            acknowledgePurchase(purchase)
                        }
                    }
                }
                _premiumPurchasedState.value = premiumFound
                onPremiumPurchased(premiumFound)
            } else {
                Log.e(TAG, "Erro ao buscar compras do usuário: ${billingResult.debugMessage}")
            }
        }
    }

    // Inicia o fluxo de compra
    fun launchBillingFlow(activity: Activity): Boolean {
        val productDetails = _productDetailsState.value
        if (!isConnected || productDetails == null) {
            Log.w(TAG, "BillingClient não está conectado ou detalhes do produto não foram carregados.")
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

    // Callback de quando uma compra é atualizada/efetuada
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
        if (purchase.products.contains(PRODUCT_LIFETIME_PREMIUM) && 
            purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            
            Log.d(TAG, "Processando compra premium bem-sucedida...")
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                _premiumPurchasedState.value = true
                onPremiumPurchased(true)
            }
        }
    }

    // Confirmação obrigatória da compra
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
}
