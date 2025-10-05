package pencil_utensil.organaut.organization.repository;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_organizations")
public class UserOrganization {
	@EmbeddedId
	private UserOrganizationId id;

	public UserOrganization() {}

	public UserOrganization(UserOrganizationId id) {
		this.id = id;
	}

	public UserOrganizationId getId() { return id; }

	public void setId(UserOrganizationId id) { this.id = id; }
}