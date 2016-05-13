package pacman.controllers.neha_patwardhan;

import java.util.Stack;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

class PacManNode {
	Game gameState;
	int depth;
	PacManNode(Game game, int m){
		gameState = game;
		depth = m;
	}
}

/* DFS starts at the root by selecting some arbitrary node as the root (here its the starting state) 
 * and explores as far as possible along each branch before backtracking.*/

public class DFS_Controller extends Controller<MOVE> {
	 public static StarterGhosts ghosts = new StarterGhosts();
		public MOVE getMove(Game game,long timeDue)
		{
			//get moves : left, right, down, up, neutral
	            MOVE[] allMoves=MOVE.values();	        
	            int highScore = -1;
	            MOVE highMove = null; 
	           // for each possible move in the moves for current game state apply DFS
	            for(MOVE m: allMoves)
	            {
	                Game gameCopy = game.copy();
	                Game gameAtM = gameCopy;
	                gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
	                int tempHighScore = this.dfs(new PacManNode(gameAtM, 0), 7);
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
	        
	    public int dfs(PacManNode rootGameState, int maxdepth)
		{		
	        	//get all possible moves: left, right, down, up, neutral
	            MOVE[] allMoves=Constants.MOVE.values();
	            int highScore = -1;	
	            
	            //create a stack as for DFS we need last in first out mechanism	            
	            Stack<PacManNode> stack = new Stack<PacManNode>();
	            
	            //push the root node into stack
	            stack.push(rootGameState);
	        
	            while(!stack.isEmpty())
	                {	/*get the last node inserted in the stack. This is because depth first search traverses depth wise. 
	            		So when we find children of the node, we put it in the stack. So opposite to breadth first search, now 
	            		we first want to fins children of those children. In short, last in first out.*/
	                    PacManNode pmnode = stack.pop();
	                    
	                    //if depth is greater than max depth, get the current score. If that score is higher than the high score, 
	                    //obviously now we have a new high score so assign that to highSCore variable. 
	                    if(pmnode.depth >= maxdepth)
	                    {
	                        int score = pmnode.gameState.getScore();
	                         if (highScore < score)
	                             highScore = score;
	                    }
	                    
	                    // for all moves in allMoves array, get the current and next state of the game. Push the node to the stack.
	                    // Now here, node means PacManNode, which is nothing but a wrapper of game state and depth.
	                    //This step is nothing but finding children of a particular node.
	                    else
	                    {
	                        for(MOVE m: allMoves)
	                        {
	                            Game gameCopy = pmnode.gameState.copy();
	                            gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
	                            PacManNode node = new PacManNode(gameCopy, pmnode.depth+1);
	                            stack.push(node);
	                        }
	                    }

			}
	                
	                return highScore;
		}
	        

}
