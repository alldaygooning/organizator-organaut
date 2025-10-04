package pencil_utensil.organaut.organization;

import java.time.ZonedDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import pencil_utensil.organaut.organization.address.Address;
import pencil_utensil.organaut.organization.coordinates.Coordinates;

@Entity
public class Organization {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id; // Значение поля должно быть больше 0, Значение этого поля должно быть
						// уникальным, Значение этого поля должно генерироваться автоматически
	private String name; // Поле не может быть null, Строка не может быть пустой

	@ManyToOne
	private Coordinates coordinates; // Поле не может быть null
	private ZonedDateTime creationDate; // Поле не может быть null, Значение этого поля должно генерироваться
										// автоматически
	@ManyToOne
	private Address address; // Поле может быть null
	private Integer annualTurnover; // Поле не может быть null, Значение поля должно быть больше 0
	private Integer employeesCount; // Поле может быть null, Значение поля должно быть больше 0
	private int rating; // Значение поля должно быть больше 0
	private String fullName; // Поле не может быть null
	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.NAMED_ENUM)
	private OrganizationType type; // Поле может быть null
	@ManyToOne
	private Address postalAddress; // Поле не может быть null

	public Organization() {}

	public Organization(String name, Coordinates coordinates, Address address,
			Integer annualTurnover, Integer employeesCount, int rating, String fullName, OrganizationType type,
			Address postalAddress) {
		super();
		this.name = name;
		this.coordinates = coordinates;
		this.creationDate = ZonedDateTime.now();
		this.address = address;
		this.annualTurnover = annualTurnover;
		this.employeesCount = employeesCount;
		this.rating = rating;
		this.fullName = fullName;
		this.type = type;
		this.postalAddress = postalAddress;
	}

	public long getId() { return id; }

	public String getName() { return name; }

	public Coordinates getCoordinates() { return coordinates; }

	public ZonedDateTime getCreationDate() { return creationDate; }

	public Address getAddress() { return address; }

	public Integer getAnnualTurnover() { return annualTurnover; }

	public Integer getEmployeesCount() { return employeesCount; }

	public int getRating() { return rating; }

	public String getFullName() { return fullName; }

	public OrganizationType getType() { return type; }

	public Address getPostalAddress() { return postalAddress; }

	public void setName(String name) { this.name = name; }

	public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

	public void setAddress(Address address) { this.address = address; }

	public void setAnnualTurnover(Integer annualTurnover) { this.annualTurnover = annualTurnover; }

	public void setEmployeesCount(Integer employeesCount) { this.employeesCount = employeesCount; }

	public void setRating(int rating) { this.rating = rating; }

	public void setFullName(String fullName) { this.fullName = fullName; }

	public void setType(OrganizationType type) { this.type = type; }

	public void setPostalAddress(Address postalAddress) { this.postalAddress = postalAddress; }

	@Override
	public String toString() {
		return "Organization [id=" + id + ", name=" + name + ", coordinates=" + coordinates + ", creationDate="
				+ creationDate + ", address=" + address + ", annualTurnover=" + annualTurnover + ", employeesCount="
				+ employeesCount + ", rating=" + rating + ", fullName=" + fullName + ", type=" + type
				+ ", postalAddress=" + postalAddress + "]";
	}
}
