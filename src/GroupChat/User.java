package GroupChat;

import java.io.Serializable;

import javax.swing.ImageIcon;

public class User implements Serializable {
	private String name;
	private ImageIcon image;
	private User user;

	public User(String name, ImageIcon image) {
		this.name = name;
		this.image = image;
	}

	public int hashCode() {
		return name.hashCode();
	}

	public boolean equals(Object obj) {
		return name.equals(obj);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ImageIcon getImage() {
		return image;
	}

	public void setImage(ImageIcon image) {
		this.image = image;
	}

	public User getUser() {
		return user;
	}
}
