package pencil_utensil.organaut.organization.coordinates;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CoordinatesService {

	private final CoordinatesRepository coordinatesRepository;

	CoordinatesService(CoordinatesRepository coordinatesRepository) {
		this.coordinatesRepository = coordinatesRepository;
	}

	@Transactional(readOnly = true)
	public List<Coordinates> getAll() { return coordinatesRepository.findAll(); }

	@Transactional(readOnly = true)
	public Coordinates get(int id) {
		return coordinatesRepository.findById(id).orElse(null);
	}

	@Transactional
	public Coordinates create(int x, int y) {
		return coordinatesRepository.save(new Coordinates(x, y));
	}
}
