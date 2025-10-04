package pencil_utensil.organaut.organization.coordinates;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Coordinates {

	public static final int X_MAX = 442;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private Integer x; // Максимальное значение поля: 442, Поле не может быть null
	private int y;

	public Coordinates() {}

	public Coordinates(Integer x, int y) {
		Objects.requireNonNull(x, "x should not be null");
		if (x > X_MAX) {
			throw new IllegalArgumentException(String.format("x should be <= %s", X_MAX));
		}
		this.x = x;
		this.y = y;
	}

	public int getId() { return id; }

	public Integer getX() { return x; }

	public int getY() { return y; }
}
