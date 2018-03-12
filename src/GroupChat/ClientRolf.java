package GroupChat;

import java.util.HashMap;

import GroupChat.Server.ClientHandler;

public class ClientRolf {
	private HashMap<User, Client> clients = new HashMap<User, Client>();

	public synchronized void put(User user, Client client) {
		clients.put(user, client);
	}

	public synchronized Client get(User user) {
		return get(user);
	}

}
