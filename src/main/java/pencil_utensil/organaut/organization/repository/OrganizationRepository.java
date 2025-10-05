package pencil_utensil.organaut.organization.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import pencil_utensil.organaut.organization.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {

	@Query(value = "SELECT * FROM deleteByType(:ownerId, CAST(:type AS organization_type))", nativeQuery = true)
	List<Long> deleteByType(@Param("ownerId") Integer ownerId, @Param("type") String type);

	@Query(value = "SELECT getTotalRating()", nativeQuery = true)
	Long getTotalRating();

	@Query(value = "SELECT * FROM getTopOrganizationIdsByTurnover()", nativeQuery = true)
	List<Long> getTopOrganizationIdsByTurnover();

	@Query(value = "SELECT getAverageEmployeeCount()", nativeQuery = true)
	Double getAverageEmployeeCount();

	@Query(value = "SELECT * FROM groupByAddress()", nativeQuery = true)
	List<AddressCountProjection> groupByAddress();

	public record AddressCountProjection(
			Integer addressId,
			Long organizationCount) {
	}
}
