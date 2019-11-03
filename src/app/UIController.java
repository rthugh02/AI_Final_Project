package app;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.io.File;
import java.util.Optional;

public class UIController
{
    @FXML
    private GridPane grid;

    @FXML
    private void initialize()
    {
        Optional<File> opFile = ReadLSQFile.fileSelection();
        //###DEBUG CODE FOR QUICK FILE SELECTION###
        //Optional<File> opFile = Optional.of(new File("samplefiles/lsq10.lsq"));
        //###END DEBUG CODE###
        if(opFile.isPresent())
        {
            LSQ lsq = ReadLSQFile.createLSQFromFile(opFile.get());
            initGrid(lsq);
        }
    }

    //initializes grid for n x n square
    private void initGrid(LSQ lsq)
    {
        int n = lsq.getDimension();
        //remove any current column and row constraints
        while(grid.getColumnConstraints().size() > 0)
            grid.getColumnConstraints().remove(0);
        while(grid.getRowConstraints().size() > 0)
            grid.getRowConstraints().remove(0);

        //add new constraints to grid
        for(int i = 0; i < n; i++)
        {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setFillWidth(true);
            cc.setHgrow(Priority.NEVER);
            cc.setHalignment(HPos.CENTER);
            cc.setPercentWidth((1.0 / (double) n)*100);
            grid.getColumnConstraints().add(cc);
        }
        for(int j = 0; j < n; j++)
        {
            RowConstraints rc = new RowConstraints();
            rc.setFillHeight(true);
            rc.setVgrow(Priority.NEVER);
            rc.setValignment(VPos.CENTER);
            rc.setPercentHeight((1.0 / (double) n)*100);
            grid.getRowConstraints().add(rc);
        }

        for(int i = 0; i < n; i++)
        {
            for(int j = 0; j < n; j++)
            {
                //pane added along with symbol
                //with style class, provides background with border as inset to show grid lines
                Pane cell = new Pane();
                cell.getStyleClass().add("cell");
                grid.add(cell, i, j, 1, 1);
                Symbol symbol = lsq.getSymbol(i, j);
                Label lblSymbol = new Label(Character.toString(symbol.getCharacter()));
                if(!symbol.isLocked())
                    lblSymbol.getStyleClass().add("symbol");
                else
                    lblSymbol.getStyleClass().add("symbol-locked");
                grid.add(lblSymbol, i, j, 1, 1);
            }
        }
    }


}
