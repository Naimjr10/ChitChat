package com.example.ChitChat.view

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.size
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.ChitChat.ChitChatActivity
import com.example.ChitChat.R
import com.google.android.material.textfield.TextInputEditText
import java.time.format.TextStyle

private const val TAG_ChattingFragment = "ChattingFragment"

class ChattingFragment : Fragment() {

    override fun onAttach(context: Context) {
        Log.d(
            TAG_ChattingFragment,
            "onAttach(\n" +
                    "context: Context\n" +
                    ")\n" +
                    "context == $context"
        )
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(
            this, (activity as ChitChatActivity).CustomOnBackPress(enabled = true)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(
            TAG_ChattingFragment,
            "onCreate(\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "savedInstanceState == $savedInstanceState"
        )
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(
            TAG_ChattingFragment,
            "onCreateView(\n" +
                    "inflater: LayoutInflater,\n" +
                    "container: ViewGroup?,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "inflater == $inflater\n" +
                    "container == $container\n" +
                    "savedInstanceState == $savedInstanceState"
        )

        val view = inflater.inflate(R.layout.fragment_chatting, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(
            TAG_ChattingFragment,
            "onViewCreated(\n" +
                    "view: View,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "view == $view\n" +
                    "savedInstanceState == $savedInstanceState"
        )
        super.onViewCreated(view, savedInstanceState)
        (activity as ChitChatActivity).socketToSocketConnection.observe(
            viewLifecycleOwner,
            // Single Abstract Method (SAM) Constructor
            Observer {
                if (it == ChitChatActivity.SOCKET_CONNECTION_NOT_CONNECTED) {
                    Toast.makeText(
                        requireContext(),
                        "Koneksi terputus",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        )

        val containerPesanPesan = view.findViewById<LinearLayout>(R.id.container_pesan_pesan)
        val textInputPesan = view.findViewById<TextInputEditText>(R.id.textinput_pesan)
        val viewKirimPesan = view.findViewById<ImageView>(R.id.imageview_kirim_pesan)

        (activity as ChitChatActivity).startReceivingMessage()
        (activity as ChitChatActivity).receivedMessage.observe(
            this.viewLifecycleOwner,
            Observer {
                val tv = TextView(requireContext())
                containerPesanPesan.addView(tv)
                (tv.layoutParams as LinearLayout.LayoutParams).apply {
                    this.width = LinearLayout.LayoutParams.WRAP_CONTENT
                    this.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    this.gravity = Gravity.START
                }
                tv.text = it
                tv.setBackgroundColor(Color.GREEN)
                tv.textSize = 25F
            }
        )

        viewKirimPesan.setOnClickListener {
            (activity as ChitChatActivity).sendMessage(textInputPesan.text.toString())

            val tv = TextView(requireContext())
            containerPesanPesan.addView(tv)
            (tv.layoutParams as LinearLayout.LayoutParams).apply {
                this.width = LinearLayout.LayoutParams.WRAP_CONTENT
                this.height = LinearLayout.LayoutParams.WRAP_CONTENT
                this.gravity = Gravity.END
            }
            tv.text = textInputPesan.text.toString()
            tv.setBackgroundColor(Color.YELLOW)
            tv.textSize = 25F

            textInputPesan.text?.clear()
        }
    }

    override fun onDestroy() {
        Log.d(
            TAG_ChattingFragment,
            "onDestroy()"
        )
        super.onDestroy()
    }
}