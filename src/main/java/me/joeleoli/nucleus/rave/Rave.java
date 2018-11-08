package me.joeleoli.nucleus.rave;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.chat.ChatFormat;
import me.joeleoli.nucleus.util.NumberUtil;
import me.joeleoli.nucleus.util.TaskUtil;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

@Getter
@RequiredArgsConstructor
public class Rave {

	private final ChatFormat previousChatFormat;
	private RaveTask raveTask;

	public void start() {
		this.raveTask = new RaveTask(this);
		this.raveTask.runTaskTimerAsynchronously(Nucleus.getInstance(), 0L, 2L);

		Nucleus.getInstance().getChatManager().setChatFormat(new RaveChatFormat());
	}

	public void end() {
		this.raveTask.cancel();

		Nucleus.getInstance().getChatManager().setChatFormat(this.previousChatFormat);
		Nucleus.getInstance().setRave(null);
	}

	public String getRandomChatColor() {
		return ChatColor.values()[NumberUtil.getRandomRange(0, 21)].toString();
	}

	public Sound getRandomSound() {
		return Sound.values()[NumberUtil.getRandomRange(31, 37)];
	}

	public void broadcastSound() {
		final Sound sound = this.getRandomSound();
		final float pitch = NumberUtil.getRandomRange();

		TaskUtil.run(() -> {
			for (Player player : Nucleus.getInstance().getServer().getOnlinePlayers()) {
				player.playSound(player.getLocation(), sound, 1.0F, pitch);
			}
		});
	}

}
