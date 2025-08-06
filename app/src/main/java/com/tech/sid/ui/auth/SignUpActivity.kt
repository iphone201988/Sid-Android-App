package com.tech.sid.ui.auth

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Handler
import android.os.Looper
import android.text.InputType
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
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivitySignUpBinding
import com.tech.sid.databinding.DialogFilterBinding
import com.tech.sid.databinding.ItemCountryBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.DashboardActivity
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
    var country: String = "91"
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
        loadSvgImage(Constants.DEFAULT_FLAG_LINK)
        apiObserver()
    }

    private fun showOrHidePassword() {
        if (binding.enterPassword.inputType == InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD) {
            binding.hideIcon.setImageResource(R.drawable.unhide_password)
            binding.enterPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            binding.hideIcon.setImageResource(R.drawable.hide_password)
            binding.enterPassword.inputType =
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        binding.enterPassword.setSelection(binding.enterPassword.length())
    }

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {

                R.id.hideIcon -> {
                    showOrHidePassword()
                }
                R.id.button -> {
                    if (binding.firstNameEt.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the first name")
                        return@observe
                    } else if (binding.lastNameEt.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the last name")
                        return@observe
                    } else if (binding.enterEmail.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the email id")
                        return@observe
                    } else if (binding.birthOfDateEt.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the birth date")
                        return@observe
                    } else if (binding.phoneNumberCardEt.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the phone number")
                        return@observe
                    } else if (binding.enterPassword.text.toString().trim().isEmpty()) {
                        showErrorToast("please enter the password")
                        return@observe
                    }
                    signUpFunction()

                }

                R.id.back_button -> {
                    finish()
                }

                R.id.calenderSelection, R.id.birth_of_dateEt, R.id.birth_of_dateLL-> {
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

    private fun signUpFunction() {
        OtpVerify.email = binding.enterEmail.text.toString().trim()
        val data = HashMap<String, Any>().apply {
            put("firstName", binding.firstNameEt.text.toString().trim())
            put("lastName", binding.lastNameEt.text.toString().trim())
            put("email", binding.enterEmail.text.toString().trim())
            put("password", binding.enterPassword.text.toString().trim())
            put("countryCode", country)
            put("DOB", binding.birthOfDateEt.text.toString().trim())
            put("phoneNumber", binding.phoneNumberCardEt.text.toString().trim())
            put("deviceType", "1")
            put("deviceToken", "aqswdasdasdasdx")
        }
        viewModel.signUpFunction(data)
    }

    private fun apiObserver() {
        viewModel.observeCommon.observe(this) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading("Loading")
                }

                Status.SUCCESS -> {

                    hideLoading()
                    when (it.message) {
                        Constants.SIGNUP_API -> {
                            try {
                                val signUpModel: AuthModelSign? =
                                    BindingUtils.parseJson(it.data.toString())
                                if (signUpModel?.success == true) {
                                    startActivity(Intent(this, OtpVerify::class.java))
                                } else {
                                    signUpModel?.message?.let { it1 -> showErrorToast(it1) }
                                }
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

    private lateinit var counrty: SimpleRecyclerViewAdapter<CountryModel, ItemCountryBinding>
    private lateinit var showFilterDialog: BaseCustomDialog<DialogFilterBinding>
    private fun filterDialog() {
        showFilterDialog = BaseCustomDialog(R.style.Dialog,this, R.layout.dialog_filter) {
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
                    country = m.countryCode
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

        val resourceId =
            this.resources?.getIdentifier("country_country_code", "raw", this.packageName)
//        val resourceId = this.resources?.getIdentifier("countrynames", "raw", this.packageName)
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

                val displayFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
//                val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
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

    private fun loadSvgImage(url: String) {
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
    var isSelected: Boolean,
    var countryCode: String
)