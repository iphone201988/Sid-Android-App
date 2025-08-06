package com.tech.sid.ui.dashboard.dashboard_with_fragment.profile_edit

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.gson.Gson
import com.tech.sid.BR
import com.tech.sid.CommonFunctionClass
import com.tech.sid.R
import com.tech.sid.base.BaseActivity
import com.tech.sid.base.BaseViewModel
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.base.utils.BaseCustomDialog
import com.tech.sid.base.utils.BindingUtils
import com.tech.sid.base.utils.Status
import com.tech.sid.base.utils.showErrorToast
import com.tech.sid.data.api.Constants
import com.tech.sid.databinding.ActivityEditProfileBinding
import com.tech.sid.databinding.ActivitySignUpBinding
import com.tech.sid.databinding.DialogFilterBinding
import com.tech.sid.databinding.ItemCountryBinding
import com.tech.sid.databinding.SelectMediaFileBinding
import com.tech.sid.ui.auth.AuthCommonVM
import com.tech.sid.ui.auth.AuthModelLogin
import com.tech.sid.ui.auth.CountryModel
import com.tech.sid.ui.auth.OtpVerify
import com.tech.sid.ui.dashboard.chat_screen.ChatAdapter
import com.tech.sid.ui.dashboard.chat_screen.ChatApiResposeModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
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

    var valueProfile: AuthModelLogin? = null
    override fun onCreateView() {
        BindingUtils.screenFillView(this)
        valueProfile = sharedPrefManager.getProfileData()
        if (valueProfile != null) {
            binding.apply {
                firstNameEt.setText(valueProfile?.user?.firstName)
                lastNameEt.setText(valueProfile?.user?.lastName)
                lastNameEt.setText(valueProfile?.user?.lastName)
                enterEmail.setText(valueProfile?.user?.email)
                phoneNumberCardEt.setText(valueProfile?.user?.phoneNumber)
                birthOfDateEt.setText(valueProfile?.user?.DOB?.let { convertToDateOnly(it) })

            }

        }
        initOnClick()
        setCountryGender()
        apiObserver()
//        loadSvgImage(Constants.DEFAULT_FLAG_LINK)
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

                        Constants.PUT_EDIT_PROFILE -> {
                            try {
                                val responseModelModel: EditeModel? =
                                    BindingUtils.parseJson(it.data.toString())
                                valueProfile = sharedPrefManager.getProfileData()
                                valueProfile?.user?. DOB=responseModelModel?.user?.DOB?:""
                                valueProfile?.user?. _id=responseModelModel?.user?._id?:""
                                valueProfile?.user?. countryCode=responseModelModel?.user?.countryCode?:""
                                valueProfile?.user?. email=responseModelModel?.user?.email?:""
                                valueProfile?.user?. firstName=responseModelModel?.user?.firstName?:""
                                valueProfile?.user?. lastName=responseModelModel?.user?.lastName?:""
                                valueProfile?.user?. phoneNumber=responseModelModel?.user?.phoneNumber?:""
                                valueProfile?.user?. profileImage=responseModelModel?.user?.profileImage?:""
                                sharedPrefManager.setLoginData(Gson().toJson(valueProfile))
                                showToast(responseModelModel?.message)
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

        for (i in countryList.indices) {
            if (countryList[i].countryCode.replace("+", "") ==valueProfile?.user?.countryCode?.replace("+", "")){
                loadSvgImage(countryList[i].image)
                country=countryList[i].countryCode
            }
        }
    }


    private var countryList = mutableListOf<CountryModel>()

    private fun initOnClick() {
        viewModel.onClick.observe(this) {
            when (it?.id) {
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
                    }
                    val data: HashMap<String, Any> = hashMapOf(
                        "firstName" to binding.firstNameEt.text.toString().trim(), // Adjust binding if needed
                        "lastName" to  binding.lastNameEt.text.toString().trim(),
                        "DOB" to  binding.birthOfDateEt.text.toString().trim(),
                        "countryCode" to country,
                        "phoneNumber" to  binding.phoneNumberCardEt.text.toString().trim(),
//                        "timezone" to "Asia/Kolkata"
                    )
                    viewModel.postEditFunction(data)
                }

                R.id.calenderSelection, R.id.birth_of_dateEt, R.id.birth_of_dateLL -> {
                    calendarOpen()
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
        showFilterDialog = BaseCustomDialog(R.style.Dialog,this, R.layout.dialog_filter) {
        }
        getCountryAdapter()
        showFilterDialog.show()
        showFilterDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

    }
    var country: String = "91"
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

    private fun convertToDateOnly(input: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val zonedDateTime = java.time.ZonedDateTime.parse(input)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
            zonedDateTime.format(formatter)
        } else {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(input)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            outputFormat.timeZone = TimeZone.getDefault()
            outputFormat.format(date!!)
        }
    }

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
}