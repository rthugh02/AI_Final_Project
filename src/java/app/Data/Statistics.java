package app.Data;

import javafx.scene.chart.XYChart;

import java.util.ArrayList;

//helper class for keeping track of statistics during running multiple trials
public class Statistics
{

    //##########Aggregate data over multiple trial runs/iterations############
    //number of times the genetic algorithm has been run in succession with no settings change
    private static int iterations;
    private static int minGenerations;
    private static int maxGenerations;
    private static int avgGenerations;
    private static int timesSolutionFound;
    private static double minFitness;
    private static double maxFitness;
    private static double avgFitness;
    private static double stdDev;
    private static LSQ aggregateBestSolution;
    private static ArrayList<LSQ> solutionHistory;
    //########################################################################

    //##########data for the current run##############
    private static LSQ currentSolution;
    //stores best solution from each generation for one iteration; used to visualize progression on UI
    private static ArrayList<LSQ> solutionProgression;
    //################################################

    //##########graph data#############################
    //series data stored here for comparison on graph
    //graph results can be saved to either A or B and compared
    private static XYChart.Series<Integer, Double> seriesA;
    private static XYChart.Series<Integer, Double> seriesB;
    //####################################################

    //sets default values for all statistics; should be called on startup or when GA settings change
    public static void reset()
    {
        iterations = 0;
        minGenerations = 0;
        maxGenerations = 0;
        avgGenerations = 0;
        timesSolutionFound = 0;
        minFitness = Double.POSITIVE_INFINITY;
        maxFitness = 0.0;
        avgFitness = 0.0;
        stdDev = 0.0;
        currentSolution = null;
        aggregateBestSolution = null;
        solutionProgression = new ArrayList<>();
        solutionHistory = new ArrayList<>();
    }

    public static void resetProgression()
    {
        solutionProgression = new ArrayList<>();
    }

    public static int getIterations()
    {
        return iterations;
    }

    public static int getMinGenerations()
    {
        return minGenerations;
    }

    public static void setMinGenerations(int minGenerations)
    {
        Statistics.minGenerations = minGenerations;
    }

    public static int getMaxGenerations()
    {
        return maxGenerations;
    }

    public static void setMaxGenerations(int maxGenerations)
    {
        Statistics.maxGenerations = maxGenerations;
    }

    public static int getAvgGenerations()
    {
        return avgGenerations;
    }

    public static void setAvgGenerations(int avgGenerations)
    {
        Statistics.avgGenerations = avgGenerations;
    }

    public static int getTimesSolutionFound()
    {
        return timesSolutionFound;
    }

    public static void setTimesSolutionFound(int timesSolutionFound)
    {
        Statistics.timesSolutionFound = timesSolutionFound;
    }

    public static double getMinFitness()
    {
        return minFitness;
    }

    public static void setMinFitness(double minFitness)
    {
        Statistics.minFitness = minFitness;
    }

    public static double getMaxFitness()
    {
        return maxFitness;
    }

    public static void setMaxFitness(double maxFitness)
    {
        Statistics.maxFitness = maxFitness;
    }

    public static double getAvgFitness()
    {
        return avgFitness;
    }

    public static void setAvgFitness(double avgFitness)
    {
        Statistics.avgFitness = avgFitness;
    }

    public static double getStdDev()
    {
        return stdDev;
    }

    public static void setStdDev(double stdDev)
    {
        Statistics.stdDev = stdDev;
    }

    public static LSQ getCurrentSolution()
    {
        return currentSolution;
    }

    public static void setCurrentSolution(LSQ lsq)
    {
        Statistics.currentSolution = new LSQ(lsq);
    }

    public static LSQ getAggregateBestSolution()
    {
        return aggregateBestSolution;
    }

    public static void setAggregateBestSolution(LSQ lsq)
    {
        Statistics.aggregateBestSolution = new LSQ(lsq);
    }

    public static ArrayList<LSQ> getSolutionProgression()
    {
        return solutionProgression;
    }

    public static void setSolutionProgression(ArrayList<LSQ> solutionProgression)
    {
        Statistics.solutionProgression = new ArrayList<>(solutionProgression);
    }

    public static void addSolutionToProgression(LSQ solutionEntry) 
    {
        if(Statistics.solutionProgression != null)
            solutionProgression.add(solutionEntry);
    }

    public static XYChart.Series<Integer, Double> getSeriesA()
    {
        return seriesA;
    }

    public static void setSeriesA(XYChart.Series<Integer, Double> series)
    {
        Statistics.seriesA = new XYChart.Series<>();
        Statistics.seriesA.setName(series.getName());
        for(XYChart.Data<Integer, Double> data: series.getData())
        {
            XYChart.Data<Integer, Double> d = new XYChart.Data<>();
            d.setXValue(data.getXValue());
            d.setYValue(data.getYValue());
            Statistics.seriesA.getData().add(d);
        }
    }

    public static XYChart.Series<Integer, Double> getSeriesB()
    {
        return seriesB;
    }

    public static void setSeriesB(XYChart.Series<Integer, Double> series)
    {
        Statistics.seriesB = new XYChart.Series<>();
        Statistics.seriesB.setName(series.getName());
        for(XYChart.Data<Integer, Double> data: series.getData())
        {
            XYChart.Data<Integer, Double> d = new XYChart.Data<>();
            d.setXValue(data.getXValue());
            d.setYValue(data.getYValue());
            Statistics.seriesB.getData().add(d);
        }
    }

    //adds new solution from a GA to aggregate data and recalculates statistics
    public static void updateAggregateData(LSQ solution, int numGenerations)
    {
        Statistics.iterations++;
        Statistics.currentSolution = new LSQ(solution);
        Statistics.solutionHistory.add(solution);
        if(Double.compare(solution.getFitness(), 2.0) == 0)
            Statistics.timesSolutionFound++;

        //update min/max values if applicable
        if(solution.getFitness() < minFitness)
        {
            Statistics.minFitness = solution.getFitness();
            aggregateBestSolution = new LSQ(solution);
        }
        if(solution.getFitness() > maxFitness)
            Statistics.maxFitness = solution.getFitness();
        if(numGenerations < minGenerations || minGenerations == 0)
            Statistics.minGenerations = numGenerations;
        if(numGenerations > maxGenerations)
            Statistics.maxGenerations = numGenerations;
        //update averages
        int totalFitness = 0;
        int totalGenerations = 0;
        for(LSQ lsq: solutionHistory)
        {
            totalFitness += lsq.getFitness();
            totalGenerations += numGenerations;
        }
        Statistics.avgFitness = totalFitness / (double) Statistics.iterations;
        Statistics.avgGenerations = totalGenerations / Statistics.iterations;

        //calculate standard deviation
        double totalDeviation = 0.0;
        for(LSQ lsq: solutionHistory)
        {
            totalDeviation += Math.sqrt(lsq.getFitness() - avgFitness);
        }
        stdDev = Math.sqrt(totalDeviation / (double) iterations);
    }

}
