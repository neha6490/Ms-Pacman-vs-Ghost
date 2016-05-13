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

public class KNN_Controller extends Controller<MOVE> {

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
			int tempHighScore = this.knn(new PacManNode(gameAtM, 0), 7, 50);
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

	public int knn(PacManNode rootGameState, int maxdepth , int k) {
		int highScore = -1;
		ArrayList<MOVE> kMoves = new ArrayList<MOVE>();
		TreeMap<Double,MOVE> dist = new TreeMap<Double,MOVE>();
		while(maxdepth > 0){
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
			double distance = 0;
			int index = 1;
					
			int currentNode = rootGameState.gameState.getPacmanCurrentNodeIndex();
			int nextNodePowerPill = gameCopy1.getPacmanCurrentNodeIndex();
			int nextNodePill = gameCopy2.getPacmanCurrentNodeIndex();
			int nextNodeGhost = gameCopy3.getPacmanCurrentNodeIndex();
			
			// distance from current pacman to pacman with move towards power pill
			distance = rootGameState.gameState.getEuclideanDistance(currentNode, nextNodePowerPill);
			dist.put(distance, power_pill_move);
			
			// distance from current pacman to pacman with move towards pill
			distance = rootGameState.gameState.getEuclideanDistance(currentNode, nextNodePill);
			dist.put(distance, pill_move);
			
			// distance from current pacman to pacman with move towards edible ghost or move away from the ghost
			distance = rootGameState.gameState.getEuclideanDistance(currentNode, nextNodeGhost);
			dist.put(distance, ghost_move);			
			maxdepth--;			
		}
		Game finalGameCopy;
		int finalIndex = 0;
					
		int count = 0;
		for (Map.Entry<Double, MOVE> entry : dist.entrySet())
		{
		    if (count == k)
		    {
		        break;
		    }
		    count++;
		    MOVE m = entry.getValue();
	        kMoves.add(m);
		}
		MOVE majorityMove = findMajorityMoves(kMoves);
		Game finalGameCopy1 = rootGameState.gameState.copy();
		finalGameCopy1.advanceGame(majorityMove, ghosts.getMove(finalGameCopy1, 0));
		return finalGameCopy1.getScore();
	}
	
	public MOVE findMajorityMoves(ArrayList<MOVE> kMoves)
	{
		HashMap<MOVE,Integer> map = new HashMap<MOVE, Integer>();
		MOVE move = null;
		int maxCount = -1;
		
		for(MOVE m : kMoves)
		{
			if(map.containsKey(m))
			{
				map.put(m, map.get(m)+1);
			}
			else
			{
				map.put(m, 1);
			}
		}
		
		Iterator it = map.entrySet().iterator();
	    while (it.hasNext())
	    {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(maxCount < (int)pair.getValue())
	        {
	        	maxCount = (int)pair.getValue();
	        	move = (MOVE)pair.getKey();
	        }
	        it.remove(); 
	    }
		return move;
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
