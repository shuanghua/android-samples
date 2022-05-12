package com.sample.slidedrawerlayout.widget

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowMetrics
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper


@SuppressLint("ObsoleteSdkInt")
class SlideDrawerContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtt: Int = 0
) : FrameLayout(context, attrs, defStyleAtt) {
    var contentView: View? = null // 第 0 位置的 view
    var drawerView: View? = null // 第 1 位置的 view

    private var containerWidth = 0  // drawer 关闭时等于屏幕的宽度, 打开时等于屏幕宽度 x2

    private var drawerViewLeftOffset = 0
    private var contentViewLeftOffset = 0

    private lateinit var dragHelper: ViewDragHelper
    private lateinit var rightDragHelper: ViewDragHelper

    private var isOpen: Boolean = false

    private val rightCallback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == contentView
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            println("---------->>$--------")
            if (releasedChild == contentView) {
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            println("---------->>$--------")
            val containerWidth = width // 布局的宽度,最大为两个屏幕宽度
            //先取最大, 再取最小
            return (containerWidth - child.width).coerceAtLeast(left.coerceAtMost(containerWidth))
        }
    }


    private val callback = object : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child == drawerView
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val width = contentView!!.width
            val minDrag = width / 4
            val drawerDx = width - releasedChild.left
            val left = releasedChild.left
//            println("---------->>is=${isOpen}  dx=$drawerDx  left=$left  三=$minDrag  V=$xvel")

            if (!isOpen) {
                // open
                if (xvel == 0f && drawerDx >= width / 2) {
                    openDrawer(releasedChild)

                }
                if (xvel < 0f && drawerDx >= minDrag) {
                    openDrawer(releasedChild)
                }

                //close
                if (xvel == 0f && drawerDx < width / 2) {
                    closeDrawer(releasedChild)
                }
                if (xvel < 0f && drawerDx < minDrag) {
                    closeDrawer(releasedChild)
                }

                // null
                if (xvel > 0f) {
                    // 移动 contentView
                }
            }

            if (isOpen) {
                // open
                if (xvel == 0f && left < width / 2) {
                    openDrawer(releasedChild)
                }
                if (xvel > 0f && left < width / 4) {
                    openDrawer(releasedChild)
                }

                // close
                if (xvel == 0f && left > width / 2) {
                    closeDrawer(releasedChild)
                }
                if (xvel > 0f && left > width / 4) {
                    closeDrawer(releasedChild)
                }
            }


        }

        override fun getViewHorizontalDragRange(child: View): Int {
            if (child == drawerView) {
                return child.width
            } else {
                return 0
            }
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            val containerWidth = width // 布局的宽度,最大为两个屏幕宽度
            //先取最大, 再取最小
            return (containerWidth - child.width).coerceAtLeast(left.coerceAtMost(containerWidth))
        }

        override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
            dragHelper.captureChildView(drawerView!!, pointerId)
        }

        // 触摸
        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,  //dx < 0 : 往左滑
            dy: Int
        ) {
//            if (!isOpen){
//                if (dx > 0){
//                    println("---------->>is=${isOpen}   left=$left   dx=$dx")
//                    //关闭activity
//                }else{
//                    if (changedView == drawerView) {
//                        changedView.elevation = 2f
//                        contentView!!.translationX = -(contentView!!.width - left) / 3f
//                    }
//                }
//            }else{
//                if (dx > 0){
//                    if (changedView == drawerView) {
//                        changedView.elevation = 2f
//                        contentView!!.translationX = -(contentView!!.width - left) / 3f
//                    }
//                }
//            }

            if (changedView == drawerView) {
                changedView.elevation = 20f
                contentView!!.translationX = -(contentView!!.width - left) / 3f
            }
        }


        fun openDrawer(releasedChild: View) {
            if (releasedChild == drawerView) {
                dragHelper.settleCapturedViewAt(0, 0)
                isOpen = true
            }
            invalidate()
            return
        }

        fun closeDrawer(releasedChild: View) {
            if (releasedChild == drawerView) {
                dragHelper.settleCapturedViewAt(contentView!!.width, 0)
                isOpen = false
            }
            invalidate()
            return
        }
    }


    init {
        dragHelper = ViewDragHelper.create(this, callback)
//        rightDragHelper = ViewDragHelper.create(this, rightCallback)

        // 激活边界触摸,同时把边界触摸的范围设置为全屏
        dragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT or ViewDragHelper.EDGE_RIGHT)
        dragHelper.edgeSize = getDisplayWidth(context) / 2

        // 让本 view 能显示在状态栏下面,需要在 xml 中设置 android:fitsSystemWindows="true"
        if (ViewCompat.getFitsSystemWindows(this)) {
            setOnApplyWindowInsetsListener { view, insets -> setWindowInsets(insets, view) }
            systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    private var lastInsets: Any? = null
    private var drawStatusBarBackground: Boolean = false

    private fun setWindowInsets(insets: WindowInsets, view: View): WindowInsets {
        val drawerContainer = view as SlideDrawerContainer
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val systemBar = insets.getInsets(WindowInsets.Type.systemBars())
            drawerContainer.setChildInsets(insets, systemBar.bottom > 0)
            WindowInsets.CONSUMED
        } else {
            drawerContainer.setChildInsets(insets, insets.systemWindowInsetTop > 0)
            insets.consumeSystemWindowInsets()
        }
    }

    private fun setChildInsets(insets: Any, draw: Boolean) {
        lastInsets = insets
        drawStatusBarBackground = draw
        setWillNotDraw(!draw && background == null)
        requestLayout()
    }

    private fun getDisplayWidth(context: Context): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics =
                (context as Activity).windowManager.maximumWindowMetrics
            windowMetrics.bounds.height()
        } else {
            resources.displayMetrics.widthPixels
        }
    }

    override fun computeScroll() {
        if (dragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun onLayout(c: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(c, l, t, r, b)
        if (hasDrawerView()) {
            var width = r - 1
            val height = b - t
            containerWidth = width  // 起始宽度为屏幕宽度, 往右滑容器宽度变大, 最大为两倍屏幕宽度

            dragHelper.edgeSize = contentView!!.width

            if (drawerViewLeftOffset < width) {
                width = if (drawerViewLeftOffset <= 0) 0 else drawerViewLeftOffset
            }

            if (contentViewLeftOffset >= containerWidth) {
                contentViewLeftOffset = containerWidth
            } else if (contentViewLeftOffset <= 0) {
                contentViewLeftOffset = 0
            }
            // 当drawer 滑出遮挡 mainView 时 ,mainView 的位置联动向左移
            // left = 左移的距离 ; right = mainView的宽度-左移距离
            contentView?.layout(0, 0, containerWidth, height)
            drawerView?.layout(containerWidth, 0, containerWidth * 2, height)
            // width = [0 ~ width]  当 width = 0 时,drawerView 完全隐藏在屏幕右侧 ,  width 变大 drawerView 正往左移动 即 width = drawerViewLeftOffset
        }
    }

    private fun hasDrawerView(): Boolean {
        if (childCount != 2) {
            return false
        }
        if (drawerView != null && contentView != null) {
            return true
        }
        val view0 = getChildAt(0)
        if (view0 != null) {
            contentView = view0
            val view1 = getChildAt(1)
            if (view1 != null) {
                drawerView = view1
                if (drawerView == null || drawerView!!.layoutParams == null) {
                    val lp = LayoutParams(
                        LayoutParams.MATCH_PARENT,
                        LayoutParams.MATCH_PARENT
                    )
                    lp.width = getDisplayWidth(context)
                    drawerView!!.layoutParams = lp
                }
                return true
            }
            throw Resources.NotFoundException("SlideDrawerContainer must have a child view which id is drawer layout!")
        }
        throw Resources.NotFoundException("SlideDrawerContainer must have a child view which id is content layout!")
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return dragHelper.shouldInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    fun isOpen(): Boolean {
        if (drawerView == null) {
            return false
        }
        return drawerView!!.left == 0
    }

    fun open() {
        if (drawerView != null) {
            if (dragHelper.smoothSlideViewTo(drawerView!!, 0, 0)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }

    fun close() {
        if (drawerView != null) {
            if (dragHelper.smoothSlideViewTo(drawerView!!, containerWidth, 0)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
    }
}
