import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class LPSolvev2 {

	int Ncol, Ncol2, Ncol3;
	double[] row;
	int[] colno;
	LpSolve lp;

	public static final boolean VERBOSE = true;
	public static final boolean OUTPUT = true;

	LPSolvev2() {
	}

	public String createString(int i, int j, int k) {
		return "" + i + j + k;
	}

	public int getIndex(int i, int j, int k) {
		return Ncol2 * (i - 1) + Ncol * (j - 1) + k;
	}

	// public void addConstraint(int i, int j, int k) throws LpSolveException {
	// int j1;
	// int v;
	// int type;
	// for(int k2 = 1; k2 <= Ncol; k2++) {
	// j1 = 0;
	// v = 0;
	// type = LpSolve.LE;
	// if(k2 == k) {
	// v = 1;
	// type = LpSolve.GE;
	// }
	// colno[j1] = getIndex(i, j, k2);
	// row[j1++] = 1;
	// lp.addConstraintex(j1, row, colno, type, v);
	// }
	// }

	public void addConstraint(int i, int j, int k) throws LpSolveException {
		double low, up;
		for(int k2 = 1; k2 <= Ncol; k2++) {
			low = 0;
			up = 0;
			if(k2 == k) {
				low = 1;
				up = lp.getInfinite();
				// up = 1; // I'd like to be able to do this.
			}
			lp.setBounds(getIndex(i, j, k2), low, up);
		}
	}

	public static void dumpTime(long t) {
		try {
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("timeDump.txt", true)));
			w.println("LPSolvev2: " + t);
			w.close();
		}
		catch(IOException e) {
		}
	}

	public void dumpToFile() throws LpSolveException {
		try {
			PrintWriter w = new PrintWriter("dump2.txt");

			for(int j = 0; j < Ncol3; j++) {
				w.println(lp.getOrigcolName(j + 1) + ": " + row[j]);
			}

			w.close();
		}
		catch(FileNotFoundException e) {
		}
	}

	public int execute() throws LpSolveException {
		int j = 0, ret = 0;

		Ncol = 9; // number of columns
		Ncol2 = Ncol * Ncol;
		Ncol3 = Ncol2 * Ncol;

		colno = new int[Ncol * Ncol * Ncol];
		row = new double[Ncol * Ncol * Ncol];

		lp = LpSolve.makeLp(0, Ncol * Ncol * Ncol);
		if(lp.getLp() == 0) {
			ret = 1; // couldn't construct a model
		}

		if(ret == 0) {
			if(VERBOSE) {
				System.out.println("Setting up model name.");
			}
			// setup model name
			lp.setLpName("SudokuV2");

			if(VERBOSE) {
				System.out.println("Setting up variable names.");
			}
			// setup variable names
			int loop = 1;
			for(int i = 1; i <= Ncol; i++) {
				for(int j1 = 1; j1 <= Ncol; j1++) {
					for(int k = 1; k <= Ncol; k++) {
						lp.setColName(loop++, "x" + createString(i, j1, k));
					}
				}
			}
		}

		if(VERBOSE) {
			System.out.println("Setting build mode.");
		}
		// setup build mode
		lp.setAddRowmode(true);

		if(ret == 0) {

			int[] cons = new int[9];
			double[] weights = new double[9];

			for(int i = 0; i < weights.length; i++) {
				weights[i] = 1;
			}

			if(VERBOSE) {
				System.out.println("Adding predefined constraints.");
			}

			// Predefined constraints corresponds to the partially filled in sudoku:
			// ===begin===
			// - - - - - - - 2 -
			// - 2 - - - - 5 - -
			// - - 7 - - 3 4 - -
			// 2 - - 1 - - 3 4 -
			// 6 4 - - 8 - - 5 9
			// - 9 5 - - 2 - - 1
			// - - 3 4 - - 8 - -
			// - - 9 - - - - 1 -
			// - 1 - - - - - - -
			// ===end===

			// ---begin predefined values---
			addConstraint(1, 8, 2);
			addConstraint(2, 2, 2);
			addConstraint(2, 7, 5);
			addConstraint(3, 3, 7);
			addConstraint(3, 6, 3);
			addConstraint(3, 7, 4);
			addConstraint(4, 1, 2);
			addConstraint(4, 4, 1);
			addConstraint(4, 7, 3);
			addConstraint(4, 8, 4);
			addConstraint(5, 1, 6);
			addConstraint(5, 2, 4);
			addConstraint(5, 5, 8);
			addConstraint(5, 8, 5);
			addConstraint(5, 9, 9);
			addConstraint(6, 2, 9);
			addConstraint(6, 3, 5);
			addConstraint(6, 6, 2);
			addConstraint(6, 9, 1);
			addConstraint(7, 3, 3);
			addConstraint(7, 4, 4);
			addConstraint(7, 7, 8);
			addConstraint(8, 3, 9);
			addConstraint(8, 8, 1);
			addConstraint(9, 2, 1);
			// --- end predefined values---

			// // constraints corresponds to the partially filled in sudoku:
			// // ===begin===
			// // - - - - - - - 1 -
			// // 4 - - - - - - - -
			// // - 2 - - - - - - -
			// // - - - - 5 - 4 - 7
			// // - - 8 - - - 3 - -
			// // - - 1 - 9 - - - -
			// // 3 - - 4 - - 2 - -
			// // - 5 - 1 - - - - -
			// // - - - 8 - 6 - - -
			// // ===end===
			//
			// // ---begin predefined values---
			// addConstraint(1, 8, 1);
			// addConstraint(2, 1, 4);
			// addConstraint(3, 2, 2);
			// addConstraint(4, 5, 5);
			// addConstraint(4, 7, 4);
			// addConstraint(4, 9, 7);
			// addConstraint(5, 3, 8);
			// addConstraint(5, 7, 3);
			// addConstraint(6, 3, 1);
			// addConstraint(6, 5, 9);
			// addConstraint(7, 1, 3);
			// addConstraint(7, 4, 4);
			// addConstraint(7, 7, 2);
			// addConstraint(8, 2, 5);
			// addConstraint(8, 4, 1);
			// addConstraint(9, 4, 8);
			// addConstraint(9, 6, 6);
			// // --- end predefined values---

			if(VERBOSE) {
				System.out.println("Beginning position filled constraints.");
			}
			// ---begin position filled constraints---
			for(int i = 0; i < Ncol2; i++) {
				j = 0;
				for(int j1 = 0; j1 < Ncol; j1++) {
					colno[j] = j + 1 + i * Ncol;
					row[j++] = 1;
				}
				lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
			}
			// ---end position filled constraints---

			if(VERBOSE) {
				System.out.println("Beginning SOS constraints.");
			}
			// ---begin SOS constraints---

			// ---begin only one number per square---
			for(int i = 0; i < cons.length; i++) {
				for(int j1 = 0; j1 < cons.length; j1++) {
					for(int k = 0; k < cons.length; k++) {
						cons[k] = getIndex(i + 1, j1 + 1, k + 1);
					}
					lp.addSOS("x" + Integer.toString(i + 1) + Integer.toString(j1 + 1) + "k", 1, 1, cons.length, cons, weights);
				}
			}
			// ---end only one number per square && square filled---

			// ---begin only one number per row---
			for(int i = 0; i < cons.length; i++) {
				for(int k = 0; k < cons.length; k++) {
					for(int j1 = 0; j1 < cons.length; j1++) {
						cons[j1] = getIndex(i + 1, j1 + 1, k + 1);
					}
					lp.addSOS("x" + Integer.toString(i + 1) + "j" + Integer.toString(k + 1), 1, 1, cons.length, cons, weights);
				}
			}
			// ---end only one number per row---

			// ---begin only one number per column---
			for(int j1 = 0; j1 < cons.length; j1++) {
				for(int k = 0; k < cons.length; k++) {
					for(int i = 0; i < cons.length; i++) {
						cons[i] = getIndex(i + 1, j1 + 1, k + 1);
					}
					lp.addSOS("xi" + Integer.toString(j1 + 1) + Integer.toString(k + 1), 1, 1, cons.length, cons, weights);
				}
			}
			// ---end only one number per column---

			// ---begin only one number per submatrix---
			int m = (int) Math.sqrt(Ncol);
			for(int k = 1; k <= cons.length; k++) {
				for(int q = 1; q <= m; q++) {
					for(int p = 1; p <= m; p++) {
						for(int j1 = m * q - m + 1; j1 <= m * q; j1++) {
							for(int i = m * p - m + 1; i <= m * p; i++) {
								cons[(m * (j1 - 1) + (i - 1)) % 9] = getIndex(i, j1, k);
							}
						}
						lp.addSOS("xij" + Integer.toString(k), 1, 1, cons.length, cons, weights);
					}
				}
			}
			// ---end only one number per submatrix---

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
			lp.writeLp("model2.lp");

			// set message type
			// lp.setVerbose(LpSolve.NORMAL);
			lp.setVerbose(LpSolve.IMPORTANT);

			if(VERBOSE) {
				System.out.println("Presolving.");
			}
			lp.setPresolve(LpSolve.PRESOLVE_ROWS | LpSolve.PRESOLVE_BOUNDS, lp.getPresolveloops());

			if(VERBOSE) {
				System.out.println("Starting to solve the model. This may take a while.");
			}
			// ---begin solving---
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
				System.out.println(row.length);
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
			new LPSolvev2().execute();
		}
		catch(LpSolveException e) {
			e.printStackTrace();
		}
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Total time: " + totalTime);
		dumpTime(totalTime);
	}
}
