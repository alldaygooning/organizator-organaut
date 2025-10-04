package pencil_utensil.organaut.organization.address;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String street; // Поле не может быть null
	private String zip; // Поле не может быть null

	public Address() {}

	public Address(String street, String zip) {
		Objects.requireNonNull(street, "street should not be null");
		Objects.requireNonNull(zip, "zip should not be null");
		this.street = street;
		this.zip = zip;
	}

	public int getId() { return id; }

	public String getStreet() { return street; }

	public String getZip() { return zip; }
}
