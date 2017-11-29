package yahtzee;

import java.sql.*;
import javafx.application.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.control.Alert.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.*;
import javafx.event.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class Yahtzee extends Application implements EventHandler <MouseEvent>
{
	DiceGroup dg;
	int numRolls = 0;
	Label lblRollsLeft;
	int fieldsLeft = 13;
	int upperscore = 0;
	int lowerscore = 0;
	int yathzees = 0;
	final int numDice = 5;
	String username = "Hidde";
	Button rollDice;
	
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
			rollDice.setDisable(false);
			yathzees = 0;
		});
		miNewGame.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
		MenuItem miHiScores = new MenuItem("Hiscores");
		miHiScores.setOnAction(e->showHiscores(stage));
		miHiScores.setAccelerator(new KeyCodeCombination(KeyCode.H, KeyCombination.CONTROL_DOWN));
		menuGame.getItems().addAll(miNewGame, miHiScores);
		Menu menuSettings = new Menu("Settings");
		MenuItem miSettings = new MenuItem("Change name");
		miSettings.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
		miSettings.setOnAction(e->openSettings(stage));
		menuSettings.getItems().addAll(miSettings);
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
		scoreLabels = new String[] {"Three of a Kind", "Four of a Kind", "Full House", "Small Straight", "Large Straight", "Chance", "Yahtzee"};
		for (int i = 0; i < 7; i++) {
			lowerScores[i] = new ScoreGroup(scoreLabels[i], 12);
			lowerSection.getChildren().add(lowerScores[i]);
		}
		
		//draw visual representation of dice
		Canvas canvas = new Canvas(400, 100);
		GraphicsContext gc = canvas.getGraphicsContext2D();
		dg = new DiceGroup(gc);
		dg.drawDice(5);
		canvas.setOnMousePressed(this);
		
		HBox rolls = new HBox();
		rollDice = new Button("Roll!");
		HBox.setMargin(rollDice, new Insets(5));
		rolls.setAlignment(Pos.BASELINE_LEFT);
		rollDice.setOnAction(e->{
			if (numRolls < 3 && fieldsLeft > 0) {
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
		stage.setResizable(false);
		stage.setTitle("Yathzee");
		stage.show();
	}

	@Override public void handle(MouseEvent mse)
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
		for (int i = 1; i <=6; i++) {
			if (dg.getDiceValues(i) == numDice) {
				yathzees++;
				break;
			}
		}
		x.disable();
		String selectedField = x.getTitle();
		int lscore = 0, uscore = 0;
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
					lscore = dg.getTotalScore();
					break;
				}
			}
			break;
		case "Four of a Kind":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) >= 4) {
					lscore = dg.getTotalScore();
					break;
				}
			}
			break;
		case "Small Straight": //we happily use a pre-sorted array here
			int[] testSmall = dg.getDiceValues();
			if (testSmall[4] - testSmall[0] < 3) {
				break;
			}
			boolean z = false;
			for (int i = 1; i < numDice; i++) {
				if (testSmall[i] == testSmall[i-1] + 1) {
					if (i == 4) {
						lscore = 30;
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
					lscore = 30;
				}
			}
			break;
		case "Large Straight":
			int[] testLarge = dg.getDiceValues();
			for (int i = 1; i < numDice; i++) {
				if (testLarge[i] == i + testLarge[0]) {
					if (i + 1 == numDice) lscore = 40;
					else continue;
				}
				else {
					break;
				}
			}
			break;
		case "Full House":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) == numDice) {
					lscore = 25; break;
				}
				if (dg.getDiceValues(i) == 3) {
					boolean fh = false;
					for (int j = 1; j <= 6; j++) {
						if (dg.getDiceValues(j) == 2 && j != i) {
							fh = true;
						}
					}
					if (fh) {
						lscore = 25;
						break;
					}
					else {
						break;
					}
				}
			}
			break;
		case "Yahtzee":
			for (int i = 1; i <=6; i++) {
				if (dg.getDiceValues(i) == numDice) {
					lscore = 50;
					break;
				}
			}
		break;
		case "Chance":
			lscore = dg.getTotalScore();
			break;
		default:
		}
		upperscore += uscore;
		lowerscore += lscore;
		x.setScore(Math.max(lscore, uscore));
		
		numRolls = 0;
		if (--fieldsLeft == 0) {
			getTotalScore();
		}
		lblRollsLeft.setText(String.valueOf(3 - numRolls));
		dg.unlockAll();
	}
	
	public void getTotalScore()
	{
		int totalScore = 0;
		int upperBonus = 35;
		int upperThreshold = 63;
		if (upperscore >= upperThreshold)
			totalScore += upperBonus;
		totalScore += lowerscore + upperscore;
		String newHiscoreMessage = "";
		if (yathzees > 1)
			totalScore += (yathzees - 1) * 100;
		rollDice.setDisable(true);
		
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			String toInsert = "INSERT INTO hiscores (user, score, date) VALUES(\"" + username + "\", \"" 
					+ String.valueOf(totalScore) + "\", CURDATE())";
			stmt.execute(toInsert);
			String getHiscores = "SELECT score FROM hiscores ORDER BY score DESC LIMIT 10";
			stmt.execute(getHiscores);
			ResultSet rs = stmt.getResultSet();
			rs.last();
			if(rs.getInt(1) <= totalScore)
				newHiscoreMessage = "New Hiscore!";
			conn.close();
		}
		catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
		catch (NullPointerException npe) {
			newHiscoreMessage = "Could not connect to database. Your score could not be saved";
		}
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Finished!");
		alert.setHeaderText(null);
		alert.setContentText("Your final score is: " + totalScore + "\r\n" + newHiscoreMessage);
		alert.showAndWait();
	}
	
	private static Connection getConnection()
	{
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/yahtzee";
			String user = "root";
			String pw = "";
			conn = DriverManager.getConnection(url, user, pw);
		}
		catch (Exception e)
		{
			System.out.println("Database connection failed");
		}
		return conn;
	}
	
	public void openSettings(Stage onStage)
	{
		final Stage settings = new Stage();
		settings.initModality(Modality.APPLICATION_MODAL);
		settings.initOwner(onStage);
		VBox vboxSettings = new VBox();
		HBox hboxUsername = new HBox();
		hboxUsername.setSpacing(5);
		vboxSettings.setSpacing(5);
		TextField txtUsername = new TextField(username);
		txtUsername.setPrefWidth(150);
		hboxUsername.getChildren().addAll(new Label("Your name: "), txtUsername);
		Button btnConfirm = new Button("Confirm Changes");
		btnConfirm.setOnAction(f->{username = txtUsername.getText(); settings.close();});
		vboxSettings.getChildren().addAll(hboxUsername, btnConfirm);
		vboxSettings.setAlignment(Pos.CENTER);
		settings.setScene(new Scene(vboxSettings, 250, 100));
		settings.show();
	}
	
	public void showHiscores(Stage onStage)
	{
		final Stage hiscores = new Stage();
		hiscores.initModality(Modality.APPLICATION_MODAL);
		hiscores.initOwner(onStage);
		
		ResultSet results;
		HBox resultsTable = new HBox();
		resultsTable.setSpacing(10);
		Label lblTableHeader = new Label("The Best Scores");
		lblTableHeader.setFont(new Font("Arial", 18));
		VBox hiscoreTable = new VBox();
		hiscoreTable.getChildren().addAll(lblTableHeader, resultsTable);
		VBox colName = new VBox();
		VBox colScore = new VBox();
		VBox colDate = new VBox();
		resultsTable.getChildren().addAll(colName, colScore, colDate);
		
		Connection conn = getConnection();
		try {
			Statement stmt = conn.createStatement();
			String fetchHiscores = "SELECT * FROM hiscores ORDER BY score DESC LIMIT 10";
			stmt.execute(fetchHiscores);
			results = stmt.getResultSet();
			while (results.next()) {
				colName.getChildren().add(new Label(results.getString(1)));
				colScore.getChildren().add(new Label(results.getString(2)));
				colDate.getChildren().add(new Label(results.getString(3)));
			}
		}
		catch (SQLException e) {
			System.out.println(e.getMessage());
			return;
		}
		catch (NullPointerException npe) {
			hiscoreTable.getChildren().add(new Label("No Connection available\r\nUnable to load hiscores"));
		}
		hiscores.setScene(new Scene(hiscoreTable, 260, 200));
		hiscores.show();
	}
}
