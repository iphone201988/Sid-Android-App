package com.tech.sid.base.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.PictureDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.LeadingMarginSpan
import android.text.style.MetricAffectingSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.caverock.androidsvg.SVG
import com.caverock.androidsvg.SVGParseException
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.tech.sid.BR
import com.tech.sid.CommonFunctionClass
import com.tech.sid.GradientView

import com.tech.sid.R
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.databinding.LogoutDeleteLayoutBinding
import com.tech.sid.databinding.RvInsightsCardItem2Binding
import com.tech.sid.databinding.RvInsightsCardItemBinding
import com.tech.sid.databinding.RvJournalCardItemBinding
import com.tech.sid.databinding.RvWantToTalkItemViewBinding
import com.tech.sid.databinding.SelectMediaFileBinding
import com.tech.sid.databinding.StartPracticingItemBinding
import com.tech.sid.databinding.StepperOnboardingSubRvItemBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.dashboard.dashboard_with_fragment.ai_coach_fragment.previous_simulations_folder.SimulationModel
import com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.DataListener
import com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalFragment
import com.tech.sid.ui.dashboard.journal_folder.TodayJournal
import com.tech.sid.ui.dashboard.result_screen.CustomCircleProgressView
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsights
import com.tech.sid.ui.dashboard.simulation_insights.SimulationInsightsModel
import com.tech.sid.ui.dashboard.start_practicing.InteractionModelPost
import com.tech.sid.ui.dashboard.start_practicing.ModelStartPracticing
import com.tech.sid.ui.onboarding_ques.JournalModel
import com.tech.sid.ui.onboarding_ques.JournalModel2
import com.tech.sid.ui.onboarding_ques.JournalModel4
import com.tech.sid.ui.onboarding_ques.SimulationRv
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import com.tech.sid.ui.onboarding_ques.StepperModel
import com.tech.sid.ui.onboarding_ques.StepperOnboardingModel
import com.tech.sid.ui.onboarding_ques.StepperPageModel
import com.tech.sid.ui.onboarding_ques.SubscriptionModel
import com.tech.sid.ui.onboarding_ques.SuggestionModel
import com.tech.sid.ui.onboarding_ques.WantToTalkModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object BindingUtils {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.CAMERA
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
    }


    fun hasPermissions(context: Context?, permissions: Array<String>?): Boolean {
        if (context != null && permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context, permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    var interactionModelPost: InteractionModelPost? = InteractionModelPost("", "", "", "")
    inline fun <reified T> parseJson(json: String): T? {
        return try {
            val gson: Gson = GsonBuilder()
                .serializeNulls() // Include nulls in the serialized output
                .registerTypeAdapter(T::class.java, JsonDeserializer<T> { jsonElement, _, _ ->
                    // Deserialize the JSON element as T
                    Gson().fromJson(jsonElement, T::class.java)
                })
                .create()
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @JvmStatic
    @BindingAdapter("simulationInSights")
    fun simulationInSights(textView: TextView, ignore: SimulationInsightsModel?) {
        if (ignore == null) {
            return
        }
        textView.text = ignore.scenarioSummary
    }


    data class CombinedData(
        var data: List<String>?,
        var ignore: Boolean
    )

    @JvmStatic
    fun combinedDataReturn(data: List<String>?, ignore: Boolean): CombinedData {
        return CombinedData(ignore = ignore, data = data)
    }

    @JvmStatic
    @BindingAdapter("bulletPoints")
    fun bulletPoints(textView: TextView, ignore: CombinedData) {

//        val resIds = listOf(
//            R.string.concern_delivery,
//            R.string.unsolicited_help,
//            R.string.emotional_authority
//        )
        val resIds = ignore.data
        if (resIds.isNullOrEmpty()) return
        val context = textView.context
        if (ignore.ignore) {
            textView.text = createBulletTextFromResIds(context, resIds)
        } else {

            val customTypeface = ResourcesCompat.getFont(context, R.font.inter_semi_bold)!!
            val customTypeface2 = ResourcesCompat.getFont(context, R.font.inter_medium)!!
            val styledText = createBulletTextFromResIds(
                context, resIds, customTypeface, customTypeface2, 3
            )

            textView.text = styledText
        }
    }


    @JvmStatic
    @BindingAdapter("bulletPointsSub")
    fun bulletPointsSub(textView: TextView, ignore: SubscriptionModel?) {
        if (ignore?.discrition!!.isEmpty()) return
        val context = textView.context
        textView.text = createBulletTextFromRes(context, ignore.discrition!!)
    }

    fun createBulletTextFromResIds(context: Context, resIds: List<String>): CharSequence {
        val bulletGap = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()

        val bulletSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
        )

        val builder = SpannableStringBuilder()
        val customTypeface = ResourcesCompat.getFont(context, R.font.inter_medium) // optional

        resIds.forEachIndexed { index, resId ->
            val text = /*context.getString(resId)*/resId
            val spannable = SpannableString(text)

            // Add bullet
            spannable.setSpan(
                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Bold or custom font for first letter
            if (text.isNotEmpty()) {
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                customTypeface?.let {
                    spannable.setSpan(
                        CustomTypefaceSpan(it),
                        0,
                        1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }

            builder.append(spannable)
            if (index != resIds.lastIndex) builder.append("\n")
        }

        return builder
    }

    //    fun createBulletTextFromResIds(context: Context, resIds: List<Int>): CharSequence {
//        val bulletGap = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
//        ).toInt()
//
//        val bulletSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
//        )
//
//        val builder = SpannableStringBuilder()
//
//        resIds.forEachIndexed { index, resId ->
//            val text = context.getString(resId)
//            val spannable = SpannableString(text)
//            spannable.setSpan(
//                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
//                0,
//                spannable.length,
//                0
//            )
//            builder.append(spannable)
//            if (index != resIds.lastIndex) builder.append("\n")
//        }
//
//        return builder
//    }

    fun createBulletTextFromResIds(
        context: Context,
        resIds: List<String>,
        firstTypeface: Typeface,
        restTypeface: Typeface,
        highlightWordsCount: Int // number of words to style with firstTypeface
    ): CharSequence {
        val bulletGap = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()

        val bulletSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
        )

        val builder = SpannableStringBuilder()

        resIds.forEachIndexed { index, resId ->
            val text = resId // If these are already strings
            val spannable = SpannableString(text)

            // Add bullet span
            spannable.setSpan(
                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Find position up to which we apply the first typeface
            val words = text.split(" ")
            var highlightLength = 0
            var wordCounter = 0

            for (i in words.indices) {
                highlightLength += words[i].length
                wordCounter++
                if (wordCounter == highlightWordsCount) break
                highlightLength++ // add space length
            }

            // Apply first font
            if (highlightLength > 0) {
                spannable.setSpan(
                    CustomTypefaceSpan(firstTypeface),
                    0,
                    highlightLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // Apply second font
            if (highlightLength < text.length) {
                spannable.setSpan(
                    CustomTypefaceSpan(restTypeface),
                    highlightLength,
                    text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            builder.append(spannable)
            if (index != resIds.lastIndex) builder.append("\n")
        }

        return builder
    }

    /*    fun createBulletTextFromResIds(
            context: Context,
            resIds: List<String>,
            firstTypeface: Typeface,
            restTypeface: Typeface,
            value: List<Int>
        ): CharSequence {
            val bulletGap = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
            ).toInt()

            val bulletSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
            )

            val builder = SpannableStringBuilder()

            resIds.forEachIndexed { index, resId ->
                val text = *//*context.getString(resId)*//*resId
            val spannable = SpannableString(text)

            // Bullet
            spannable.setSpan(
                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
                0,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            val highlightLength = value.getOrElse(index) { 0 }.coerceAtMost(text.length)

            // Apply first font to first N characters
            if (highlightLength > 0) {
                spannable.setSpan(
                    CustomTypefaceSpan(firstTypeface),
                    0,
                    highlightLength,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            // Apply second font to the rest
            if (highlightLength < text.length) {
                spannable.setSpan(
                    CustomTypefaceSpan(restTypeface),
                    highlightLength,
                    text.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            builder.append(spannable)
            if (index != resIds.lastIndex) builder.append("\n")
        }

        return builder
    }*/

    //    fun createBulletTextFromResIds(context: Context, resIds: List<Int>, typeface: Typeface,value:List<Int>): CharSequence {
//        val bulletGap = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
//        ).toInt()
//
//        val bulletSize = TypedValue.applyDimension(
//            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
//        )
//
//        val builder = SpannableStringBuilder()
//
//        resIds.forEachIndexed { index, resId ->
//            val text = context.getString(resId)
//            val spannable = SpannableString(text)
//
//            // Apply bullet span to the entire text
//            spannable.setSpan(
//                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
//                0,
//                spannable.length,
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//
//            // Apply different font or style to the first letter
//            spannable.setSpan(
//                CustomTypefaceSpan(typeface), // Custom span to apply Typeface
//                0,
//                value[index], // First letter
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
//            )
//
//            builder.append(spannable)
//            if (index != resIds.lastIndex) builder.append("\n")
//        }
//
//        return builder
//    }
    class CustomTypefaceSpan(private val newType: Typeface) : MetricAffectingSpan() {
        override fun updateMeasureState(paint: TextPaint) {
            apply(paint)
        }

        override fun updateDrawState(tp: TextPaint) {
            apply(tp)
        }

        private fun apply(paint: Paint) {
            paint.typeface = newType
        }
    }

    fun createBulletTextFromRes(context: Context, resIds: List<String>): CharSequence {
        val bulletGap = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()

        val bulletSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
        )

        val builder = SpannableStringBuilder()

        resIds.forEachIndexed { index, resId ->
            val spannable = SpannableString(resId)
            spannable.setSpan(
                CustomBulletSpan(gapWidth = bulletGap, bulletRadius = bulletSize),
                0,
                spannable.length,
                0
            )
            builder.append(spannable)
            if (index != resIds.lastIndex) builder.append("\n")
        }

        return builder
    }

    fun screenFillView(context: Activity) {
        context.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        context.window.statusBarColor = Color.TRANSPARENT
    }

    @BindingAdapter("setColorStepper")
    @JvmStatic
    fun setColorStepper(view: GlowCircleView, model: StepperModel?) {

//            if (ignore != null) {
//                view.fillColor=ignore.fillColor
//                view.glowColor=ignore.fillColorShadow
//
//
//            }
//            view.invalidate()
        if (model != null) {
            view.setGlowColor(model.fillColorShadow)
            view.setFillColor(model.fillColor)

        }

    }

    @JvmStatic
    @BindingAdapter("setSelectedBorder")
    fun setSelectedBorder(view: CardView, isSelected: Boolean?) {
        val strokeWidth = if (isSelected == true) 1 else 0
        val backgroundColor = (view.cardBackgroundColor.defaultColor ?: Color.WHITE)
        val drawable = GradientDrawable().apply {
            setColor(backgroundColor)
            cornerRadius = view.radius
            setStroke(
                strokeWidth.dpToPx(view.context),
                Color.BLACK
            ) // color is required, use black or any dummy color
        }
        view.background = drawable
    }

    @JvmStatic
    fun Int.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    @BindingAdapter("setColorCardBlur")
    @JvmStatic
    fun setColorCardBlur(view: CardView, isSelected: Boolean?) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val blurEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
//            view.setRenderEffect(blurEffect)
//        }

    }

    @BindingAdapter("setColorCard")
    @JvmStatic
    fun setColorCard(view: CardView, isSelected: String?) {

        view.setCardBackgroundColor(Color.parseColor(isSelected))


    }

    @BindingAdapter("setColorCardSub")
    @JvmStatic
    fun setColorCardSub(view: GradientView, isSelected: String?) {

        if (isSelected.equals("1")) {
            view.setFillColor(ContextCompat.getColor(view.context, R.color.card_color_result))
            view.setStartColor(ContextCompat.getColor(view.context, R.color.subStart))
            view.setEndColor(ContextCompat.getColor(view.context, R.color.subEnd))
            val startColor = ContextCompat.getColor(
                view.context,
                R.color.subStart
            ) // Replace with your color resource
            val endColor = ContextCompat.getColor(
                view.context,
                R.color.subEnd
            )   // Replace with your color resource
            view.setGradientColors(intArrayOf(startColor, endColor))
        } else {
            view.setFillColor(ContextCompat.getColor(view.context, R.color.transparent))
            view.setStartColor(ContextCompat.getColor(view.context, R.color.transparent))
            view.setEndColor(ContextCompat.getColor(view.context, R.color.transparent))
            val startColor = ContextCompat.getColor(
                view.context,
                R.color.transparent
            ) // Replace with your color resource
            val endColor = ContextCompat.getColor(
                view.context,
                R.color.transparent
            )   // Replace with your color resource
            view.setGradientColors(intArrayOf(startColor, endColor))
        }
    }

    @BindingAdapter("textColor")
    @JvmStatic
    fun textColor(view: TextView, isSelected: String) {
        view.setTextColor(Color.parseColor(isSelected))
    }

    @BindingAdapter("setImageLinearLayout")
    @JvmStatic
    fun setImageLinearLayout(view: LinearLayout, isSelected: Int?) {
        if (isSelected == null) {
            view.visibility = View.GONE

        } else {
            view.setBackgroundResource(isSelected)
        }


    }

    @BindingAdapter("setColorCardLL")
    @JvmStatic
    fun setColorCardLL(view: LinearLayout, isSelected: String) {

        if (isSelected.isEmpty()) {
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#CAB8FF"),
                    Color.parseColor("#B5EAEA")
                )
            )
            gradientDrawable.cornerRadius = 0f // Optional: round corners if needed
            view.background = gradientDrawable

        } else {
            view.setBackgroundColor(Color.parseColor(isSelected))
        }

    }

    @BindingAdapter("textColorGradientColor")
    @JvmStatic
    fun textColorGradientColor(textView: TextView, isSelected: String) {
        val fullText = isSelected
        val targetWord = "Overwhelmed"

        val spannable = SpannableString(fullText)

        val start = fullText.indexOf(targetWord)
        val end = start + targetWord.length

        // Ensure the target word exists in the text to avoid invalid spans
        if (start >= 0 && end <= fullText.length) {
            val paint = textView.paint
            val textWidth = paint.measureText(fullText, start, end)

            // Create the gradient shader for "Overwhelmed"
            val shader = LinearGradient(
                0f, 0f, textWidth, 0f,
                intArrayOf(Color.parseColor("#9773FF"), Color.parseColor("#00ACAC")),
                null,
                Shader.TileMode.CLAMP
            )

            // Apply gradient to "Overwhelmed"
            val gradientSpan = object : CharacterStyle() {
                override fun updateDrawState(tp: TextPaint) {
                    tp.shader = shader
                }
            }

            // Load DM Sans Italic font for "Overwhelmed" (ensure font is in assets/font/dm_sans_italic.ttf)
            val italicTypeface = ResourcesCompat.getFont(textView.context, R.font.inter_semi_bold)

            // Load Inter Medium font for the rest of the text (ensure font is in assets/font/inter_medium.ttf)
            val regularTypeface = ResourcesCompat.getFont(textView.context, R.font.inter_medium)

            // Apply DM Sans Italic to "Overwhelmed"
            val italicTypefaceSpan = CustomTypefaceSpan2("", italicTypeface!!)

            // Apply Inter Medium to the entire text first
            val regularTypefaceSpan = CustomTypefaceSpan2("", regularTypeface!!)
            spannable.setSpan(
                regularTypefaceSpan,
                0,
                fullText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply gradient and DM Sans Italic to "Overwhelmed"
            spannable.setSpan(gradientSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannable.setSpan(italicTypefaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            textView.text = spannable
        } else {
            // Fallback if "Overwhelmed" is not found: apply Inter Medium to all text
            val regularTypeface = ResourcesCompat.getFont(textView.context, R.font.inter_medium)
            val regularTypefaceSpan = CustomTypefaceSpan2("", regularTypeface!!)
            spannable.setSpan(
                regularTypefaceSpan,
                0,
                fullText.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            textView.text = spannable
        }
    }


//    @BindingAdapter("textColorGradientColor")
//    @JvmStatic
//    fun textColorGradientColor(textView: TextView, isSelected: String) {
////
////        val fullText = isSelected
////        val targetWord = "Overwhelmed"
////
////        val spannable = SpannableString(fullText)
////
////        val start = fullText.indexOf(targetWord)
////        val end = start + targetWord.length
////
////
////        val paint = textView.paint
////        val textWidth = paint.measureText(fullText, start, end)
////
////
////        val shader = LinearGradient(
////            0f, 0f, textWidth, 0f,
////            intArrayOf(Color.parseColor("#9773FF"), Color.parseColor("#00ACAC")),
////            null,
////            Shader.TileMode.CLAMP
////        )
////
////
////        val gradientSpan = object : CharacterStyle() {
////            override fun updateDrawState(tp: TextPaint) {
////                tp.shader = shader
////            }
////        }
////
////        spannable.setSpan(gradientSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
////
////        textView.text = spannable
//
//        val fullText = isSelected
//        val targetWord = "Overwhelmed"
//
//        val spannable = SpannableString(fullText)
//
//        val start = fullText.indexOf(targetWord)
//        val end = start + targetWord.length
//
//        // Ensure the target word exists in the text to avoid invalid spans
//        if (start >= 0 && end <= fullText.length) {
//            val paint = textView.paint
//            val textWidth = paint.measureText(fullText, start, end)
//
//            // Create the gradient shader for "Overwhelmed"
//            val shader = LinearGradient(
//                0f, 0f, textWidth, 0f,
//                intArrayOf(Color.parseColor("#9773FF"), Color.parseColor("#00ACAC")),
//                null,
//                Shader.TileMode.CLAMP
//            )
//
//            // Apply gradient to "Overwhelmed"
//            val gradientSpan = object : CharacterStyle() {
//                override fun updateDrawState(tp: TextPaint) {
//                    tp.shader = shader
//                }
//            }
//
//            // Load DM Sans Italic font for "Overwhelmed" (ensure font is in assets/font/inter_semi_bold.ttf)
//            val italicTypeface = Typeface.createFromAsset(textView.context.assets, "font/inter_semi_bold.ttf")
//                ?: Typeface.create("inter_semi_bold", Typeface.ITALIC) // Fallback to default italic
//
//            // Load DM Sans Regular font for the rest of the text (ensure font is in assets/font/inter_medium.ttf)
//            val regularTypeface = Typeface.createFromAsset(textView.context.assets, "font/inter_medium.ttf")
//                ?: Typeface.create("inter_medium", Typeface.NORMAL) // Fallback to default regular
//
//            // Apply DM Sans Italic to "Overwhelmed"
//            val italicTypefaceSpan = CustomTypefaceSpan2("", italicTypeface)
//
//            // Apply DM Sans Regular to the entire text first
//            val regularTypefaceSpan = CustomTypefaceSpan2("", regularTypeface)
//            spannable.setSpan(regularTypefaceSpan, 0, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            // Apply gradient and DM Sans Italic to "Overwhelmed"
//            spannable.setSpan(gradientSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            spannable.setSpan(italicTypefaceSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            textView.text = spannable
//        } else {
//            // Fallback if "Overwhelmed" is not found: apply DM Sans Regular to all text
//            val regularTypeface = Typeface.createFromAsset(textView.context.assets, "font/inter_medium.ttf")
//                ?: Typeface.create("sans-serif", Typeface.NORMAL)
//            val regularTypefaceSpan = CustomTypefaceSpan2("", regularTypeface)
//            spannable.setSpan(regularTypefaceSpan, 0, fullText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//            textView.text = spannable
//        }
//    }

    class CustomTypefaceSpan2(family: String, private val typeface: Typeface) :
        TypefaceSpan(family) {
        override fun updateDrawState(ds: TextPaint) {
            applyCustomTypeFace(ds, typeface)
        }

        override fun updateMeasureState(paint: TextPaint) {
            applyCustomTypeFace(paint, typeface)
        }

        private fun applyCustomTypeFace(paint: TextPaint, tf: Typeface) {
            paint.typeface = tf
        }
    }


    @BindingAdapter("setColorLinear")
    @JvmStatic
    fun setColorLinear(view: LinearLayout, isSelected: Int) {
        view.background = ContextCompat.getDrawable(view.context, isSelected)
    }

    @BindingAdapter("styleTv")
    @JvmStatic
    fun styleTv(view: TextView, isSelected: Boolean) {
        val styleRes = if (isSelected) {
            R.style.dm_sans_semibold_textview
        } else {
            R.style.inter_medium_textview
        }
        view.setTextAppearance(styleRes)
    }

    @BindingAdapter("customCircleProgressView")
    @JvmStatic
    fun customCircleProgressView(view: CustomCircleProgressView, isSelected: String) {
        view.setValue(isSelected.toFloat() ?: 0f)
    }

    @BindingAdapter("customCircleProgressViewText")
    @JvmStatic
    fun customCircleProgressViewText(view: CustomCircleProgressView, isSelected: String) {
        view.setTextCustom(isSelected)
    }

    /*

    // SIMPLE METHOD 1: Direct ViewHolder Access (Your Current Approach - Fixed)
    private void updateTextInRecyclerView(int postPosition, int mediaPosition, String newText) {
        try {
            // Find the post ViewHolder
            RecyclerView.ViewHolder postHolder = rvPost.findViewHolderForAdapterPosition(postPosition);

            if (postHolder instanceof SimpleRecyclerViewAdapter.SimpleViewHolder) {
                SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding> holder =
                    (SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding>) postHolder;

                if (holder.binding != null && holder.binding.rvMediaHome != null) {

                    // Find the media ViewHolder inside
                    RecyclerView.ViewHolder mediaHolder =
                        holder.binding.rvMediaHome.findViewHolderForAdapterPosition(mediaPosition);

                    if (mediaHolder instanceof MediaHomeAdapter.VideoViewHolder) {
                        MediaHomeAdapter.VideoViewHolder videoHolder =
                            (MediaHomeAdapter.VideoViewHolder) mediaHolder;

                        // UPDATE TEXT DIRECTLY - NO REFRESH!
                        if (videoHolder.mBinding != null && videoHolder.mBinding.exoPlayer != null) {
                            videoHolder.mBinding.exoPlayer.setText(newText);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Update", "Failed to update text", e);
        }
    }

    // SIMPLE METHOD 2: Loop Through Visible Items (Your Original Code - Fixed)
    private void updateAllVisibleTexts(String newText) {
        try {
            // Loop through visible post items
            for (int i = 0; i < rvPost.getChildCount(); i++) {
                View postView = rvPost.getChildAt(i);
                RecyclerView.ViewHolder postHolder = rvPost.getChildViewHolder(postView);

                if (postHolder instanceof SimpleRecyclerViewAdapter.SimpleViewHolder) {
                    SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding> holder =
                        (SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding>) postHolder;

                    if (holder.binding != null && holder.binding.rvMediaHome != null) {

                        // Loop through visible media items
                        RecyclerView mediaRV = holder.binding.rvMediaHome;
                        for (int j = 0; j < mediaRV.getChildCount(); j++) {
                            View mediaView = mediaRV.getChildAt(j);
                            RecyclerView.ViewHolder mediaHolder = mediaRV.getChildViewHolder(mediaView);

                            if (mediaHolder instanceof MediaHomeAdapter.VideoViewHolder) {
                                MediaHomeAdapter.VideoViewHolder videoHolder =
                                    (MediaHomeAdapter.VideoViewHolder) mediaHolder;

                                // UPDATE TEXT DIRECTLY - NO REFRESH!
                                if (videoHolder.mBinding != null && videoHolder.mBinding.exoPlayer != null) {
                                    videoHolder.mBinding.exoPlayer.setText(newText);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Update", "Failed to update texts", e);
        }
    }

    // SIMPLE METHOD 3: Update Data + View Together (BEST PRACTICE)
    private void updateTextSafely(int postPosition, int mediaPosition, String newText) {
        try {
            // STEP 1: Update your data source first
            if (postAdapter.getList().size() > postPosition) {
                // Update your actual data model here
                // Example: postAdapter.getList().get(postPosition).media.get(mediaPosition).text = newText;
            }

            // STEP 2: Update the visible view (if it exists)
            RecyclerView.ViewHolder postHolder = rvPost.findViewHolderForAdapterPosition(postPosition);
            if (postHolder instanceof SimpleRecyclerViewAdapter.SimpleViewHolder) {
                SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding> holder =
                    (SimpleRecyclerViewAdapter.SimpleViewHolder<ItemPostsBinding>) postHolder;

                if (holder.binding != null && holder.binding.rvMediaHome != null) {
                    RecyclerView.ViewHolder mediaHolder =
                        holder.binding.rvMediaHome.findViewHolderForAdapterPosition(mediaPosition);

                    if (mediaHolder instanceof MediaHomeAdapter.VideoViewHolder) {
                        MediaHomeAdapter.VideoViewHolder videoHolder =
                            (MediaHomeAdapter.VideoViewHolder) mediaHolder;

                        if (videoHolder.mBinding != null && videoHolder.mBinding.exoPlayer != null) {
                            videoHolder.mBinding.exoPlayer.setText(newText);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Update", "Failed to update safely", e);
        }
    }
    // HOW TO USE:
    // For single item: updateTextSafely(0, 0, "New Text");
    // For all visible: updateAllVisibleTexts("Same text for all");
    // For specific position: updateTextInRecyclerView(2, 1, "Text for post 2, media 1");
      */
    @BindingAdapter("rvRecyclerviewSuggest")
    @JvmStatic
    fun rvRecyclerviewSuggest(view: RecyclerView, isSelected: StepperPageModel) {
        val itemListData = ArrayList<SuggestionModel>()
        itemListData.add(SuggestionModel("#SilentProcessor", "#CAB8FF"))
        itemListData.add(SuggestionModel("#ConnectionSeeker", "#B5EAEA"))
        itemListData.add(SuggestionModel("#DirectResponder", "#FFD9DA"))
        itemListData.add(SuggestionModel("#ConnectionSeeker", "#B5EAEA"))
        itemListData.add(SuggestionModel("#SilentProcessor", "#CAB8FF"))
        val adapter: SimpleRecyclerViewAdapter<SuggestionModel, SuggestionItemCardBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.suggestion_item_card, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.mainLayout -> {
                        val temp = StringBuilder(isSelected.textLastText.get().toString())
                        temp.append(" ")
                        temp.append(itemListData[pos].titleValue)
                        val result: String = temp.toString()
                        isSelected.textLastText.set(result)
                        isSelected.textLastText.notifyChange()
                        val editText =
                            view.rootView.findViewById<AppCompatEditText>(R.id.editNoteSuggestion)
                        editText?.postDelayed({
                            editText.setSelection(editText.text?.length ?: 0)
                            editText.requestFocus()
                        }, 100)
                    }
                }

            }
        view.adapter = adapter
        adapter.list = itemListData

        /*     val postHolder = view.findViewHolderForAdapterPosition(0)
        if (postHolder is SimpleViewHolder<*>) {
            val binding = postHolder.binding
            if (binding is SuggestionItemCardBinding)
                binding.notifyChange()
            }*/
        view.isNestedScrollingEnabled = true
    }


    @BindingAdapter("rvPersonResponse")
    @JvmStatic
    fun rvPersonResponse(view: RecyclerView, isSelected: Boolean) {
        val itemListData = ArrayList<StartPracticingModel>()
        itemListData.add(
            StartPracticingModel(
                "Supportive",
                "#E9FFFF",
                R.drawable.hand_icon_blue
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Direct",
                "#FFFFFF",
                R.drawable.dart_icon
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Balanced",
                "#FFEEEE",
                R.drawable.equality
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Challenging",
                "#F0EBFF",
                R.drawable.search
            )
        )

        val adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.start_practicing_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }


    fun formatDate(inputDate: String): String {
        if (inputDate.isEmpty()) {
            return ""
        }
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(inputDate)

            val outputFormat = SimpleDateFormat("dd MMMM, yyyy", Locale.ENGLISH)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            ""
        }
    }

    data class CombinedDataGeneric(
        var firstData: com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalModel?,
        var secondData: DataListener
    )


    @JvmStatic
    fun combinedDataReturn(
        firstData: com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalModel?,
        secondData: DataListener
    ): CombinedDataGeneric {
        return CombinedDataGeneric(firstData = firstData, secondData = secondData)
    }

    @BindingAdapter("rvJournal")
    @JvmStatic
    fun rvJournal(
        view: RecyclerView,
//        isSelected: combinedDataReturn<>?
        isSelected: CombinedDataGeneric?
//        isSelected: com.tech.sid.ui.dashboard.dashboard_with_fragment.journal_fragment.JournalModel?
    ) {

        val colors = listOf(
            "#FFEEEE",
            "#E9FFFF",
            "#F0EBFF",

            )
        val colors2 = listOf(
            "#FFB06B", // Orange-ish
            "#00ACAC", // Teal
            "#9773FF", // Purple

        )
        if (isSelected?.firstData == null) {
            return
        }
        if (isSelected.firstData?.data == null) {
            return
        }
        val itemListData = ArrayList<JournalModel2>()
        for (i in isSelected.firstData?.data!!.indices) {
            val colorIndex = i % colors.size


            itemListData.add(
                JournalModel2(
                    colors[colorIndex],
                    colors2[colorIndex],
                    formatDate(isSelected.firstData!!.data[i].updatedAt),
                    isSelected.firstData!!.data[i].title,
                    isSelected.firstData!!.data[i].content,
                    isSelected.firstData!!.data[i]._id,
                    isSelected.firstData!!.data[i].__v,
                    isSelected.firstData!!.data[i].createdAt,
                    isSelected.firstData!!.data[i].tags,
                    isSelected.firstData!!.data[i].userId,
                )
            )
        }
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#E9FFFF",
//                "#00ACAC",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#F0EBFF",
//                "#9773FF",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )

        val adapter: SimpleRecyclerViewAdapter<JournalModel2, RvJournalCardItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_journal_card_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.binIconJournal -> {
                        functionDelete(view.context, isSelected.secondData, m.id.toString())

                    }

                    R.id.mainLayout -> {
                        TodayJournal.isEdited = true
                        TodayJournal.data = m
//                        CommonFunctionClass.logPrint(response="${Gson().toJson(m).toString()}")
                        view.context.startActivity(
                            Intent(
                                view.context,
                                TodayJournal::class.java
                            )
                        )
                    }

                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    private fun functionDelete(context: Context, secondData: DataListener, id: String) {
        lateinit var logOutDelete: BaseCustomDialog<LogoutDeleteLayoutBinding>
        logOutDelete = BaseCustomDialog(
            R.style.Dialog2,
            context,
            R.layout.logout_delete_layout
        ) { view ->
            view?.let {
                when (it.id) {
                    R.id.yesButton -> {
                        secondData.onDataReceived(id)
                        logOutDelete.dismiss()
                    }

                    R.id.tvCancel -> {
                        logOutDelete.dismiss()
                    }
                }
            }
        }

        logOutDelete.binding.tvTitle.text = "Are you sure you want to delete journal"
        logOutDelete.binding.Logout.text = "Delete"
        logOutDelete.window?.apply {
            setBackgroundDrawableResource(R.color.transparent_white_30)
            setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }
        logOutDelete.show()


    }

    @BindingAdapter("rvInsights")
    @JvmStatic
    fun rvInsights(view: RecyclerView, isSelected: Boolean) {
//        val itemListData = ArrayList<JournalModel>()
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#E9FFFF",
//                "#00ACAC",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#F0EBFF",
//                "#9773FF",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//
//        val adapter: SimpleRecyclerViewAdapter<JournalModel, RvInsightsCardItemBinding> =
//            SimpleRecyclerViewAdapter(
//                R.layout.rv_insights_card_item, BR.bean
//            ) { v, m, pos ->
//                when (v.id) {
//                }
//            }
//
//        view.adapter = adapter
//        adapter.list = itemListData
//        view.isNestedScrollingEnabled = true
    }

    @BindingAdapter("rvInsights2")
    @JvmStatic
    fun rvInsights2(
        view: RecyclerView,
        isSelected: com.tech.sid.ui.dashboard.dashboard_with_fragment.insights_fragment.InsightsModel?
    ) {

        if (isSelected == null) {
            return
        }

        if (isSelected.summaries == null) {
            return
        }
        val colors = listOf(
            "#FFEEEE",
            "#E9FFFF",
            "#F0EBFF",

            )
        val colors2 = listOf(
            "#FFB06B", // Orange-ish
            "#00ACAC", // Teal
            "#9773FF", // Purple

        )
        val itemListData = ArrayList<JournalModel4>()
        for (i in isSelected.summaries.indices) {
            val colorIndex = i % colors.size
            itemListData.add(
                JournalModel4(
                    isSelected.summaries[i].description ?: "",
                    isSelected.summaries[i].simulationId ?: "",
                    isSelected.summaries[i].title ?: "",
                    colors[colorIndex],
                    colors2[colorIndex],
                )
            )
        }


//
//        val itemListData = ArrayList<JournalModel>()
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#E9FFFF",
//                "#00ACAC",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#F0EBFF",
//                "#9773FF",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )

        val adapter: SimpleRecyclerViewAdapter<JournalModel4, RvInsightsCardItem2Binding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_insights_card_item_2, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.ViewInsights -> {

                        SimulationInsights.isChatRoute = false
                        SimulationInsights.simulationInsightsId = m.simulationId.toString()
                        view.context.startActivity(
                            Intent(
                                view.context,
                                SimulationInsights::class.java
                            )
                        )
                    }
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    data class GlowModel(
        var data: Int,
        var ignore: Int
    )

    @JvmStatic
    fun GlowModelReturn(data: Int, ignore: Int): GlowModel {
        return GlowModel(ignore = ignore, data = data)
    }

    @BindingAdapter("selectionGlowLayout")
    @JvmStatic
    fun selectionGlowLayout(view: GlowCircleView, isSelected: GlowModel?) {
        if (isSelected?.data == isSelected?.ignore || isSelected?.data!! < isSelected.ignore) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }


    @BindingAdapter("unSelectionGlowLayout")
    @JvmStatic
    fun unSelectionGlowLayout(view: GlowCircleView2, isSelected: GlowModel?) {
        if (isSelected?.data!! > isSelected.ignore) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    @BindingAdapter("rvInsights3")
    @JvmStatic
    fun rvInsights3(
        view: RecyclerView,
        isSelected: SimulationModel?
    ) {

        if (isSelected == null) {
            return
        }
        val colors = listOf(
            "#FFEEEE",
            "#E9FFFF",
            "#F0EBFF",

            )
        val colors2 = listOf(
            "#FFB06B", // Orange-ish
            "#00ACAC", // Teal
            "#9773FF", // Purple

        )
        val itemListData = ArrayList<SimulationRv>()
        for (i in isSelected.simulations.indices) {
            val colorIndex = i % colors.size
            itemListData.add(
                SimulationRv(

//                    "16 June, 2025",
//                    formatDate(isSelected.summaries[i].updatedAt),
                    isSelected.simulations[i].__v,
                    isSelected.simulations[i]._id,
                    isSelected.simulations[i].chatId,
                    isSelected.simulations[i].createdAt,
                    isSelected.simulations[i].momentId,
                    isSelected.simulations[i].momentTitle,
                    isSelected.simulations[i].relation,
                    isSelected.simulations[i].responseStyle,
                    isSelected.simulations[i].scenarioId,
                    isSelected.simulations[i].scenarioTitle,
                    isSelected.simulations[i].simulationInsight,
//                    isSelected.simulations[i].updatedAt,
                    formatDate(isSelected.simulations[i].updatedAt ?: ""),
                    isSelected.simulations[i].userId,
                    colors[colorIndex],
                    colors2[colorIndex],
                )
            )
        }


//
//        val itemListData = ArrayList<JournalModel>()
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#E9FFFF",
//                "#00ACAC",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#F0EBFF",
//                "#9773FF",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )
//        itemListData.add(
//            JournalModel(
//                "#FFEEEE",
//                "#FFB06B",
//                "16 June, 2025",
//                "Friend feeling down after work",
//                "Felt a bit overwhelmed today, but taking a walk really helped clear my head. Trying to focus on small wins."
//            )
//        )

        val adapter: SimpleRecyclerViewAdapter<SimulationRv, RvInsightsCardItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_insights_card_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                    R.id.ViewInsights -> {
                        SimulationInsights.isChatRoute = false
                        SimulationInsights.simulationInsightsId = m._id.toString()
                        view.context.startActivity(
                            Intent(
                                view.context,
                                SimulationInsights::class.java
                            )
                        )
                    }
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    @BindingAdapter("rvWantToTalk")
    @JvmStatic
    fun rvWantToTalk(view: RecyclerView, isSelected: Boolean) {
        val itemListData = ArrayList<WantToTalkModel>()
        itemListData.add(WantToTalkModel("Father", "#FFEEEE"))
        itemListData.add(WantToTalkModel("Mother", "#F0EBFF"))
        itemListData.add(WantToTalkModel("Brother", "#E9FFFF"))
        itemListData.add(WantToTalkModel("Sister", "#FFFFFF"))
        itemListData.add(WantToTalkModel("Cousin", "#F0EBFF"))
        itemListData.add(WantToTalkModel("Child", "#FFEEEE"))


        val adapter: SimpleRecyclerViewAdapter<WantToTalkModel, RvWantToTalkItemViewBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_want_to_talk_item_view, BR.bean
            ) { v, m, pos ->
                when (v.id) {

                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    @BindingAdapter("rvStartPracticing2")
    @JvmStatic
    fun rvStartPracticing2(view: RecyclerView, isSelected: ModelStartPracticing) {
//        val itemListData = ArrayList<StartPracticingModel>()
//        itemListData.add(StartPracticingModel("Conflict", "#E9FFFF", R.drawable.icon_heartbreak))
//        itemListData.add(
//            StartPracticingModel(
//                "Boundaries",
//                "#FFFFFF",
//                R.drawable.icon_divide_solid,
//                "Boundaries"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Ghosting or \nEmotional Distance",
//                "#FFEEEE",
//                R.drawable.ghost_icon,
//                "Ghosting or Emotional Distance"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Criticism &\n" + "Judgment",
//                "#F0EBFF",
//                R.drawable.thumb_down,
//                "Criticism and Judgment"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Guilt or " + "Emotional Pressure",
//                "#FFFFFF",
//                R.drawable.sad_face,
//                "Guilt or Emotional Pressure"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Miscommunication",
//                "#E9FFFF",
//                R.drawable.line_md_heart,
//                "Miscommunication"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Closeness &\n" + "Reassurance",
//                "#F0EBFF",
//                R.drawable.smile_facing,
//                "Closeness & Reassurance"
//            )
//        )
//        itemListData.add(
//            StartPracticingModel(
//                "Feeling\n" + "Undervalued",
//                "#FFEEEE",
//                R.drawable.line_graph,
//                "Feeling Undervalued"
//            )
//        )
//        val adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItemBinding> =
//            SimpleRecyclerViewAdapter(
//                R.layout.start_practicing_item, BR.bean
//            ) { v, m, pos ->
//                when (v.id) {
//                }
//            }
//
//        view.adapter = adapter
//        adapter.list = itemListData
//        view.isNestedScrollingEnabled = true
    }

    @BindingAdapter("rvStartPracticing")
    @JvmStatic
    fun rvStartPracticing(view: RecyclerView, isSelected: Boolean) {
        val itemListData = ArrayList<StartPracticingModel>()
        itemListData.add(StartPracticingModel("Conflict", "#E9FFFF", R.drawable.icon_heartbreak))
        itemListData.add(
            StartPracticingModel(
                "Boundaries",
                "#FFFFFF",
                R.drawable.icon_divide_solid,
                "Boundaries"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Ghosting or \nEmotional Distance",
                "#FFEEEE",
                R.drawable.ghost_icon,
                "Ghosting or Emotional Distance"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Criticism &\n" + "Judgment",
                "#F0EBFF",
                R.drawable.thumb_down,
                "Criticism and Judgment"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Guilt or " + "Emotional Pressure",
                "#FFFFFF",
                R.drawable.sad_face,
                "Guilt or Emotional Pressure"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Miscommunication",
                "#E9FFFF",
                R.drawable.line_md_heart,
                "Miscommunication"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Closeness &\n" + "Reassurance",
                "#F0EBFF",
                R.drawable.smile_facing,
                "Closeness & Reassurance"
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Feeling\n" + "Undervalued",
                "#FFEEEE",
                R.drawable.line_graph,
                "Feeling Undervalued"
            )
        )
        val adapter: SimpleRecyclerViewAdapter<StartPracticingModel, StartPracticingItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.start_practicing_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }

    @BindingAdapter("rvRecyclerviewOnboarding")
    @JvmStatic
    fun rvRecyclerviewOnboarding(view: RecyclerView, ignore: List<StepperOnboardingModel>) {
        val adapter: SimpleRecyclerViewAdapter<StepperOnboardingModel, StepperOnboardingSubRvItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.stepper_onboarding_sub_rv_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {

                    R.id.mainLayout -> {
                        ignore.forEachIndexed { index, item ->
                            item.select = index == pos
                        }
                        (view.adapter as? SimpleRecyclerViewAdapter<*, *>)?.notifyDataSetChanged()
                    }
                }

            }
        view.adapter = adapter
        adapter.list = ignore
    }

    @BindingAdapter("textResult")
    @JvmStatic
    fun textResult(view: TextView, ignore: String?) {
      val  ignoreNew:String? = ignore?.replace(","," ,")
        view.text=ignoreNew.toString()
    }
    @BindingAdapter("textLinearGradient")
    @JvmStatic
    fun textLinearGradient(view: TextView, ignore: String?) {
        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)

                val width = view.width
                if (width <= 0) return

                val shader = LinearGradient(
                    0f, 0f,
                    width.toFloat(), 0f,
                    intArrayOf(
                        Color.parseColor("#CAB8FF"),
                        Color.parseColor("#B5EAEA")
                    ),
                    floatArrayOf(0f, 1f),
                    Shader.TileMode.CLAMP
                )

                view.paint.shader = shader
                view.invalidate()
            }
        })
    }

    @BindingAdapter("backgroundLinearGradient")
    @JvmStatic
    fun backgroundLinearGradient(view: View, ignore: Boolean?) {

        if (ignore == true) {
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#CAB8FF"),
                    Color.parseColor("#B5EAEA")
                )
            )
            gradientDrawable.cornerRadius = 0f // Optional: round corners if needed
            view.background = gradientDrawable
        } else {
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    Color.parseColor("#FFFFFFFF"),
                    Color.parseColor("#FFFFFFFF")
                )
            )

            view.background = gradientDrawable
        }

    }
//    fun textLinearGradient(view: TextView, ignore: String?) {
//        view.post {
//            val width = view.width
//            if (width == 0) return@post // View not yet laid out
//
//            val shader = LinearGradient(
//                0f, 0f, width.toFloat(), 0f, // Horizontal gradient
//                intArrayOf(
//                    Color.parseColor("#CAB8FF"), // Start color
//                    Color.parseColor("#B5EAEA")  // End color
//                ),
//                null,
//                Shader.TileMode.CLAMP
//            )
//            view.paint.shader = shader
//            view.invalidate()
//        }
//    }

    @BindingAdapter("setImageFromUrl")
    @JvmStatic
    fun setImageFromUrl(image: ShapeableImageView, url: String?) {
        if (url != null) {
            Glide.with(image.context).load(url).placeholder(R.drawable.user)
                .error(R.drawable.user).into(image)
        }
    }


    fun styleSystemBars(activity: Activity, color: Int) {
        activity.window.navigationBarColor = color
    }

    fun statusBarStyleWhite(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            activity.window.statusBarColor = Color.TRANSPARENT
        }
    }

    fun statusBarStyleBlack(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR // Ensures black text/icons
            activity.window.statusBarColor = Color.TRANSPARENT // Transparent status bar
        }
    }


    class CustomBulletSpan(
        private val gapWidth: Int = 2,
        private val bulletRadius: Float = 1f,
        private val bulletColor: Int? = null
    ) : LeadingMarginSpan {

        override fun getLeadingMargin(first: Boolean): Int {
            return (2 * bulletRadius + gapWidth).toInt()
        }

        override fun drawLeadingMargin(
            c: Canvas, p: Paint, x: Int, dir: Int,
            top: Int, baseline: Int, bottom: Int,
            text: CharSequence, start: Int, end: Int,
            first: Boolean, layout: android.text.Layout
        ) {
            if ((text as? Spanned)?.getSpanStart(this) != start) return

            val style = p.style
            val oldColor = p.color

            bulletColor?.let { p.color = it }

            p.style = Paint.Style.FILL

            val yPosition = (top + bottom) / 2f
            c.drawCircle(x + dir * bulletRadius, yPosition, bulletRadius, p)

            p.style = style
            p.color = oldColor
        }
    }

    @BindingAdapter("loadSvgImage")
    @JvmStatic
    fun loadSvgImage(image: ImageView, url: String) {
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
                                image.setImageDrawable(drawable)
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
