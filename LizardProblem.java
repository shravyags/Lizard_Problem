import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LizardProblem {
	private static BufferedReader bufferedReader;
	private static int dimension = 0;
	private static String algo;
	private static int totalBabyLizards = 0;
	private static int[][] input = null;
	private static int[][] output = null;
	private static int treeCount = 0;
	private static Map<Integer, List<Integer>> treePositions = new LinkedHashMap<>();
	private static int[][] currentZoo = null;
	private static int[][] zoo = null;
	private static Map<Integer, Position> placedLizardPositions = new LinkedHashMap<>();
	private static Map<Integer, Position> newLizardsPositions = new LinkedHashMap<>();
	private static Random rand = new Random();
	private static double temparature = 15;
	private static double reducingFactor = 0.00001;
	private static double time = 1.1;
	private static List<Position> availablePositions = new ArrayList<Position>();
	private static List<Position> curAvailablePositions = new ArrayList<Position>();
	private static long startTime;
	private static String inputFilePath = "input.txt";
	private static String outputFilePath = "output.txt";

	public static void put(Map<Integer, List<Integer>> map, Integer key, Integer value) {
		if (map.get(key) == null) {
			List<Integer> list = new ArrayList<>();
			list.add(value);
			map.put(key, list);
		} else {
			map.get(key).add(value);
		}
	}

	public static Boolean isSafe(int[][] intermediate, int row, int col) {
		int i, j;

		/* Check if the location contains tree */
		if (intermediate[row][col] == 2) {
			return false;
		}

		/* Check this row on left side */
		for (i = col - 1; i >= 0; i--) {
			if (intermediate[row][i] == 2)
				break;
			else if (intermediate[row][i] == 1)
				return false;
		}

		/* Check this col on top side */
		for (i = row - 1; i >= 0; i--) {
			if (intermediate[i][col] == 2)
				break;
			else if (intermediate[i][col] == 1)
				return false;
		}

		/* Check upper diagonal on left side */
		for (i = row, j = col; i >= 0 && j >= 0; i--, j--) {
			if (intermediate[i][j] == 2)
				break;
			else if (intermediate[i][j] == 1)
				return false;
		}

		/* Check upper diagonal on right side */
		for (i = row, j = col; i >= 0 && j < dimension; i--, j++) {
			if (intermediate[i][j] == 2)
				break;
			else if (intermediate[i][j] == 1)
				return false;
		}
		return true;
	}

	public static class Graph {
		private int[][] intermediate = null;
		private int lizardCount;
		private int lastLevelPlaced;
		private int lastRow;
		private int lastCol;

		Graph() {
		}

		Graph(Graph copyConstructor) {
			this.intermediate = new int[dimension][dimension];
			for (int i = 0; i < dimension; i++)
				for (int j = 0; j < dimension; j++)
					this.intermediate[i][j] = copyConstructor.intermediate[i][j];
			this.lastLevelPlaced = copyConstructor.lastLevelPlaced;
			this.lizardCount = copyConstructor.lizardCount;
			this.lastCol = copyConstructor.lastCol;
			this.lastRow = copyConstructor.lastRow;
		}

		public static void getAllLizards(int[][] output) {
			try {
				List<String> lines = new ArrayList<String>();
				lines.add("OK");
				for (int i = 0; i < dimension; i++) {
					String res = "";
					for (int j = 0; j < dimension; j++) {
						res = res + output[i][j];
					}
					lines.add(res);
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				Path file = Paths.get(outputFilePath);
				Files.write(file, lines, Charset.forName("UTF-8"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		public int getLizardCount() {
			return lizardCount;
		}

		public int getLastRow() {
			return lastRow;
		}

		public void setLastRow(int lastRow) {
			this.lastRow = lastRow;
		}

		public int getLastCol() {
			return lastCol;
		}

		public void setLastCol(int lastCol) {
			this.lastCol = lastCol;
		}

		public int[][] getIntermediate() {
			return intermediate;
		}

		public void setIntermediate(int[][] intermediate) {
			this.intermediate = intermediate;
		}

		public int getLastLevelPlaced() {
			return lastLevelPlaced;
		}

		public void setLastLevelPlaced(int lastLevelPlaced) {
			this.lastLevelPlaced = lastLevelPlaced;
		}

		public void setLizardCount(int lizardCount) {
			this.lizardCount = lizardCount;
		}
	}

	public static class Position {
		private int x;
		private int y;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}
	}

	public static int checkLizardAttacks(Map<Integer, Position> placedLizardPos, int[][] zooPos) {
		int numAttacks = 0;

		for (int lizard = 1; lizard <= totalBabyLizards; lizard++) {
			Position lizPos = placedLizardPos.get(lizard);
			int row = lizPos.getX();
			int col = lizPos.getY();

			// upper left diagonal
			for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
				if (zooPos[i][j] == 2)
					break;
				else if (zooPos[i][j] == 1)
					numAttacks++;
			}

			// upper right diagonal
			for (int i = row - 1, j = col + 1; i >= 0 && j < dimension; i--, j++) {
				if (zooPos[i][j] == 2)
					break;
				else if (zooPos[i][j] == 1)
					numAttacks++;
			}

			// upper vertical
			for (int i = row - 1; i >= 0; i--) {
				if (zooPos[i][col] == 2)
					break;
				else if (zooPos[i][col] == 1)
					numAttacks++;
			}

			/* Check this row on left side */
			for (int i = col - 1; i >= 0; i--) {
				if (zooPos[row][i] == 2)
					break;
				else if (zooPos[row][i] == 1)
					numAttacks++;
			}

			// lower left diagonal
			for (int i = row + 1, j = col - 1; i < dimension && j >= 0; i++, j--) {
				if (zooPos[i][j] == 2)
					break;
				else if (zooPos[i][j] == 1)
					numAttacks++;
			}

			// lower right diagonal
			for (int i = row + 1, j = col + 1; i < dimension && j < dimension; i++, j++) {
				if (zooPos[i][j] == 2)
					break;
				else if (zooPos[i][j] == 1)
					numAttacks++;
			}

			// lower vertically down
			for (int i = row + 1; i < dimension; i++) {
				if (zooPos[i][col] == 2)
					break;
				else if (zooPos[i][col] == 1)
					numAttacks++;
			}

			// horizontal right
			for (int j = col + 1; j < dimension; j++) {
				if (zooPos[row][j] == 2)
					break;
				else if (zooPos[row][j] == 1)
					numAttacks++;
			}
		}
		return numAttacks;
	}

	public static int generateRandLizardPos() {
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				zoo[i][j] = input[i][j];
				currentZoo[i][j] = input[i][j];
			}
		}
		placedLizardPositions = new LinkedHashMap<>();
		newLizardsPositions = new LinkedHashMap<>();
		for (int i = 1; i <= totalBabyLizards; i++) {
			Random r = new Random();
			int availPosKey = r.nextInt(availablePositions.size() - 0) + 0;
			int x = availablePositions.get(availPosKey).getX();
			int y = availablePositions.get(availPosKey).getY();

			Position p = new Position();
			p.x = x;
			p.y = y;
			placedLizardPositions.put(i, p);
			newLizardsPositions.put(i, p);
			zoo[x][y] = 1;
			currentZoo[x][y] = 1;
			availablePositions.remove(availPosKey);
			curAvailablePositions.remove(availPosKey);
		}
		// displayOutput();
		return checkLizardAttacks(placedLizardPositions, zoo);
	}

	public static int generateNeighborSolution() {
		Position p;
		newLizardsPositions = new LinkedHashMap<>();
		for (int i = 1; i <= totalBabyLizards; i++) {
			p = new Position();
			p.setX(placedLizardPositions.get(i).getX());
			p.setY(placedLizardPositions.get(i).getY());
			newLizardsPositions.put(i, p);
		}

		curAvailablePositions = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (zoo[i][j] == 0) {
					Position pos = new Position();
					pos.x = i;
					pos.y = j;
					curAvailablePositions.add(pos);
				}
			}
		}

		int selectedAvailPos = rand.nextInt(curAvailablePositions.size() - 0) + 0;
		int changingLiz = rand.nextInt(totalBabyLizards - 1 + 1) + 1; // .nextInt(totalBabyLizards - 1) + 1;
		for (int i = 0; i < dimension; i++)
			for (int j = 0; j < dimension; j++)
				currentZoo[i][j] = zoo[i][j];

		int oldXPos = newLizardsPositions.get(changingLiz).getX();
		int oldYPos = newLizardsPositions.get(changingLiz).getY();
		int newXPos = curAvailablePositions.get(selectedAvailPos).getX();
		int newYPos = curAvailablePositions.get(selectedAvailPos).getY();

		Position pos = new Position();
		pos.setX(newXPos);
		pos.setY(newYPos);
		newLizardsPositions.put(changingLiz, pos);
		currentZoo[newXPos][newYPos] = 1;
		currentZoo[oldXPos][oldYPos] = 0;
		curAvailablePositions.remove(selectedAvailPos);
		Position oldPos = new Position();
		oldPos.setX(oldXPos);
		oldPos.setY(oldYPos);
		curAvailablePositions.add(oldPos);
		// displayOutput();
		return checkLizardAttacks(newLizardsPositions, currentZoo);
	}

	public static void acceptNeighbour() {
		availablePositions = new ArrayList<>();
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (currentZoo[i][j] == 0) {
					Position pos = new Position();
					pos.setX(i);
					pos.setY(j);
					availablePositions.add(pos);
				}
				zoo[i][j] = currentZoo[i][j];
			}
		}
		Position p;
		for (int i = 1; i <= totalBabyLizards; i++) {
			p = new Position();
			int x = newLizardsPositions.get(i).getX();
			int y = newLizardsPositions.get(i).getY();
			p.setX(x);
			p.setY(y);
			placedLizardPositions.put(i, p);
		}
	}

	public static Boolean acceptNeighbourWithProb(int curNoOfAttacks, int prevNoOfAttacks) {
		int deltaEnergy = curNoOfAttacks - prevNoOfAttacks;
		double T = 1 / (Math.log(System.currentTimeMillis() - startTime));
//		double T = 5 / (Math.log(time));
		double probability = Math.exp(-deltaEnergy / T);
		double randProbability = rand.nextDouble();
		if (randProbability < probability) {
			return true;
		}
		return false;
	}

	public static void displayOutput() {
		try {
			List<String> lines = new ArrayList<String>();
			lines.add("OK");
			for (int i = 0; i < dimension; i++) {
				String res = "";
				for (int j = 0; j < dimension; j++) {
					res = res + zoo[i][j];
				}
				lines.add(res);
			}
			Path file = Paths.get(outputFilePath);
			Files.write(file, lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		// The name of the file to open.
		String fileName = inputFilePath;
		String line = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			bufferedReader = new BufferedReader(fileReader);
			int lineNo = 0;

			while ((line = bufferedReader.readLine()) != null) {
				switch (lineNo) {
				case 0: {
					algo = line.trim();
					// System.out.println(line);
					break;
				}
				case 1: {
					dimension = Integer.parseInt(line.trim());
					// System.out.println(line);
					input = new int[dimension][dimension];
					output = new int[dimension][dimension];
					zoo = new int[dimension][dimension];
					currentZoo = new int[dimension][dimension];
					break;
				}
				case 2: {
					totalBabyLizards = Integer.parseInt(line.trim());
					// System.out.println(line);
					break;
				}
				default: {
					String nodes = line;
					int rowNo = lineNo - 3;
					for (int i = 0; i < nodes.trim().length(); i++) {
						input[rowNo][i] = Character.getNumericValue(nodes.trim().charAt(i));
						zoo[rowNo][i] = Character.getNumericValue(nodes.trim().charAt(i));
						if (input[rowNo][i] == 2) {
							put(treePositions, rowNo, i);
							treeCount++;
						} else {
							Position p = new Position();
							p.setX(rowNo);
							p.setY(i);
							availablePositions.add(p);
							curAvailablePositions.add(p);
						}
					}
				}
				}
				lineNo++;
			}

			if (totalBabyLizards == 0) {
				displayOutput();
				return;
			}
			
			if (treeCount == 0 && (totalBabyLizards > dimension)) {
				List<String> lines = new ArrayList<String>();
				lines.add("FAIL");
				Path file = Paths.get(outputFilePath);
				Files.write(file, lines, Charset.forName("UTF-8"));
				return;
			}

			if ("BFS".equalsIgnoreCase(algo) || "DFS".equalsIgnoreCase(algo)) {
				Graph g;
				LinkedList<Graph> queue = new LinkedList<Graph>();

				// ading root node to queue
				g = new Graph();
				g.setLizardCount(0);
				g.setLastLevelPlaced(0);
				g.setLastRow(0);
				g.setLastCol(-1);
				int[][] adjMatrix = new int[dimension][dimension];
				for (int i = 0; i < dimension; i++) {
					for (int j = 0; j < dimension; j++) {
						adjMatrix[i][j] = input[i][j];
					}
				}
				g.setIntermediate(adjMatrix);

				queue.add(g);
				long endTime = System.currentTimeMillis() + 295000;
				while (!queue.isEmpty()) {
					if (System.currentTimeMillis() > endTime) {
						List<String> lines = new ArrayList<String>();
						lines.add("FAIL");
						Path file = Paths.get(outputFilePath);
						Files.write(file, lines, Charset.forName("UTF-8"));
						return;
					}
					Graph parent = new Graph();
					if ("BFS".equalsIgnoreCase(algo)) {
						// System.out.println("BFS");
						parent = queue.removeFirst();
					} else if ("DFS".equalsIgnoreCase(algo)) {
						// System.out.println("DFS");
						parent = queue.removeLast();
					}
					int prevLevel = parent.getLastLevelPlaced();
					if (prevLevel < dimension) {
						Graph child;
						Boolean isLizrdPlaced = false;

						for (int j = parent.lastCol + 1; j < dimension; j++) {
							child = new Graph(parent);
							if (isSafe(child.intermediate, prevLevel, j)) {
								child.intermediate[prevLevel][j] = 1;
								child.setLastCol(j);
								child.setLastRow(prevLevel);
								child.setLastLevelPlaced(prevLevel);
								child.setLizardCount(child.getLizardCount() + 1);

								if (child.getLizardCount() == totalBabyLizards) {
									child.getAllLizards(child.intermediate);
									return;
								}
								queue.add(child);
								isLizrdPlaced = true;
							}
						}

						if (!isLizrdPlaced && ((prevLevel + 1) < dimension)) {
							for (int j = 0; j < dimension; j++) {
								child = new Graph(parent);
								if (isSafe(child.intermediate, prevLevel + 1, j)) {
									child.intermediate[prevLevel + 1][j] = 1;
									child.setLastCol(j);
									child.setLastRow(prevLevel + 1);
									child.setLastLevelPlaced(prevLevel + 1);
									child.setLizardCount(child.getLizardCount() + 1);
									if (child.getLizardCount() == totalBabyLizards) {
										child.getAllLizards(child.intermediate);
										return;
									}
									queue.add(child);
									isLizrdPlaced = true;
								}
							}
						}

						if (treeCount > 0) {
							child = new Graph(parent);
							child.setLastCol(-1);
							child.setLastRow(parent.getLastRow() + 1);
							child.setLastLevelPlaced(prevLevel + 1);
							queue.add(child);
						}
					}
				}
				List<String> lines = new ArrayList<String>();
				lines.add("FAIL");
				Path file = Paths.get(outputFilePath);
				Files.write(file, lines, Charset.forName("UTF-8"));
				return;
			} else if ("SA".equalsIgnoreCase(algo)) {
				startTime = System.currentTimeMillis();
				long endTime = System.currentTimeMillis() + 280000;
				if (availablePositions.size() < totalBabyLizards) {
					// System.out.println("FAIL");
					List<String> lines = new ArrayList<String>();
					lines.add("FAIL");
					Path file = Paths.get(outputFilePath);
					Files.write(file, lines, Charset.forName("UTF-8"));
					return;
				}
				int prevNoOfAttacks = generateRandLizardPos();
				if (availablePositions.size() == 0 && prevNoOfAttacks > 1) {
					List<String> lines = new ArrayList<String>();
					lines.add("FAIL");
					Path file = Paths.get(outputFilePath);
					Files.write(file, lines, Charset.forName("UTF-8"));
					return;
				}
				if (prevNoOfAttacks == 0) {
					displayOutput();
					return;
				}
				int curNoOfAttacks;

				while (System.currentTimeMillis() < endTime) {
					prevNoOfAttacks = checkLizardAttacks(placedLizardPositions, zoo);
					curNoOfAttacks = generateNeighborSolution();
					if (curNoOfAttacks == 0) {
						acceptNeighbour();
						displayOutput();
						return;
					} else if (curNoOfAttacks < prevNoOfAttacks) {
						acceptNeighbour();
					} else if (acceptNeighbourWithProb(curNoOfAttacks, prevNoOfAttacks)) {
						acceptNeighbour();
					}
					time = time + 0.01;
				}
				List<String> lines = new ArrayList<String>();
				lines.add("FAIL");
				Path file = Paths.get(outputFilePath);
				Files.write(file, lines, Charset.forName("UTF-8"));
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			// System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			// System.out.println("Error reading file '" + fileName + "'");
		}
	}
}
