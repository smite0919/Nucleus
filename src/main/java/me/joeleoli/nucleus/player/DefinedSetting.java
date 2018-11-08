package me.joeleoli.nucleus.player;

public interface DefinedSetting {

	String name();

	enum GlobalPlayerSetting implements DefinedSetting {

		RECEIVE_PRIVATE_MESSAGES,
		RECEIVE_GLOBAL_MESSAGES,
		PLAY_MESSAGE_SOUNDS

	}

}
