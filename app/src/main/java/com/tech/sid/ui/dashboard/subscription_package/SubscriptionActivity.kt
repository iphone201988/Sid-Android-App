package com.tech.sid.ui.dashboard.subscription_package

import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivitySubscriptionBinding
import com.tech.sid.databinding.ViewItemSubscriptionRvBinding
import com.tech.sid.ui.onboarding_ques.SubscriptionModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionActivity : BaseActivity<ActivitySubscriptionBinding>() {
    private val viewModel: SubscriptionVm by viewModels()
    private lateinit var subscriptionAdapter: SimpleRecyclerViewAdapter<SubscriptionModel, ViewItemSubscriptionRvBinding>
    private lateinit var billingClient: BillingClient
    private var selectedProductDetails: SubscriptionModel? = null
    private var selectedType:Int?=null
    override fun getLayoutResource(): Int {
        return R.layout.activity_subscription
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases(
                PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
            ).setListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    purchases.forEach { purchase ->
                        verifyPurchase(purchase)
                    }
                }
            }.build()
        initOnClick()
        initObserver()
        rvSubscription(binding.rvSubscription, 1)
    }

    private fun spinner(it: View) {
        val data = arrayOf(
            "Monthly",
            "Simulation Bundles",
            "Yearly",
        )
        val popupMenu = PopupMenu(this, it)
        data.forEachIndexed { index, course ->
            popupMenu.menu.add(0, index, index, course)
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            binding.tvPlan.text = menuItem.title.toString()
            val position = menuItem.itemId
            rvSubscription(binding.rvSubscription, position + 1)
            selectedProductDetails=null
            popupMenu.dismiss()
            true
        }
        popupMenu.show()
    }

    private fun rvSubscription(view: RecyclerView, int: Int) {
        val itemListData = ArrayList<SubscriptionModel>()
        /** month list **/
        itemListData.add(
            SubscriptionModel(
                "Free",
                "#FFEEEE",
                listOf(
                    "1 simulation/month",
                    "Limited access to simulation library",
                    "Access to AI journaling"
                ),
                "\$0",
                "/month",
                "Best for exploring the basics of emotional growth.",
                1

                )
        )

        itemListData.add(
            SubscriptionModel(
                "Premium",
                "#E9FFFF",
                listOf(
                    "15 simulation/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$7.99",
                "/month",
                "Best for steady, meaningful growth — a few times a week",
                1

                )
        )

        itemListData.add(
            SubscriptionModel(
                "Pro",
                "#F0EBFF",
                listOf(
                    "100 simulation/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$14.99",
                "/month",
                "Best for deep, consistent practice and transformative growth.",1
            )
        )


        /**-- simulation list  ----*/
        itemListData.add(
            SubscriptionModel(
                "5 simulation",
                "#FFEEEE",
                listOf(
                    "Instant access", "No monthly commitment", "Flexible learning"
                ),
                "\$2.99",
                "",
                "",2

                )
        )

        itemListData.add(
            SubscriptionModel(
                "10 simulation",
                "#E9FFFF",
                listOf(
                    "Instant access",
                    "More practice",
                    "Better value",

                    ),
                "\$5.49",
                "",
                "",2
                )
        )

        itemListData.add(
            SubscriptionModel(
                "25 simulation",
                "#F0EBFF",
                listOf(
                    "Extensive practice",
                    "Deepest insights",
                    "Significant savings",

                    ),
                "\$8.99",
                "",
                "",2
                )
        )
        /******   year list -*/
        itemListData.add(
            SubscriptionModel(
                "Free",
                "#FFEEEE",
                listOf(
                    "1 simulation/month",
                    "Limited access to simulation library",
                    "Access to AI journaling"
                ),
                "\$0",
                "/month",
                "Best for exploring the basics of emotional growth.",3

                )
        )

        itemListData.add(
            SubscriptionModel(
                "Premium",
                "#E9FFFF",
                listOf(
                    "15 simulation/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$69.99",
                "/year",
                "Best for steady, meaningful growth — a few times a week",3

                )
        )

        itemListData.add(
            SubscriptionModel(
                "Pro",
                "#F0EBFF",
                listOf(
                    "100 simulation/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$119.99",
                "/year",
                "Best for deep, consistent practice and transformative growth.",3

                )
        )
        /**********-----*/

        subscriptionAdapter = SimpleRecyclerViewAdapter(
            R.layout.view_item_subscription_rv, BR.bean
        ) { v, m, pos ->
            when (v.id) {
                R.id.mainLayout -> {
                    val previousSelectedPos =
                        subscriptionAdapter.list.indexOfFirst { it.isSelected == true }
                    if (previousSelectedPos != -1) {
                        subscriptionAdapter.list[previousSelectedPos].isSelected = false
                        subscriptionAdapter.notifyItemChanged(previousSelectedPos)
                    }
                    selectedProductDetails = m
                    selectedType=m.type
                    subscriptionAdapter.list[pos].isSelected = true
                    subscriptionAdapter.notifyItemChanged(pos)
                    subscriptionAdapter.notifyDataSetChanged()
                }
            }
        }
        view.adapter = subscriptionAdapter
        subscriptionAdapter.list =
            if (int == 1) itemListData.take(3) else if (int == 2) itemListData.subList(
                3, 6
            ) else itemListData.subList(6, 9)
        view.isNestedScrollingEnabled = true
    }


    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    if (selectedProductDetails == null) {
                        showToast("Please select product to continue")
                        return@observe
                    }
                    launchPurchaseFlow(selectedProductDetails)

                }

                R.id.back_button -> {
                    finish()
                }

                R.id.spinnerSelection -> {
                    spinner(it)
                }
                R.id.tvTerms->{
                    val termsUrl = "https://thryve-os-copy-53bbf16d.base44.app/terms"
                    val intent = Intent(Intent.ACTION_VIEW, termsUrl.toUri())
                    startActivity(intent)

                }
                R.id.tvPrivacy->{
                    val privacyUrl = "https://thryve-os-copy-53bbf16d.base44.app/privacy-policy"
                    val intent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
                    startActivity(intent)
                }

            }
        }
    }

    private fun initObserver() {
        viewModel.subsObserver.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {
                    hideLoading()
                    when (it.message) {
                        Constants.CREATE_SUBSCRIPTION -> {
                            try {

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }

                        Constants.PURCHASE_BUNDLE -> {
                            try {

                            } catch (e: Exception) {
                                showErrorToast(e.toString())
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                Status.UN_AUTHORIZE -> {
                    hideLoading()
                    showUnauthorised()
                }

                else -> {
                    hideLoading()
                }
            }
        }
    }


    /*** purchase **/
    fun establishConnection() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    showProducts()
                }
            }

            override fun onBillingServiceDisconnected() {
                establishConnection()
            }
        })
    }

    fun showProducts() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder().setProductId("your_product_id")
                .setProductType(BillingClient.ProductType.SUBS).build()
        )
        val params = QueryProductDetailsParams.newBuilder().setProductList(productList).build()

        billingClient.queryProductDetailsAsync(params) { billingResult, prodDetailsList ->
            // Handle loading into your UI (RecyclerView adapter etc.)
        }
    }

    fun launchPurchaseFlow(subsModel: SubscriptionModel?) {
        if (subsModel != null && subsModel.productDetails != null) {
            val offerToken =
                subsModel.productDetails?.subscriptionOfferDetails?.firstOrNull()?.offerToken
                    ?: return
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(subsModel.productDetails!!).setOfferToken(offerToken).build()
            )
            val billingFlowParams =
                BillingFlowParams.newBuilder().setProductDetailsParamsList(productDetailsParamsList)
                    .build()
            billingClient.launchBillingFlow(this, billingFlowParams)
        } else {
            showToast("Products Not Added Yet")
        }
    }

    fun verifyPurchase(purchase: Purchase) {
        val acknowledgePurchaseParams =
            AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Activate feature for user, e.g. set premium
                if (selectedType==2){
                    buySimulation()
                }
                else{
                    subscriptionApiCall()
                }
            }
        }
    }

    /** api call **/
    private fun subscriptionApiCall() {
        val request = HashMap<String, Any>()
        request["transaction_id"] = ""
        request["subscription_purchase_date"] = ""
        request["product_id"] = ""
        request["original_transaction_id"] = ""
        viewModel.buySubscription(request)
    }


    private fun buySimulation() {
        val request = HashMap<String, Any>()
        request["productId"] = ""
        viewModel.buySimulationSubscription(request)
    }
}