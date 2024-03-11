package com.example.ChitChat.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.ChitChat.ChitChatActivity
import com.example.ChitChat.R
import com.google.android.material.textfield.TextInputEditText

private const val TAG_ClientFragment = "ClientFragment"

class ClientFragment : Fragment() {

    override fun onAttach(context: Context) {
        Log.d(
            TAG_ClientFragment,
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
            TAG_ClientFragment,
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
            TAG_ClientFragment,
            "onCreateView(\n" +
                    "inflater: LayoutInflater,\n" +
                    "container: ViewGroup?,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "inflater == $inflater\n" +
                    "container == $container\n" +
                    "savedInstanceState == $savedInstanceState"
        )

        val view = inflater.inflate(R.layout.fragment_client, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(
            TAG_ClientFragment,
            "onViewCreated(\n" +
                    "view: View,\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "view == $view\n" +
                    "savedInstanceState == $savedInstanceState"
        )

        view.findViewById<Button>(R.id.button_hubungkan_ke_server).apply {

            setOnClickListener {
                val chitChatActivity = activity as ChitChatActivity
                val textInputAlamatIP =
                    view.findViewById<TextInputEditText>(R.id.textinput_alamat_ip)
                val textInputNomorPort =
                    view.findViewById<TextInputEditText>(R.id.textinput_nomor_port)

                try {
                    this.text = "Menghubungkan..."
                    chitChatActivity.connectToServer(
                        textInputAlamatIP.text.toString(), textInputNomorPort.text.toString()
                    )
                    // jika berhasil terhubung, lanjut ke sesi chat
                    findNavController().navigate(R.id.action_clientFragment_to_chattingFragment)
                } catch (e: Exception) {
                    // jika gagal terhubung, beritahu pengguna
                    this.text = "Hubungkan"
                    Toast.makeText(
                        requireContext(), "Tidak dapat terhubung", Toast.LENGTH_SHORT
                    ).show()

                    e.printStackTrace()
                }

            }

        }
    }

    override fun onDestroy() {
        Log.d(
            TAG_ClientFragment,
            "onDestroy()"
        )

        super.onDestroy()
    }
}