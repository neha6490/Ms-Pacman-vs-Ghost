package pacman.controllers.neha_patwardhan;

import java.util.ArrayList;
import java.util.List;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterGhosts;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

public class Evolutionary_Controller extends Controller<MOVE>{

	MOVE highMove;
	int highScore = -1;
	public static StarterGhosts ghosts = new StarterGhosts();
	
	@Override
	public MOVE getMove(Game game, long timeDue)
	{
		MOVE highMove = null;
		int highScore = -1;
		Game gameCopy = game.copy();
		
		Evolution_FitnessChecking ef = new Evolution_FitnessChecking();
		List<ArrayList<MOVE>> parents = ef.initialPopulation();
		ArrayList<MOVE> moves = ef.runExperiment(new Evolutionary_Controller(), new StarterGhosts(), 10, parents);
		
		for(MOVE m : moves)
		{
			gameCopy.advanceGame(m, ghosts.getMove(gameCopy, timeDue));
			int score = gameCopy.getScore();
			if(highScore < score)
			{
				highScore = score;
				highMove = m;
			}
		}
		
		return highMove;
	}
	
	public MOVE getMove(Game game, long timeDue, ArrayList<MOVE> individulas)
	{
		MOVE temp = null;
		int highScore = -1;
		Game gameCopy = game.copy();
		Game gameAtM = gameCopy;
		
			for(MOVE m : individulas)
			{
				gameAtM.advanceGame(m, ghosts.getMove(gameAtM, timeDue));
				int score = gameAtM.getScore();
				if(highScore < score)
	            {
					highScore = score;
	                temp = m;
	            }
				
			}
		
		return temp;
	}

}
