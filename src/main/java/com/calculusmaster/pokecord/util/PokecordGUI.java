package com.calculusmaster.pokecord.util;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PokecordGUI extends Application
{
    @Override
    public void start(Stage stage) throws Exception
    {
        stage.setScene(new Scene(new GridPane()));

        stage.show();
    }
}
