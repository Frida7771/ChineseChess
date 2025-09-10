package com.frida.chinese.jchess.game;


public interface IGameView {

    void postRepaint();

    void drawPiece(int pc, int xx, int yy);

    void drawSelected(int xx, int yy);
}
