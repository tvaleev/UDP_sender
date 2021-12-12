package com.valeev.udp_sender;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class MainActivity extends AppCompatActivity {

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void sendUDPPackets(byte[] data, InetAddress address, int port) {
        new Thread() {
            @Override
            public void run() {
                try {
                    DatagramSocket UDPSocket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                    UDPSocket.send(packet);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    public void sendDataToServer(String serverAddress, int port, String experimentId, String city, String operator) {
        int numPackets = 128;
        try {
            for (int i = 0; i < numPackets; i++) {
                String sData = "_" + experimentId + "_" + city + "_" + operator + "_" + getLocalIpAddress() + "_" + i;
                byte[] data = sData.getBytes();
                sendUDPPackets(data,InetAddress.getByName(serverAddress), port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void  sendData(String experimentId, String city, String operator) {
        String address_server1 = "159.138.201.72";
        int port1 = 30000;
        String address_server2 = "159.138.205.40";
        int port2 = 40000;
        sendDataToServer(address_server1, port1, experimentId, city, operator);
        sendDataToServer(address_server2, port2, experimentId, city, operator);
        Toast.makeText(getApplicationContext(), "Finished", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.SendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String city = ((TextView) findViewById(R.id.editTextCity)).getText().toString();
                String operator = ((TextView) findViewById(R.id.editTextTextTelecom)).getText().toString();
                long unixTime = System.currentTimeMillis() / 1000L;
                String exId = Long.toString(unixTime);

                sendData(exId, city, operator);
            }
        });
    }
}