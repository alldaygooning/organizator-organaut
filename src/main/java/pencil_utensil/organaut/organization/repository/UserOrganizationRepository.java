package pencil_utensil.organaut.organization.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserOrganizationRepository extends JpaRepository<UserOrganization, UserOrganizationId> {

	boolean existsById_UserIdAndId_OrganizationId(Integer userId, Long organizationId);

	@Query(value = "select uo.id.userId from UserOrganization uo where uo.id.organizationId = :organizationId")
	Integer findOwnerId(@Param("organizationId") Long organizationId);
}
