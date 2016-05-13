package pacman.controllers.neha_patwardhan;

import java.util.Queue;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

class PacManNode2 {
	Game gameState;
	int g;
	double h;
	int depth;

	public PacManNode2(Game game, int g, double h, int m) {
		gameState = game;
		this.g = g;
		this.h = h;
		depth = m;
	}
}

/*
 * A* is an informed search algorithm. It solves problems by searching among all
 * possible paths to the target and among these paths it first considers the
 * ones that appear to lead most quickly to the solution. It is formulated in
 * terms of weighted graphs. Instead of selecting heuristics randomly, I have
 * used part of ID3 algorithm to choose the best heuristic.
 */

public class MyOwn_Controller extends Controller<MOVE> {
	public static StarterGhosts ghosts = new StarterGhosts();

	public MOVE getMove(Game game, long timeDue) {
		// get moves : left, right, down, up, neutral
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
		// for each possible move in the moves for current game state apply A*
		for (MOVE m : allMoves) {
			Game gameCopy = game.copy();
			Game gameAtM = gameCopy;
			gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
			int tempHighScore = this.astar(new PacManNode2(gameAtM, 0, 0, 0), 7);
			// based on high score, assign move to high move. That is selecting
			// the move.
			if (highScore < tempHighScore) {
				highScore = tempHighScore;
				highMove = m;
			}
			System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
		}
		System.out.println("High Score: " + highScore + ", High Move:" + highMove);
		return highMove;
	}

	public int astar(PacManNode2 rootGameState, int maxdepth) {
		// get all possible moves: left, right, down, up, neutral
		MOVE[] allMoves;
		MOVE pacmanLastMove = rootGameState.gameState.getPacmanLastMoveMade();
		int currIndex = rootGameState.gameState.getPacmanCurrentNodeIndex();
		if (pacmanLastMove != null) {
			allMoves = rootGameState.gameState.getPossibleMoves(currIndex, pacmanLastMove);
		} else {
			allMoves = rootGameState.gameState.getPossibleMoves(currIndex);
		}
		int highScore = -1;

		// create a queue as we need for A*
		Queue<PacManNode2> queue = new LinkedList<PacManNode2>();
		// closed list
		ArrayList closed = new ArrayList<PacManNode2>();

		// add root node in the queue
		queue.add(rootGameState);

		while (!queue.isEmpty()) {
			// Get the node from queue
			PacManNode2 pmnode = queue.remove();

			// condition for depth. Consider it as height of graph
			if (pmnode.depth >= maxdepth) {
				int score = pmnode.gameState.getScore();
				if (highScore < score)
					highScore = score;
			} else {
				double weight = 0;
				MOVE nextMove = null;
				Double h = 0.0;
				// children. Iterate through moves.
				for (MOVE m : allMoves) {
					Game gameCopy = pmnode.gameState.copy();
					gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
					int minDistance = Integer.MAX_VALUE;
					GHOST minGhost = null;

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

					// decide which factor is the best for calculating the
					// heuristics.
					Game gainGameCopy;
					int index = 1;
					if (gameCopy1.getScore() >= gameCopy2.getScore() && gameCopy1.getScore() >= gameCopy3.getScore()) {
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
					int target = 0;

					if (index == 1) {
						int target_list[] = gameCopy.getPowerPillIndices();
						ArrayList<Double> heuristics = new ArrayList<Double>();
						for (int t : target_list) {
							// heuristic would be euclidean distance between
							// current node index and the target
							double temp_h = pmnode.gameState
									.getEuclideanDistance(pmnode.gameState.getPacmanCurrentNodeIndex(), t);
							heuristics.add(temp_h);
						}
						Collections.sort(heuristics);
					}

					if (index == 2) {
						int target_list[] = gameCopy.getPillIndices();
						ArrayList<Double> heuristics = new ArrayList<Double>();
						for (int t : target_list) {
							// heuristic would be euclidean distance between
							// current node index and the target
							double temp_h = pmnode.gameState
									.getEuclideanDistance(pmnode.gameState.getPacmanCurrentNodeIndex(), t);
							heuristics.add(temp_h);
						}
						Collections.sort(heuristics);
					}

					if (index == 3) {
						// I have considered my target for calculating heuristic
						// as edible ghosts. So this is to find nearest edible
						// ghost and
						// consider it as a target.
						for (GHOST ghost : GHOST.values())
							if (gameCopy.getGhostEdibleTime(ghost) > 0) {
								int distance = pmnode.gameState.getShortestPathDistance(
										pmnode.gameState.getPacmanCurrentNodeIndex(),
										gameCopy.getGhostCurrentNodeIndex(ghost));

								if (distance < minDistance) {
									minDistance = distance;
									minGhost = ghost;
								}
							}

						if (minGhost != null)
							target = gameCopy.getGhostCurrentNodeIndex(minGhost);

						// heuristic would be euclidean distance between current
						// node index and the target
						h = pmnode.gameState.getEuclideanDistance(pmnode.gameState.getPacmanCurrentNodeIndex(), target);
					}

					// moving cost i.e. g will be increased by 1 uniformly.
					// Assigning h,g to the PacManNode.
					PacManNode2 node = new PacManNode2(gameCopy, pmnode.g + 1, h, pmnode.depth + 1);
					// if heuristic + move cost is less than the current weight,
					// update the parameters of the node and select that move as
					// next move
					if ((double) (pmnode.g + node.g + node.h) < weight) {
						weight = pmnode.g + node.g + node.h;
						h = node.h;
						nextMove = m;
					}
				}
				// get the game state
				Game gameCopy = pmnode.gameState.copy();
				// update it by making the next move
				gameCopy.advanceGame(nextMove, ghosts.getMove(gameCopy, 0));
				// update pmnode to node.
				PacManNode2 node = new PacManNode2(gameCopy, pmnode.g + 1, h, pmnode.depth + 1);
				// add pmnode node to the closed list
				closed.add(pmnode);
				if (!closed.contains(node)) {
					// add the current node to the queue, if not already in
					// closed
					queue.add(node);
				}
			}
		}

		return highScore;
	}

	// get moves
	public static MOVE getPowerPillsGameCopyMOVE(Game game) {
		int targetsPowerPills[] = game.getActivePowerPillsIndices();
		if (targetsPowerPills.length > 0)
			return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), targetsPowerPills, DM.PATH),
					DM.PATH);
		else
			return MOVE.RIGHT;
	}

	public static MOVE getPillsGameCopyMOVE(Game game) {
		int targetsPills[] = game.getPillIndices();
		return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
				game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), targetsPills, DM.PATH),
				DM.PATH);
	}

	public static MOVE getGhostsGameCopyMOVE(Game game) {
		int MIN_DISTANCE = 20;
		int minDistance = Integer.MAX_VALUE;
		GHOST minGhost = null;
		for (GHOST ghost : GHOST.values())
			if (game.getGhostEdibleTime(ghost) > 0) {
				int distance = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
						game.getGhostCurrentNodeIndex(ghost));

				if (distance < minDistance) {
					minDistance = distance;
					minGhost = ghost;
				}
			}
		MOVE ghost_move = null;
		if (minGhost != null) // we found an edible ghost
			ghost_move = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(),
					game.getGhostCurrentNodeIndex(minGhost), DM.PATH);
		else {
			for (GHOST ghost : GHOST.values())
				if (game.getGhostEdibleTime(ghost) == 0 && game.getGhostLairTime(ghost) == 0)
					if (game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(),
							game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE)
						ghost_move = game.getNextMoveAwayFromTarget(game.getPacmanCurrentNodeIndex(),
								game.getGhostCurrentNodeIndex(ghost), DM.PATH);
		}
		return ghost_move;
	}
}