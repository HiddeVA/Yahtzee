package yathzee;

import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Yathzee extends Application implements EventHandler <MouseEvent>
{
	DiceGroup dg;
	int numRolls = 0;
	Label lblRollsLeft;
	int fieldsLeft = 13;
	int upperscore = 0;
	int lowerscore = 0;
	
	public static void main(String[] args)
	{
		launch(args);
	}
	
	@Override public void start(Stage stage)
	{
		BorderPane root = new BorderPane();
		ScoreGroup[] upperScores = new ScoreGroup[6];
		ScoreGroup[] lowerScores = new ScoreGroup[7];
		
		//Make menu here
		MenuBar menu = new MenuBar();
		Menu menuGame = new Menu("Game");
		MenuItem miNewGame = new MenuItem("New Game");
		miNewGame.setOnAction(e->{
			for (ScoreGroup sg : upperScores) {sg.reset();}
			for (ScoreGroup sg : lowerScores) {sg.reset();}
			upperscore = lowerscore = 0;
			fieldsLeft = 13;
			numRolls = 0;
			lblRollsLeft.setText("3");
			dg.unlockAll();
		});
		MenuItem miHiScores = new MenuItem("Hiscores");
		menuGame.getItems().addAll(miNewGame, miHiScores);
		Menu menuSettings = new Menu("Settings");
		menu.getMenus().addAll(menuGame, menuSettings);
		root.setTop(menu);
		
		//Make upper and lower sections
		VBox mainWindow = new VBox();
		root.setCenter(mainWindow);
		FlowPane upperSection = new FlowPane();
		String[] scoreLabels = new String[] {"Ones", "Twos", "Threes", "Fours", "Fives", "Sixes"};
		for (int i = 0; i < 6; i++) {
			upperScores[i] = new ScoreGroup(scoreLabels[i], 18);
			upperSection.getChildren().add(upperScores[i]);
		}
		FlowPane lowerSection = new FlowPane();
		scoreLabels = new String[] {"Three of a Kind", "Four of a Kind", "Full House", "Small Straight", "Large Straight", "Chance", "Yathzee"};
		for (int i = 0; i < 7; i++) {
			lowerScores[i] = new ScoreGroup(scoreLabels[i], 12);
			lowerSection.getChildren().add(lowerScores[i]);
		}
		
		//draw visual representation of dice
		Canvas canvas = new Canvas(400, 100);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		dg = new DiceGroup(gc);
		dg.drawDice(5);
		canvas.setOnMouseClicked(this);
		
		HBox rolls = new HBox();
		Button rollDice = new Button("Roll!");
		HBox.setMargin(rollDice, new Insets(5));
		rolls.setAlignment(Pos.BASELINE_LEFT);
		rollDice.setOnAction(e->{
			if (numRolls < 3) {
				dg.roll();
				numRolls++;
				lblRollsLeft.setText(String.valueOf(3 - numRolls));
			}
		});
		Label lblRollsLeftText = new Label("Rolls Left: ");
		lblRollsLeft = new Label("3");
		rolls.getChildren().addAll(rollDice, lblRollsLeftText, lblRollsLeft);
		
		mainWindow.getChildren().addAll(upperSection, lowerSection, canvas, rolls);
		mainWindow.setSpacing(15);
		
		stage.setScene(new Scene(root, 400, 500));
		stage.setTitle("Yathzee");
		stage.show();
	}

	@Override
	public void handle(MouseEvent mse)
	{
		if (numRolls > 0)
			dg.lockDie(mse.getX(), mse.getY());
	}
	
	private class ScoreGroup extends HBox 
	{
		private Label lblName;
		private Label lblScore;
		private Button btnPick;
		
		public ScoreGroup(String name, double fontSize)
		{
			lblName = new Label(name);
			lblName.setPrefWidth(70);
			lblName.setWrapText(true);
			lblName.setFont(new Font(fontSize));
			lblScore = new Label();
			lblScore.setPrefWidth(50);
			btnPick = new Button("+");
			btnPick.setOnAction(e->getScore(this));
			this.getChildren().addAll(lblName, btnPick, lblScore);
			this.setSpacing(10);
			this.setPadding(new Insets(5));
			this.setBorder(new Border(new BorderStroke(Color.BLACK, 
		            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		}
		
		public String getTitle()
		{
			return lblName.getText();
		}
		
		public void disable()
		{
			btnPick.setDisable(true);
		}
		
		public void setScore(int score)
		{
			lblScore.setText(String.valueOf(score));
		}
		
		public void reset()
		{
			lblScore.setText("");
			btnPick.setDisable(false);
		}
	}
	
	public void getScore(ScoreGroup x)
	{
		if (numRolls == 0) return;
		x.disable();
		String selectedField = x.getTitle();
		int score = 0, uscore = 0;
		switch (selectedField) {
		case "Ones":
			uscore = dg.getDiceValues(1);
			break;
		case "Twos":
			uscore = dg.getDiceValues(2) * 2;
			break;
		case "Threes":
			uscore = dg.getDiceValues(3) * 3;
			break;
		case "Fours":
			uscore = dg.getDiceValues(4) * 4;
			break;
		case "Fives":
			uscore = dg.getDiceValues(5) * 5;
			break;
		case "Sixes":
			uscore = dg.getDiceValues(6) * 6;
			break;
		case "Three of a Kind":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) >= 3) {
					score = dg.getTotalScore();
					break;
				}
			}
			break;
		case "Four of a Kind":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) >= 4) {
					score = dg.getTotalScore();
					break;
				}
			}
			break;
		case "Small Straight":
			int[] testSmall = dg.getDiceValues();
			if (testSmall[4] - testSmall[0] < 3) {
				break;
			}
			boolean z = false;
			for (int i = 1; i < 5; i++) {
				if (testSmall[i] == testSmall[i-1] + 1) {
					if (i == 4) {
						score = 30;
						break;
					}
					else continue;
				}
				else if (testSmall[i] == testSmall[i-1]) {
					if (z) {
						break;
					}
					else z = true;
				}
				else if (testSmall[i] == testSmall[i-1] + 2) {
					if (i == 1 || i == 4) {
						if (z) {
							break;
						}
						else z = true;
					}
					else {
						break;
					}
				}
				else {
					break;
				}
				if (i == 4) {
					score = 30;
				}
			}
			break;
		case "Large Straight":
			int[] testLarge = dg.getDiceValues(); //this array is sorted
			for (int i = 1; i < 5; i++) {
				if (testLarge[i] == i + testLarge[0]) {
					if (i == 4) score = 40;
					else continue;
				}
				else {
					break;
				}
			}
			break;
		case "Full House":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) == 5) {
					score = 25; break;
				}
				if (dg.getDiceValues(i) == 3) {
					boolean fh = false;
					for (int j = 1; j <= 6; j++) {
						if (dg.getDiceValues(j) == 2 && j != i) {
							fh = true;
						}
					}
					if (fh) {
						score = 25;
						break;
					}
					else {
						break;
					}
				}
			}
			break;
		case "Yathzee":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) == 5) {
					score = 50;
					break;
				}
			}
		break;
		case "Chance":
			score = dg.getTotalScore();
			break;
		default:
		}
		upperscore += uscore;
		lowerscore += score;
		x.setScore(Math.max(score, uscore));
		
		numRolls = 0;
		if (--fieldsLeft == 0) {
			getTotalScore();
		}
		lblRollsLeft.setText(String.valueOf(3 - numRolls));
		dg.unlockAll();
	}
	
	public void getTotalScore()
	{
		int totalScore = (upperscore >= 63) ? upperscore + lowerscore + 35 : upperscore + lowerscore;
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Finished!");
		alert.setHeaderText(null);
		alert.setContentText("Your final score is: " + totalScore);
		alert.showAndWait();
	}
}
