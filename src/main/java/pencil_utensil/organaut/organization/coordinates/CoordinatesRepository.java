package pencil_utensil.organaut.organization.coordinates;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CoordinatesRepository extends JpaRepository<Coordinates, Integer> {
	Optional<Coordinates> findByXAndY(int x, int y);
}
