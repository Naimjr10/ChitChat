package com.example.ChitChat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.net.*
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.net.*
import kotlin.random.Random
import kotlin.random.nextInt

private const val TAG_ChitChatActivity = "ChitChatActivity"
private const val TAG_OnInternetCallback = "OnInternetCallback"

class ChitChatActivity : AppCompatActivity() {
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var onInternetCallback: OnInternetCallback

    private var myClientSocket: Socket? = null
    private var myServerSocket: ServerSocket? = null

    private var _myIP: InetAddress? = null
    val myIP: InetAddress?
        get() {
            return if (_myIP == null) null
            else _myIP!!
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(
            TAG_ChitChatActivity, "onCreate(\n" +
                    "savedInstanceState: Bundle?\n" +
                    ")\n" +
                    "savedInstanceState == $savedInstanceState"
        )

        super.onCreate(savedInstanceState)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        onInternetCallback = OnInternetCallback()
        val internetRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()

        connectivityManager.registerNetworkCallback(internetRequest, onInternetCallback)

        setContentView(R.layout.fragment_container)
    }

    override fun onDestroy() {
        Log.d(TAG_ChitChatActivity, "onDestroy()")

        super.onDestroy()

        connectivityManager.unregisterNetworkCallback(onInternetCallback)
    }

    // metode ini akan melemparkan pengecualian jika
    // tidak dapat terhubung ke server.
    // catatan : metode ini nge-block main thread
    fun connectToServer(
        serverIP: String,
        serverPort: String
    ) {
        val connectingThread = object : Thread() {
            override fun run() {
                myClientSocket = Socket()
                var myClientPort = Random.nextInt(0..65500)

                // bind sampai berhasil selagi ada koneksi
                while (myIP != null) {
                    try {
                        myClientSocket!!.bind(
                            InetSocketAddress(myIP, myClientPort)
                        )

                        // berhenti jika bind berhasil
                        break
                    } catch (e: Exception) {
                        // ganti nomor port jika bind gagal
                        myClientPort = Random.nextInt(0..65500)

                        e.printStackTrace()
                    }
                }
                try {
                    // proses input user menjadi IP
                    val serverAddress = InetAddress.getByName(serverIP)
                    // proses input user menjadi nomor Port
                    val serverPortNumber = serverPort.toInt()

                    // hubungkan ke server
                    myClientSocket!!.connect(
                        InetSocketAddress(serverAddress, serverPortNumber),
                        5_000
                    )
                } catch (e: Exception) {
                    // gagal menghubungkan ke server

                    myClientSocket = null
                    e.printStackTrace()
                }
            }
        }
        connectingThread.name = "ConnectingToServerThread"
        connectingThread.start()

        // block main thread dan tunggu connectingThread untuk selesai mengeksekusi
        connectingThread.join()

        if (myClientSocket == null) {
            throw Exception("Tidak dapat terhubung ke server cuy")
        }
    }

    private inner class OnInternetCallback : ConnectivityManager.NetworkCallback() {
        override fun onUnavailable() {
            Log.d(TAG_OnInternetCallback, "onUnavailable()")
        }

        override fun onAvailable(network: Network) {
            Log.d(
                TAG_OnInternetCallback,
                "onAvailable(\n" +
                        "network: Network\n" +
                        ")\n" +
                        "network == $network"
            )

            val linkAddresses = connectivityManager.getLinkProperties(network)!!.linkAddresses
            for (linkAddress in linkAddresses) {
                val ip = linkAddress.address
                if (ip.isReachable(1_000)) {
                    _myIP = ip
                }
            }
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            Log.d(
                TAG_OnInternetCallback,
                "onCapabilitiesChanged(\n" +
                        "network: Network,\n" +
                        "networkCapabilities: NetworkCapabilities\n" +
                        ")\n" +
                        "network == $network\n" +
                        "networkCapabilities == $networkCapabilities"
            )
        }

        override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
            Log.d(
                TAG_OnInternetCallback,
                "onLinkPropertiesChanged(\n" +
                        "network: Network,\n" +
                        "linkProperties: LinkProperties\n" +
                        ")\n" +
                        "network == $network\n" +
                        "linkProperties == $linkProperties"
            )
        }

        override fun onLost(network: Network) {
            Log.d(
                TAG_OnInternetCallback,
                "onLost(\n" +
                        "network: Network\n" +
                        ")\n" +
                        "network == $network"
            )
            myClientSocket = null
            myServerSocket = null

            _myIP = null
        }
    }
}