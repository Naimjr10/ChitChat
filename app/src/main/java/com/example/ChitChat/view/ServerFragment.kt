package com.example.ChitChat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ChitChat.ChitChatActivity
import com.example.ChitChat.R

class ServerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_server, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val alamatIP = view.findViewById<TextView>(R.id.textview_alamat_ip)
        val nomorPort = view.findViewById<TextView>(R.id.textview_nomor_port)

    }
}