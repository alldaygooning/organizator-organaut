package pencil_utensil.organaut.organization.coordinates;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pencil_utensil.organaut.network.sse.BroadcastEvent;
import pencil_utensil.organaut.network.sse.SseService;

@Service
public class CoordinatesService {

	private final SseService sseService;

	private final CoordinatesRepository coordinatesRepository;

	CoordinatesService(CoordinatesRepository coordinatesRepository, SseService sseService) {
		this.coordinatesRepository = coordinatesRepository;
		this.sseService = sseService;
	}

	@Transactional(readOnly = true)
	public List<Coordinates> getAll() { return coordinatesRepository.findAll(); }

	@Transactional(readOnly = true)
	public Coordinates get(int id) {
		return coordinatesRepository.findById(id).orElse(null);
	}

	@Transactional
	public Coordinates create(int x, int y) {
		Optional<Coordinates> opt = coordinatesRepository.findByXAndY(x, y);
		if (opt.isPresent()) {
			return opt.get();
		}

		Coordinates coordinates = coordinatesRepository.save(new Coordinates(x, y));
		sseService.broadcastEvent(BroadcastEvent.COORDINATES_CREATED, coordinates);
		return coordinates;
	}
}
