package pacman.controllers.neha_patwardhan;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import pacman.controllers.neha_patwardhan.DFS_Controller;
import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;


public class SimulatedAnnealing_Controller extends Controller<MOVE>
{
	public static StarterGhosts ghosts = new StarterGhosts();
	public static StarterPacMan pacMan = new StarterPacMan();
	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		Random rnd=new Random();
        MOVE[] allMoves=MOVE.values();
    
        int highScore = -1;
        MOVE highMove = null;
        // temperature that will continuously decrease
        double temperature = 1;
        for(MOVE m: allMoves)
	        {
	            //System.out.println("Trying Move: " + m);
	            Game gameCopy = game.copy();
	            Game gameAtM = gameCopy;
	            gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
	            int tempHighScore = this.sa(new PacManNode(gameAtM, 0), 7,temperature);
	            
	            if(highScore < tempHighScore)
	            {
	                highScore = tempHighScore;
	                highMove = m;
	            }
	            
	            System.out.println("Trying Move: " + m + ", Score: " + tempHighScore);
	            // reduce the temperature
	            temperature = temperature * 0.8;  
	        } 
        System.out.println("High Score: " + highScore + ", High Move:" + highMove);
        return highMove;        
	}
	   	
	public int sa(PacManNode rootGameState, int maxdepth,double temperature)
	{
		MOVE[] allMoves=Constants.MOVE.values();
        int depth = 0;
        int highScore = -1;
        
        Queue<PacManNode> queue = new LinkedList<PacManNode>();
        	queue.add(rootGameState);
        
        while(!queue.isEmpty())
        {
        	PacManNode pmnode = queue.remove();
        	if(pmnode.depth >= maxdepth)
            {
                int score = pmnode.gameState.getScore();
                if (highScore < score)
                          highScore = score;
            }
        	else
            {
        		for(MOVE m: allMoves)
                {	
        			int current_value = pmnode.gameState.getScore();
                    Game gameCopy = pmnode.gameState.copy();
                    gameCopy.advanceGame(m, ghosts.getMove(gameCopy, 0));
                    PacManNode next = new PacManNode(gameCopy, pmnode.depth+1);
                    int next_value = next.gameState.getScore();
                    // if Value[current] less than Value[next], select next
                    if(current_value < next_value)
                    {
                    	queue.add(next);
                    }
                    // calculate probability and decide whether to accept 'next' or not
                    else
                    {
                    	double r = 0.5;
                    	double probability = Math.exp((next_value - current_value)/temperature);
                    	if(probability >= r)
                    		queue.add(next);
                    }
                }
            }
        }        
        return highScore;
	}
}