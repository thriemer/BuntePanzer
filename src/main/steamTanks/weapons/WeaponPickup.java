package weapons;

import org.joml.Vector2f;

import bus.Message;
import bus.MessageBus;
import collision.AABB;
import entities.Entity;
import map.MapHandler;
import texture.TextureManager;

public class WeaponPickup extends Entity {
	public static final String TYPE_NAME = "WeaponPickup";
	public Weapon pickup;

	public WeaponPickup(Vector2f center, Weapon toBePickedUp, MessageBus bus) {
		super(center, new AABB(center, 0.25f, 0.25f), TextureManager.getTexture("pickupTexture", 2, 2), bus);
		pickup = toBePickedUp;
		this.hitbox.setCenter(center);
		this.texture.setFrameID(toBePickedUp.pickupTexture);
		this.texture.setSize(0.25f, 0.25f);
		this.type = TYPE_NAME;
	}

}
