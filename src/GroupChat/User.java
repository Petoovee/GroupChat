package GroupChat;

import java.io.Serializable;
import javax.swing.ImageIcon;

/**
 * 
 * @author Maida, Malin, Antoine, Anton, Petar, Sara
 *
 *         User-klass som representerar en anv�ndare i programmet, och
 *         identifieras via namn + bild. Implementerar Serializable f�r att
 *         kunna skickas i objektstr�mmar.
 */
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public String getImagePath() {
		return image.toString();
	}
}