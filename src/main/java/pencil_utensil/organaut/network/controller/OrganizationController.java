package pencil_utensil.organaut.network.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pencil_utensil.organaut.exception.OrganizationNotFoundException;
import pencil_utensil.organaut.exception.OwnershipException;
import pencil_utensil.organaut.network.security.filter.JwtFilter;
import pencil_utensil.organaut.network.sse.SseService;
import pencil_utensil.organaut.organization.Organization;
import pencil_utensil.organaut.organization.OrganizationService;
import pencil_utensil.organaut.organization.OrganizationType;
import pencil_utensil.organaut.organization.address.Address;
import pencil_utensil.organaut.organization.address.AddressService;
import pencil_utensil.organaut.organization.coordinates.Coordinates;
import pencil_utensil.organaut.organization.coordinates.CoordinatesService;
import pencil_utensil.organaut.organization.repository.OrganizationRepository.AddressCountProjection;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

	private final SseService sseService;

	private final AddressService addressService;
	private final CoordinatesService coordinatesService;
	private final OrganizationService organizationService;

	private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationController.class);

	OrganizationController(OrganizationService organizationService, CoordinatesService coordinatesService,
			AddressService addressService, SseService sseService) {
		this.organizationService = organizationService;
		this.coordinatesService = coordinatesService;
		this.addressService = addressService;
		this.sseService = sseService;
	}

	@GetMapping
	public ResponseEntity<List<OrganizationResponse>> all() {
		List<Organization> organizations = organizationService.getAll();
		return ResponseEntity.ok(organizations.stream()
				.map(org -> {
					return organizationService.getDto(org);
				})
				.collect(Collectors.toList()));
	}

	@GetMapping("/{id}")
	public ResponseEntity<Organization> getById(@PathVariable Long id) {
		return ResponseEntity.ok(organizationService.get(id));
	}

	public static class OrganizationResponse {
		public OrganizationResponse(Integer userId, Organization organization) {
			this.ownerId = userId;
			this.organization = organization;
		}

		public Organization organization;
		public Integer ownerId;
	}



	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		UserCredentials user = getUserCredentials();
		Organization organization;

		try {
			organization = organizationService.get(id);
			organizationService.delete(user.id, id);
		} catch (OwnershipException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (OrganizationNotFoundException e) {
			return ResponseEntity.badRequest().build();
		}

		LOGGER.info("{} deleted {}", user.name, organization.getFullName());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PatchMapping("/update/{id}")
	public ResponseEntity<Organization> update(@PathVariable Long id, @Valid @RequestBody UpdateRequest req) {
		UserCredentials user = getUserCredentials();
		String old;
		Organization current;

		Coordinates coordinates = null;
		Address address = null;
		Address postalAddress = null;
		if (req.coordinatesId != null) {
			coordinates = coordinatesService.get(req.coordinatesId);
		}
		if (req.addressId != null) {
			address = addressService.get(req.addressId);
		}
		if (req.postalAddressId != null) {
			postalAddress = addressService.get(req.postalAddressId);
		}

		try {
			old = organizationService.get(id).toString();
			current =
					organizationService.update(user.id, id, req.name, coordinates, address, req.annualTurnover,
							req.employeesCount, req.rating, req.fullName, req.type, postalAddress);
		} catch (OwnershipException e) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		} catch (OrganizationNotFoundException | IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}

		LOGGER.info("{} updated \n{} ->\n{}", user.name, old, current.toString());
		return ResponseEntity.ok(current);
	}

	public static class UpdateRequest {
		@Size(min = 1, message = "organization.name.size")
		public String name;

		@Size(min = 1, message = "organization.full-name.size")
		public String fullName;

		public Integer coordinatesId;
		public Integer addressId;
		public Integer postalAddressId;

		@Min(value = 1, message = "organization.annual-turnover.size")
		public Integer annualTurnover;

		@Min(value = 1, message = "organization.employees-count.size")
		public Integer employeesCount;

		@Min(value = 1, message = "organization.rating.size")
		public Integer rating;

		public OrganizationType type;
	}

	@PostMapping("/create")
	public ResponseEntity<Organization> create(@Valid @RequestBody CreateRequest req) {
		UserCredentials user = getUserCredentials();

		Coordinates coordinates = coordinatesService.get(req.coordinatesId);
		Address address = addressService.get(req.addressId);
		Address postalAddress = addressService.get(req.postalAddressId);
		if (coordinates == null || address == null || postalAddress == null) {
			/*
			 * No explanation because client should not be able to end up in this situation.
			 * Only bad actors may end up in this clause, and I do not care about their
			 * experience debugging what went wrong.
			 * 
			 * UPD: Now when I think about it. What if Address/Coordinates are deleted while
			 * this request is being handled? Repository would attempt to link non-existent
			 * object to Organization. Maybe read about locks and think about it?
			 */
			return ResponseEntity.badRequest().build();
		}
		Organization organization =
				organizationService.create(user.id, req.name, coordinates, address, req.annualTurnover,
						req.employeesCount, req.rating,
						req.fullName, req.type, postalAddress);

		LOGGER.info("{} created {}", user.name, organization.getFullName());
		return ResponseEntity.status(HttpStatus.CREATED).body(organization);
	}

	public static class CreateRequest {
		@NotBlank(message = "organization.name.notblank")
		public String name;
		@NotNull(message = "organization.full-name.notnull")
		public String fullName;

		@NotNull(message = "organization.coordinates.notnull")
		public Integer coordinatesId;
		@NotNull(message = "organization.address.notnull")
		public Integer addressId;
		@NotNull(message = "organization.postal-address.notnull")
		public Integer postalAddressId;

		@Min(value = 1, message = "organization.annual-turnover.size")
		public int annualTurnover;
		@Min(value = 1, message = "organization.employees-count.size")
		public int employeesCount;
		@Min(value = 1, message = "organization.rating.size")
		public int rating;

		public OrganizationType type;
	}

	@GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter sse() {
		LOGGER.info("New SSE connection established");
		return sseService.createEmitter();
	}

	private UserCredentials getUserCredentials() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof JwtFilter.JwtAuthentication jwt) {
			return new UserCredentials(jwt.getId(), jwt.getName());
		}
		return null;
	}

	static record UserCredentials(Integer id, String name) {
	};

	// FUNNIES FUNNIES FUNNIES...

	@DeleteMapping("/delete/type")
	public ResponseEntity<List<Long>> deleteByType(@RequestBody OrganizationType type) {
		UserCredentials user = getUserCredentials();
		return ResponseEntity.ok(organizationService.deleteByType(user.id, type));
	}

	@GetMapping("/total-rating")
	public ResponseEntity<Long> getTotalRating() { return ResponseEntity.ok(organizationService.getTotalRating()); }

	@GetMapping("/top-turnover")
	public ResponseEntity<List<Organization>> getTopByTurnover() {
		return ResponseEntity.ok(organizationService.getTopByTurnover());
	}

	@GetMapping("/employee-count")
	public ResponseEntity<Double> getAverageEmployeeCount() {
		return ResponseEntity.ok(organizationService.getAverageEmployeeCount());
	}

	@GetMapping("/group-by-address")
	public ResponseEntity<List<AddressCountProjection>> groupByAddress() {
		return ResponseEntity.ok(organizationService.grouptByAddress());
	}
}
