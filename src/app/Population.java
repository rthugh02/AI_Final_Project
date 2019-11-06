package app;

import java.util.ArrayList;



public class Population
{
    private ArrayList<LSQ> populationMembers;
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

    public ArrayList<LSQ> getPopulationMembers() 
    {
        return populationMembers;
    }

    //Take the LSQ, generate random solutions for the given population
    private void initPop(int popSize)
    {
        populationMembers = new ArrayList<>(popSize);
        
        for(int i = 0; i < popSize; i++) 
        {
            LSQ newMember = new LSQ(givenLSQ);
            newMember.randomize();
            populationMembers.add(newMember);
        }
    }
}