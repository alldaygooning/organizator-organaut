package pencil_utensil.organaut.organization.address;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
	Optional<Address> findByStreetAndZip(String street, String zip);
}
