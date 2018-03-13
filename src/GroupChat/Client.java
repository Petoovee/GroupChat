package GroupChat;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import laboration2.Person;

public class Client implements Serializable {
	private int port = 4471;
	private String IP;
	private User user;
	private Message massage;
	private LinkedList<User> contactList = new LinkedList<User>();

	private ObjectOutputStream out;

	public Client(String name, ImageIcon image) {
		createUser(name, image);
		massage = new Message("hej", null, user, user);
		Thread thread;
		thread = new Connect(this, user);
		thread.start();
	}
	
	public static ArrayList<User> readFriends( String filename ) {
        ArrayList<User> list = new ArrayList<User>();
        try {
            BufferedReader br = new BufferedReader( new FileReader( filename ) );
            String[] parts;
            User friend;
            String str = br.readLine();
            int i=0;
            while( str != null ) {
                parts = str.split( "," );
                friend = new User( parts[ 0 ], new ImageIcon(parts[ 1 ]));
                list.add( friend );
                str = br.readLine();
                System.out.println(list.get(i).getName() + " är tillagd som vän med bilden: " + friend.getImage());
                i++;
                
            }
            br.close();
        } catch( IOException e ) {
            System.out.println( "readPersons: " + e );
        }
        return list;
    }
	
	public void addFriend(User newFriend, String filename) {
		PrintWriter writer;
		try {
			writer = new PrintWriter(filename, "UTF-8");
			writer.println(newFriend.getName() + "," + newFriend.getImage());
			writer.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	

	public void disconnect() {

	}

	public void sendMessage(Message message) {
		try {
			out.writeObject(massage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createUser(String name, ImageIcon image) {
		this.user = new User(name, image);
		addFriend(this.user,  "files/friends.txt" );
		readFriends("files/friends.txt");
	}

	public static void main(String[] args) {

		User user = new User("anttttt", null);
		User user2 = new User("KJHDS", null);
		Server ser = new Server();
		Client client = new Client(JOptionPane.showInputDialog("Skriv namn"), null);
		Client client2 = new Client(JOptionPane.showInputDialog("Skriv namn"), null);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientUI frame = new ClientUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
