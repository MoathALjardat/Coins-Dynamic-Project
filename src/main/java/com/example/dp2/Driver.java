package com.example.dp2;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Driver extends Application {
    final static String RELATION = "T[i][j] = Coin[i]           If j == i\n" +
            "T[i][j] = max(Coin[i], Coin[j])        If j == i + 1\n" +
            "0        If j <  i\n" +
            "T[i][j] = Max(Coin[i] + min(T[i+2][j], T[i+1][j-1] ), " +
            "Coin[j] + min(T[i+1][j-1], T[i][j-2] ))";
    Group mainStageRoot;
    VBox mainStageButtonsVBox;
    Scene mainStageScene;
    Button playButton;
    Button tableButton;
    Button chooseAnotherFileButton;
    TextArea relationTextArea;
    int[] coins;
    int [][] dpTable;
    int[] chosenCoinsSequence;
    int[][] theChosenIndexFromCoinsInDpTable;
    Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        this.mainStage = stage;
        fileChooser();
    }

    public static void main(String[] args) {
        launch();
    }

    public void startPage() {
        prepareUIAndShowTheMainStage();
        addActionsForTheUIComponents();
    }

    public int[][] dpAlgorithm() {
        this.dpTable = new int[coins.length][coins.length];
        for (int i = 0; i < coins.length; i++) {
            dpTable[i][i] = coins[i];
        }
        for (int gap = 2; gap <= coins.length; gap++) {
            // 2  -> 3 -> 4
            for (int i = 0; i < coins.length - gap + 1; ++i) {
                int j = i + gap - 1;
                // 2
                if (j == i + 1)
                    dpTable[i][j] = Math.max(coins[i], coins[j]); //table[i][j] = max(coin[i],coin[j]) when j =i + 1
                else
                    dpTable[i][j] = Math.max(
                            (coins[i] + Math.min(dpTable[i + 2][j], dpTable[i + 1][j - 1])), // 13 + 10 = 23
                            (coins[j] + Math.min(dpTable[i + 1][j - 1], dpTable[i][j - 2])) // 13 + 10 = 23
                    );
            }

        }
        return dpTable;
    }

    public int[][] getTheChosenIndexFromCoinsInDpTable( int[][] dpTable) {
        int[][] theChosenIndexFromCoinsInDpTable = new int[coins.length][coins.length];
        for (int i = 0; i < coins.length; i++) {
            theChosenIndexFromCoinsInDpTable[i][i] = i;
        }
        for (int gap = 2; gap <= coins.length; gap++) {
            for (int i = 0; i < coins.length - gap + 1; ++i) {
                int j = i + gap - 1;
                if (j == i + 1) {
                    if (coins[i] > coins[j])
                        theChosenIndexFromCoinsInDpTable[i][j] = i;
                    else
                        theChosenIndexFromCoinsInDpTable[i][j] = j;
                } else {
                    if ((coins[i] + Math.min(dpTable[i + 2][j], dpTable[i + 1][j - 1])) >= (coins[j] + Math.min(dpTable[i + 1][j - 1], dpTable[i][j - 2])))
                        theChosenIndexFromCoinsInDpTable[i][j] = i;
                    else
                        theChosenIndexFromCoinsInDpTable[i][j] = j;
                }
            }
        }
        return theChosenIndexFromCoinsInDpTable;
    }

    public int getTheResult() {
        int max = dpTable[0][0];
        for (int j = 0; j < dpTable.length; j++) {
            for (int i = 0; i < dpTable[j].length; i++)
            {
                if (dpTable[j][i] > max) {
                    max = dpTable[j][i];
                }
            }
        }
        return max;
    }

    public void showTableStage() {
        Group tableStageGroup = new Group();
        Scene tableStageScene = new Scene(tableStageGroup, 600, 300, Color.WHEAT);
        Stage tableStage = new Stage();
        for (int i = 0; i < coins.length; i++) {
            Label xAxesCoin = new Label(String.valueOf(coins[i]));
            xAxesCoin.setFont(new Font("Cambria", 20));
            xAxesCoin.setTextFill(Color.GREEN);
            xAxesCoin.setTranslateY(2);
            xAxesCoin.setTranslateX(60 + (i * 70));
            Label yAxesCoin = new Label(String.valueOf(coins[i]));
            yAxesCoin.setFont(new Font("Cambria", 20));
            yAxesCoin.setTextFill(Color.GREEN);
            yAxesCoin.setTranslateY(30 + (i * 40));
            yAxesCoin.setTranslateX(2);
            tableStageGroup.getChildren().addAll(xAxesCoin, yAxesCoin);
        }
        for (int i = 0; i < dpTable.length; i++) {
            for (int j = 0; j < dpTable.length; j++) {
                Label dpTableElement = new Label(String.valueOf(dpTable[j][i]));
                dpTableElement.setTranslateX(65 + (i * 70));
                dpTableElement.setTranslateY(30 + (j * 40));
                dpTableElement.setFont(new Font("Cambria", 20));
                dpTableElement.setTextFill(Color.RED);
                tableStageGroup.getChildren().add(dpTableElement);
            }
        }
        tableStage.setScene(tableStageScene);
        tableStage.show();
    }

    public int[] getChosenCoinsSequence() {
        int[] sequenceOfChosenCoins = new int[(coins.length)];
        int i = 0;
        int j = coins.length - 1;
        int contuer = 0;
        while (contuer <= coins.length - 1) {
            if (theChosenIndexFromCoinsInDpTable[i][j] == i) {
                sequenceOfChosenCoins[contuer] = i;
                i++;  //if we choose from right we increment i
            } else {
                sequenceOfChosenCoins[contuer] = j;
                j--; //if we choose from left we decrement j
            }
            contuer++;
        }
        return sequenceOfChosenCoins;
    }

    public static void warning_Message(String x) {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setAlertType(Alert.AlertType.WARNING);
        alert.setContentText(x);
        alert.show();
    }

    public void fileChooser() {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            try {
                Scanner scanner = new Scanner(file);
                List<Integer> integers = new ArrayList<>();
                while (scanner.hasNextInt()) {
                    integers.add(scanner.nextInt());
                }
                scanner.close();
                this.coins = new int[integers.size()];
                for (int i = 0; i < coins.length; i++) {
                    coins[i] = integers.get(i);
                }
                if (coins.length % 2 == 1) {
                    throw new Exception("Number of the coins is odd");
                } else {
                    this.theChosenIndexFromCoinsInDpTable = getTheChosenIndexFromCoinsInDpTable(dpAlgorithm());
                    this.chosenCoinsSequence = getChosenCoinsSequence();
                    mainStage.close();
                    startPage();
                }
            } catch (FileNotFoundException e) {
            } catch (Exception e) {
                if (e.getMessage().equals("Number of the coins is odd")) {
                    warning_Message("Number of the coins is odd");
                } else {
                    warning_Message("Choose a file contains a sequence of integers and the number of these integers is even !!");
                }
                try {
                    start(mainStage);
                } catch (IOException ex) {
                }
            }
        }

    }

    public void prepareUIAndShowTheMainStage() {

        mainStageRoot = new Group();
        mainStageButtonsVBox = new VBox();
        mainStageButtonsVBox.setSpacing(30);
        mainStageButtonsVBox.setTranslateX(30);
        mainStageButtonsVBox.setTranslateY(65);
        mainStageScene = new Scene(mainStageRoot, 1000, 350, Color.WHEAT);
        playButton = new Button("Simulation");
        tableButton = new Button(" Show Table");
        chooseAnotherFileButton = new Button("Choose File");
        chooseAnotherFileButton.setFont(new Font("Times-Roman", 20));
        chooseAnotherFileButton.setPrefWidth(170);
        tableButton.setFont(new Font("Times-Roman", 20));
        tableButton.setPrefWidth(170);
        playButton.setFont(new Font("Times-Roman", 20));
        playButton.setPrefWidth(170);
        relationTextArea = new TextArea(RELATION);
        relationTextArea.setFont(new Font(18));
        relationTextArea.setTranslateX(240);
        relationTextArea.setTranslateY(30);
        mainStageRoot.getChildren().add(relationTextArea);
        mainStageButtonsVBox.getChildren().addAll(playButton, tableButton, chooseAnotherFileButton);
        mainStageRoot.getChildren().addAll(mainStageButtonsVBox);
        mainStage.setScene(mainStageScene);
        mainStage.show();
    }

    public void addActionsForTheUIComponents() {

        playButton.setOnAction((ActionEvent e) -> {
            Group rootInPlayStage = new Group();
            Stage playStage = new Stage();
            Scene playScene = new Scene(rootInPlayStage, 1250, 400, Color.WHEAT);
            HBox coinsHBox = new HBox();
            coinsHBox.setSpacing(20);
            coinsHBox.setAlignment(Pos.CENTER);
            coinsHBox.setTranslateX(200);
            coinsHBox.setTranslateY(110);
            Label player1Label = new Label("PLAYER 1:");
            player1Label.setFont(new Font("Cambria", 30));
            player1Label.setTranslateX(10);
            player1Label.setTranslateY(70);
            Label player2Label = new Label("PLAYER 2:");
            player2Label.setFont(new Font("Cambria", 30));
            player2Label.setTranslateX(10);
            player2Label.setTranslateY(180);
            Circle[] Circles = new Circle[coins.length];
            StackPane[] coinsStackPane = new StackPane[coins.length];
            Label[] coinsNumberLabels = new Label[coins.length];
            for (int i = 0; i < coins.length; i++) {
                coinsStackPane[i] = new StackPane();
                Circles[i] = new Circle();
                coinsNumberLabels[i] = new Label(String.valueOf(coins[i]));
                Circles[i].setRadius(30);
                Circles[i].setFill(Color.CYAN);
                coinsNumberLabels[i].setFont(new Font("Cambria", 30));
                coinsNumberLabels[i].setTextFill(Color.BLACK);
                coinsStackPane[i].getChildren().addAll(Circles[i], coinsNumberLabels[i]);
            }
            for (int i = 0; i < coinsStackPane.length; i++)
            coinsHBox.getChildren().addAll(coinsStackPane[i]);
            TranslateTransition[] translate = new TranslateTransition[chosenCoinsSequence.length];
            PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1));
            SequentialTransition sequentialTransition = new SequentialTransition();
            sequentialTransition.getChildren().add(pauseTransition);
            for (int i = 0; i < chosenCoinsSequence.length; i++) {
                translate[i] = new TranslateTransition();
                translate[i].setDuration(Duration.seconds(1));
                translate[i].setNode(coinsStackPane[chosenCoinsSequence[i]]);
                if (i % 2 == 0)
                    translate[i].setByY(-50);
                else {
                    translate[i].setByY(+50);
                }
                sequentialTransition.getChildren().add(translate[i]);
            }
            sequentialTransition.setOnFinished((ActionEvent l) -> {
                Text resultText = new Text("RESULT : " + String.valueOf(getTheResult()));
                resultText.setFont(new Font(45));
                resultText.setFill(Color.BLACK);
                resultText.setTranslateX(500);
                resultText.setTranslateY(300);
                rootInPlayStage.getChildren().add(resultText);
            });
            sequentialTransition.play();
            rootInPlayStage.getChildren().addAll(coinsHBox, player1Label, player2Label);
            playStage.setScene(playScene);
            playStage.show();
        });
        tableButton.setOnAction((ActionEvent e) -> {
            showTableStage();
        });
        chooseAnotherFileButton.setOnAction((ActionEvent again) -> {
            fileChooser();
        });
    }
}