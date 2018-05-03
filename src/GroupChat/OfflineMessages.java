package GroupChat;

import java.util.ArrayList;
import java.util.HashMap;

public class OfflineMessages {

	private HashMap<String, ArrayList<Message>> offlineMessages = new HashMap<String, ArrayList<Message>>();

	public synchronized void put(String userName, Message message) {
		// Om receiver inte är online kontrolleras det ifall receivern redan har
		// offlinemeddelanden och lägger till
		if (offlineMessages.containsKey(userName)) {
			offlineMessages.get(userName).add(message);
		} else {
			// Om receiver inte har offline meddelande array skapas en och lägger till
			// meddelande
			ArrayList<Message> newList = new ArrayList<Message>();
			newList.add(message);
			offlineMessages.put(userName, newList);
		}
	}

	public synchronized void remove(String userName) {
		offlineMessages.remove(userName);
	}

	public synchronized ArrayList<Message> get(String userName) {
		return offlineMessages.get(userName);
	}

	public synchronized boolean contains(String userName) {
		return offlineMessages.containsKey(userName);
	}

}