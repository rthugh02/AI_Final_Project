package app.Algorithm;

import app.Data.*;

import java.util.*;

public class GA
{
    //variable counting number of generations where highest fitness hasn't changed
    private static int noFitnessChanges = 0;
    private static LSQ bestSolution;
    private static Population workingPopulation; 
    //Best solution will be added to Statistics.SolutionProgression for each generation
    public static Population calcGeneticSolution(Population population)
    {
           /* GA Algorithm set up:
            
            while(!termination_condition)
            {
                - apply mutation operation
                - evaluate population fitness
                - Perform selection process (tournament)
                - Perform crossover operation between parents
                - integrate children into population
                - kill off weakest solutions
            }*/


        Statistics.resetProgression();
        workingPopulation = population;
        noFitnessChanges = 0;
        int numGenerations = 0; 
        bestSolution = population.getPopulationMembersSorted().get(0);
        System.out.println("Begin GA");
        while(noFitnessChanges != 100 && bestSolution.getFitness() != 2 && numGenerations < 1000)
        {
            //1. apply mutation operator to population
            Random rand = new Random();
            double mutationChance = GASettings.getMutationChance();
            Population prevPopCopy = new Population(workingPopulation);
            Iterator<LSQ> it = workingPopulation.getPopulationMembers().iterator();
            ArrayList<LSQ> mutatedMembers = new ArrayList<>();
            while(it.hasNext())
            {
              LSQ solution = it.next();
              double randomVal = rand.nextDouble();
              if(randomVal <= mutationChance)
              {
                  LSQ mutatedSolution = mutation(solution);
                  mutatedMembers.add(mutatedSolution);
                  it.remove();
              }
            }
            workingPopulation.addMembers(mutatedMembers);

            //2. Tournament Selection
            int selectionAmount = GASettings.getTourneySelectionNumber();
            ArrayList<LSQ> parents = new ArrayList<>();

            //number of parents will be 20% of total population size
            for(int i = 0; i < (int)(workingPopulation.getPopulationMembers().size() * 0.2); i++)
            {
                parents.add(selectByTournament(workingPopulation, selectionAmount));
            }

            //3. Crossover operations
            ArrayList<LSQ> children = new ArrayList<>();
            for(int i = 0; i < parents.size() - 2; i++)
            {
                children.addAll(crossoverRandomColumns(parents.get(i), parents.get(i+1)));
            }

            //4a. adding the elite to the next generation
            int elitism = GASettings.getElitism();
            ArrayList<LSQ> sortedMembers = workingPopulation.getPopulationMembersSorted();
            ArrayList<LSQ> nextGeneration = new ArrayList<>();
            for(int i = 0; i < elitism; i++)
            {
                nextGeneration.add(sortedMembers.remove(0));
            }

            //add woc and make room for one more child if woc is enabled
            int wocNum = 0;
            if(GASettings.isWisdomOfCrowds())
            {
                nextGeneration.add(WOC.getWOCSolution(prevPopCopy));
                wocNum = 1;
            }

            //4b. adding children into population and evaluating
            sortedMembers.addAll(children);
            Collections.sort(sortedMembers);
            int populationSize = GASettings.getPopSize();
            for(int i = 0; i < populationSize - elitism - wocNum; i++)
            {
                nextGeneration.add(sortedMembers.get(i));
            }

            Collections.sort(nextGeneration);
            workingPopulation = new Population(nextGeneration);
            numGenerations++;
            LSQ topMember = nextGeneration.get(0);

            if(Double.compare(topMember.getFitness(), bestSolution.getFitness()) > 0)
              {
                  bestSolution = topMember;
                  noFitnessChanges = 0;
              }
            else if(Double.compare(topMember.getFitness(), bestSolution.getFitness() ) == 0)
              noFitnessChanges++;


            Statistics.addSolutionToProgression(topMember);
        }
        Statistics.updateAggregateData(bestSolution, numGenerations);
        System.out.println("End GA");
        return workingPopulation;
    }

    private static LSQ selectByTournament(Population population, int selectionAmount) 
    {
        Random rand = new Random();
        int randomIndex;
        
        //copy for full removal
        Population copy = new Population(population);
        Population tournament = new Population();

        for(int i = 0; i < selectionAmount; i++) 
        {
            randomIndex = rand.nextInt(copy.size());
            tournament.addMember(copy.getMember(randomIndex));
            copy.removeMember(randomIndex);
        }

        return tournament.getPopulationMembersSorted().get(0);
    }
      
    
    private static LSQ mutation(LSQ original)
    {
    	LSQ mutated = new LSQ(original);
    	int max = original.getDimension();		//uses the dimension to get the range for the random array slot
    	boolean symb1clear = false;		//booleans to make sure that both symbols are not locked
    	boolean symb2clear = false;
    	int symb1col, symb1row, symb2col, symb2row;
    	symb1col = symb1row = symb2col = symb2row = 0;

    	Random rand = new Random();
    	while(!symb1clear)		//randomly picks symbol until it isn't locked
    	{
    		symb1col = rand.nextInt(max);
    		symb1row = rand.nextInt(max);
    		if(!original.getSymbol(symb1col, symb1row).isLocked())
    		{
    			symb1clear = true;
    		}
    	}
    	while(!symb2clear)
    	{
    		symb2col = rand.nextInt(max);
    		symb2row = rand.nextInt(max);
    		if(!original.getSymbol(symb2col, symb2row).isLocked())
    		{
    			symb2clear = true;
    		}
    	}
    		
    		//first replaces the first symbol with the second from the parent
    		mutated.setSymbol(new Symbol(original.getSymbol(symb2col, symb2row)), symb1col, symb1row);
    		
    		//next replaces the second symbol with the first symbol
    		mutated.setSymbol(new Symbol(original.getSymbol(symb1col, symb1row)), symb2col, symb2row);

    		if(!mutated.validateTable())
    		    System.out.println("invalid mutation");
   		return mutated;
    }

    //select random number of columns, select them at random and pass on to child
    //, try to fill empty cells with opposite parent
private static ArrayList<LSQ> crossoverRandomColumns(LSQ parent1, LSQ parent2)
    {
        Random rand = new Random();
        int dim = parent1.getDimension();
        //determine number of columns randomly
        int numCols = rand.nextInt(dim);
        //create children and empty their tables
        LSQ child1 = new LSQ(parent1);
        LSQ child2 = new LSQ(parent1);
        child1.emptyTable();
        child2.emptyTable();
        //get available characters for each child
        ArrayList<Character> charPool1 = child1.getCharacterPool();
        ArrayList<Character> charPool2 = child2.getCharacterPool();
        //list to keep track of which columns have/have not been selected
        ArrayList<Integer> remainingCols = new ArrayList<>();
        for(int x = 0; x < dim; x++)
        {
            remainingCols.add(x);
        }

        //add random number of columns
        for(int x = 0; x < numCols; x++)
        {
            //select random column from remaining columns
            int colIndex = remainingCols.remove(rand.nextInt(remainingCols.size()));
            for(int i = 0; i < dim; i++)
            {
                Symbol p1Symbol = parent1.getSymbol(colIndex, i);
                //copy values of parent1 column to child1
                if(!p1Symbol.isLocked() && charPool1.contains(p1Symbol.getCharacter()))
                {
                    child1.setSymbol(new Symbol(p1Symbol), colIndex, i);
                    charPool1.remove(p1Symbol.getCharacter());
                }

                Symbol p2Symbol = parent2.getSymbol(colIndex, i);
                //copy values of parent2 column to child2
                if(!p2Symbol.isLocked() && charPool2.contains(p2Symbol.getCharacter()))
                {
                    child1.setSymbol(new Symbol(p2Symbol), colIndex, i);
                    charPool2.remove(p2Symbol.getCharacter());
                }
            }
        }

        //try to fill empty cells with opposite parent if possible
        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                Symbol p2Symbol = parent2.getSymbol(i, j);
                //for empty cell, if symbol in opposite parent's cell is available, assign it
                if(!p2Symbol.isLocked() && charPool1.contains(p2Symbol.getCharacter()))
                {
                    child1.setSymbol(new Symbol(p2Symbol), i, j);
                    charPool1.remove(p2Symbol.getCharacter());
                }
                //same for other child
                Symbol p1Symbol = parent1.getSymbol(i, j);
                if(!p1Symbol.isLocked() && charPool1.contains(p1Symbol.getCharacter()))
                {
                    child2.setSymbol(new Symbol(p1Symbol), i, j);
                    charPool2.remove(p1Symbol.getCharacter());
                }
            }
        }

        //fill any remaining empty cells at random with available characters
        child1.fillEmptyCells();
        child2.fillEmptyCells();

        if(!child1.validateTable() || !child2.validateTable())
        {
            System.out.println("crossover created invalid child");
        }

        //return the new children
        ArrayList<LSQ> children = new ArrayList<>();
        children.add(child1);
        children.add(child2);
        return children;
    }

}
