import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

public class LPSolve {

	int Ncol, Ncol2, Ncol3;
	double[] row;
	int[] colno;
	LpSolve lp;

	public static final boolean VERBOSE = true;

	LPSolve() {
	}

	public String createString(int i, int j, int k) {
		return "" + i + j + k;
	}

	public int getIndex(int i, int j, int k) {
		return Ncol2 * (i - 1) + Ncol * (j - 1) + k;
	}

	public void addConstraint(int i, int j, int k) throws LpSolveException {
		int j1;
		int v;
		for(int k2 = 1; k2 <= Ncol; k2++) {
			j1 = 0;
			v = 0;
			if(k2 == k) {
				v = 1;
			}
			colno[j1] = getIndex(i, j, k2);
			row[j1++] = 1;
			lp.addConstraintex(j1, row, colno, LpSolve.EQ, v);
		}
	}
	
	public static void dumpTime(long t) {
		try {
			PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("timeDump.txt", true)));
			w.println("LPSolvev: " + t);
			w.close();
		}
		catch(IOException e) {
		}
	}

	public void dumpToFile() throws LpSolveException {
		try {
			PrintWriter w = new PrintWriter("dump.txt");

			for(int j = 0; j < Ncol3; j++) {
				w.println(lp.getColName(j + 1) + ": " + row[j]);
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
			lp.setLpName("Sudoku");

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

			if(VERBOSE) {
				System.out.println("Setting up variable types.");
			}
			// setup variable types
			loop = 1;
			for(int i = 1; i <= Ncol; i++) {
				for(int j1 = 1; j1 <= Ncol; j1++) {
					for(int k = 1; k <= Ncol; k++) {
						lp.setBinary(loop++, true);
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

			if(VERBOSE) {
				System.out.println("Beginning column constraints.");
			}
			// ---begin column constraints---
			int initialCol = 0; // start at 0 so everything works nicely.
			for(int i = 0; i < Ncol2; i++) {
				j = 0;
				initialCol++;
				int col = initialCol;
				for(int j1 = 0; j1 < Ncol; j1++) {
					colno[j] = col;
					row[j++] = 1;
					col += Ncol2;
				}

				lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
			}
			// ---end column constraints---

			if(VERBOSE) {
				System.out.println("Beginning row constraints.");
			}
			// ---begin row constraints---
			int initialRow = 0;
			for(int k = 0; k < Ncol; k++) {
				for(int i = 0; i < Ncol; i++) {
					j = 0;
					initialRow++;
					int cRow = initialRow;
					for(int j1 = 0; j1 < Ncol; j1++) {
						colno[j] = cRow;
						row[j++] = 1;
						cRow += Ncol;
					}

					lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
				}
				initialRow += Ncol2 - Ncol;
			}
			// ---end row constraints---

			if(VERBOSE) {
				System.out.println("Beginning submatrix constraints.");
			}
			// ---begin submatrix constraints---
			int subMatSize = (int) Math.sqrt(Ncol);
			int subMatPerRow = Ncol / subMatSize;

			for(int initialI = 0; initialI < subMatPerRow; initialI++) {
				for(int initialJ = 0; initialJ < subMatPerRow; initialJ++) {
					for(int k = 1; k <= Ncol; k++) {
						j = 0;
						for(int i = 1 + initialI * subMatSize; i <= subMatSize + initialI * subMatSize; i++) {
							for(int j1 = 1 + initialJ * subMatSize; j1 <= subMatSize + initialJ * subMatSize; j1++) {
								colno[j] = getIndex(i, j1, k);
								row[j++] = 1;
							}
						}
						lp.addConstraintex(j, row, colno, LpSolve.EQ, 1);
					}
				}
			}
			// -- end submatrix constraints---

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
//			int[] cons = new int[9];
//			double[] weights = new double[9];
//			for(int i = 0; i < cons.length; i++) {
//				cons[i] = i + 1;
//				weights[i] = 1;
//			}
//			lp.addSOS("11k", 1, 1, 9, cons, weights);
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
			row[j++] = 0; // value of first colum

			// set the objective function in lpsolve
			lp.setObjFnex(j, row, colno);

			// set the object direction to maximise
			lp.setMaxim();
			// ---end objective function---

			if(VERBOSE) {
				System.out.println("Writing model to file.");
			}
			// write model to file
			lp.writeLp("model.lp");

			// set message type
			lp.setVerbose(LpSolve.IMPORTANT);

			if(VERBOSE) {
				System.out.println("Presolving.");
			}
			lp.setPresolve(LpSolve.PRESOLVE_ROWS | LpSolve.PRESOLVE_LINDEP | LpSolve.PRESOLVE_BOUNDS, lp.getPresolveloops());

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
			System.out.println("Objective value:" + lp.getObjective());
			lp.getVariables(row);
			for(j = 0; j < Ncol3; j++) {
				System.out.println(lp.getColName(j + 1) + ": " + row[j]);
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
			new LPSolve().execute();
		}
		catch(LpSolveException e) {
			e.printStackTrace();
		}
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Total time: " + totalTime);
		dumpTime(totalTime);
	}
}