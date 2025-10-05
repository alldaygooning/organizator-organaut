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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import pencil_utensil.organaut.organization.coordinates.Coordinates;
import pencil_utensil.organaut.organization.coordinates.CoordinatesService;

@RestController
@RequestMapping("/api/organizations/coordinates")
public class CoordinatesController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoordinatesController.class);

	private final CoordinatesService coordinatesService;

	CoordinatesController(CoordinatesService coordinatesService) {
		this.coordinatesService = coordinatesService;
	}

	@GetMapping
	public ResponseEntity<List<Coordinates>> getAll() { return ResponseEntity.ok(coordinatesService.getAll()); }

	@PostMapping("/create")
	public ResponseEntity<Coordinates> create(@Valid @RequestBody CreateRequest req) {
		Coordinates coordinates = coordinatesService.create(req.x, req.y);
		LOGGER.info("{} created", coordinates.toString());
		return ResponseEntity.status(HttpStatus.CREATED).body(coordinates);
	}

	static class CreateRequest {
		@NotNull(message = "coordinates.x.notnull")
		@Max(value = Coordinates.X_MAX, message = "coordinates.x.size")
		public Integer x;
		@NotNull(message = "coordinates.y.notnull")
		public Integer y;
	}
}
