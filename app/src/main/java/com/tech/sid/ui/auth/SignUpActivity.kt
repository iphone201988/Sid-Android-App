package com.tech.sid.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.gson.Gson
import com.tech.sid.R
import com.tech.sid.BR
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BaseCustomDialog
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.databinding.ActivitySignUpBinding
import com.tech.sid.databinding.DialogFilterBinding
import com.tech.sid.databinding.ItemCountryBinding
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
class SignUpActivity : BaseActivity<ActivitySignUpBinding>() {

    private val viewModel: AuthCommonVM by viewModels()
    override fun getLayoutResource(): Int {
        return R.layout.activity_sign_up
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
    private fun calendarOpen() {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this, R.style.DialogTheme,
            { _, selectedYear, selectedMonth, selectedDay ->

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0)

                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDisplayDate = displayFormat.format(selectedCalendar.time)
                binding.birthOfDateEt.setText(formattedDisplayDate)

                val apiFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                apiFormat.timeZone = TimeZone.getTimeZone("UTC")
                val formattedApiDate = apiFormat.format(selectedCalendar.time)

                Log.d("FormattedDates", "Display: $formattedDisplayDate, API: $formattedApiDate")
            }, year, month, day
        )

        // ✅ Restrict to users 18 years or older
        val maxDate = Calendar.getInstance()
        maxDate.add(Calendar.YEAR, -18)
        datePickerDialog.datePicker.maxDate = maxDate.timeInMillis

        datePickerDialog.show()

        val positiveColor = ContextCompat.getColor(this, R.color.text_color_splash)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(positiveColor)
        datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(positiveColor)
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
                R.id.button -> {
                    startActivity(Intent(this, OtpVerify::class.java))
                }

                R.id.back_button -> {
                    finish()
                }

                R.id.calenderSelection -> {
                    calendarOpen()
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
}

data class CountryModel(
    val code: String,
    val emoji: String,
    val image: String,
    val name: String,
    val unicode: String,
    var isSelected: Boolean
)