package pacman.controllers.neha_patwardhan;

import java.util.Queue;
import java.util.ArrayList;
import java.util.LinkedList;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

class PacManNode1 
{
    Game gameState;
    int g;
    double h;
    int depth;
    
    public PacManNode1(Game game, int g, double h, int m)
    {
        gameState = game;
        this.g = g;
        this.h = h;
        depth = m;
    }
}

/*	A* is an informed search algorithm. It solves problems by searching among all possible paths to the target
 *  and among these paths it first considers the ones that appear to lead most quickly to the solution.  
 *  It is formulated in terms of weighted graphs */

public class AStar_Controller extends Controller<MOVE>{
	public static StarterGhosts ghosts = new StarterGhosts();
		public MOVE getMove(Game game, long timeDue)
		{
			//get moves : left, right, down, up, neutral
			MOVE[] allMoves=MOVE.values();
			int highScore = -1;
			MOVE highMove = null;
			// for each possible move in the moves for current game state apply A*
   			for(MOVE m: allMoves)
			{
				Game gameCopy = game.copy();
				Game gameAtM = gameCopy;
				gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
				int tempHighScore = this.astar(new PacManNode1(gameAtM, 0,0,0), 7);
				//based on high score, assign move to high move. That is selecting the move.
				if(highScore < tempHighScore)
				{
					highScore = tempHighScore;
					highMove = m;
				}
				System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);   
			}        
			System.out.println("High Score: " + highScore + ", High Move:" + highMove);
			return highMove;        
		}
	
    	
	public int astar(PacManNode1 rootGameState, int maxdepth)
	{
		//get all possible moves: left, right, down, up, neutral
		MOVE[] allMoves=Constants.MOVE.values();
        int highScore = -1;
        
        //create a queue as we need for A*	
        Queue<PacManNode1> queue = new LinkedList<PacManNode1>();
        //closed list
        ArrayList closed = new ArrayList<PacManNode1>();
        
        // add root node in the queue
        queue.add(rootGameState);
        
        while(!queue.isEmpty())
        {	
        	//Get the node from queue
        	PacManNode1 pmnode = queue.remove();
        	
        	//condition for depth. Consider it as height of graph
        	if(pmnode.depth >= maxdepth)
            {
                int score = pmnode.gameState.getScore();
                 if (highScore < score)
                     highScore = score;
            }
        	else
            {   double weight = 0;
            	MOVE nextMove = null;
            	Double h = 0.0;
            	//children. Iterate through moves.
        		for(MOVE m: allMoves)
                {
                    Game gameCopy = pmnode.gameState.copy();
                    gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
                    int minDistance=Integer.MAX_VALUE;
            		GHOST minGhost=null;		
            		
            		// I have considered my target for calculating heuristic as edible ghosts. So this is to find nearest edible ghost and
            		//consider it as a target.
            		for(GHOST ghost : GHOST.values())
            			if(gameCopy.getGhostEdibleTime(ghost)>0)
            			{
            				int distance=pmnode.gameState.getShortestPathDistance(pmnode.gameState.getPacmanCurrentNodeIndex(),gameCopy.getGhostCurrentNodeIndex(ghost));
            				
            				if(distance<minDistance)
            				{
            					minDistance=distance;
            					minGhost=ghost;
            				}
            			}
            		
            		int target = 0;
            		if(minGhost!=null)
                    target = gameCopy.getGhostCurrentNodeIndex(minGhost); 
            		// if there is no edible ghost, target to find heuristic would be any power pill. I am selecting it randomely by 
            		// picking up first power pill in the array of indices.
            		else
            		{
            			int target_list[] = gameCopy.getPowerPillIndices();
            			target= target_list[0];
            		}
            		
            		// heuristic would be euclidean distance between current node index and the target
            		h = pmnode.gameState.getEuclideanDistance(pmnode.gameState.getPacmanCurrentNodeIndex(), target);
            		
            		// moving cost i.e. g will be increased by 1 uniformly. Assigning h,g to the PacManNode.
                    PacManNode1 node = new PacManNode1(gameCopy, pmnode.g+1,h, pmnode.depth+1);
                    //if heuristic + move cost is less than the current weight, update the parameters of the node and select that move as 
                    //next move
                    if((double) (pmnode.g+node.g+node.h) < weight)
                    	{
                    		weight = pmnode.g+node.g+node.h;
                    		h = node.h;
                    		nextMove = m;
                    	}
                } 
        		// get the game state
        		Game gameCopy = pmnode.gameState.copy();
        		// update it by making the next move
                gameCopy.advanceGame(nextMove, ghosts.getMove(gameCopy, 0));
                // update pmnode to node.
                PacManNode1 node = new PacManNode1(gameCopy,pmnode.g+1,h, pmnode.depth+1);
                // add pmnode node to the closed list
                closed.add(pmnode);
                if(!closed.contains(node))
                {	
                	// add the current node to the queue, if not already in closed
                	queue.add(node);
                }
            }
        }
        
        return highScore;
	}

}