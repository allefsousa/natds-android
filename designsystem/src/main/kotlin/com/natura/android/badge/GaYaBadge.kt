package com.natura.android.badge

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.natura.android.R
import com.natura.android.exceptions.MissingThemeException
import com.natura.android.resources.getColorTokenFromTheme

private const val TIME_LOOP_PULSE = 400L

class GaYaBadge @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private var badgeAttributeArray: TypedArray
    private var attrNumber: Int = 0
    private var attrVisibility: Boolean = true
    private var isFontWeight: Boolean = false
    private var limit: Int = UNLIMITED
    private var variant: Int = STANDARD
    private var attrColor: GaYaBadgeColor = GaYaBadgeColor.primary

    private val imageContainer by lazy { findViewById<ImageView>(R.id.badgeImage) }
    private val badgeRipple by lazy { findViewById<ImageView>(R.id.badgeRipple) }
    private val badgeDot by lazy { findViewById<ImageView>(R.id.badgeDot) }
    private lateinit var badgeDrawable: GaYaBadgeDrawable

    var number: Int = 0
        get() = attrNumber
        set(value) {
            field = value
            if (variant == STANDARD) {
                attrNumber = value
                badgeDrawable.updateBadgeDrawable(value)
            }
        }

    var color: GaYaBadgeColor = GaYaBadgeColor.primary
        get() = attrColor
        set(value) {
            field = value
            if (variant == STANDARD || variant == DOT) {
                attrColor = value
                badgeDrawable.updateColorBadgeDrawable(value.value)
            }
        }

    var isVisible: Boolean = true
        get() = attrVisibility
        set(value) {
            attrVisibility = value
            field = value
            configureVisibility()
        }

    init {
        try {
            View.inflate(context, R.layout.badge, this)
        } catch (e: Exception) {
            throw (MissingThemeException())
        }

        badgeAttributeArray = context.obtainStyledAttributes(attrs, R.styleable.GaYaBadge)

        getAttributes()
        handlerVariant()
        configureVisibility()

        badgeAttributeArray.recycle()
    }

    private fun updateDrawable() {
        if (::badgeDrawable.isInitialized) {
            badgeDrawable.updateBadgeDrawable(number)
            badgeDrawable.updateColorBadgeDrawable(color.value)
            imageContainer.setImageDrawable(badgeDrawable)
        }
    }

    private fun getAttributes() {
        attrNumber = badgeAttributeArray.getInteger(R.styleable.GaYaBadge_badgeNumber, 0)
        attrVisibility = badgeAttributeArray.getBoolean(R.styleable.GaYaBadge_badgeVisibility, true)
        variant = badgeAttributeArray.getInt(R.styleable.GaYaBadge_badgeVariant, STANDARD)
        attrColor = GaYaBadgeColor.fromInt(badgeAttributeArray.getInt(R.styleable.GaYaBadge_bdg_color, GaYaBadgeColor.primary.value))
        limit = badgeAttributeArray.getInt(R.styleable.GaYaBadge_badgeLimitNumber, UNLIMITED)
        isFontWeight = badgeAttributeArray.getBoolean(R.styleable.GaYaBadge_isFontWeight, false)
    }

    private fun createBadgeDrawable() {
        imageContainer.visibility = View.VISIBLE
        updateDrawable()
        badgeDrawable = GaYaBadgeDrawable(context, attrNumber, imageContainer.drawable, variant, attrColor.value, limit, isFontWeight)
    }

    private fun handlerVariant() {
        if (variant == PULSE) {
            createBadgePulse()
        } else {
            createBadgeDrawable()
        }
    }

    private fun createBadgePulse() {
        badgeRipple.visibility = View.VISIBLE
        badgeDot.visibility = View.VISIBLE

        badgeDot.drawable.setTint(getColorTokenFromTheme(context, getPulseColorByAttr()))
        badgeRipple.drawable.setTint(getColorTokenFromTheme(context, getPulseColorByAttr()))

        initializeAnimators()
    }

    private fun configureVisibility() {
        this.visibility = if (attrVisibility) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun initializeAnimators() {
        val pulseAnimation: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            badgeRipple,
            PropertyValuesHolder.ofFloat("scaleX", 0.5f),
            PropertyValuesHolder.ofFloat("scaleY", 0.5f),
            PropertyValuesHolder.ofFloat("alpha", 0.48f, 0.24f, 0.12f)
        )
        pulseAnimation.duration = TIME_LOOP_PULSE

        pulseAnimation.repeatCount = ObjectAnimator.INFINITE
        pulseAnimation.repeatMode = ObjectAnimator.REVERSE
        pulseAnimation.start()
    }

    private fun getPulseColorByAttr(): Int {
        return when (color) {
            GaYaBadgeColor.primary -> R.attr.badgeColorPrimaryBackground
            GaYaBadgeColor.secondary -> R.attr.badgeColorSecondaryBackground
            GaYaBadgeColor.inverse -> R.attr.badgeColorInverseBackground
            GaYaBadgeColor.success -> R.attr.badgeColorSuccessBackground
            GaYaBadgeColor.alert -> R.attr.badgeColorAlertBackground
        }
    }

    fun getFontWeightOption(): Boolean {
        return isFontWeight
    }

    companion object {
        const val STANDARD = 0
        const val DOT = 1
        const val PULSE = 2

        const val PRIMARY = 0
        const val SECONDARY = 1
        const val INVERSE = 2
        const val ALERT = 3
        const val SUCCESS = 4

        const val NINE = 0
        const val NINETY_NINE = 1
        const val UNLIMITED = 2
    }
}

enum class GaYaBadgeColor(val value: Int) {
    primary(0),
    secondary(1),
    inverse(2),
    alert(3),
    success(4);

    companion object {
        fun fromInt(value: Int) = values().firstOrNull { it.value == value } ?: primary
    }
}
