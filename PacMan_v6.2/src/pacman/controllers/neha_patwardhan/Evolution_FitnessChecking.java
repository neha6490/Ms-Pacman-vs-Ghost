package pacman.controllers.neha_patwardhan;

import static pacman.game.Constants.DELAY;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Random;

import pacman.Executor;
import pacman.controllers.Controller;
import pacman.game.Game;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;

public class Evolution_FitnessChecking extends Executor {

	public static List<ArrayList<MOVE>> parents = new ArrayList<ArrayList<MOVE>>();

	public static List<ArrayList<MOVE>> initialPopulation() {
		List<MOVE> initialPopulation1 = new ArrayList<MOVE>();
		List<MOVE> initialPopulation2 = new ArrayList<MOVE>();
		List<MOVE> initialPopulation3 = new ArrayList<MOVE>();
		List<MOVE> initialPopulation4 = new ArrayList<MOVE>();
		List<ArrayList<MOVE>> parents = new ArrayList<ArrayList<MOVE>>();

		for (int i = 0; i < 50; i++) {
			int max = 5;

			int ip1 = (int) (Math.random() * max);
			initialPopulation1.add(MOVE.valueOf(ip1));

			int ip2 = (int) (Math.random() * max);
			initialPopulation2.add(MOVE.valueOf(ip2));

			int ip3 = (int) (Math.random() * max);
			initialPopulation3.add(MOVE.valueOf(ip3));

			int ip4 = (int) (Math.random() * max);
			initialPopulation4.add(MOVE.valueOf(ip4));
		}

		parents.add((ArrayList<MOVE>) initialPopulation1);
		parents.add((ArrayList<MOVE>) initialPopulation2);
		parents.add((ArrayList<MOVE>) initialPopulation3);
		parents.add((ArrayList<MOVE>) initialPopulation4);

		return parents;
	}

	public ArrayList<MOVE> runExperiment(Controller<MOVE> Evolutionary_Contoller,
			Controller<EnumMap<GHOST, MOVE>> ghostController, int trials,
			List<ArrayList<MOVE>> parents) {
		int tempScore = 0;
		int highScore = 0;
		Random rnd = new Random(0);
		Game game;
		int counter = 0;

		game = new Game(rnd.nextLong());
		for (int j = 0; j <= 3; j++) {
			ArrayList<MOVE> ip = parents.get(j);
			for (MOVE m : ip) {
				Game gameAtM = game.copy();
				gameAtM.advanceGame(
						m,
						ghostController.getMove(gameAtM,
								System.currentTimeMillis()));
				tempScore = tempScore + gameAtM.getScore();
			}
			if (highScore < tempScore) {
				highScore = tempScore;
				counter = j;
			}
		}

		System.out.println(parents.get(counter));
		ArrayList<MOVE> p = mutation(parents.get(counter));
		return p;

	}

	public static ArrayList<MOVE> mutation(ArrayList<MOVE> selected) {
		for (int i = 0; i < 50; i++) {
			if (i >= 0 && i < 40)
				selected.add(i, selected.get(i));

			if (i >= 40 && i + 1 < 50) {
				MOVE temp = selected.get(i);
				selected.add(i, selected.get(i + 1));
				selected.add(i + 1, temp);
			}
		}

		return selected;
	}
}
