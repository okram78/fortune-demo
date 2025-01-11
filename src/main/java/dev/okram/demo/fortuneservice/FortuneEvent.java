package dev.okram.demo.fortuneservice;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

public class FortuneEvent extends ApplicationEvent {

    private Long id;
    private String fortune;

    public FortuneEvent(Object source, Long id, String fortune) {
        super(source);
        this.id = id;
        this.fortune = fortune;
    }

    public Long getId() {
        return id;
    }

    public String getFortune() {
        return fortune;
    }
}
