package me.joeleoli.nucleus.listener;

import me.joeleoli.nucleus.Nucleus;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldListener implements Listener {

	@EventHandler
	public void onWorldLoad(WorldLoadEvent event) {
		event.getWorld().setTime(12_000L);
		event.getWorld().setThundering(false);

		int removed = 0;

		for (Entity entity : event.getWorld().getEntities()) {
			removed++;
			entity.remove();
		}

		Nucleus.getInstance().getLogger()
		       .info("Cleared " + removed + " entities from " + event.getWorld().getName() + " on world load.");
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		event.setCancelled(true);
	}

	@EventHandler
	public void onExplosion(ExplosionPrimeEvent event) {
		event.setCancelled(true);
	}

}
