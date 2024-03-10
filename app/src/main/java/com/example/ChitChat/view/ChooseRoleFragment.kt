package com.example.ChitChat.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.ChitChat.ChitChatActivity
import com.example.ChitChat.R
import java.net.ServerSocket
import java.net.Socket

class ChooseRoleFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_choose_role, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.textview_choose_role).apply {

        }
        view.findViewById<Button>(R.id.button_role_client).apply {
            setOnClickListener {
                findNavController().navigate(R.id.action_chooseRoleFragment_to_clientFragment)
            }
        }
        view.findViewById<Button>(R.id.button_role_server).apply {
            setOnClickListener {
                val chitChatActivity = activity as ChitChatActivity
                if (chitChatActivity.myIP == null) {
                    Toast.makeText(
                        requireContext(),
                        "Tidak ada koneksi",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    findNavController().navigate(R.id.action_chooseRoleFragment_to_serverFragment)
                }
            }
        }
    }
}