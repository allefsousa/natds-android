package com.natura.android.button

import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import com.natura.android.R
import com.natura.android.resources.getDimenFromTheme
import com.natura.android.resources.getFontFromTheme
import com.natura.android.resources.getIconResourceIdFromName
import com.natura.android.resources.getStringFromTheme
import java.util.Locale

class GaYaButton : MaterialButton {

    private lateinit var buttonAttributesArray: TypedArray
    private var attrs: AttributeSet? = null
    private var btnSize: Int = 0
    private var btnColor: Int = 0
    private var btnType: Int = 0
    private var btnIcon: Int = 0
    private var btnIconPosition: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, resolveStyle(context, attrs)) {
        this.attrs = attrs
        init(attrs)
    }

    constructor(context: Context) : super(context, null, resolveStyle(context, null)) {
        init(null)
    }

    private fun init(attrs: AttributeSet?) {
        buttonAttributesArray = context.obtainStyledAttributes(attrs, R.styleable.GaYaButton)

        context.theme.obtainStyledAttributes(attrs, R.styleable.GaYaButton, 0, 0).apply {
            try {
                btnSize = buttonAttributesArray.getInteger(R.styleable.GaYaButton_btn_size, SEMIX_SIZE)
                btnColor = buttonAttributesArray.getInteger(R.styleable.GaYaButton_btn_color, DEFAULT)
                btnType = buttonAttributesArray.getInteger(R.styleable.GaYaButton_btn_type, FILLED)
                btnIcon = buttonAttributesArray.getResourceId(R.styleable.GaYaButton_btn_icon, 0)
                btnIconPosition = buttonAttributesArray.getInt(R.styleable.GaYaButton_btn_icon_position, 0)

                if (btnIcon != 0) {
                    val drawable = ContextCompat.getDrawable(context, btnIcon)
                    setIcon(drawable)
                }

                setIconGravity(btnIconPosition)
            } finally {
                recycle()
            }
        }

        setAppearanceAttributesFromTheme()
        setComponentStyleBySize()
        applyStyle()
    }

    fun setBtnSize(size: Int) {
        btnSize = size
        setComponentStyleBySize()
        applyStyle()
    }

    fun setBtnColor(color: Int) {
        btnColor = color
        setAppearanceAttributesFromTheme()
        applyStyle()
    }

    fun setBtnType(type: Int) {
        btnType = type
        applyStyle()
    }

    fun setBtnIcon(iconName: String?) {
        iconName?.let {
            val drawableId = getIconResourceIdFromName(context, it)
            val drawable = ContextCompat.getDrawable(context, drawableId)
            drawable?.setTint(currentTextColor)
            setIcon(drawable)
        }
    }

    fun setBtnIconPosition(position: Int) {
        btnIconPosition = position
        setIconGravity(btnIconPosition)
    }

    override fun setIconGravity(value: Int) {
        super.setIconGravity(
            when (value) {
                0 -> ICON_GRAVITY_TEXT_START
                1 -> ICON_GRAVITY_TEXT_END
                else -> ICON_GRAVITY_TEXT_START
            }
        )
    }

    private fun setAppearanceAttributesFromTheme() {
        when (btnColor) {
            DEFAULT -> getStyleAttributes(R.attr.buttonDefault)
            PRIMARY -> getStyleAttributes(R.attr.buttonPrimary)
            ONPRIMARY -> getStyleAttributes(R.attr.buttonOnPrimary)
            SECONDARY -> getStyleAttributes(R.attr.buttonSecondary)
            ONSECONDARY -> getStyleAttributes(R.attr.buttonOnSecondary)
            INVERSE -> getStyleAttributes(R.attr.buttonInverse)
            NEUTRAL -> getStyleAttributes(R.attr.buttonNeutral)
            else -> getStyleAttributes(R.attr.buttonPrimary)
        }
    }

    private fun applyStyle() {
        this.rippleColor = ContextCompat.getColorStateList(context, R.color.button_ripple_color_v23)
        this.textSize = getDimenFromTheme(context, R.attr.buttonLabelFontSize) / context.resources.displayMetrics.scaledDensity
        this.iconPadding = getDimenFromTheme(context, R.attr.spacingTiny).toInt()
        this.letterSpacing = getDimenFromTheme(context, R.attr.buttonLabelLetterSpacing)
        this.typeface = getFontFromTheme(context, R.attr.buttonLabelPrimaryFontWeight, R.attr.buttonLabelPrimaryFontWeight)
        this.ellipsize = TextUtils.TruncateAt.END
        this.maxLines = 1

        val buttonTextTransform = getStringFromTheme(context, R.attr.buttonTextTransform)
        when (buttonTextTransform) {
            "uppercased" -> {
                this.isAllCaps = true
                this.text = text.toString().uppercase(Locale.getDefault())
            }
            "lowercased" -> {
                this.isAllCaps = false
                this.text = text.toString().lowercase(Locale.getDefault())
            }
            "capitalized" -> {
                this.isAllCaps = false
                val originalText = text.toString()
                if (originalText.isNotEmpty()) {
                    val words = originalText.split("\\s+".toRegex())
                    val sb = StringBuilder()
                    for (word in words) {
                        if (word.isNotEmpty()) {
                            sb.append(word.substring(0, 1).uppercase(Locale.getDefault()))
                            sb.append(word.substring(1).lowercase(Locale.getDefault()))
                            sb.append(" ")
                        }
                    }
                    this.text = sb.toString().trim()
                }
            }
            "none" -> {
                this.isAllCaps = false
            }
            else -> {
                this.isAllCaps = false
                this.text = text.toString()
            }
        }

        this.iconSize = when (btnSize) {
            SEMI_SIZE -> getDimenFromTheme(context, R.attr.sizeSmall).toInt()
            SEMIX_SIZE, MEDIUM_SIZE -> getDimenFromTheme(context, R.attr.sizeStandard).toInt()
            else -> getDimenFromTheme(context, R.attr.sizeStandard).toInt()
        }

        when (btnType) {
            FILLED -> configureFilledButton()
            OUTLINED -> configureOutlinedButton()
            GHOST -> configureGhostButton()
            TONAL -> configureTonalButton()
        }
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        val buttonTextTransform = getStringFromTheme(context, R.attr.buttonTextTransform)
        val transformedText = when (buttonTextTransform) {
            "uppercased" -> text.toString().uppercase(Locale.getDefault())
            "lowercased" -> text.toString().lowercase(Locale.getDefault())
            "capitalized" -> text.toString()
                .split("\\s+".toRegex())
                .joinToString(" ") { word ->
                    if (word.isNotEmpty())
                        word.substring(0, 1).uppercase(Locale.getDefault()) + word.substring(1).lowercase(Locale.getDefault())
                    else
                        word
                }
            "none" -> text.toString()
            else -> text.toString()
        }
        super.setText(transformedText, type)
    }

    private fun configureFilledButton() {
        this.elevation = 0f * resources.displayMetrics.density
        this.stateListAnimator = null

        val backgroundTint = when (btnColor) {
            DEFAULT -> R.color.gaya_filled_background_default_v23
            PRIMARY -> R.color.gaya_filled_background_primary_v23
            ONPRIMARY -> R.color.gaya_filled_background_on_primary_v23
            SECONDARY -> R.color.gaya_filled_background_secondary_v23
            ONSECONDARY -> R.color.gaya_filled_background_on_secondary_v23
            INVERSE -> R.color.gaya_filled_background_inverse_v23
            NEUTRAL -> R.color.gaya_filled_background_neutral_v23
            else -> R.color.gaya_filled_background_default_v23
        }
        this.backgroundTintList = ContextCompat.getColorStateList(context, backgroundTint)

        val textColor = when (btnColor) {
            DEFAULT -> R.color.gaya_filled_label_default_v23
            PRIMARY -> R.color.gaya_filled_label_primary_v23
            ONPRIMARY -> R.color.gaya_filled_label_on_primary_v23
            SECONDARY -> R.color.gaya_filled_label_secondary_v23
            ONSECONDARY -> R.color.gaya_filled_label_on_secondary_v23
            INVERSE -> R.color.gaya_filled_label_inverse_v23
            NEUTRAL -> R.color.gaya_filled_label_neutral_v23
            else -> R.color.gaya_filled_label_default_v23
        }

        val textColorStateList = ContextCompat.getColorStateList(context, textColor)
        this.setTextColor(textColorStateList)
        this.iconTint = textColorStateList
    }

    private fun configureOutlinedButton() {
        this.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_background_default_v23)
        this.cornerRadius = getDimenFromTheme(context, R.attr.buttonBorderRadius).toInt()

        when (btnColor) {
            DEFAULT -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_default_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_default_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            PRIMARY -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_primary_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_primary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            ONPRIMARY -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_on_primary_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_on_primary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            SECONDARY -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_secondary_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_secondary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            ONSECONDARY -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_on_secondary_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_on_secondary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            INVERSE -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_inverse_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_inverse_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            NEUTRAL -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_neutral_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_neutral_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            else -> {
                this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_outlined_stroke_default_v23)
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_outlined_label_default_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
        }
    }

    private fun configureGhostButton() {
        this.backgroundTintList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_background_v23)
        this.strokeColor = ContextCompat.getColorStateList(context, R.color.gaya_ghost_stroke_v23)
        this.cornerRadius = getDimenFromTheme(context, R.attr.buttonBorderRadius).toInt()

        when (btnColor) {
            DEFAULT -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_default_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            PRIMARY -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_primary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            ONPRIMARY -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_on_primary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            SECONDARY -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_secondary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            ONSECONDARY -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_on_secondary_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            INVERSE -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_inverse_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            NEUTRAL -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_neutral_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
            else -> {
                val textColorStateList = ContextCompat.getColorStateList(context, R.color.gaya_ghost_label_default_v23)
                this.setTextColor(textColorStateList)
                this.iconTint = textColorStateList
            }
        }
    }

    private fun configureTonalButton() {
        this.textSize = getDimenFromTheme(context, R.attr.buttonLabelFontSize) / context.resources.displayMetrics.scaledDensity
        this.iconPadding = getDimenFromTheme(context, R.attr.spacingTiny).toInt()
        this.letterSpacing = getDimenFromTheme(context, R.attr.buttonLabelLetterSpacing)
        this.typeface = getFontFromTheme(context, R.attr.buttonLabelPrimaryFontWeight, R.attr.buttonLabelPrimaryFontWeight)
        this.elevation = 0f * resources.displayMetrics.density
        this.stateListAnimator = null

        val backgroundTint = when (btnColor) {
            DEFAULT -> R.color.gaya_tonal_background_default_v23
            PRIMARY -> R.color.gaya_tonal_background_primary_v23
            ONPRIMARY -> R.color.gaya_tonal_background_on_primary_v23
            SECONDARY -> R.color.gaya_tonal_background_secondary_v23
            ONSECONDARY -> R.color.gaya_tonal_background_on_secondary_v23
            INVERSE -> R.color.gaya_tonal_background_inverse_v23
            NEUTRAL -> R.color.gaya_tonal_background_neutral_v23
            else -> R.color.gaya_tonal_background_default_v23
        }
        this.backgroundTintList = ContextCompat.getColorStateList(context, backgroundTint)

        val textColor = when (btnColor) {
            DEFAULT -> R.color.gaya_tonal_label_default_v23
            PRIMARY -> R.color.gaya_tonal_label_primary_v23
            ONPRIMARY -> R.color.gaya_tonal_label_on_primary_v23
            SECONDARY -> R.color.gaya_tonal_label_secondary_v23
            ONSECONDARY -> R.color.gaya_tonal_label_on_secondary_v23
            INVERSE -> R.color.gaya_tonal_label_inverse_v23
            NEUTRAL -> R.color.gaya_tonal_label_neutral_v23
            else -> R.color.gaya_tonal_label_default_v23
        }

        val textColorStateList = ContextCompat.getColorStateList(context, textColor)
        this.setTextColor(textColorStateList)
        this.iconTint = textColorStateList
    }

    private fun getStyleAttributes(componentAttr: Int) {
        context
            .theme
            .obtainStyledAttributes(
                attrs,
                R.styleable.GaYaButtonStyle,
                componentAttr,
                0
            )
            .apply {
            }
    }

    private fun setComponentStyleBySize() {
        when (btnSize) {
            SEMI_SIZE -> {
                this.minHeight = getDimenFromTheme(context, R.attr.sizeSemi).toInt()
                this.insetBottom = getDimenFromTheme(context, R.attr.spacingMicro).toInt()
                this.insetTop = getDimenFromTheme(context, R.attr.spacingMicro).toInt()
            }
            SEMIX_SIZE -> {
                this.minHeight = getDimenFromTheme(context, R.attr.sizeSemiX).toInt()
                this.insetBottom = getDimenFromTheme(context, R.attr.spacingNone).toInt()
                this.insetTop = getDimenFromTheme(context, R.attr.spacingNone).toInt()
            }
            MEDIUM_SIZE -> {
                this.minHeight = getDimenFromTheme(context, R.attr.sizeMedium).toInt()
                this.insetBottom = getDimenFromTheme(context, R.attr.spacingNone).toInt()
                this.insetTop = getDimenFromTheme(context, R.attr.spacingNone).toInt()
            }
            else -> {
                this.minHeight = getDimenFromTheme(context, R.attr.sizeSemiX).toInt()
                this.insetBottom = getDimenFromTheme(context, R.attr.spacingMicro).toInt()
                this.insetTop = getDimenFromTheme(context, R.attr.spacingMicro).toInt()
            }
        }
    }

    companion object {
        const val SEMI_SIZE = 0
        const val SEMIX_SIZE = 1
        const val MEDIUM_SIZE = 2

        const val START = 0
        const val END = 1

        const val DEFAULT = 0
        const val PRIMARY = 1
        const val ONPRIMARY = 2
        const val SECONDARY = 3
        const val ONSECONDARY = 4
        const val INVERSE = 5
        const val NEUTRAL = 6

        const val FILLED = 0
        const val OUTLINED = 1
        const val GHOST = 2
        const val TONAL = 3

        private fun resolveStyle(context: Context, attrs: AttributeSet?): Int {
            val a = context.obtainStyledAttributes(attrs, R.styleable.GaYaButton)
            val btnType = a.getInteger(R.styleable.GaYaButton_btn_type, FILLED)
            a.recycle()

            return when (btnType) {
                FILLED -> R.attr.filledButton
                OUTLINED -> R.attr.outlinedButton
                GHOST -> R.attr.textButton
                else -> R.attr.filledButton
            }
        }
    }
}
