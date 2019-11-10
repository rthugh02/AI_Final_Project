package app.Algorithm;

import app.Data.LSQ;
import app.Data.Population;
import app.Data.Symbol;

import java.util.ArrayList;
import java.util.Random;

public class GA
{
    //Best solution will be added to Statistics.SolutionProgression for each generation
    public static void calcGeneticSolution(Population population)
    {
        //TODO implement Genetic Algorithm based on population
        
        /*
            GA Algorithm set up:
            
            while(!termination_condition)
            {
                - apply mutation operation
                - evaluate population fitness
                - Perform selection process (tournament)
                - Perform crossover operation between parents
                - integrate children into population
                - kill off weakest solutions
            }

            //TODO make sure to call Statistics.updateAggregateData() at end of GA calculation
        */  
    }

    //crossover that passes on each parent's best(least conflicts) row and column to separate children
    //..and fills the remaining cells with the opposite parent's corresponding cells
    private ArrayList<LSQ> crossover(LSQ parent1, LSQ parent2)
    {
        ArrayList<LSQ> children = new ArrayList<>();
        //since remaining cells are filled from opposite parent, just start with clone of opposite parent
        LSQ child1 = new LSQ(parent2);
        LSQ child2 = new LSQ(parent1);

        int p1Col = parent1.getLowestColumn();
        int p1Row = parent1.getLowestRow();
        int p2Col = parent2.getLowestColumn();
        int p2Row = parent2.getLowestRow();

        //i is index that can represent col or row
        for(int i = 0; i < parent1.getDimension(); i++)
        {
            //copy values of parent1's best row to child1
            if(!child1.getSymbol(i, p1Row).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(i, p1Row)), i, p1Row);

            //copy values of parent2's best row to child2
            if(!child2.getSymbol(i, p2Row).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(i, p2Row)), i, p2Row);

            //copy values of parent1's best col to child1
            if(!child1.getSymbol(p1Col, i).isLocked())
                child1.setSymbol(new Symbol(parent1.getSymbol(p1Col, i)), p1Col, i);

            //copy values of parent2's best col to child2
            if(!child2.getSymbol(p2Col, i).isLocked())
                child2.setSymbol(new Symbol(parent2.getSymbol(p2Col, i)), p2Col, i);
        }
        //recalculate new fitness
        child1.calcFitness();
        child2.calcFitness();

        //return the new children
        children.add(child1);
        children.add(child2);
        return children;
    }

    private LSQ selectByTournament(Population population, int selectionAmount) 
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
      
    
    private LSQ mutation(LSQ original)
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
    			symb1clear = false;
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
    		mutated.setSymbol(new Symbol(original.getSymbol(symb1col, symb2row)), symb2col, symb2row);
   
   		return mutated;
    }
}
