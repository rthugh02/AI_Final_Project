package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;



public class Population
{
    private ArrayList<Symbol[][]> populationMembers;
    private LSQ givenLSQ;

    public Population(LSQ givenLSQ, int popSize)
    {
        this.givenLSQ = givenLSQ; 
        initPop(popSize);
    }

    public int size()
    {
        return populationMembers.size();
    }

    //Take the LSQ, generate random solutions for the given population
    private void initPop(int popSize)
    {
        populationMembers = new ArrayList<>(popSize);
        Symbol[][] providedTable = givenLSQ.getLsqTable();
        for(int i = 0; i < popSize; i++) 
        {
            //solution to be generated
            Symbol[][] newMember = new Symbol[givenLSQ.getDimension()][givenLSQ.getDimension()];

            //need to go through every symbol in the LSQ.LsqTable to fill non-locked symbols
            for(int j = 0; j < givenLSQ.getDimension(); j++) 
            {
                for(int k = 0; k < givenLSQ.getDimension(); k++) 
                {
                    if(providedTable[i][j].isLocked()) 
                        newMember[i][j] = providedTable[i][j];
                    else
                    {
                        //TODO: Generate a random symbol of the available symbols and put in newMember[i][j]
                    }
                } 
            }
            populationMembers.add(newMember);
        }
    }
}