package pencil_utensil.organaut.network.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import pencil_utensil.organaut.organization.address.Address;
import pencil_utensil.organaut.organization.address.AddressService;

@RestController
@RequestMapping("/api/organizations/addresses")
public class AddressController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AddressController.class);

	private final AddressService addressService;

	AddressController(AddressService addressService) {
		this.addressService = addressService;
	}

	@GetMapping
	public ResponseEntity<List<Address>> getAll() { return ResponseEntity.ok(addressService.getAll()); }

	@PostMapping("/create")
	public ResponseEntity<Address> create(@Valid @RequestBody CreateRequest req) {
		Address address = addressService.create(req.street, req.zip);
		LOGGER.info("{} created", address.toString());
		return ResponseEntity.status(HttpStatus.CREATED).body(address);
	}
	
	static class CreateRequest {
		@NotNull(message = "address.street.notnull")
		public String street;
		@NotNull(message = "address.zip.notnull")
		public String zip;
	}
}
