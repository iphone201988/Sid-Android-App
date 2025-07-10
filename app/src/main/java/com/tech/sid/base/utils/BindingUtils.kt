package com.tech.sid.base.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LeadingMarginSpan

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tech.sid.R
import com.google.android.material.imageview.ShapeableImageView
import com.tech.sid.BR
import com.tech.sid.base.SimpleRecyclerViewAdapter
import com.tech.sid.databinding.RvInsightsCardItemBinding
import com.tech.sid.databinding.RvJournalCardItemBinding
import com.tech.sid.databinding.StartPracticingItemBinding
import com.tech.sid.databinding.StepperOnboardingSubRvItemBinding
import com.tech.sid.databinding.SuggestionItemCardBinding
import com.tech.sid.ui.onboarding_ques.JournalModel
import com.tech.sid.ui.onboarding_ques.StartPracticingModel
import com.tech.sid.ui.onboarding_ques.StepperModel
import com.tech.sid.ui.onboarding_ques.StepperOnboardingModel
import com.tech.sid.ui.onboarding_ques.StepperPageModel
import com.tech.sid.ui.onboarding_ques.SubscriptionModel
import com.tech.sid.ui.onboarding_ques.SuggestionModel

object BindingUtils {


    @JvmStatic
    @BindingAdapter("bulletPoints")
    fun bulletPoints(textView: TextView, ignore: Boolean?) {
        val resIds = listOf(
            R.string.concern_delivery,
            R.string.unsolicited_help,
            R.string.emotional_authority
        )
        if (resIds.isEmpty()) return
        val context = textView.context
        textView.text = createBulletTextFromResIds(context, resIds)
    }


    @JvmStatic
    @BindingAdapter("bulletPointsSub")
    fun bulletPointsSub(textView: TextView, ignore: SubscriptionModel?) {
        if (ignore?.discrition!!.isEmpty()) return
        val context = textView.context
        textView.text = createBulletTextFromRes(context, ignore.discrition!!)
    }

    fun createBulletTextFromResIds(context: Context, resIds: List<Int>): CharSequence {
        val bulletGap = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 10f, context.resources.displayMetrics
        ).toInt()

        val bulletSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 3f, context.resources.displayMetrics
        )

        val builder = SpannableStringBuilder()

        resIds.forEachIndexed { index, resId ->
            val text = context.getString(resId)
            val spannable = SpannableString(text)
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


    @BindingAdapter("setColorCard")
    @JvmStatic
    fun setColorCard(view: CardView, isSelected: String) {
        view.setCardBackgroundColor(Color.parseColor(isSelected))
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

        }else{
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


         val paint = textView.paint
         val textWidth = paint.measureText(fullText, start, end)


         val shader = LinearGradient(
             0f, 0f, textWidth, 0f,
             intArrayOf(Color.parseColor("#9773FF"), Color.parseColor("#00ACAC")),
             null,
             Shader.TileMode.CLAMP
         )


         val gradientSpan = object : CharacterStyle() {
             override fun updateDrawState(tp: TextPaint) {
                 tp.shader = shader
             }
         }

         spannable.setSpan(gradientSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

         textView.text = spannable


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


    @BindingAdapter("rvJournal")
    @JvmStatic
    fun rvJournal(view: RecyclerView, isSelected: Boolean) {
        val itemListData = ArrayList<JournalModel>()
        itemListData.add(JournalModel("#FFEEEE", "#FFB06B", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#E9FFFF", "#00ACAC", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#F0EBFF", "#9773FF", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#FFEEEE", "#FFB06B", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))

        val adapter: SimpleRecyclerViewAdapter<JournalModel, RvJournalCardItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_journal_card_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
    }
    @BindingAdapter("rvInsights")
    @JvmStatic
    fun rvInsights(view: RecyclerView, isSelected: Boolean) {
        val itemListData = ArrayList<JournalModel>()
        itemListData.add(JournalModel("#FFEEEE", "#FFB06B", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#E9FFFF", "#00ACAC", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#F0EBFF", "#9773FF", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))
        itemListData.add(JournalModel("#FFEEEE", "#FFB06B", "16 June, 2025","Friend feeling down after work","Focused on active listening and validation."))

        val adapter: SimpleRecyclerViewAdapter<JournalModel, RvInsightsCardItemBinding> =
            SimpleRecyclerViewAdapter(
                R.layout.rv_insights_card_item, BR.bean
            ) { v, m, pos ->
                when (v.id) {
                }
            }

        view.adapter = adapter
        adapter.list = itemListData
        view.isNestedScrollingEnabled = true
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
                R.drawable.icon_divide_solid
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Ghosting or \nEmotional Distance",
                "#FFEEEE",
                R.drawable.ghost_icon
            )
        )
        itemListData.add(
            StartPracticingModel(
                "#Criticism &\n" + "Judgment",
                "#F0EBFF",
                R.drawable.thumb_down
            )
        )
        itemListData.add(
            StartPracticingModel(
                "#Guilt or\n" + "Emotional Pressure",
                "#FFFFFF",
                R.drawable.sad_face
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Miscommunication",
                "#E9FFFF",
                R.drawable.line_md_heart
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Closeness &\n" + "Reassurance",
                "#F0EBFF",
                R.drawable.smile_facing
            )
        )
        itemListData.add(
            StartPracticingModel(
                "Feeling\n" + "Undervalued",
                "#FFEEEE",
                R.drawable.line_graph
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

}
