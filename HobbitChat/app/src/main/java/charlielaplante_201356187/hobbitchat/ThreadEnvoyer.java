package charlielaplante_201356187.hobbitchat;

import android.view.View;
import android.widget.EditText;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ThreadEnvoyer implements  Runnable
{
	// Declaration des variables
    byte tamponEnvoyer[];
    DatagramPacket paquetEnvoyer;
    MulticastSocket socketEnvoyer;
    String MessageEnvoie;
    InetAddress adresseEnvoyer;
    String Port;
    String leUsername;

	// Constructeur de la classe
    public ThreadEnvoyer(String Message,InetAddress adresse, String Port, String Username)
    {
        try
        {
			// affectation des valeurs passées en parametre aux variables de la classe
            MessageEnvoie = Message;
            adresseEnvoyer = adresse;
            leUsername = Username;
            this.Port = Port;

        }catch(Exception e)
        {

        }
    }

	// Fonction qui envoit les messages aux serveurs 
    public void EnvoyerMSG()
    {
        try
        {
			// Transformer le message qu'on veut envoyer en bytes pour mettre dans le tampon
            tamponEnvoyer = (leUsername + " : " + MessageEnvoie).getBytes();
			// Faire un paquet avec les tampons pour l'envoyer au serveur
            paquetEnvoyer = new DatagramPacket(tamponEnvoyer,0,tamponEnvoyer.length,adresseEnvoyer,Integer.parseInt(Port));
			// Creer le socket en multicast
            socketEnvoyer = new MulticastSocket();
			// Sert du socket pour envoyer le paquet dans le neant multicast ou qqun va pouvoir l'attraper
            socketEnvoyer.send(paquetEnvoyer);

        }catch(Exception e)
        {
            System.err.println("Ca marche pas tabarnak");
            e.printStackTrace();
            System.exit(1);
        }
    }

	// Debute a chaque fois qu'on crée le thread
    @Override
    public void run()
    {
        EnvoyerMSG();
    }
}
