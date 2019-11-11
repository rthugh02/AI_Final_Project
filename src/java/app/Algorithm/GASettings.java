package app.Algorithm;

import app.Data.Statistics;

//helper class for managing genetic algorithm settings
public class GASettings
{
    private static int popSize;
    private static double mutationChance;
    private static boolean wisdomOfCrowds;
    private static int elitism;
    private static int tourneySelectionNumber;
    private static int numTrials;

    public static void setDefaults()
    {
        popSize = 200;
        mutationChance = 0.1;
        wisdomOfCrowds = true;
        elitism = 2;
        tourneySelectionNumber = 3;
        numTrials = 1;
        Statistics.reset();
    }

    public static int getPopSize()
    {
        return popSize;
    }

    public static void setPopSize(int popSize)
    {
        GASettings.popSize = popSize;
        Statistics.reset();
    }

    public static double getMutationChance()
    {
        return mutationChance;
    }

    public static void setMutationChance(double mutationChance)
    {
        GASettings.mutationChance = mutationChance;
        Statistics.reset();
    }

    public static boolean isWisdomOfCrowds()
    {
        return wisdomOfCrowds;
    }

    public static void setWisdomOfCrowds(boolean wisdomOfCrowds)
    {
        GASettings.wisdomOfCrowds = wisdomOfCrowds;
        Statistics.reset();
    }

    public static int getElitism()
    {
        return elitism;
    }

    public static void setElitism(int elitism)
    {
        GASettings.elitism = elitism;
        Statistics.reset();
    }

    public static int getTourneySelectionNumber()
    {
        return tourneySelectionNumber;
    }

    public static void setTourneySelectionNumber(int tourneySelectionNumber)
    {
        GASettings.tourneySelectionNumber = tourneySelectionNumber;
        Statistics.reset();
    }

    public static int getNumTrials()
    {
        return numTrials;
    }

    public static void setNumTrials(int numTrials)
    {
        GASettings.numTrials = numTrials;
    }
}
