package pencil_utensil.organaut.organization.repository;

import java.io.Serializable;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import pencil_utensil.organaut.organization.repository.UserOrganizationRepository.UserOrganization;
import pencil_utensil.organaut.organization.repository.UserOrganizationRepository.UserOrganizationId;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UserOrganizationId> {

	boolean existsById_UserIdAndId_OrganizationId(Integer userId, Long organizationId);

	@Embeddable
	class UserOrganizationId implements Serializable {
		private static final long serialVersionUID = 1L;

		@Column(name = "user_id")
		private Integer userId;

		@Column(name = "organization_id")
		private Long organizationId;

		public UserOrganizationId() {}

		public Integer getUserId() { return userId; }

		public void setUserId(Integer userId) { this.userId = userId; }

		public Long getOrganizationId() { return organizationId; }

		public void setOrganizationId(Long organizationId) { this.organizationId = organizationId; }
	}

	@Entity
	@Table(name = "users_organizations")
	class UserOrganization {
		@EmbeddedId
		private UserOrganizationId id;

		public UserOrganization() {}

		public UserOrganizationId getId() { return id; }
	}
}
