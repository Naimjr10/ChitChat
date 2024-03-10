package com.example.ChitChat.view

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

class ClientFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client, container, false)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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

}