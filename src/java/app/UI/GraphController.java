package app.UI;

import app.Algorithm.GASettings;
import app.Data.LSQ;
import app.Data.Statistics;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.MenuItem;

public class GraphController
{
    @FXML
    private LineChart<Integer, Double> lineChart;
    @FXML
    private MenuItem miGraphA;
    @FXML
    private MenuItem miGraphB;

    private XYChart.Series<Integer, Double> series;

    @FXML
    private void initialize()
    {
        if(Statistics.getSeriesA() != null)
            miGraphA.setText("{Saved}");
        else
            miGraphA.setText("{Empty}");
        if(Statistics.getSeriesB() != null)
            miGraphB.setText("{Saved}");
        else
            miGraphB.setText("{Empty}");

        series = new XYChart.Series<>();
        lineChart.setLegendVisible(false);

        if(GASettings.isWisdomOfCrowds())
            series.setName("GA+WoC");
        else
            series.setName("GA");

        //iterate through solution progression at 10 generation intervals, adding current fitness as data point
        for(int i = 0; i < Statistics.getSolutionProgression().size(); i++)
        {
            LSQ lsq = Statistics.getSolutionProgression().get(i);
            series.getData().add(new XYChart.Data<>(i, lsq.getFitness()));
        }
        //add series to linechart
        lineChart.getData().add(series);
    }

    public void onClickCompare()
    {
        if(Statistics.getSeriesA() != null && Statistics.getSeriesB() != null
        && !Statistics.getSeriesA().equals(Statistics.getSeriesB()))
        {
            //remove current series
            lineChart.getData().remove(series);

            //add data of comparison series
            lineChart.getData().add(Statistics.getSeriesA());
            lineChart.getData().add(Statistics.getSeriesB());

            lineChart.setLegendVisible(true);
        }
    }

    public void onSaveA()
    {
        Statistics.setSeriesA(series);
        miGraphA.setText("{Saved}");
    }

    public void onSaveB()
    {
        Statistics.setSeriesB(series);
        miGraphB.setText("{Saved}");
    }

}
