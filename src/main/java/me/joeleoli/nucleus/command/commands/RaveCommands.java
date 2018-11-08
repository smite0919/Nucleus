package me.joeleoli.nucleus.command.commands;

import me.joeleoli.nucleus.Nucleus;
import me.joeleoli.nucleus.command.Command;
import me.joeleoli.nucleus.rave.Rave;
import org.bukkit.command.CommandSender;

public class RaveCommands {

	@Command(names = "nucleus rave", permissionNode = "nucleus.rave")
	public static void rave(CommandSender sender) {
		if (Nucleus.getInstance().getRave() != null) {
			Nucleus.getInstance().getRave().end();
		} else {
			Nucleus.getInstance().setRave(new Rave(Nucleus.getInstance().getChatManager().getChatFormat()));
			Nucleus.getInstance().getRave().start();
		}
	}

}
