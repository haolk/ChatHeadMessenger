package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.ImageView


class ChatHeadService : Service() {
    private var windowManager: WindowManager? = null
    private var chatHeadView: View? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("RtlHardcoded", "ClickableViewAccessibility")
    override fun onCreate() {
        super.onCreate()
        chatHeadView = LayoutInflater.from(this).inflate(R.layout.layout_chat_head, null)

        // Add the view to the window
        val param = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        // Specify the chat head position
        param.gravity = Gravity.TOP
        param.x = 0
        param.y = 100

        // Add the view to the window
        windowManager = getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        windowManager?.addView(chatHeadView, param)

        // Set the close button
        val btnClose = chatHeadView?.findViewById<ImageView>(R.id.btnClose)
        btnClose?.setOnClickListener {
            stopSelf()
        }

        val imgChatHead = chatHeadView?.findViewById<ImageView>(R.id.imgChatHead)
        imgChatHead?.setOnTouchListener(object : OnTouchListener {
            private var lastAction: Int = 0
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = param.x
                        initialY = param.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY

                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        //As we implemented on touch listener with ACTION_MOVE,
                        //we have to check if the previous action was ACTION_DOWN
                        //to identify if the user clicked the view or not.
                        if (lastAction == MotionEvent.ACTION_DOWN) {
                            //Open the chat conversation click.
                            val intent = Intent(this@ChatHeadService, ChatActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            //close the service and remove the chat heads
                            stopSelf()
                        }
                        lastAction = event.action
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        param.x = initialX + (event.rawX - initialTouchX).toInt()
                        param.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        windowManager?.updateViewLayout(chatHeadView, param)
                        lastAction = event.action
                        return true
                    }
                }
                return false
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (chatHeadView != null) windowManager?.removeView(chatHeadView)
    }
}