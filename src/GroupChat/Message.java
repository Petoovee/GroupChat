package GroupChat;

import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * 
 * @author Antoine, Anton, Petar, Maida, Malin, Sara Klass
 */

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;
	private String textMsg;
	private ImageIcon image;
	private User sender;
	private ArrayList<User> receivers = new ArrayList<User>();
	private String timeSent;
	private String timeDelivered;
	private Message message;

	public Message(String textMsg, ImageIcon image, User sender, ArrayList<User> receivers) {

		this.textMsg = textMsg;
		this.image = image;
		this.sender = sender;
		this.receivers = receivers;
	}

	public String getTextMsg() {
		return textMsg;
	}

	public void setTextMsg(String textMsg) {
		this.textMsg = textMsg;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public ArrayList<User> getReceivers() {
		return receivers;
	}

	public void setReceivers(ArrayList<User> receivers) {
		this.receivers = receivers;
	}

	public String getTimeSent() {
		return timeSent;
	}

	public void setTimeSent(String timeSent) {
		this.timeSent = timeSent;
	}

	public String getTimeDelivered() {
		return timeDelivered;
	}

	public void setTimeDelivered(String timeDelivered) {
		this.timeDelivered = timeDelivered;
	}

	public Message getMessage() {
		return message;
	}

}