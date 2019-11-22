package go.view.screen.impl;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import go.view.panel.BoardPanel;
import go.view.panel.OutputPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class GameScreen extends GoScreenImpl {
	
	private Container boardPanel;
	private OutputPanel outputPanel;
	
	private JToolBar commandPanel;
	private JButton undoButton;
	private JButton passButton;
	
	/**
	 * Constructor
	 * Displays the GoBoard to play on and notifies GoView
	 * of any user input events from the User
	 */
	public GameScreen() {
		super();
		boardPanel = new BoardPanel();
		outputPanel = new OutputPanel();

		commandPanel = new JToolBar();
		commandPanel.setFloatable(false);
		commandPanel.setRollover(true);
		commandPanel.setLayout(new BorderLayout());
        undoButton = new JButton("Undo");
		passButton = new JButton("Pass");
		undoButton.setActionCommand("UNDO");
		passButton.setActionCommand("PASS");
		undoButton.addActionListener(outputPanel);
		passButton.addActionListener(outputPanel);
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameScreen.this.notifyObserversOfActionEvent(e);
			}
		});
		passButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				GameScreen.this.notifyObserversOfActionEvent(e);
			}
		});
		commandPanel.add(undoButton, BorderLayout.NORTH);
		commandPanel.add(passButton, BorderLayout.CENTER);

		JPanel eastPanel = new JPanel();
		// temporarily make eastPanel larger for debug purposes
		eastPanel.setPreferredSize(ConfigScreen.CenterDim());
		eastPanel.setLayout(new BorderLayout());
		eastPanel.add(commandPanel, BorderLayout.NORTH);
		eastPanel.add(outputPanel, BorderLayout.SOUTH);
		
		this.setBackground(Color.RED);
		this.setLayout(new BorderLayout());
		this.add(boardPanel, BorderLayout.CENTER);
		this.add(eastPanel, BorderLayout.EAST);
		
		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				notifyObserversOfMouseEvent(e);
				GameScreen.this.outputPanel.dispatchEvent(e);
			}
		});
	}

	@Override
	public void paintOval(Point location, Color color) {
		Graphics g = boardPanel.getGraphics();
		g.setColor(Color.ORANGE);
		g.fillRect(location.x - ConfigScreen.HALF_TILE_SIZE(), 
				location.y - ConfigScreen.HALF_TILE_SIZE(), 
				ConfigScreen.TILE_SIZE(), ConfigScreen.TILE_SIZE());
		g.setColor(color);
		g.fillOval(location.x - ConfigScreen.HALF_TILE_SIZE(), 
				location.y - ConfigScreen.HALF_TILE_SIZE(), 
				ConfigScreen.TILE_SIZE(), ConfigScreen.TILE_SIZE());
	}

	@Override
	public void paintGrid(Point location) {
		Graphics g = boardPanel.getGraphics();
		g.setColor(Color.ORANGE);
		g.fillRect(location.x - ConfigScreen.HALF_TILE_SIZE(), 
				location.y - ConfigScreen.HALF_TILE_SIZE(), 
				ConfigScreen.TILE_SIZE(), ConfigScreen.TILE_SIZE());
		g.setColor(Color.BLACK);
		if (isCornerLocated(location))
			drawEmptyCornerSpace(g, location, getCardinalCorner(location));
		else if (isEdgeLocated(location))
			drawEmptyEdgeSpace(g, location, getCardinalEdge(location));
		else
			drawEmptyInnerSpace(g, location);
	}
	
	private void drawEmptyCornerSpace(Graphics g, Point location, int cardinalDirection) {
		System.out.println("Drawing empty corner space");
		switch(cardinalDirection)
		{
		case GridBagConstraints.NORTHWEST:
			//left to right
			g.drawLine(location.x, location.y, 
					location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
			//top to bottom
			g.drawLine(location.x, location.y, 
					location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
			break;
		case GridBagConstraints.NORTHEAST:
			//left to right
			g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y, 
					location.x, location.y);
			//top to bottom
			g.drawLine(location.x, location.y, 
					location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
			break;
		case GridBagConstraints.SOUTHWEST:
			//top to bottom
			g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(), 
					location.x, location.y);
			//left to right
			g.drawLine(location.x, location.y, 
					location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
			break;
		case GridBagConstraints.SOUTHEAST:
			//left to right
			g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y, 
					location.x, location.y);
			//top to bottom
			g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(), 
					location.x, location.y);
			break;
		default:
			System.err.println("CardinalDirection not supported in drawEmptyCornerSpace");
		}
	}

	private void drawEmptyEdgeSpace(Graphics g, Point location, int cardinalDirection) {
		System.out.println("Drawing Edge Space X: " + location.x + " Y: " + location.y + " Dir: " + cardinalDirection);
		switch(cardinalDirection)
		{
		case GridBagConstraints.WEST:
			//top to bottom
			g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(), 
					location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
			//left to right
			g.drawLine(location.x, location.y, 
					location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
			break;
		case GridBagConstraints.EAST:
			//top to bottom
			g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(), 
					location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
			//left to right
			g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y, 
					location.x, location.y);
			break;
		case GridBagConstraints.NORTH:
			//left to right
			g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y, 
					location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
			//top to bottom
			g.drawLine(location.x, location.y, 
					location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
			break;
		case GridBagConstraints.SOUTH:
			//left to right
			g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y, 
					location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
			//top to bottom
			g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(), 
					location.x, location.y);
			break;
		default:
			System.err.println("CardinalDirection not supported in drawEmptyEdgeSpace");
		}
	}

	private void drawEmptyInnerSpace(Graphics g, Point location) {
		g.drawLine(location.x - ConfigScreen.HALF_TILE_SIZE(), location.y,
				location.x + ConfigScreen.HALF_TILE_SIZE(), location.y);
		g.drawLine(location.x, location.y - ConfigScreen.HALF_TILE_SIZE(),
				location.x, location.y + ConfigScreen.HALF_TILE_SIZE());
	}

	private boolean isCornerLocated(Point location) {
		int xCoord = location.x / ConfigScreen.TILE_SIZE();
		int yCoord = location.y / ConfigScreen.TILE_SIZE();
		if (xCoord == 1 && yCoord == 1)
			return true;
		if (xCoord == 1 && yCoord == ConfigScreen.BOARD_SIZE())
			return true;
		if (xCoord == ConfigScreen.BOARD_SIZE() && yCoord == 1)
			return true;
		if (xCoord == ConfigScreen.BOARD_SIZE() && yCoord == ConfigScreen.BOARD_SIZE())
			return true;
		return false;
	}

	private boolean isEdgeLocated(Point location) {
		System.out.println("Is Edge Located");
		int xCoord = location.x / ConfigScreen.TILE_SIZE();
		int yCoord = location.y / ConfigScreen.TILE_SIZE();
		if (xCoord == 1 || xCoord == ConfigScreen.BOARD_SIZE() ||
				yCoord == 1 || yCoord == ConfigScreen.BOARD_SIZE())
			return true;
		return false;
	}

	private int getCardinalCorner(Point location) {
		int xCoord = location.x / ConfigScreen.TILE_SIZE();
		int yCoord = location.y / ConfigScreen.TILE_SIZE();
		if (xCoord == 1 && yCoord == 1)
			return GridBagConstraints.NORTHWEST;
		if (xCoord == 1 && yCoord == ConfigScreen.BOARD_SIZE())
			return GridBagConstraints.SOUTHWEST;
		if (xCoord == ConfigScreen.BOARD_SIZE() && yCoord == 1)
			return GridBagConstraints.NORTHEAST;
		if (xCoord == ConfigScreen.BOARD_SIZE() && yCoord == ConfigScreen.BOARD_SIZE())
			return GridBagConstraints.SOUTHEAST;
		return -1;
	}

	private int getCardinalEdge(Point location) {
		int xCoord = location.x / ConfigScreen.TILE_SIZE();
		int yCoord = location.y / ConfigScreen.TILE_SIZE();
		if (xCoord == 1)
			return GridBagConstraints.WEST;
		if (xCoord == ConfigScreen.BOARD_SIZE())
			return GridBagConstraints.EAST;
		if (yCoord == 1)
			return GridBagConstraints.NORTH;
		if (yCoord == ConfigScreen.BOARD_SIZE())
			return GridBagConstraints.SOUTH;
		return -1;
	}
	
}
