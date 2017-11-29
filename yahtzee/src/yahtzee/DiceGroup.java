package yahtzee;

import java.util.Arrays;
import javafx.scene.canvas.GraphicsContext;

public class DiceGroup
{
	private double hGap = 15;
	private double size = 50;
	private double hMargin = 25;
	private double vMargin = 25;
	private GraphicsContext gc;
	private Die[] dice;
	
	public DiceGroup(GraphicsContext gc)
	{
		this.gc = gc;
	}
	
	public int getDiceValues(int value)
	{
		int result = 0;
		for (Die die : dice) {
			if (value == die.getValue()) result++;
		}
		return result;
	}
	
	public int[] getDiceValues()
	{
		int[] result = new int[dice.length];
		for (int i = 0; i < dice.length; i++) {
			result[i] = dice[i].getValue();
		}
		Arrays.sort(result);
		return result;
	}
	
	public int getTotalScore()
	{
		int result = 0;
		for (Die die : dice) {
			result += die.getValue();
		}
		return result;
		
	}
	
	public void drawDice(int amount)
	{
		dice = new Die[amount];
		for (int i = 0; i < amount; i++) {
			double hpos = hMargin + i * (size + hGap);
			dice[i] = new Die(gc, hpos, vMargin, size);
			dice[i].setValue(i + 1);
			dice[i].draw();
		}
	}
	
	public void roll()
	{
		for (Die die : dice)
		{
			die.roll();
		}
	}
	
	public void lockDie(double xPos, double yPos)
	{
		if (yPos < vMargin || yPos > vMargin + size) return; //above & below dice
		if (xPos < hMargin || xPos > hMargin + (size + hGap) * dice.length) return; //before & after dice
		if ((xPos - hMargin) % (size + hGap) > size) return; //inbetween dice
		int i = (int)((xPos - hMargin) / (hGap + size));
		dice[i].switchLocked();
	}
	
	public void unlockAll()
	{
		for (Die die : dice)
		{
			die.unlock();
		}
	}
	
	public void setMargins(double h, double v)
	{
		this.hMargin = h;
		this.vMargin = v;
	}
	
	public void setHGap(double x)
	{
		this.hGap = x;
	}
}
