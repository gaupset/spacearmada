package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.event.Event;
import no.ntnu.tdt4240.project.event.EventManager;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;

public class EventSystem extends IteratingSystem {
    public EventManager events;
    private HealthComponent component;

    public EventSystem(int priority) {
        super(Family.all(
            HealthComponent.class
        ).get(), priority);
        events = new EventManager();
    }

    @Override
    protected void processEntity(Entity e, float dt) {
        component = Mapper.health.get(e);
        process();
    }

    private void process() {
        if (component.health <= 0) {
            events.alert(Event.LOSE);
        }
    }
}
