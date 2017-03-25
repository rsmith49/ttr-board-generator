package com.csc570.rsmith.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.csc570.rsmith.TicketToRide;
import com.csc570.rsmith.graphics.GraphicsUtils;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Ticket to Ride: Space Edition";
		config.height = GraphicsUtils.WINDOW_HEIGHT;
		config.width = GraphicsUtils.WINDOW_WIDTH;

		new LwjglApplication(new TicketToRide(), config);
	}
}
