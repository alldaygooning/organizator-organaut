package pencil_utensil.organaut.network.controller;

import java.util.List;

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

	private final AddressService addressService;

	AddressController(AddressService addressService) {
		this.addressService = addressService;
	}

	@GetMapping
	public ResponseEntity<List<Address>> getAll() { return ResponseEntity.ok(addressService.getAll()); }

	@PostMapping("/create")
	public ResponseEntity<Address> create(@Valid @RequestBody CreateRequest req) {
		return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(req.street, req.zip));
	}
	
	static class CreateRequest {
		@NotNull(message = "address.street.notnull")
		public String street;
		@NotNull(message = "address.zip.notnull")
		public String zip;
	}
}
