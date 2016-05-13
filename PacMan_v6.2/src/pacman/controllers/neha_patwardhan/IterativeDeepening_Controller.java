package pacman.controllers.neha_patwardhan;

import java.util.Stack;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

/*strategy in which a depth-limited search is run repeatedly, increasing the depth 
 * limit with each iteration until it reaches d, the depth of the goal state. 
 * That means we need to apply DFS on while increasing depth with every iteration*/

public class IterativeDeepening_Controller extends Controller<MOVE> {
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
	                
	                // For every move, apply DFS iteratively. As with each iteration, max depth(i) will be increased by 1
	                for(int i=1; i<=7;i++){
	                	int tempHighScore = this.dfs(new PacManNode(gameAtM, 0), i);
	                	//based on high score, assign move to move to high move. That is selecting the move.
	                	if(highScore < tempHighScore)
	                	{
	                    highScore = tempHighScore;
	                    highMove = m;
	                	}
	                	System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
	                }
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
	                {
	            	/*get the last node inserted in the stack. This is because depth first search traverses depth wise. 
            		So when we find children of the node on each levels, we put it in the stack. So opposite to breadth first search, now 
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
