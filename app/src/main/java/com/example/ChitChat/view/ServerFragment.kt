package com.example.ChitChat.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.ChitChat.ChitChatActivity
import com.example.ChitChat.R

private const val TAG_ServerFragment = "ServerFragment" 
        
class ServerFragment : Fragment() {
    override fun onAttach(context: Context) {
        Log.d(
            TAG_ServerFragment,
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
            TAG_ServerFragment,
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
            TAG_ServerFragment,
            "onCreateView(\n" +
                    "inflater: LayoutInflater,\n" +
                    "container: ViewGroup?,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "inflater == $inflater\n" +
                    "container == $container\n" +
                    "savedInstanceState == $savedInstanceState"
        )
        val chitChatActivity = (activity as ChitChatActivity)
        chitChatActivity.startAcceptingClient()
        chitChatActivity.socketToSocketConnection.observe(
            this.viewLifecycleOwner,

            // Single Abstract Method (SAM) constructor
            Observer {
                if (it == ChitChatActivity.SOCKET_CONNECTION_CONNECTED) {
                    findNavController().navigate(R.id.action_serverFragment_to_chattingFragment)
                }
                if (chitChatActivity.myIP == null) {
                    Toast.makeText(
                        requireContext(),
                        "Koneksi terputus",
                        Toast.LENGTH_SHORT
                    ).show()
                    findNavController().popBackStack()
                }
            }
        )

        val view = inflater.inflate(R.layout.fragment_server, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(
            TAG_ServerFragment,
            "onViewCreated(\n" +
                    "view: View,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "view == $view\n" +
                    "savedInstanceState == $savedInstanceState"
        )

        val chitChatActivity = (activity as ChitChatActivity)
        val alamatIP = view.findViewById<TextView>(R.id.textview_alamat_ip)
        val nomorPort = view.findViewById<TextView>(R.id.textview_nomor_port)

        while (chitChatActivity.myIP == null || chitChatActivity.myServerPort == null) {
            // block sampai ip dan server port ada
        }

        alamatIP.text = "Alamat IP saya : ${chitChatActivity.myIP}"
        nomorPort.text = "Nomor Port saya : ${chitChatActivity.myServerPort}"
    }

    override fun onResume() {
        Log.d(TAG_ServerFragment, "onResume()")
        super.onResume()
    }

    override fun onDestroy() {
        Log.d(TAG_ServerFragment, "onDestroy()")
        super.onDestroy()
    }
}