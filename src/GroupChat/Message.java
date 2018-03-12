package GroupChat;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class Message implements Serializable {
	private String textMsg;
	private ImageIcon image;
	private User sender;
	private User recievers;
	private String timeSent;
	private String timeDelivered;

	public Message(String message, ImageIcon image, User sender, User recievers) {
		
		this.textMsg = message;
		this.image = image;
		this.sender = sender;
		this.recievers = recievers;
	}

	// public Message getMessage() {
	// return new Message(textMsg, image, sender, recievers);
	// }

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

	public User getRecievers() {
		return recievers;
	}

	public void setRecievers(User recievers) {
		this.recievers = recievers;
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

	public String toString() {
		return this.textMsg + " " + this.sender.getName() + " " + this.recievers.getName();
	}

}
