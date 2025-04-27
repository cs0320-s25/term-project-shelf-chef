README
> **GETTING STARTED:** You must start from some combination of the CSV Sprint code that you and your partner ended up with. Please move your code directly into this repository so that the `pom.xml`, `/src` folder, etc, are all at this base directory.

> **IMPORTANT NOTE**: In order to run the server, run `mvn package` in your terminal then `./run` (using Git Bash for Windows users). This will be the same as the first Sprint. Take notice when transferring this run sprint to your Sprint 2 implementation that the path of your Server class matches the path specified in the run script. Currently, it is set to execute Server at `edu/brown/cs/student/main/server/Server`. Running through terminal will save a lot of computer resources (IntelliJ is pretty intensive!) in future sprints.

Estimated Time: 10 Hours
GitHub Link: https://github.com/cs0320-s25/server-ky-sim.git


# Project Details
This project is a server application. It offers endpoints to load and analyze CSV datasets and 
retrieve broadband statistics for specific regions. The API supports basic operations such as 
loading CSV files, viewing their contents, searching by column or value, and retrieving mocked 
broadband percentage data for a given state and county. Built with defensive programming principles 
and robust error handling, the server ensures reliable responses in JSON format while maintaining 
security by limiting file access. This project currently uses mocked data for testing and 
demonstration purposes, with future plans to integrate real data sources like CSV files and 
external APIs.


# Design Choices
From a high level the project is structured into 4 main packages. Below are there descriptions
- CreatedObject: contains the classes of the possible objects a creator can create
- Creator: contains the CreatorFromRow interface and all the creator classes that implement it
- Exceptions: contains all unique exceptions
- Parser: contains the parser class
- Searcher: contains CSVSearcher class
- REPL: contains the command processor
- JSONParser: contains the DataSource object that handles, loading and searching the ACS json data


# Errors/Bugs
N/A

# Tests
	⁃	Test a successful CSV load that loads a file that exists in the “data” directory
	⁃	Test a failed CSV load that loads a file that does not exist
	⁃	Test a failed CSV load that loads a file that exists outside of the data directory
	⁃	Test a failed CSV load that does not pass in a file path
	⁃	Test a successful view csv command that properly loads and views a CSV file
	⁃	Test a successful search that properly loads a CSV and searches for a state and column parameter that exists
	⁃	Test a successful broadband search that properly passes in the state and county to search and returns the percentage, time/date + the parameters
	⁃	Test a failed broadband search that fails to pass in a county parameter and returns an error


# How to
- navigate to APIServer, hit run, and use http://localhost:3000 with params  
- or run mvn package, then ./run, and use http://localhost:3000 with params 
  - to search you'll need query and column params 
  - to load you'll need a filePath param 
  - to view no params are needed 
  - to get the broadband percentage you'll need the state and county params  

