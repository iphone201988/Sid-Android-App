package com.tech.sid.ui.dashboard.subscription_package
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivitySubscriptionBinding
import com.tech.sid.databinding.ViewItemSubscriptionRvBinding
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.onboarding_ques.SubscriptionModel
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class SubscriptionActivity : BaseActivity<ActivitySubscriptionBinding>() {
    private val viewModel: SubscriptionVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_subscription
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()

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
            popupMenu.dismiss()
            true
        }
        popupMenu.show()
    }

    private fun rvSubscription(view: RecyclerView, int: Int) {
        val itemListData = ArrayList<SubscriptionModel>()
        itemListData.add(
            SubscriptionModel(
                "Free",
                "#FFEEEE",
                listOf(
                    "3 simulations/month",
                    "Limited access to simulation library",
                    "Access to AI journaling"
                ),
                "\$0",
                "/month",
                "Best for exploring the basics of emotional growth."
            )
        )

        itemListData.add(
            SubscriptionModel(
                "Premium",
                "#E9FFFF",
                listOf(
                    "15 simulations/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$7.99",
                "/month",
                "Best for steady, meaningful growth — a few times a week"
            )
        )

        itemListData.add(
            SubscriptionModel(
                "Pro",
                "#E9FFFF",
                listOf(
                    "100 simulations/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$14.99",
                "/month",
                "Best for deep, consistent practice and transformative growth."
            )
        )


        /**------*/

        itemListData.add(
            SubscriptionModel(
                "5 Simulations",
                "#FFEEEE",
                listOf(
                    "Instant access",
                    "No monthly commitment",
                    "Flexible learning"
                ),
                "\$2.99",
                "",
                ""
            )
        )

        itemListData.add(
            SubscriptionModel(
                "10 Simulations",
                "#E9FFFF",
                listOf(
                    "Instant access",
                    "More practice",
                    "Better value",

                    ),
                "\$5.49",
                "",
                ""
            )
        )

        itemListData.add(
            SubscriptionModel(
                "25 Simulations",
                "#E9FFFF",
                listOf(
                    "Extensive practice",
                    "Deepest insights",
                    "Significant savings",

                    ),
                "\$8.99",
                "",
                ""
            )
        )
        /******-*/
        itemListData.add(
            SubscriptionModel(
                "Free",
                "#FFEEEE",
                listOf(
                    "3 simulations/month",
                    "Limited access to simulation library",
                    "Access to AI journaling"
                ),
                "\$0",
                "/month",
                "Best for exploring the basics of emotional growth."
            )
        )

        itemListData.add(
            SubscriptionModel(
                "Premium",
                "#E9FFFF",
                listOf(
                    "15 simulations/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$69.99",
                "/year",
                "Best for steady, meaningful growth — a few times a week"
            )
        )

        itemListData.add(
            SubscriptionModel(
                "Pro",
                "#E9FFFF",
                listOf(
                    "100 simulations/month",
                    "Full access to simulation library",
                    "Access to Personalized scenarios based on your emotional patterns",
                    "Access to AI journaling",
                ),
                "\$119.99",
                "/year",
                "Best for deep, consistent practice and transformative growth."
            )
        )
        /**********-----*/

        val adapter: SimpleRecyclerViewAdapter<SubscriptionModel, ViewItemSubscriptionRvBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.view_item_subscription_rv, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        startActivity(Intent(this, TodayJournal::class.java))
                    }
                }
            }
        view.adapter = adapter
        adapter.list = if (int == 1) itemListData.take(3) else if (int == 2) itemListData.subList(
            3,
            6
        ) else itemListData.subList(6, 9)
        view.isNestedScrollingEnabled = true
    }


    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {

                    startActivity(Intent(this, TodayJournal::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

                R.id.spinnerSelection -> {
                    spinner(it)
                }

            }
        }
    }
}