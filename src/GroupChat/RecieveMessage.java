package GroupChat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class RecieveMessage extends Thread {
	Socket socket;
	ObjectInputStream in;

	public RecieveMessage(Socket socket) {
		this.socket = socket;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		Message mess;
		Object obj = null;
		while (!Thread.interrupted()) {
			try {
				obj = in.readObject();
				if (obj != null) {
					mess = (Message) obj;
					System.out.println("Meddelande mottaget från servern ytterligare är: " + " " + mess.toString());
					System.out.println("lol");
				}

				Thread.sleep(200);
			} catch (InterruptedException | ClassNotFoundException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
