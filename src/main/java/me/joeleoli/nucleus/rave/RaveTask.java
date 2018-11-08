package me.joeleoli.nucleus.rave;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import me.joeleoli.nucleus.util.Style;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class RaveTask extends BukkitRunnable {

	private Rave rave;
	private int ticks = 0;
	private String title = "IT'S A RAVE";
	private List<String> lines = new ArrayList<>();
	private boolean titleSwitch = false;

	public RaveTask(Rave rave) {
		this.rave = rave;
	}

	@Override
	public void run() {
		this.ticks = this.ticks + 2;

		if (this.ticks % 4 == 0) {
			this.rave.broadcastSound();
		}

		if (this.ticks < 60) {
			if (this.ticks % 10 == 0) {
				this.title = Style.MAGIC + ChatColor.getLastColors(this.title) + ChatColor.stripColor(this.title);
			} else {
				this.title = this.rave.getRandomChatColor() + Style.strip(this.title);
			}
		} else {
			if (!this.titleSwitch) {
				this.title = Style.strip(this.title);
				this.titleSwitch = true;
			}

			final StringBuilder titleBuilder = new StringBuilder();
			final char[] chars = Style.strip(this.title).toCharArray();

			for (char c : chars) {
				if (c != ' ') {
					titleBuilder.append(this.rave.getRandomChatColor());
				}

				titleBuilder.append(c);
			}

			this.title = titleBuilder.toString();
		}

		final List<String> lines = new ArrayList<>();

		lines.add(0, Style.BORDER_LINE_SCOREBOARD);
		lines.add(Style.BORDER_LINE_SCOREBOARD);

		this.lines = lines;

		if (this.ticks >= 600) {
			this.ticks = 0;
			this.titleSwitch = false;
		}
	}

}
