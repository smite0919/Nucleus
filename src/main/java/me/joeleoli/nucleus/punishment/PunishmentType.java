package me.joeleoli.nucleus.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.joeleoli.nucleus.util.Style;

@AllArgsConstructor
public enum PunishmentType {

	MUTE(
			"temporarily muted",
			"unmuted",
			Style.RED + "You are currently muted for %DURATION%.",
			null
	),
	TEMPBAN(
			"temporarily banned",
			"unbanned",
			"\n" + Style.RED +
			"Your account has been temporarily suspended from " + Style.SERVER_NAME +
			".\nExpires in %EXPIRATION%.\n\n" +
			Punishment.APPEAL_FOOTER,
			"\n" + Style.RED +
			"Your account has been temporarily suspended from " + Style.SERVER_NAME +
			" for a punishment related to %PLAYER%.\nExpires in %EXPIRATION%.\n\n" +
			Punishment.APPEAL_FOOTER
	),
	BAN(
			"permanently banned",
			"unbanned",
			"\n" + Style.RED + "Your account has been suspended from " + Style.SERVER_NAME + ".\n\n" +
			Punishment.APPEAL_FOOTER,
			"\n" + Style.RED +
			"Your account has been suspended from " + Style.SERVER_NAME + " for a punishment related to %PLAYER%.\n\n" +
			Punishment.APPEAL_FOOTER
	);

	@Getter private String context;
	@Getter private String undoContext;
	@Getter private String message;
	@Getter private String sharedMessage;

}
