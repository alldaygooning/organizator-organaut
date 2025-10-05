package pencil_utensil.organaut.organization;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pencil_utensil.organaut.exception.OrganizationNotFoundException;
import pencil_utensil.organaut.exception.OwnershipException;
import pencil_utensil.organaut.network.controller.OrganizationController;
import pencil_utensil.organaut.network.controller.OrganizationController.OrganizationResponse;
import pencil_utensil.organaut.network.sse.BroadcastEvent;
import pencil_utensil.organaut.network.sse.SseService;
import pencil_utensil.organaut.organization.address.Address;
import pencil_utensil.organaut.organization.coordinates.Coordinates;
import pencil_utensil.organaut.organization.repository.OrganizationRepository;
import pencil_utensil.organaut.organization.repository.UserOrganization;
import pencil_utensil.organaut.organization.repository.UserOrganizationId;
import pencil_utensil.organaut.organization.repository.UserOrganizationRepository;

@Service
public class OrganizationService {

	private final SseService sseService;

	private final UserOrganizationRepository userOrganizationRepository;

	private final OrganizationRepository organizationRepository;

	OrganizationService(OrganizationRepository organizationRepository,
			UserOrganizationRepository userOrganizationRepository, SseService sseService) {
		this.organizationRepository = organizationRepository;
		this.userOrganizationRepository = userOrganizationRepository;
		this.sseService = sseService;
	}

	@Transactional(readOnly = true)
	public List<Organization> getAll() { return organizationRepository.findAll(); }

	@Transactional(readOnly = true)
	public Organization get(Long id) {
		return organizationRepository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public boolean exists(Long id) {
		return organizationRepository.existsById(id);
	}

	@Transactional
	public Organization update(Integer userId, Long organizationId, String name, Coordinates coordinates,
			Address address, Integer annualTurnover,
			Integer employeesCount, Integer rating, String fullName, OrganizationType type, Address postalAddress) {
		if (!isOwner(userId, organizationId)) {
			throw new OwnershipException();
		}

		Organization organization = organizationRepository.findById(organizationId)
				.orElseThrow(OrganizationNotFoundException::new);

		if (name != null) {
			if (name.trim().isEmpty()) {
				throw new IllegalArgumentException("organization.name.notblank");
			}
			organization.setName(name);
		}

		if (coordinates != null) {
			organization.setCoordinates(coordinates);
		}

		if (address != null) {
			organization.setAddress(address);
		}

		if (employeesCount != null) {
			if (employeesCount <= 0) {
				throw new IllegalArgumentException("organization.employees-count.size");
			}
			organization.setEmployeesCount(employeesCount);
		}

		if (annualTurnover != null) {
			if (annualTurnover <= 0) {
				throw new IllegalArgumentException("organization.annual-turnover.size");
			}
			organization.setAnnualTurnover(annualTurnover);
		}

		if (rating != null) {
			if (rating <= 0) {
				throw new IllegalArgumentException("organization.rating.size");
			}
			organization.setRating(rating);
		}

		if (fullName != null) {
			organization.setFullName(fullName);
		}

		if (type != null) {
			organization.setType(type);
		}

		if (postalAddress != null) {
			organization.setPostalAddress(postalAddress);
		}
		return organization;
	}

	public OrganizationController.OrganizationResponse getDto(Organization organization) {
		Integer ownerId = findOwnerId(organization.getId());
		return new OrganizationResponse(ownerId, organization);
	}

	@Transactional
	public Organization create(Integer userId, String name, Coordinates coordinates, Address address,
			Integer annualTurnover,
			Integer employeesCount, int rating, String fullName, OrganizationType type, Address postalAddress) {
		Organization organization = new Organization(name, coordinates, address, annualTurnover, employeesCount, rating,
				fullName, type, postalAddress);
		Organization saved = organizationRepository.save(organization);
		UserOrganizationId ownershipId = new UserOrganizationId(userId, saved.getId());
		UserOrganization uo = new UserOrganization(ownershipId);
		userOrganizationRepository.save(uo);

		sseService.broadcastEvent(BroadcastEvent.ORGANIZATION_CREATED, saved);
		return saved;
	}

	@Transactional
	public void delete(Integer userId, Long organizationId) {
		if (!exists(organizationId)) {
			throw new OrganizationNotFoundException();
		}
		if (!isOwner(userId, organizationId)) {
			throw new OwnershipException();
		}
		organizationRepository.deleteById(organizationId);
	}

	@Transactional(readOnly = true)
	public boolean isOwner(Integer userId, Long organizationId) {
		return userOrganizationRepository.existsById_UserIdAndId_OrganizationId(
				Objects.requireNonNull(userId, "User ID should not be null"),
				Objects.requireNonNull(organizationId, "Organization ID should not be null"));
	}

	@Transactional(readOnly = true)
	public Integer findOwnerId(Long organizationId) {
		return userOrganizationRepository.findOwnerId(organizationId);
	}
}
