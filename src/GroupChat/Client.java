package GroupChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import javax.swing.ImageIcon;

public class Client implements Serializable {
	private Thread thread;
	private Thread threadMess;
	private int port = 4462;
	private String IP;
	private User user;
	private Message massage;
	private LinkedList<User> contactList = new LinkedList<User>();
	// private ObjectInputStream in;
	private ObjectOutputStream out;

	public Client(String username) {
		createUser(username, null);
		massage = new Message("hej meddelande från clienten", null, user, user);
		thread=new Connect(this, user);
		thread.start();
		


	}

	public Client getClient() {
		return this;
	}

	public void startRecieve(Socket socket) {
		threadMess = new RecieveMessage(socket);
		threadMess.start();
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
	}

	public static void main(String[] args) {
		Server ser = new Server();
		Client client = new Client("Antoine");
		Client clinet2 = new Client("Anton");

	}

}
