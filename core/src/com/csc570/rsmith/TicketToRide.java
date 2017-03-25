package com.csc570.rsmith;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.csc570.rsmith.boardgenerator.BoardGenerator;
import com.csc570.rsmith.boardgenerator.DefaultBoardGenerator;
import com.csc570.rsmith.graphics.TTRMainScreen;
import com.csc570.rsmith.graphics.TTRStartScreen;
import com.csc570.rsmith.mechanics.board.GameBoard;
import com.csc570.rsmith.graphics.GraphicsUtils;
import com.csc570.rsmith.graphics.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TicketToRide extends Game {

	TTRStartScreen startScreen;
	TTRMainScreen mainScreen;

	private boolean inStart = true;
	
	@Override
	public void create () {
		startScreen = new TTRStartScreen();
		setScreen(startScreen);
	}

	@Override
	public void render() {
		super.render();

		if (inStart && startScreen.isDoneChoosing()) {
			mainScreen = new TTRMainScreen(
					startScreen.getNumHumanPlayers(),
					startScreen.getNumCompPlayers(),
					startScreen.getColorsSelected(),
					startScreen.useGE());

			setScreen(mainScreen);
			inStart = false;
		}
	}
	
	@Override
	public void dispose () {
		GraphicsUtils.disposeAll();
		if (startScreen != null) {
			startScreen.dispose();
		}
		if (mainScreen != null) {
			mainScreen.dispose();
		}
	}
}
