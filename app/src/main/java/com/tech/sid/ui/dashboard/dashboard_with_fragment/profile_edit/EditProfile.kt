package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.gson.Gson
import com.tech.sid.BR
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BaseCustomDialog
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivityEditProfileBinding
import com.tech.sid.databinding.ActivitySignUpBinding
import com.tech.sid.databinding.DialogFilterBinding
import com.tech.sid.databinding.ItemCountryBinding
import com.tech.sid.ui.auth.AuthCommonVM
import com.tech.sid.ui.auth.CountryModel
import com.tech.sid.ui.auth.OtpVerify
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@AndroidEntryPoint
class EditProfile : BaseActivity<ActivityEditProfileBinding>() {

    private val viewModel: EditProfileVm by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_edit_profile
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        initOnClick()
        setCountryGender()
        loadSvgImage("https://cdn.jsdelivr.net/npm/country-flag-emoji-json@2.0.0/dist/images/IN.svg")
    }
    fun loadSvgImage(url: String) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val svgString = response.body?.string()
                    svgString?.let {
                        val svg = SVG.getFromString(it)
                        val handler = Handler(Looper.getMainLooper())
                        handler.post {
                            try {
                                val drawable = PictureDrawable(svg.renderToPicture())
                                binding.contryFlag.setImageDrawable(drawable)
                            } catch (e: SVGParseException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        })
    }
    private fun setCountryGender() {

        val resourceId = this.resources?.getIdentifier("countrynames", "raw", this.packageName)
        val inputStream = resources.openRawResource(resourceId!!)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val jsonArray = JSONArray(jsonString)
        val gson = Gson()
        countryList = mutableListOf()
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val country = gson.fromJson(jsonObject.toString(), CountryModel::class.java)
            countryList.add(country)
        }
    }
    private var countryList = mutableListOf<CountryModel>()

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {

                }
                R.id.back_button -> {
                    finish()
                }
                R.id.cardFlag -> {
                    filterDialog()
                }
                R.id.cardFlagArrow -> {
                    filterDialog()
                }
            }
        }

    }
    private lateinit var counrty: SimpleRecyclerViewAdapter<CountryModel, ItemCountryBinding>


    private lateinit var showFilterDialog: BaseCustomDialog<DialogFilterBinding>
    private fun filterDialog() {
        showFilterDialog = BaseCustomDialog(this, R.layout.dialog_filter) {
        }
        getCountryAdapter()
        showFilterDialog.show()
        showFilterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    private fun getCountryAdapter() {
        counrty = SimpleRecyclerViewAdapter(R.layout.item_country, BR.bean) { v, m, pos ->
            when (v?.id) {

                R.id.llCountry -> {

                    loadSvgImage(m.image)

                    showFilterDialog.dismiss()
                }
            }

            for (i in counrty.list.indices) {
                countryList[i].isSelected = i == pos
            }
            counrty.list = countryList
            counrty.notifyDataSetChanged()
        }
        showFilterDialog.binding.rvCountry.adapter = counrty

        counrty.list = countryList

    }
}