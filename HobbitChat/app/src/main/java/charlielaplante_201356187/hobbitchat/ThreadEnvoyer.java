package charlielaplante_201356187.hobbitchat;

import android.view.View;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ThreadEnvoyer implements  Runnable
{
    byte tamponEnvoyer[];
    DatagramPacket paquetEnvoyer;
    MulticastSocket socketEnvoyer;
    String MessageEnvoie;
    InetAddress adresseEnvoyer;
    String Port;
    String leUsername;

    public ThreadEnvoyer(String Message,InetAddress adresse, String Port, String Username)
    {
        try
        {
            MessageEnvoie = Message;
            adresseEnvoyer = adresse;
            leUsername = Username;
            this.Port = Port;

        }catch(Exception e)
        {

        }
    }

    public void EnvoyerMSG()
    {
        try
        {
            tamponEnvoyer = (leUsername + " : " + MessageEnvoie).getBytes();

            paquetEnvoyer = new DatagramPacket(tamponEnvoyer,0,tamponEnvoyer.length,adresseEnvoyer,Integer.parseInt(Port));

            socketEnvoyer = new MulticastSocket();

            socketEnvoyer.send(paquetEnvoyer);

        }catch(Exception e)
        {
            System.err.println("Ca marche pas tabarnak");
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run()
    {
        EnvoyerMSG();
    }
}
