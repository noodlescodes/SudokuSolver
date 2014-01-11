Note: There is an error in LPSolve.java. This program is now superseded by LPSolve2.java anyway, and as a result, the bug won't be fixed.

If I get around to it, I plan to add a method to parse in the predefined constraints from a file so it is less annoying to enter a new sudoku, not my top priority though.

In LPSolve2.java, there is a commented out sudoku that has 17 initial constraints. It is considered computationally hard to solve due to the lack of initial conditions. This can be solved in just over an hour on moderate hardware with LPSolve2.java. Feel free to race it!

A program that can be used to solve Sudoku's of size 9x9.

Depends on lp_solve. [Found here](http://lpsolve.sourceforge.net/5.5/).

Initially based on [this](http://langvillea.people.cofc.edu/sudoku5.pdf) paper by A. C. Bartlett, et al.
