package com.example.ChitChat

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import android.net.*
import android.net.ConnectivityManager.NetworkCallback
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.net.*

private const val TAG_ConnectingActivity = "ConnectingActivity"
private const val TAG_OnInternetCallback = "ConnectingActivity.OnInternetCallback"

class ConnectingActivity : AppCompatActivity() {

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var onInternetCallback: OnInternetCallback
    private var myIPs: MutableLiveData<List<LinkAddress>?> = MutableLiveData(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(
            TAG_ConnectingActivity, "onCreate(\n" +
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
            .build()

        connectivityManager.registerNetworkCallback(internetRequest, onInternetCallback)

        setContentView(R.layout.activity_main)
        val wrapperTextInputAlamatIP =
            findViewById<TextInputLayout>(R.id.wrapper_text_input_alamat_IP)
        val textInputAlamatIP =
            findViewById<TextInputEditText>(R.id.text_input_alamat_IP)
        val buttonHubungkan =
            findViewById<MaterialButton>(R.id.button_hubungkan)
        val buttonMulai =
            findViewById<MaterialButton>(R.id.button_mulai)

        myIPs.observe(this) { IPs: List<LinkAddress>? ->
            if (IPs == null) {
                wrapperTextInputAlamatIP.apply {
                    helperText = "Alamat IP saya : " + "null\n" + "Tidak ada koneksi internet"
                }
                textInputAlamatIP.apply {

                }
                buttonHubungkan.apply {
                    setOnClickListener{
                        Toast.makeText(
                            this@ConnectingActivity,
                            "Tidak ada koneksi internet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                wrapperTextInputAlamatIP.apply {
                    helperText = "Alamat IP saya : "
                    for (IP in IPs) {
                        if (IPs.indexOf(IP) != IPs.lastIndex) {
                            helperText = helperText.toString() + IP.address.toString() + ", "
                        } else {
                            helperText = helperText.toString() + IP.address.toString() + "."
                        }
                    }
                }

                buttonHubungkan.apply {
                    setOnClickListener{

                    }
                }
            }

        }

    }

    override fun onDestroy() {
        Log.d(TAG_ConnectingActivity, "onDestroy()")

        super.onDestroy()

        connectivityManager.unregisterNetworkCallback(onInternetCallback)
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
            runOnUiThread(
                Runnable {
                    myIPs.value = linkProperties.linkAddresses
                }
            )
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

        override fun onLost(network: Network) {
            Log.d(
                TAG_OnInternetCallback,
                "onLost(\n" +
                        "network: Network\n" +
                        ")\n" +
                        "network == $network"
            )
            runOnUiThread(
                Runnable {
                    myIPs.value = null
                }
            )
        }

    }
}