package engine.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import engine.bus.CommunicationNode;
import engine.bus.MessageBus;
import graphics.core.OpenGLGraphics;
import engine.interfaces.RenderAble;
import engine.interfaces.TimeDependentUpdate;

public abstract class EntityHandler implements TimeDependentUpdate, RenderAble,CommunicationNode {

	public Map<String, ArrayList<Entity>> allEntities = new HashMap<String, ArrayList<Entity>>();
	public List<Entity> toRemove = new ArrayList<>();
	
	protected MessageBus bus;
	
	public EntityHandler(MessageBus bus) {
		this.bus=bus;
		bus.add(this);
	}
	

	public void addEntity(Entity e) {
		addEntity(e.type, e);
	}

	public void addEntity(String type, Entity e) {
		if (!allEntities.containsKey(type)) {
			allEntities.put(type, new ArrayList<Entity>());
		}
		allEntities.get(type).add(e);
	}

	@Override
	public void update(float delta) {
		for (String key : allEntities.keySet()) {
			allEntities.get(key).forEach(e -> {
				e.update(delta);
				if (!e.isAlive) {
					toRemove.add(e);
				}
			});
			allEntities.get(key).removeAll(toRemove);
			toRemove.clear();
		}
	}

	@Override
	public void draw(OpenGLGraphics g) {
		for (String key : allEntities.keySet()) {
			allEntities.get(key).forEach(e -> e.draw(g));
		}
	}

}
