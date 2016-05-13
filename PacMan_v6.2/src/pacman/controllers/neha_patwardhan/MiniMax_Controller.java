package pacman.controllers.neha_patwardhan;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class MiniMax_Controller extends Controller<MOVE> {
	class PacManNode {
		Game gameState;
		int depth;

		PacManNode(Game game) {
			gameState = game;
		}
	}
	
	public static StarterGhosts ghosts = new StarterGhosts();

	public MOVE getMove(Game game, long timeDue) {
		//MOVE[] allMoves = MOVE.values();
		
		MOVE[] allMoves;

		MOVE pacmanLastMove = game.getPacmanLastMoveMade();

		int currIndex = game.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null){

		     allMoves = game.getPossibleMoves(currIndex,pacmanLastMove);

		}else{

		     allMoves = game.getPossibleMoves(currIndex);

		}

		int highScore = -1;
		MOVE highMove = null;

		for (MOVE m : allMoves) {
			// System.out.println("Trying Move: " + m);
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.minimax(new PacManNode(gameAtM), 7,Integer.MIN_VALUE,Integer.MAX_VALUE, true);
			if (highScore < tempHighScore) {
				highScore = tempHighScore;
				highMove = m;
			}
			
			System.out.println("Trying Move: " + m + ", Score: "
					+ tempHighScore);
		}

		System.out.println("High Score: " + highScore + ", High Move:"
				+ highMove);
		return highMove;
	}

	public int minimax(PacManNode rootGameState, int maxdepth, int alpha, int beta, boolean max) {
		//MOVE[] allMoves = Constants.MOVE.values();
		
		MOVE[] allMoves;

		MOVE pacmanLastMove = rootGameState.gameState.getPacmanLastMoveMade();

		int currIndex = rootGameState.gameState.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null){

		     allMoves = rootGameState.gameState.getPossibleMoves(currIndex,pacmanLastMove);

		}else{

		     allMoves = rootGameState.gameState.getPossibleMoves(currIndex);

		}
		
		int d = 0;
		if (maxdepth <= 0) {
			int minD = Integer.MAX_VALUE;
			for(GHOST ghost : GHOST.values()){
				d = rootGameState.gameState.getShortestPathDistance(rootGameState.gameState.getPacmanCurrentNodeIndex(),rootGameState.gameState.getGhostCurrentNodeIndex(ghost));
				minD = Math.min(d,minD);
			}
			return (rootGameState.gameState.getScore() + minD);
		}
		if (max) {
			int highscore = Integer.MIN_VALUE;
			for (MOVE m : allMoves) {
				Game gameCopy = rootGameState.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
				PacManNode node = new PacManNode(gameCopy);
				int temp = minimax(node, maxdepth - 1,alpha,beta, false);
				highscore = Math.max(highscore, temp);
				alpha = Math.max(alpha,highscore);
				if(beta <= alpha){
					break;
				}
			}
			return highscore;
		} else {
			int highscore = Integer.MAX_VALUE;
			for (MOVE m : allMoves) {
				Game gameCopy = rootGameState.gameState.copy();
				gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));				
				PacManNode node = new PacManNode(gameCopy);
				int temp = minimax(node, maxdepth - 1,alpha,beta, true);
				highscore = Math.min(highscore, temp);
				beta = Math.min(beta,highscore);
				if(beta <= alpha){
					break;
				}
			}
			return highscore;
		}
	}
}
