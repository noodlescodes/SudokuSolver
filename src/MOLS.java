import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class MOLS {

	int Ncol, Ncol2, Ncol3, Nsquares;
	double[] row;
	int[] colno;
	LpSolve lp;

	public static final boolean VERBOSE = true;
	public static final boolean OUTPUT = true;

	MOLS() {
	}

	public String createString(int i, int j, int k) {
		return "" + i + j + k;
	}

	public int getIndex(int s, int i, int j, int k) {
		return Ncol3 * (s - 1) + Ncol2 * (i - 1) + Ncol * (j - 1) + k;
	}

	public void addBounds(int s, int i, int j, int k) throws LpSolveException {
		double low, up;
		for(int k2 = 1; k2 <= Ncol; k2++) {
			low = 0;
			up = 0;
			if(k2 == k) {
				low = 1;
				up = lp.getInfinite();
				// up = 1; // I'd like to be able to do this.
			}
			lp.setBounds(getIndex(s, i, j, k2), low, up);
		}
	}

	public static void dumpTime(long t) {
		try {
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("timeDumpMOLS.txt", true)));
			w.println("MOLS: " + t);
			w.close();
		}
		catch(IOException e) {
		}
	}

	public void dumpToFile() throws LpSolveException {
		try {
			PrintWriter w = new PrintWriter("dumpMOLS.txt");

			for(int j = 0; j < Ncol3 * Nsquares; j++) {
				w.println(lp.getOrigcolName(j + 1) + ": " + row[j]);
			}

			w.close();
		}
		catch(FileNotFoundException e) {
		}
	}

	public void addOrthogonalityConstraints(int a, int b) throws LpSolveException {
		int j1 = 0;

		for(int m = 1; m <= Ncol; m++) {
			for(int k = 1; k <= Ncol; k++) {
				for(int i = 1; i <= Ncol; i++) {
					for(int j = 1; j <= Ncol; j++) {
						for(int i0 = i; i0 <= Ncol; i0++) {
							for(int j0 = 1; j0 <= Ncol; j0++) {
								if(i != i0 && j != j0) {
									colno[j1] = getIndex(a, i, j, k);
									row[j1++] = 1;
									colno[j1] = getIndex(b, i, j, m);
									row[j1++] = 1;
									colno[j1] = getIndex(a, i0, j0, k);
									row[j1++] = 1;
									colno[j1] = getIndex(b, i0, j0, m);
									row[j1++] = 1;
									lp.addConstraintex(j1, row, colno, LpSolve.LE, 3);
									j1 = 0;
								}
							}
						}
					}
				}
			}
		}
	}

	public int execute() throws LpSolveException {
		int ret = 0;
		int j = 0;
		String[] prefixes = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m"};

		Ncol = 5; // Order of Latin Squares
		Ncol2 = Ncol * Ncol;
		Ncol3 = Ncol2 * Ncol;
		Nsquares = 3; // Number of MOLS looking for
		int[] cons = new int[Ncol];

		colno = new int[Ncol3 * Nsquares];
		row = new double[Ncol3 * Nsquares];

		lp = LpSolve.makeLp(0, Ncol * Ncol * Ncol);
		if(lp.getLp() == 0) {
			ret = 1; // couldn't construct a model
		}

		if(ret == 0) {
			if(VERBOSE) {
				System.out.println("Setting up model name.");
			}
			// setup model name
			lp.setLpName("MOLS");

			if(VERBOSE) {
				System.out.println("Setting up variable names.");
			}
			// setup variable names
			int loop = 1;
			for(int squaresCompleted = 0; squaresCompleted < Nsquares; squaresCompleted++) {
				for(int i = 1; i <= Ncol; i++) {
					for(int j1 = 1; j1 <= Ncol; j1++) {
						for(int k = 1; k <= Ncol; k++) {
							lp.setColName(loop++, prefixes[squaresCompleted] + createString(i, j1, k));
						}
					}
				}
			}
		}

		if(ret == 0) {
			double[] weights = new double[9];

			for(int i = 0; i < weights.length; i++) {
				weights[i] = 1;
			}

			if(VERBOSE) {
				System.out.println("Setting build mode.");
			}
			// setup build mode
			lp.setAddRowmode(true);

			if(VERBOSE) {
				System.out.println("Adding predefined constraints.");
			}
			// These still have to be input manually because I can't be bothered currently to figure out the algorithm I need.
			// When I do figure out the algorithm it'll add more bounds than what are currently added as well, which should
			// improve computation time as well.
			// ---begin predefined values---
			addBounds(1, 1, 1, 1);
			addBounds(1, 1, 2, 2);
			addBounds(1, 1, 3, 3);
			addBounds(1, 1, 4, 4);
			addBounds(1, 1, 5, 5);
//			addBounds(1, 1, 6, 6);
//			addBounds(1, 1, 7, 7);
//			addBounds(1, 1, 8, 8);
			addBounds(1, 2, 1, 2);
			addBounds(1, 3, 1, 3);
			addBounds(1, 4, 1, 4);
			addBounds(1, 5, 1, 5);
//			addBounds(1, 6, 1, 6);
//			addBounds(1, 7, 1, 7);
//			addBounds(1, 8, 1, 8);
			addBounds(2, 1, 1, 1);
			addBounds(2, 1, 2, 2);
			addBounds(2, 1, 3, 3);
			addBounds(2, 1, 4, 4);
			addBounds(2, 1, 5, 5);
//			addBounds(2, 1, 6, 6);
//			addBounds(2, 1, 7, 7);
//			addBounds(2, 1, 8, 8);
			addBounds(3, 1, 1, 1);
			addBounds(3, 1, 2, 2);
			addBounds(3, 1, 3, 3);
			addBounds(3, 1, 4, 4);
			addBounds(3, 1, 5, 5);
//			addBounds(3, 1, 6, 6);
//			addBounds(3, 1, 7, 7);
//			addBounds(3, 1, 8, 8);
			// --- end predefined values---

			if(VERBOSE) {
				System.out.println("Beginning position filled constraints.");
			}
			// ---begin position filled constraints---
			for(int squaresCompleted = 1; squaresCompleted <= Nsquares; squaresCompleted++) {
				for(int i = 1; i <= Ncol; i++) {
					for(int j1 = 1; j1 <= Ncol; j1++) {
						for(int k = 1; k <= Ncol; k++) {
							colno[j] = getIndex(squaresCompleted, i, j1, k);
							row[j++] = 1;
						}
						lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
						j = 0;
					}
				}
			}
			// ---end position filled constraints---

			if(VERBOSE) {
				System.out.println("Beginning SOS constraints.");
			}
			// ---begin SOS constraints---

			// ---begin only one number per square---
			for(int squaresCompleted = 0; squaresCompleted < Nsquares; squaresCompleted++) {
				for(int i = 0; i < cons.length; i++) {
					for(int j1 = 0; j1 < cons.length; j1++) {
						for(int k = 0; k < cons.length; k++) {
							cons[k] = getIndex(1 + squaresCompleted, i + 1, j1 + 1, k + 1);
						}
						lp.addSOS(prefixes[squaresCompleted] + Integer.toString(i + 1) + Integer.toString(j1 + 1) + "k", 1, 1, cons.length, cons, weights);
					}
				}
			}
			// ---end only one number per square---

			// ---begin only one number per row---
			for(int squaresCompleted = 0; squaresCompleted < Nsquares; squaresCompleted++) {
				for(int i = 0; i < cons.length; i++) {
					for(int k = 0; k < cons.length; k++) {
						for(int j1 = 0; j1 < cons.length; j1++) {
							cons[j1] = getIndex(1 + squaresCompleted, i + 1, j1 + 1, k + 1);
						}
						lp.addSOS(prefixes[squaresCompleted] + Integer.toString(i + 1) + "j" + Integer.toString(k + 1), 1, 1, cons.length, cons, weights);
					}
				}
			}
			// ---end only one number per row---

			// ---begin only one number per column---
			for(int squaresCompleted = 0; squaresCompleted < Nsquares; squaresCompleted++) {
				for(int j1 = 0; j1 < cons.length; j1++) {
					for(int k = 0; k < cons.length; k++) {
						for(int i = 0; i < cons.length; i++) {
							cons[i] = getIndex(1 + squaresCompleted, i + 1, j1 + 1, k + 1);
						}
						lp.addSOS(prefixes[squaresCompleted] + "i" + Integer.toString(j1 + 1) + Integer.toString(k + 1), 1, 1, cons.length, cons, weights);
					}
				}
			}
			// ---end only one number per column---

			// ---begin every orthogonality---
			for(int squaresCompleted = 1; squaresCompleted <= Nsquares; squaresCompleted++) {
				for(int orthogonalSquare = squaresCompleted + 1; orthogonalSquare <= Nsquares; orthogonalSquare++) {
					addOrthogonalityConstraints(squaresCompleted, orthogonalSquare);
				}
			}
			// ---end every orthogonality---

			// ---end SOS constraints---

			if(VERBOSE) {
				System.out.println("Turning off build mode.");
			}
			// turn off build mode
			lp.setAddRowmode(false);

			if(VERBOSE) {
				System.out.println("Beginning objective function.");
			}
			// ---begin objective function---
			j = 0;

			colno[j] = 1; // first column
			row[j++] = 0; // value of first column

			// set the objective function in lpsolve
			lp.setObjFnex(j, row, colno);

			// set the object direction to maximise
			lp.setMaxim();
			// ---end objective function---

			if(VERBOSE) {
				System.out.println("Writing model to file.");
			}
			// write model to file
			lp.writeLp("modelMOLS.lp");

			// set message type
			lp.setVerbose(LpSolve.NORMAL);
			// lp.setVerbose(LpSolve.IMPORTANT);

			// ---begin solving---
			if(VERBOSE) {
				System.out.println("Presolving.");
			}
			lp.setPresolve(LpSolve.PRESOLVE_BOUNDS, lp.getPresolveloops());

			if(VERBOSE) {
				System.out.println("Starting to solve the model. This may take a while.");
			}

			ret = lp.solve();
			if(ret == LpSolve.OPTIMAL) {
				ret = 0;
			}
			else {
				ret = 5;
			}
			// ---end solving---
		}

		if(VERBOSE) {
			System.out.println("Beginning output.");
		}
		// ---begin output---
		if(ret == 0) {
			lp.getVariables(row);
			// row = lp.getPtrVariables();
			if(OUTPUT) {
				for(j = 1; j <= 1 + row.length; j++) {
					// System.out.println(lp.getColName(j) + ": " + lp.getNameindex(lp.getColName(j), false));
					// System.out.println(lp.getColName(j));
					// int index = lp.getNameindex(lp.getColName(j), false);
					// System.out.println(lp.getOrigcolName(index) + ": " + lp.getVarPrimalresult(index));
				}
			}
			dumpToFile();
		}
		else {
			System.out.println("No solution found.");
			lp.deleteLp();
		}
		// -- end output---

		return ret;
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		String date = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(startTime));
		System.out.println("Start time: " + date);
		try {
			new MOLS().execute();
		}
		catch(LpSolveException e) {
			e.printStackTrace();
		}
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Total time: " + totalTime);
		dumpTime(totalTime);
	}
}
