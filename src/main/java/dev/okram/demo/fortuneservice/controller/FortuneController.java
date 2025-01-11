package dev.okram.demo.fortuneservice.controller;

import dev.okram.demo.fortuneservice.FortuneResponse;
import dev.okram.demo.fortuneservice.service.FortuneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


@RestController
public class FortuneController {

    private final FortuneService fortuneService;

    @Autowired
    public FortuneController(FortuneService fortuneService) {
        this.fortuneService = fortuneService;
    }

    @GetMapping(value ="/fortune", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<FortuneResponse>> getFortune() {
        return fortuneService.getFortuneStream(null);
    }

    @GetMapping(value ="/fortune/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<FortuneResponse>> getFortuneFromId(@PathVariable Long id) {
        return fortuneService.getFortuneStream(id);
    }

}
