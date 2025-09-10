package com.frida.chinese.jchess.game;


public interface IGameCallback {

    // post calls may not run on UI thread
    void postPlaySound(int soundIndex);

    void postShowMessage(String message);

    void postShowMessage(int messageId);

    void postStartThink();

    void postEndThink();
}
