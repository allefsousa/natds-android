package com.natura.android.appbartop

import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.content.Context
import android.content.res.TypedArray
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.AppBarLayout
import com.natura.android.R
import com.natura.android.exceptions.MissingThemeException
import com.natura.android.extensions.setVisibilityFromBoolean
import com.natura.android.resources.BarColors
import com.natura.android.resources.getColorTokenFromTheme
import com.natura.android.resources.getDrawableFromTheme
import com.natura.android.textfield.TextField

class StandardAppBarTop(context: Context, attrs: AttributeSet) : AppBarLayout(context, attrs) {

    private var typedArray: TypedArray
    private val imageView = ImageView(context)
    private var barColor: Int = PRIMARY

    fun setAppBarColor(color: BarColors) {
        barColor = color.value
        addContent()
        setAppBarColorAndTextColor(context)
    }

    private fun setAppBarColorAndTextColor(context: Context) {
        val textView = findViewById<TextView>(R.id.contentText)
        if (barColor == NONE) {
            toolbar.setBackgroundColor(getColorTokenFromTheme(context, R.attr.colorSurface))
            textView?.setTextColor(getColorTokenFromTheme(context, R.attr.colorOnSurface))
        } else if (barColor == PRIMARY) {
            toolbar.setBackgroundColor(getColorTokenFromTheme(context, R.attr.colorPrimary))
            textView?.setTextColor(getColorTokenFromTheme(context, R.attr.colorOnPrimary))
        } else if (barColor == SECONDARY) {
            toolbar.setBackgroundColor(getColorTokenFromTheme(context, R.attr.colorSecondary))
            textView?.setTextColor(getColorTokenFromTheme(context, R.attr.colorOnSecondary))
        } else if (barColor == INVERSE) {
            toolbar.setBackgroundColor(getColorTokenFromTheme(context, R.attr.colorSurface))
            textView?.setTextColor(getColorTokenFromTheme(context, R.attr.colorOnSurface))
        }
    }

    private var enabledElevation: Boolean = true
        set(value) {
            field = value
            setElevation(value)
        }

    private var scrollable: Boolean = false
        set(value) {
            field = value
            handleScroll(value)
        }

    private var menu: Int? = null
        set(value) {
            field = value
            value?.let {
                if (it != NOT_RESOURCE_FOUND_CODE)
                    toolbar.inflateMenu(it)
            }
        }
    private var contentText: String? = ""
        set(value) {
            field = value
            if (contentType == TEXT)
                value?.let { addTextView(context, it) }
        }

    private var contentType: Int = TEXT
    private var contentMedia: Int = 0
    private var mediaHeight: Int = WRAP_CONTENT
    private var mediaWidth: Int = WRAP_CONTENT
    private var actionRight: Boolean = false
    private var actionLeft: Boolean = false
    private var proeminentContent: Boolean = false
    private var contentPosition: Int = CENTER

    val toolbar: Toolbar by lazy { findViewById(R.id.toolbar) }
    private val actionLeftContainer by lazy { findViewById<LinearLayout>(R.id.actionLeftContainer) }
    private val actionRightContainer by lazy { findViewById<LinearLayout>(R.id.actionRightContainer) }
    private val actionCenterContainer by lazy { findViewById<LinearLayout>(R.id.actionCenterContainer) }

    init {
        try {
            View.inflate(context, R.layout.standard_appbar_top, this)
        } catch (e: Exception) {
            throw (MissingThemeException())
        }

        typedArray = context.obtainStyledAttributes(attrs, R.styleable.StandardAppBarTop)

        val appBarHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 56f, resources.displayMetrics
        ).toInt()

        toolbar.layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            appBarHeight
        )

        initialConfigurations()
        getAttributes()
        toolbar.setNavigationIcon(null)
        addContent()
        typedArray.recycle()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        resetProperty()
        positionActions()
        throwsCountElementsException()
        changeActionsVisibility()
        removeParentsElevation()
        setAppBarColorAndTextColor(context)
    }

    fun getElevationEnabled(): Boolean {
        return enabledElevation
    }

    fun getScrollable(): Boolean {
        return scrollable
    }

    fun getActionRight(): Boolean {
        return actionRight
    }

    fun getActionLeft(): Boolean {
        return actionLeft
    }

    fun getProeminentContent(): Boolean {
        return proeminentContent
    }

    fun getContentPosition(): Int {
        return contentPosition
    }

    fun getContentText(): String? {
        return contentText
    }

    fun getContentImage(): Int {
        return contentMedia
    }

    fun getContentType(): Int {
        return contentType
    }

    fun getMediaHeight(): Any {
        return mediaHeight
    }

    fun getMediaWidth(): Any {
        return mediaWidth
    }

    fun setMenu(menuFile: Int) {
        menu = menuFile
    }

    fun setText(text: String) {
        contentType = TEXT
        contentText = text
    }

    private fun changeActionsVisibility() {
        actionRightContainer.setVisibilityFromBoolean(actionRight)
        actionLeftContainer.setVisibilityFromBoolean(actionLeft)
    }

    private fun positionActions() {
        when {
            childCount == COUNT_ELEMENTS_ONLY_ACTION_LEFT -> {
                positionActionLeft()
                actionRightContainer.setVisibilityFromBoolean(false)
            }

            childCount > COUNT_ELEMENTS_ONLY_ACTION_LEFT -> {
                positionActionLeft()
                positionActionRight()
            }
        }
    }

    private fun addContentView(view: View) {
        if (proeminentContent) {
            actionLeftContainer.addView(view)
            actionLeftContainer.layoutParams = LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                2.5F
            )
            actionLeftContainer.orientation = LinearLayout.VERTICAL
            actionCenterContainer.setVisibilityFromBoolean(false)
        } else {
            actionCenterContainer.removeAllViews()
            actionCenterContainer.gravity = Gravity.CENTER
            actionCenterContainer.addView(view)
        }
    }

    private fun positionActionLeft() {
        val child = getChildAt(ACTION_LEFT_ELEMENT_INDEX)
        this.removeView(child)
        actionLeftContainer.addView(child)
    }

    private fun positionActionRight() {
        while (haveChildrenToMove()) {
            val child = getNextChild()
            removeView(child)
            actionRightContainer.addView(child)
        }
    }

    private fun addContent() {
        when (contentType) {
            MEDIA -> {
                if (contentMedia != NOT_RESOURCE_FOUND_CODE) {
                    addImage(context, contentMedia)
                }
            }

            SEARCH -> addTextField(context)
            LOGO -> {

                val imageHeight = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 28f, resources.displayMetrics
                ).toInt()

                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                imageView.adjustViewBounds = true
                imageView.layoutParams = LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    imageHeight
                ).apply {
                    gravity = Gravity.CENTER
                }

                val drawable = when (barColor) {
                    NONE -> getDrawableFromTheme(context, R.attr.assetBrandNeutralAFile)
                    PRIMARY -> {
                        imageView.setColorFilter(
                            getColorTokenFromTheme(
                                context,
                                R.attr.colorOnPrimary
                            )
                        )
                        getDrawableFromTheme(context, R.attr.assetBrandCustomAFile)
                    }

                    SECONDARY -> {
                        imageView.setColorFilter(
                            getColorTokenFromTheme(
                                context,
                                R.attr.colorOnSecondary
                            )
                        )
                        getDrawableFromTheme(context, R.attr.assetBrandCustomAFile)
                    }

                    INVERSE -> {
                        imageView.setColorFilter(
                            getColorTokenFromTheme(
                                context,
                                R.attr.colorOnSurface
                            )
                        )
                        getDrawableFromTheme(context, R.attr.assetBrandCustomAFile)
                    }

                    else -> getDrawableFromTheme(context, R.attr.assetBrandNeutralAFile)
                }

                imageView.setImageDrawable(drawable)

                actionCenterContainer.removeAllViews()
                actionCenterContainer.gravity = Gravity.CENTER
                actionCenterContainer.addView(imageView)
            }
        }
    }

    private fun resetProperty() {
        toolbar.title = ""
    }

    private fun getAttributes() {
        menu = typedArray.getResourceId(R.styleable.StandardAppBarTop_menu, NOT_RESOURCE_FOUND_CODE)
        enabledElevation =
            typedArray.getBoolean(R.styleable.StandardAppBarTop_enabledElevation, true)
        barColor = typedArray.getInt(R.styleable.StandardAppBarTop_appBarColor, DEFAULT)
        contentType = typedArray.getInt(R.styleable.StandardAppBarTop_contentType, TEXT)
        contentMedia = typedArray.getResourceId(
            R.styleable.StandardAppBarTop_contentMedia,
            NOT_RESOURCE_FOUND_CODE
        )
        contentText = typedArray.getString(R.styleable.StandardAppBarTop_contentText)
        actionRight = typedArray.getBoolean(R.styleable.StandardAppBarTop_actionRight, false)
        actionLeft = typedArray.getBoolean(R.styleable.StandardAppBarTop_actionLeft, false)
        mediaHeight = typedArray.getDimensionPixelSize(
            R.styleable.StandardAppBarTop_mediaHeight,
            LayoutParams.WRAP_CONTENT
        )
        mediaWidth = typedArray.getDimensionPixelSize(
            R.styleable.StandardAppBarTop_mediaWidth,
            LayoutParams.WRAP_CONTENT
        )
        scrollable = typedArray.getBoolean(R.styleable.StandardAppBarTop_scrollable, false)
        contentPosition =
            typedArray.getInteger(R.styleable.StandardAppBarTop_contentPosition, CENTER)
        proeminentContent = typedArray.getBoolean(
            R.styleable.StandardAppBarTop_proeminentContent,
            false
        )
    }

    private fun setElevation(enabledElevation: Boolean) {
        if (enabledElevation) {
            toolbar.elevation = getElevationFromTheme(context)
        } else {
            toolbar.elevation = 0F
        }
    }

    private fun getElevationFromTheme(context: Context): Float {
        val typedValue = TypedValue()
        if (context.theme.resolveAttribute(R.attr.elevation02, typedValue, true)) {
            return TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
                .toFloat()
        }
        return 0f
    }

    private fun handleScroll(scrollable: Boolean) {
        val params = toolbar.layoutParams as LayoutParams
        params.scrollFlags = when (scrollable) {
            true -> (LayoutParams.SCROLL_FLAG_SCROLL)
            false -> (LayoutParams.SCROLL_FLAG_NO_SCROLL)
        }
    }

    private fun removeParentsElevation() {
        val stListAnimator = StateListAnimator()
        stListAnimator.addState(
            IntArray(0),
            ObjectAnimator.ofFloat(this, "elevation", 0.1F)
        )
        stateListAnimator = stListAnimator
    }

    private fun throwsCountElementsException() {
        if (countElements() > MAX_COUNT_ELEMENTS) {
            throw IllegalArgumentException("⚠️ ⚠️ GaYaIssue: Standard App Bar Top can't have more than five elements (including the content)")
        }
    }

    private fun initialConfigurations() {
        toolbar.contentInsetStartWithNavigation = 0
        clipToPadding = false
        clipChildren = false
    }

    private fun addImage(context: Context, resourceImage: Int) {
        val imageView = ImageView(context)
        imageView.id = R.id.contentMedia
        imageView.setImageResource(resourceImage)
        imageView.layoutParams =
            LayoutParams(
                mediaWidth,
                mediaHeight
            )
        addContentView(imageView)
    }

    private fun addTextView(context: Context, text: String) {
        val textView = TextView(context)
        textView.id = R.id.contentText
        textView.text = text
        textView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            context.resources.getDimension(R.dimen.ds_size_h6)
        )
        textView.isSingleLine = false
        textView.ellipsize = TextUtils.TruncateAt.END
        textView.setLines(1)
        addContentView(textView)
    }

    private fun addTextField(context: Context) {
        val textField = TextField(context)
        textField.id = R.id.contentSearch
        textField.layoutParams =
            LayoutParams(
                MATCH_PARENT,
                WRAP_CONTENT
            )
        addContentView(textField)
    }

    private fun haveChildrenToMove(): Boolean = getChildAt(ACTION_RIGHT_FIRST_ELEMENT_INDEX) != null

    private fun getNextChild(): View? = getChildAt(ACTION_RIGHT_FIRST_ELEMENT_INDEX)

    private fun countElements(): Int {
        return actionCenterContainer.childCount + actionRightContainer.childCount + actionLeftContainer.childCount
    }

    companion object {
        const val DEFAULT = 0
        const val NONE = 1
        const val PRIMARY = 2
        const val SECONDARY = 3
        const val INVERSE = 4

        const val TEXT = 0
        const val MEDIA = 1
        const val SEARCH = 2
        const val LOGO = 3

        const val LEFT = 0
        const val CENTER = 1

        private const val MAX_COUNT_ELEMENTS = 5
        private const val COUNT_ELEMENTS_ONLY_ACTION_LEFT = 2
        private const val ACTION_LEFT_ELEMENT_INDEX = 1
        private const val ACTION_RIGHT_FIRST_ELEMENT_INDEX = 1

        private const val NOT_RESOURCE_FOUND_CODE = 0
    }
}
