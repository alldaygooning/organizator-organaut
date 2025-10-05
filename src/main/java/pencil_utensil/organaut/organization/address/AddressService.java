package pencil_utensil.organaut.organization.address;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pencil_utensil.organaut.network.sse.BroadcastEvent;
import pencil_utensil.organaut.network.sse.SseService;

@Service
public class AddressService {

	private final SseService sseService;

	private final AddressRepository addressRepository;

	AddressService(AddressRepository addressRepository, SseService sseService) {
		this.addressRepository = addressRepository;
		this.sseService = sseService;
	}

	@Transactional(readOnly = true)
	public List<Address> getAll() { return addressRepository.findAll(); }

	@Transactional(readOnly = true)
	public Address get(int id) {
		return addressRepository.findById(id).orElse(null);
	}

	@Transactional
	public Address create(String street, String zip) {
		Optional<Address> opt = addressRepository.findByStreetAndZip(street, zip);
		if (opt.isPresent()) {
			return opt.get();
		}

		Address address = addressRepository.save(new Address(street, zip));
		sseService.broadcastEvent(BroadcastEvent.ADDRESS_CREATED, address);
		return address;
	}
}
