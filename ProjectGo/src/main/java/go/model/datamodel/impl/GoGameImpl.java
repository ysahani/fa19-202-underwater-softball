package go.model.datamodel.impl;

import go.model.datamodel.GoGame;
import go.model.datamodel.GoGameBoard;
import go.model.datamodel.GoMove;
import go.model.datamodel.GoPoint;
import go.model.datamodel.StoneColor;
import go.model.gameplay.GoCapture;
import go.model.gameplay.GoScoringStrategy;
import go.model.gameplay.capturing.GoCaptureImpl;
import go.model.gameplay.scoring.SimpleScoringStrategy;
import go.model.observer.GoModelConfigObserver;
import go.model.observer.GoGameObserver;
import go.model.observer.GoGameSubject;
import go.model.observer.GoMoveObserver;

import java.util.LinkedList;
import java.util.List;


public class GoGameImpl implements GoGameSubject, GoGame {
    private GoGameBoard board;
    private StoneColor nextPlayer;
    private boolean lastMovePassed;
    private List<GoMoveObserver> moveObservers;
    private List<GoGameObserver> gameObservers;
    private List<GoModelConfigObserver> configObservers;
    private GoCapture capture;
    private GoScoringStrategy scoringStrategy;

    private static int BOARD_SIZE = 9;


    public GoGameImpl() {
        // @todo determine which strategy we'll actually use - we needn't implement both.
        this(new GoCaptureImpl(BOARD_SIZE), new SimpleScoringStrategy());
    }
    public GoGameImpl(int boardSize) {
    	this(new GoCaptureImpl(boardSize), new SimpleScoringStrategy());
    }
    
    public GoGameImpl(GoCapture capture, GoScoringStrategy strategy) {
        nextPlayer = StoneColor.BLACK;
        lastMovePassed = false;
        moveObservers = new LinkedList<>();
        gameObservers = new LinkedList<>();
        configObservers = new LinkedList<>();
        GoGameBoardImpl board = new GoGameBoardImpl(BOARD_SIZE);
        this.board = board;
        addMoveObserver(board);
        this.capture = capture;
        this.scoringStrategy = strategy;
    }

    @Override
    public void makeMove(GoPoint point) {
    	System.out.println("XY in GameGameImpl: " + point.getX() + " " + point.getY());
    	System.out.println("Size of Board: " + board.size());
    	// Point is already occupied
    	if (board.getStone(point).isPresent())
    		return;
        GoMove move = new GoMoveImpl(point, nextPlayer);
        this.notifyObserversOfPiecePlacement(move);
        List<GoPoint> potentiallyCapturedPieces = capture.capturePiecesForMove(board, move);
        System.out.println(potentiallyCapturedPieces.toString());
        if (potentiallyCapturedPieces.contains(point)) {
        	//the last move was an illegal suicide, this turn is invalid
        	this.notifyObserversOfPieceRemoval(point);
        	return;
        }
        else
        	potentiallyCapturedPieces.forEach(this::notifyObserversOfPieceRemoval);
        rotateNextPlayer();
        lastMovePassed = false;
    }

    @Override
    public void pass() {
        if (lastMovePassed) {
            StoneColor winner = scoringStrategy.determineWinner(board);
            notifyObserversOfGameEnd(winner);
        } else {
            rotateNextPlayer();
            lastMovePassed = true;
        }
    }
    
    @Override
    public void reset(){
        //reset the board
        board.reset();
        //reset next player
        nextPlayer = StoneColor.BLACK;
    }
    
	@Override
	public void configureBoardSize(int size) {
		GoGameImpl.BOARD_SIZE = size;
		GoCaptureImpl newCapture = new GoCaptureImpl(size);
		GoGameBoardImpl newBoard = new GoGameBoardImpl(size);
		
		moveObservers.remove((GoMoveObserver) this.board);
		addMoveObserver(newBoard);
		this.capture = newCapture;
		this.board = newBoard;
		this.notifyObserversOfBoardSizeChange(size);
	}

    @Override
    public void addMoveObserver(GoMoveObserver observer) {
        moveObservers.add(observer);
    }

    @Override
    public void addGameObserver(GoGameObserver observer) {
        gameObservers.add(observer);
    }
    
	@Override
	public void addModelConfigObserver(GoModelConfigObserver observer) {
		configObservers.add(observer);
	}

    @Override
    public void notifyObserversOfPiecePlacement(GoMove move) {
        moveObservers.forEach(observer -> observer.handlePieceAdditionEvent(move));
    }

    @Override
    public void notifyObserversOfPieceRemoval(GoPoint point) {
        moveObservers.forEach(observer -> observer.handlePieceRemovalEvent(point));
    }

    @Override
    public void notifyObserversOfGameEnd(StoneColor winner) {
        gameObservers.forEach(observer -> observer.handleGameEnd(winner));
    }
    
	@Override
	public void notifyObserversOfBoardSizeChange(int boardSize) {
		configObservers.forEach(observer -> observer.handleBoardSizeChange(boardSize));
	}

    private void rotateNextPlayer() {
        nextPlayer = nextPlayer == StoneColor.BLACK ? StoneColor.WHITE : StoneColor.BLACK;
    }
}
