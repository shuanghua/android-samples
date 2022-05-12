package com.sample.slidedrawerlayout.touch

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.FrameLayout
import timber.log.Timber

fun switchEvent(e: Int): String {
    return when (e) {
        0 -> "Down"
        1 -> "Up"
        2 -> "Move"
        else -> {
            "XXXX:$e"
        }
    }
}

class TouchViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAtt: Int = 0
) : FrameLayout(context, attrs, defStyleAtt) {

    // 如果重写开发者了 down 时, 没有"主动"去调用  onInterceptTouchEvent 和 onTouchEvent 或 super.dispatchTouchEvent(ev) ,
    // 分发方法不管返回 true 或者 false , 则本 ViewGroup 的 onInterceptTouchEvent 和 onTouchEvent 就收不到 该事件 和 后续事件, 因为覆盖了系统默认分发机制

    // 如果重写了 down , 对于 down 没有 "主动"去调用  onInterceptTouchEvent 和 onTouchEvent , 意味 down 没有被本 view 消费掉
    // 同样因为重写而没有调用 super.dispatchTouchEvent(ev) , 系统默认的 down 也就失去了类似先锋探子的身份,  ViewGroup.java 中的就会把后续的第一个事件(也就是 move)作为先锋探子,

    // 重写, 返回 true, 意思是把事件给子 view, 但如果留空没有写明传递给哪个子 View , 则子 View 也收不到事件, 直接变成空消耗结束本次事件, 系统将向 Activity 发送下一次事件,

    // 是否把事件传递给 子View
    // 返回 true : 空消费, 不会回传到 activity 结束消费 也不会调用 onTouchEvent 结束;

    // 返回 false : 事件直接按回程机制返回, 也就是回到到 父View 的 onTouchEvent ...  如果其上层父ViewGroup 都不消费, 则回到 Activity.onTouchEvent
    // 如果回程时发现 onTouchEvent 返回 true, 则结束该事件,并让系统派发后续事件
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 11111.dispatchTouchEvent->")
        return super.dispatchTouchEvent(ev)
    }

    // 返回 true, 则直接把该事件传给当前的 onTouchEvent:
    // 1.如 onTouchEvent 返回 true 消费了 down, 后续事件在 dispatchTouchEvent 后直接都丢给 onTouchEvent ,不经过 onInterceptTouchEvent
    // 如 onTouchEvent 返回 false 不消费, 该事件遵从正常的回路机制从 父View 的 onTouchEvent 返回到 Activity.onTouch(到了 Activity 说明没任何 view 消费该事件, 所以后续的事件都不会再走这个 View 的所有事件方法,而是将后续事件直接传给 Activity.onTouchEvent),
    // 但如果在回程的路上发现有一个 父View 可以消费该事件, 则后续的事件按正常事件分发(dispatchTouchEvent)处理机制, 看 onenote 笔记图 或者 1.
    // 对于后续事件, 如果不是本 View 的 onTouchEvent 消费, 则 onInterceptTouchEvent 会被调用 (类似高铁站, 此站(此View)不是该事件的终点站, onInterceptTouchEvent 会触发 )
    // 如果是终点站, onInterceptTouchEvent 才不会被调用
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 11111.onInterceptTouchEvent->")
        return super.onInterceptTouchEvent(ev)
    }

    // 如果 setOnTouchListener 返回 true 消费 , 则所有事件都不会在这里消费(包括开始的 Down 事件)
    // 但如果 setOnTouchListener 返回 false 不消费 , 则事件先走 setOnTouchListener ,发现 setOnTouchListener 不消费, 然后走这里查看是否消费
    // 如果两个地方都返回 false ,意思都不消费, 则 Down 先经过 setOnTouchListener, 然后在经过这里, 且后续的事件都不再经过本 View
    // [ setOnTouchListener 的 onTouchEvent 的优先级比这里 "高" , 也就是说只要 setOnTouchListener 不为空, setOnTouchListener中的 onTouchEvent 都会被调用 ]
    // onTouch 是 onTouchEvent 的爹, onTouch 能决定  onTouchEvent 能不能被调用  , 而 onTouchEvent 是 onClick 的爹 , onTouchEvent 决定 onClick 能不能被调用
    // onTouch 返回 true , onTouchEvent 不会被调用, 也导致 onClick 不会被调用

    // 一但重写了 onTouchEvent , onTouchEvent 和 onTouch 无论返回 true 还是 false , onClick 都不会执行 ,
    // 因为开发者打算自己消费, 重写覆盖掉了 View.java 源码中的 onClick 调用, 要想 onClick 能被继续调用, 则需要返回 super.onTouchEvent(event) 或者调用 super.onTouchEvent(event) + 返回 true
    // 无论 ViewGroup 是否拦截事件, onTouchEvent 只要返回 true 就都可以消费所有事件

    // Down 事件,在默认不重写任何方法的情况下,Down 像探子一样, 都会经过 dispatchTouchEvent , onInterceptTouchEvent, onTouchEvent ,
    // 探子 Down 观察哪个 View 的 onTouchEvent 中返回的是消费 true , 之后就把后续的事件给该 View ,
    // 如果 down 遇到 onTouchEvent 返回 false ,则后续的事件都不会再经过该 View 的 dispatchTouchEvent , onInterceptTouchEvent, onTouchEvent,
    // 如果整个 View 树的 onTouchEvent 都不返回 true ,则交给 Activity 的 onTouchEvent

    // onTouch 返回值决定 onTouchEvent 能不能被调用 , onTouch 返回 true 则不调用
    // super.onTouchEvent(ev) 决定 onClick 能不能被调用
    // 先锋事件被消费, 则后续的事件都会经过 onTouchEvent , 如果先锋事件不被消费, 则后续的事件都不会经过该 view ( dispatchTouchEvent , onInterceptTouchEvent, onTouchEvent 不会被调用了 )
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        Timber.d("${switchEvent(ev.actionMasked)} -> 11111.onTouchEvent")
        //super.onTouchEvent(ev)         // 后面返回 true, 才可以生效以调用 onClick ,
        return super.onTouchEvent(ev)   // 由系统决定, 系统默认根据 setOnTouchListener 或者 setOnClick 来决定
    }
}

// 默认分发情况下, 不拦截 down , down 默认进入 onTouchEvent , 也不消费 down , 则后续的 move 和 up 都不会再经过这个 view
// 如果你的 view 想接收到某一个事件的后续事件, 则一定要消费该事件 (onTouchEvent 返回 true) , 不然后续事件就不再经过你的 view ,
// 且如果整个 view 树都不消费当前事件, 则该事件的后续所有事件都不再经过 View , 而是直接给 Activity.onTouchEvent 处理

// 如果 setOnTouchListener 的 onTouch-> false 时, 意味着, 当 down 事件发送到当前 TextView 的 onTouch, down 遇到你设置的是 false ,
// down 就会找到 onTouchEvent , 如果 onTouchEvent 返回 super.onTouchEvent(ev), down 可以 onClick 在一起了, 之后的 move 和 up 也不会再经过你的 setOnTouchListener 中的 onTouch,
// 因为你告诉系统你作为开发者你不打算消费这个事件, 你只要消费了 Down , 系统才会给你发后续的其他事件.

// 默认不消费事件流程:
// 父分发 -> 父拦截 -> 父分发 -> 父拦截 -> 被触摸的子View分发 -> 被触摸的子View.onTouchEvent -> 父View.onTouchEvent -> 父View.onTouchEvent -> Activity.OnTouchEvent
// (dispatchTouchEvent , onInterceptTouchEvent) -> (dispatchTouchEvent , onInterceptTouchEvent) -> (dispatchTouchEvent, onTouchEvent) -> onTouchEvent -> onTouchEvent
//      ViewGroup1                                                ViewGroup2                                    View                       ViewGroup1      ViewGroup1

// onClick 在 up 消费结束后才调用
// 系统的默认处理事件: onTouch 决定 onTouchEvent 是否能被调用, onTouchEvent 决定 onClick
// 对于前一个事件, 如果 View 树中都不消费, 则后续的事件不再经过 view 树, 而是直接给 Activity.onTouchEvent 消费
// 只有被触摸到的 view 或 ViewGroup 才能收到事件,  两个重叠的 View, 则最离根部最远的 view 获得 (这是由事件的传递顺序决定的, 在回路上看先碰到哪个 View 的 onTouchEvent-true)
// 如果 ViewGroup2 消费了 down , 则后续的事件沿最短下发路径进入 ViewGroup2.dispatchTouchEvent -> ViewGroup2.onTouchEvent (注意这里不经过 ViewGroup2.onInterceptTouchEvent)


// 事件在回程的时候检查消费情况, 当发现有能消费的他的 View 或 ViewGroup, 则后续的事件在去路的时候直接就进入该 View 的 onTouchEvent
// 事件在去路进入 onTouchEvent 后, 如发现不能被消费, 则直接调用 Activity.onTouchEvent 消费该事件, 然后发下一个事件.


// 空消费(事件不用进入 onTouchEvent 结束消费, 也不会回传到 activity 结束消费), 如重写 dispatchTouchEvent 返回 true , 但没有说明分发给哪个子 view 时就会发生,
// 如果某一个事件是空消费, 并不影响后续其他类型事件,但会影响事件序列的完整性
// 如 onClick 点击事件 (down - up ) 如果其中某一个事件出现了空消耗, 对于系统的 onClick 逻辑而言, 意味事件序列不完整, onClik 不会被调用
// onClick 是在 up 后被调用


// 对于触摸来说, down 是所有事件的起始事件, 如果你 onTouchEvent 不消费 down , 那么你 onTouchEvent 将收不到后续事件...
// 如果你是 ViewGroup , 且你的子 View 消费了 down , 则你的 dispatchTouchEvent , onInterceptTouchEvent 还是能收到后续事件,
// 因为后续事件下发到 子View, 必须要经过 父View 的 dispatchTouchEvent 和 onInterceptTouchEvent


// 滑动冲突,假设外层 RecyclerView 是竖直滚动, 里面的 ViewPager 是横向滑动
// 环境:
// 都是 ViewGroup 且重叠: 所以默认 ViewPager 的 onTouchEvent 会先收到 Down 且消费 Down
// 我们希望 水平距离 > 竖直距离的时候, 允许 ViewPager 滑 , 也就是 ViewPager获得 move 事件, 相反就是 RecyclerView 获得事件
// 我们重写外层的 RecyclerView 的 onInterceptTouchEvent 方法, 在 down 的时候记录按下坐标 , 然后默认返回, 在 move 时计算距离(因为 RecyclerView 在外层,RecyclerView 的 dispatchTouchEvent , onInterceptTouchEvent 还是会收到后续事件)
// 当水平 > 竖直时, 我们就不能让 RecyclerView 消费 move 事件; 当 水平 < 竖直时, 让 RecyclerView 消费 move 事件