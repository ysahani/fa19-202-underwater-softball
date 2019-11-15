package go.view.screen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import go.view.observer.GoScreenObserver;
import go.view.observer.GoScreenSubject;

@SuppressWarnings("serial")
public class WelcomeScreen extends JComponent implements GoScreenSubject {
	
	private List<GoScreenObserver> observers;
	
	private final int PANEL_SIZE = 200;
	private final int SCREEN_SIZE = 600;
	private final Dimension buttonDim = new Dimension(150, 100);
	private final Image bgImg = new ImageIcon("images/welcome.jpg").getImage();
	private final Border buttonBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED, 
			Color.DARK_GRAY, Color.LIGHT_GRAY);
	
	private JComponent[][] welcomePanels;
	private List<JComponent> buttons;
	
	private JButton configNewGame;
	private JButton startNewGame;
	
	public WelcomeScreen() {
		observers = new LinkedList<GoScreenObserver>();
		this.setLayout( new GridBagLayout() );
		this.setPreferredSize(new Dimension(SCREEN_SIZE, SCREEN_SIZE));
		
		welcomePanels = new JPanel[3][3];
		buttons = new ArrayList<JComponent>();
		initPanels();
		initButtons();
		
		// Fill in welcomeScreen panels
		addComponent(startNewGame, GridBagConstraints.LINE_START);
		addComponent(Box.createRigidArea(buttonDim), GridBagConstraints.CENTER);
		addComponent(configNewGame, GridBagConstraints.LINE_END);
		
        // Filler bottom component that will push the rest to the top
		GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        this.add(Box.createGlue(), gbc);
	}
	
	private void addComponent(Component component, int gridBagConstraint)
	{
		switch(gridBagConstraint)
		{
		case GridBagConstraints.FIRST_LINE_START:
			welcomePanels[0][0].add(component);
			break;
		case GridBagConstraints.PAGE_START:
			welcomePanels[1][0].add(component);
			break;
		case GridBagConstraints.FIRST_LINE_END:
			welcomePanels[2][0].add(component);
			break;
		case GridBagConstraints.LINE_START:
			welcomePanels[0][1].add(component);
			break;
		case GridBagConstraints.CENTER:
			welcomePanels[1][1].add(component);
			break;
		case GridBagConstraints.LINE_END:
			welcomePanels[2][1].add(component);
			break;
		case GridBagConstraints.LAST_LINE_START:
			welcomePanels[0][2].add(component);
			break;
		case GridBagConstraints.PAGE_END:
			welcomePanels[1][2].add(component);
			break;
		case GridBagConstraints.LAST_LINE_END:
			welcomePanels[2][2].add(component);
			break;
		default:
			System.err.println("Invalid WelcomeScreen Constraint in " + this.getClass().getName() +
					" with GridBagConstraint of " + gridBagConstraint);
		}
	}
	
	private void initPanels()
	{
		GridBagConstraints c = new GridBagConstraints();
		EmptyBorder buttonBorder = new EmptyBorder((PANEL_SIZE - buttonDim.height) / 2,
				(PANEL_SIZE - buttonDim.width) / 2,
				(PANEL_SIZE - buttonDim.height) / 2,
				(PANEL_SIZE - buttonDim.width) / 2);
		
		for (int x = 0; x < 3; x ++)
			for (int y = 0; y < 3; y++) {
				welcomePanels[x][y] = new JPanel();
				welcomePanels[x][y].setLayout(new BorderLayout());
				welcomePanels[x][y].setBorder(buttonBorder);
				welcomePanels[x][y].setOpaque(false);
				c.gridx = x;
				c.gridy = y;
				this.add(welcomePanels[x][y], c);
			}
	}
	
	private void initButtons()
	{
		configNewGame = new JButton("Config Start");
		startNewGame = new JButton("Quick Start");
		
		configNewGame.setActionCommand("CONFIG_START");
		startNewGame.setActionCommand("QUICK_START");
		
		buttons.add(configNewGame);
		buttons.add(startNewGame);
		
		for (JComponent button : buttons)
		{
			button.setPreferredSize(buttonDim);
			button.setForeground(Color.RED);
			button.setBackground(Color.BLACK);
			button.setBorder(buttonBorder);
		}
		
		configNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyObserversOfActionEvent(e);
			}
		});
		startNewGame.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notifyObserversOfActionEvent(e);
			}
		});
	}
	
    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        Dimension lmPrefSize = getLayout().preferredLayoutSize(this);
        size.width = Math.max(size.width, lmPrefSize.width);
        size.height = Math.max(size.height, lmPrefSize.height);
        return size;
    }
    
    @Override
    public void paintComponent(Graphics g) {
    	super.paintComponent(g);
        g.drawImage(bgImg, 0, 0, null);
      }

	@Override
	public void notifyObserversOfActionEvent(ActionEvent event) {
		observers.forEach(observer -> observer.handleActionEvent(event));
	}

	@Override
	public void notifyObserversOfMouseEvent(MouseEvent event) {
		observers.forEach(observer -> observer.handleMouseEvent(event));
	}

	@Override
	public void registerGoScreenObserver(GoScreenObserver observer) {
		this.observers.add(observer);
	}
}
