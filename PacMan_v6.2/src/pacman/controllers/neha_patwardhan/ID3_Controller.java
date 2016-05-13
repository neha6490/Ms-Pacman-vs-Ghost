package pacman.controllers.neha_patwardhan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.neha_patwardhan.MiniMax_Controller.PacManNode;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Constants;
import pacman.game.Game;

public class ID3_Controller extends Controller<MOVE> {

	class PacManNode {
		Game gameState;
		int depth;

		PacManNode(Game game, int m) {
			gameState = game;
			depth = m;
		}
	}

	public static StarterGhosts ghosts = new StarterGhosts();

	@Override
	public MOVE getMove(Game game, long timeDue) {
		// MOVE[] allMoves = MOVE.values();

		MOVE[] allMoves;

		MOVE pacmanLastMove = game.getPacmanLastMoveMade();

		int currIndex = game.getPacmanCurrentNodeIndex();

		if (pacmanLastMove != null) {

			allMoves = game.getPossibleMoves(currIndex, pacmanLastMove);

		} else {

			allMoves = game.getPossibleMoves(currIndex);

		}

		int highScore = -1;
		MOVE highMove = null;

		for (MOVE m : allMoves) {
			// System.out.println("Trying Move: " + m);
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.id3(new PacManNode(gameAtM, 0), 7);
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

	public int id3(PacManNode rootGameState, int maxdepth) {
		int highScore = -1;

		// power pills
		MOVE power_pill_move = getPowerPillsGameCopyMOVE(rootGameState.gameState);

		// pills
		MOVE pill_move = getPillsGameCopyMOVE(rootGameState.gameState);

		// edible ghost
		MOVE ghost_move = getGhostsGameCopyMOVE(rootGameState.gameState);
		
		Game gameCopy1 = rootGameState.gameState.copy();
		Game gameCopy2 = rootGameState.gameState.copy();
		Game gameCopy3 = rootGameState.gameState.copy();
		gameCopy1.advanceGame(power_pill_move, ghosts.getMove(gameCopy1, 0));
		gameCopy2.advanceGame(pill_move, ghosts.getMove(gameCopy2, 0));
		gameCopy3.advanceGame(ghost_move, ghosts.getMove(gameCopy3, 0));

		Game gainGameCopy;
		int index = 1;
		if (gameCopy1.getScore() >= gameCopy2.getScore()
				&& gameCopy1.getScore() >= gameCopy3.getScore()) {
			gainGameCopy = gameCopy1;
			index = 1;
		} else if (gameCopy2.getScore() >= gameCopy1.getScore()
				&& gameCopy2.getScore() >= gameCopy3.getScore()) {
			gainGameCopy = gameCopy2;
			index = 2;
		} else {
			gainGameCopy = gameCopy3;
			index = 3;
		}

		Game gainGameCopy1 = gainGameCopy.copy();
		Game gainGameCopy2 = gainGameCopy.copy();
		Game finalGameCopy;
		int finalIndex = 0;
		if (index == 1) {
			pill_move = getPillsGameCopyMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(pill_move, ghosts.getMove(gainGameCopy1, 0));
			
			ghost_move = getGhostsGameCopyMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(ghost_move, ghosts.getMove(gainGameCopy2, 0));
			
			if(gainGameCopy1.getScore() > gainGameCopy2.getScore())
			{
				finalIndex = 1;
				finalGameCopy = gainGameCopy1;
			}
			else
			{
				finalIndex = 2;
				finalGameCopy = gainGameCopy2;
			}
			
			if(finalIndex == 1)
			{
				ghost_move = getGhostsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(ghost_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
			else
			{
				pill_move = getPillsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(pill_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
		}
		if(index == 2)
		 {
			power_pill_move = getPowerPillsGameCopyMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(power_pill_move, ghosts.getMove(gainGameCopy1, 0));
			
			ghost_move = getGhostsGameCopyMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(ghost_move, ghosts.getMove(gainGameCopy2, 0));
			
			if(gainGameCopy1.getScore() > gainGameCopy2.getScore())
			{
				finalIndex = 1;
				finalGameCopy = gainGameCopy1;
			}
			else
			{
				finalIndex = 2;
				finalGameCopy = gainGameCopy2;
			}
			
			if(finalIndex == 1)
			{
				ghost_move = getGhostsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(ghost_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
			else
			{
				power_pill_move = getPowerPillsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(power_pill_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
		}else{
			power_pill_move = getPowerPillsGameCopyMOVE(gainGameCopy1);
			gainGameCopy1.advanceGame(power_pill_move, ghosts.getMove(gainGameCopy1, 0));
			
			pill_move = getPillsGameCopyMOVE(gainGameCopy2);
			gainGameCopy2.advanceGame(pill_move, ghosts.getMove(gainGameCopy2, 0));
			
			if(gainGameCopy1.getScore() > gainGameCopy2.getScore())
			{
				finalIndex = 1;
				finalGameCopy = gainGameCopy1;
			}
			else
			{
				finalIndex = 2;
				finalGameCopy = gainGameCopy2;
			}
			
			if(finalIndex == 1)
			{
				pill_move = getPillsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(pill_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
			else
			{
				power_pill_move = getPowerPillsGameCopyMOVE(finalGameCopy);
				finalGameCopy.advanceGame(power_pill_move, ghosts.getMove(finalGameCopy, 0));
				return finalGameCopy.getScore();
			}
		}

	}

	public static MOVE getPowerPillsGameCopyMOVE(Game game) {
		int targetsPowerPills[] = game.getActivePowerPillsIndices();
		if(targetsPowerPills.length > 0)
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getClosestNodeIndexFromNodeIndex(
						game.getPacmanCurrentNodeIndex(), targetsPowerPills,
						DM.PATH), DM.PATH);
		else 
			return MOVE.RIGHT;
	}

	public static MOVE getPillsGameCopyMOVE(Game game) {
		int targetsPills[] = game.getPillIndices();
		return game
				.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
						game.getClosestNodeIndexFromNodeIndex(
								game.getPacmanCurrentNodeIndex(), targetsPills,
								DM.PATH), DM.PATH);
	}

	public static MOVE getGhostsGameCopyMOVE(Game game) {
		int MIN_DISTANCE = 20;
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;
		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) > 0) {
				int distance = game.getShortestPathDistance(
						game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}
		MOVE ghost_move = null;
		if (minGhost != null) // we found an edible ghost
			ghost_move = game.getNextMoveTowardsTarget(
					game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(minGhost), DM.PATH);
		else {
			for (GHOST ghost : GHOST.values())
				if (game.getGhostEdibleTime(ghost) == 0
						&& game.getGhostLairTime(ghost) == 0)
					if (game.getShortestPathDistance(
							game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE)
						ghost_move = game.getNextMoveAwayFromTarget(
								game.getPacmanCurrentNodeIndex(),
								game.getGhostCurrentNodeIndex(ghost), DM.PATH);
		}
		return ghost_move;
	}

}
