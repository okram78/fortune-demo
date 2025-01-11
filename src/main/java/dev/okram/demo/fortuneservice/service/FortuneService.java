package dev.okram.demo.fortuneservice.service;

import dev.okram.demo.fortuneservice.FortuneEvent;
import dev.okram.demo.fortuneservice.FortuneResponse;
import org.springframework.context.event.EventListener;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class FortuneService {

    private static final String FORTUNE_EVENT_NAME = "fortune";
    // This is for demonstration purposes only. In production, you should use a
    // database or similar persistence mechanism to store the FortuneResponse objects and
    // create the Flux dynamically from that source.
    private final Sinks.Many<ServerSentEvent<FortuneResponse>> sink = Sinks.many().replay().all();

    private final AtomicLong sseId = new AtomicLong(5000L);

    @EventListener
    public void onFortuneEvent(FortuneEvent fortuneEvent){
        FortuneResponse response = new FortuneResponse(fortuneEvent.getId(), fortuneEvent.getFortune());
        ServerSentEvent<FortuneResponse> sse = ServerSentEvent.<FortuneResponse>builder()
                .id( String.valueOf(sseId.incrementAndGet()))
                .event(FORTUNE_EVENT_NAME)
                .data(response)
                .build();
        sink.tryEmitNext(sse);
    }

    public Flux<ServerSentEvent<FortuneResponse>> getFortuneStream(Long id) {
        if ( id == null ) {
            return sink.asFlux();
        } else {
            return sink.asFlux().filter(response -> {
                assert response.data() != null;
                return response.data().id().compareTo(id) > 0;
            });
        }
    }
}
