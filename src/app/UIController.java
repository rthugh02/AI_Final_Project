package app;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class UIController
{
    @FXML
    private GridPane grid;

    @FXML
    private void initialize()
    {
        initGrid(9);
    }

    //initializes grid for n x n square
    private void initGrid(int n)
    {
        //remove any current column and row constraints
        while(grid.getColumnConstraints().size() > 0)
            grid.getColumnConstraints().remove(0);
        while(grid.getRowConstraints().size() > 0)
            grid.getRowConstraints().remove(0);

        //add new constraints
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

        //just fill it with alphabet characters in order for now
        char c;
        for(int i = 0; i < n; i++)
        {
            c = 65;
            for(int j = 0; j < n; j++)
            {
                //pane added along with symbol; provides background with border for cells based on css style class
                Pane cell = new Pane();
                cell.getStyleClass().add("cell");
                grid.add(cell, i, j, 1, 1);
                Label symbol = new Label(Character.toString(c));
                symbol.getStyleClass().add("symbol");
                c++;
                grid.add(symbol, i, j, 1, 1);
            }
        }
    }


}
