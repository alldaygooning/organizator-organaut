package pencil_utensil.organaut.network.sse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SseService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SseService.class);
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
	private final ObjectMapper om;

	public SseService(ObjectMapper objectMapper) {
		this.om = objectMapper;
	}

	public SseEmitter createEmitter() {
		SseEmitter emitter = new SseEmitter(3600000L);
		emitters.add(emitter);

		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError((e) -> emitters.remove(emitter));

		try {
			Map<String, Object> connectionEvent = new HashMap<>();
			connectionEvent.put("message", "Connected to Organization SSE");
			connectionEvent.put("timestamp", System.currentTimeMillis());

			emitter.send(SseEmitter.event()
					.name("CONNECTED")
					.data(om.writeValueAsString(connectionEvent))
					.id(String.valueOf(System.currentTimeMillis())));
		} catch (Exception e) {
			LOGGER.error("Failed to send initial connection event: {}", e.getMessage());
		}

		return emitter;
	}

	public void broadcastEvent(BroadcastEvent event, Object data) {
		LOGGER.info("Broadcasting event: {} with data: {}", event, data);
		LOGGER.info("Active emitters: {}", emitters.size());

		List<SseEmitter> deadEmitters = new CopyOnWriteArrayList<>();

		emitters.forEach(emitter -> {
			try {
				emitter.send(SseEmitter.event()
						.name(event.name())
						.data(om.writeValueAsString(data))
						.id(String.valueOf(System.currentTimeMillis())));
			} catch (Exception e) {
				LOGGER.error("Failed to send event to emitter: {}", e.getMessage());
				deadEmitters.add(emitter);
			}
		});

		emitters.removeAll(deadEmitters);
	}
}