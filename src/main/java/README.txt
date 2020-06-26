CodeForStudents: 
contains the visualizer and correctness checker for the usage of the students as source code and jar files.
All necessary commands to execute the jar files are stored in the "Jars" directory as text file.



PackingProblem: 
contains our created object structures and the solver algorithms. 
In the class "TournamentSampleTests" all problems from the tournament suite can be calculated. 
Be careful to halve the container size for the pentomino problems (see multiplicationConstant in the "TournamentSampleTests" class)
because we assumed that the pentominoes consist of 5 0.5x0.5x0.5 atomic cubes like in the original project description. 
To make coding easier for the students, we changed the size of the atomic cubes to 1x1x1 for the tournament. 
So it is important that a multiplicationConstant of 0.5 is multiplied before setting the container size for the pentomino problem (see multiplicationConstant in the "TournamentSampleTests" class). 
For the parcel problems this multiplicationConstant is not necessary because both the size of the container and the sizes of the parcels are adjusted internally.



TournamentSuite: 
contains all necessary components to run the tournament automatically. 



	-Code: 
	contains all the code that was developed to create the TournamentSuite.



	-Executables: 
	insert here the jar files of the students. Note that the students' code must have a specific input and output format.



	-Input: 
	contains a separate input file for each tournament problem.

	

	-Master: 
	contains a separate file for each tournament problem. In this file the weight and packing value found by our algorithms for the specific problem is stored.



	-Output: 
	this is where all files are stored when you run the "tournament_script". Please delete all files before running the script again.



	-Script: 
	contains the main batch script "tournament_script" which automatically creates all the necessary output files and then performs the ranking.
	We have successfully tested the script for Windows 10. To run the script on a Linux system the "TIMEOUT" and the "TASKKILL" commands have to be adapted. 
	The "TournamentRanking" jar file is executed by the "tournament_script" to perform the ranking of the students' code.
