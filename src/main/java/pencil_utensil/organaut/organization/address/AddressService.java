package pencil_utensil.organaut.organization.address;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressService {

	private final AddressRepository addressRepository;

	AddressService(AddressRepository addressRepository) {
		this.addressRepository = addressRepository;
	}

	@Transactional(readOnly = true)
	public Address get(int id) {
		return addressRepository.findById(id).orElse(null);
	}

	@Transactional
	public Address create(String street, String zip) {
		Address address = new Address(street, zip);
		return addressRepository.save(address);
	}
}
