package com.example.ChitChat

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.net.*
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*
import kotlin.random.Random
import kotlin.random.nextInt

private const val TAG_ChitChatActivity = "ChitChatActivity"
private const val TAG_OnInternetCallback = "OnInternetCallback"
private const val TAG_CustomOnBackPress = "CustomOnBackPress"

class ChitChatActivity : AppCompatActivity() {
    companion object {
        // konstan yang ditujukan untuk properti '_socketToSocketConnection'
        const val SOCKET_CONNECTION_CONNECTED = "connected"
        const val SOCKET_CONNECTION_NOT_CONNECTED = "not_connected"
    }

    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var onInternetCallback: OnInternetCallback

    private var _myIP: InetAddress? = null
    val myIP: InetAddress?
        get() = _myIP

    private var myClientSocket: Socket? = null

    private var myServerSocket: Socket? = null
    private var _myServerPort: Int? = null
    val myServerPort: Int?
        get() = _myServerPort

    // menunjukkan keadaan koneksi dari soket perangkat ini ke soket perangkat lain
    private var _socketToSocketConnection = MutableLiveData<String>()
    val socketToSocketConnection: LiveData<String>
        get() = _socketToSocketConnection

    // berisi pesan yang diterima dari perangkat lain
    private val _receivedMessage = MutableLiveData<String>()
    val receivedMessage: LiveData<String>
        get() = _receivedMessage


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
        Log.d(
            TAG_ChitChatActivity,
            "connectToServer(\n" +
                    "serverIP: String,\n" +
                    "serverPort: String\n" +
                    ")\n" +
                    "serverIP == $serverIP\n" +
                    "serverPort == $serverPort"
        )
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
                        3_000
                    )
                    runOnUiThread {
                        _socketToSocketConnection.value = SOCKET_CONNECTION_CONNECTED
                    }
                } catch (e: Exception) {
                    // gagal menghubungkan ke server
                    myClientSocket?.close()
                    runOnUiThread {
                        _socketToSocketConnection.value = SOCKET_CONNECTION_NOT_CONNECTED
                    }

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

    fun startAcceptingClient() {
        Log.d(TAG_ChitChatActivity, "startAcceptingClient()")

        val acceptingThread = object : Thread() {
            override fun run() {
                val serverSocket = ServerSocket()
                var serverPort = Random.nextInt(0..65500)

                // bind socket selagi ada koneksi internet
                while (myIP != null) {
                    try {
                        serverSocket.bind(
                            InetSocketAddress(myIP, serverPort)
                        )

                        // berhenti jika bind berhasil
                        break
                    } catch (e: Exception) {
                        // ganti nomor port lain jika bind gagal
                        serverPort = Random.nextInt(0..65500)

                        e.printStackTrace()
                    }
                }
                _myServerPort = serverPort

                try {
                    myServerSocket = serverSocket.accept()

                    // jika metode .accpet() keluar (return), maka sudah berhasil terhubung
                    runOnUiThread {
                        _socketToSocketConnection.value = SOCKET_CONNECTION_CONNECTED
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        _socketToSocketConnection.value = SOCKET_CONNECTION_NOT_CONNECTED
                    }

                    serverSocket.close()
                    myServerSocket = null

                    e.printStackTrace()
                }
            }
        }
        acceptingThread.name = "AcceptingClientThread"
        acceptingThread.start()
    }

    fun sendMessage(message: String) {
        val sendingThread = object : Thread() {
            override fun run() {
                try {
                    // cek jika perannya sebagai client
                    if (myClientSocket != null) {
                        // kirim pesan
                        ObjectOutputStream(myClientSocket!!.getOutputStream()).writeObject(message)
                        return
                    }
                    // cek jika perannya sebagai server
                    if (myServerSocket != null) {
                        // kirim pesan
                        ObjectOutputStream(myServerSocket!!.getOutputStream()).writeObject(message)
                        return
                    }
                } catch (e: Exception) {
                    // jika terjadi masalah dengan koneksi socket,
                    // update keadaan koneksi socket
                    runOnUiThread {
                        _socketToSocketConnection.value = SOCKET_CONNECTION_NOT_CONNECTED
                    }
                    if (myClientSocket != null) {
                        myClientSocket!!.close()
                        myClientSocket = null
                    }
                    if (myServerSocket != null) {
                        myServerSocket!!.close()
                        myServerSocket = null
                    }

                    e.printStackTrace()
                }

            }
        }
        sendingThread.name = "SendingMessageThread"
        sendingThread.start()
    }

    fun startReceivingMessage() {
        val receivingThread = object : Thread() {
            override fun run() {
                while (true) {
                    try {
                        // cek jika perannya sebagai client
                        if (myClientSocket != null) {
                            // baca pesan yang dikirim oleh perangkat lain
                            val receivedMessage =
                                ObjectInputStream(myClientSocket!!.getInputStream()).readObject() /* timeout readObject kenapa lama? */
                            runOnUiThread {
                                _receivedMessage.value = (receivedMessage as String)
                            }
                            continue
                        }
                        // cek jika perannya sebagai server
                        if (myServerSocket != null) {
                            // baca pesan yang dikirim oleh perangkat lain
                            val receivedMessage =
                                ObjectInputStream(myServerSocket!!.getInputStream()).readObject()
                            runOnUiThread {
                                _receivedMessage.value = (receivedMessage as String)
                            }
                            continue
                        }
                    } catch (e: Exception) {
                        // jika terjadi masalah dengan koneksi socket,
                        // update keadaan koneksi socket
                        runOnUiThread {
                            _socketToSocketConnection.value = SOCKET_CONNECTION_NOT_CONNECTED
                        }

                        if (myClientSocket != null) {
                            myClientSocket!!.close()
                            myClientSocket = null
                        }
                        if (myServerSocket != null) {
                            myServerSocket!!.close()
                            myServerSocket = null
                        }
                        e.printStackTrace()

                        break
                    }
                }
            }
        }
        receivingThread.name = "ReceivingMessageThread"
        receivingThread.start()
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

                // IP ini didapat dari perangkat lokal itu sendiri, jadi menurutku
                // pelemparan pengecualian tidak mungkin terjadi
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

            _myIP = null
        }
    }

    inner class CustomOnBackPress(enabled: Boolean) : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() {
            Log.d(
                TAG_CustomOnBackPress,
                "handleOnBackPressed()"
            )
            val navigationHost = findNavController(R.id.fragment_container)
            val currentDestination = navigationHost.currentDestination!!

            // id para fragment terletak pada file 'navigation_graphic.xml'

            when (currentDestination.id) {
                R.id.clientFragment -> {
                    navigationHost.popBackStack()
                    myClientSocket = null
                    return
                }
                R.id.serverFragment -> {
                    navigationHost.popBackStack()
                    myServerSocket = null
                    return
                }
                R.id.chattingFragment -> {
                    navigationHost.popBackStack()

                    // cek jika perannya sebagai client
                    if (myClientSocket != null) {
                        // tutup client
                        myClientSocket!!.close()
                        myClientSocket = null

                        return
                    }

                    // cek jika perannya sebagai server
                    if (myServerSocket != null) {
                        // tutup server
                        myServerSocket!!.close()
                        myServerSocket = null

                        return
                    }
                }
            }
        }
    }
}