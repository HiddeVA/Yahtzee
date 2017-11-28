package yathzee;

import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import java.util.Random;

//-----------------------------------------------------
//Deze class maakt een dobbelsteen aan. GraphicsContext vereist als parameter
//In de constructor moet positie en grootte meegegeven worden
//Dobbelsteen kan rollen, wat een random-value(int) aan de dobbelsteen geeft
//Dobbelsteen kan locked zijn
//Locked betekent dat roll niet meer beschikbaar is
//----------------------------------------------------

public class Die
{
	private GraphicsContext dice;
	private double hpos;
	private double vpos;
	private double size;
	private int value = 1;
	private boolean locked = false;
	
	public Die(GraphicsContext gc, double hpos, double vpos, double size)
	{
		dice = gc;
		this.hpos = hpos;
		this.vpos = vpos;
		this.size = size;
	}
	
	public void switchLocked()
	{
		locked = !locked;
		draw();
	}
	
	public void roll()
	{
		if (!locked) {
			Random r = new Random();
			value = r.nextInt(6) + 1;
			draw();
		}
	}
	
	public void unlock()
	{
		if (locked) switchLocked();
	}
	
	public int getValue()
	{
		return this.value;
	}
	
	public void setValue(int value)
	{
		if (value < 1 || value > 6) {
			System.out.println("Invalid value: " + value);
			value = 1;
		}
		this.value = value;
	}
	
	public void draw()
	{
		dice.setFill(Color.BLACK);
		double roundness = size * 0.3;
		dice.fillRoundRect(hpos, vpos, size, size, roundness, roundness);
		
		double cx = hpos + size / 2;
		double cy = vpos + size / 2;
		dice.setFill(Color.WHITE);
		double dotSize = 8;
		double offSet = size / 4;
				
		switch (value) {
		case 5:
			dice.fillOval(cx - dotSize / 2 - offSet, cy - dotSize / 2 - offSet, dotSize, dotSize);			
			dice.fillOval(cx - dotSize / 2 + offSet, cy - dotSize / 2 + offSet, dotSize, dotSize);
		case 3:
			dice.fillOval(cx - dotSize / 2 + offSet, cy - dotSize / 2 - offSet, dotSize, dotSize);
			dice.fillOval(cx - dotSize / 2 - offSet, cy - dotSize / 2 + offSet, dotSize, dotSize);
		case 1:
			dice.fillOval(cx - dotSize / 2, cy - dotSize / 2, dotSize, dotSize);
		break;
		case 6:
			dice.fillOval(cx - dotSize / 2 + offSet, cy - dotSize / 2, dotSize, dotSize);
			dice.fillOval(cx - dotSize / 2 - offSet, cy - dotSize / 2, dotSize, dotSize);
		case 4:
			dice.fillOval(cx - dotSize / 2 - offSet, cy - dotSize / 2 + offSet, dotSize, dotSize);
			dice.fillOval(cx - dotSize / 2 + offSet, cy - dotSize / 2 - offSet, dotSize, dotSize);
		case 2:
			dice.fillOval(cx - dotSize / 2 - offSet, cy - dotSize / 2 - offSet, dotSize, dotSize);			
			dice.fillOval(cx - dotSize / 2 + offSet, cy - dotSize / 2 + offSet, dotSize, dotSize);
		break;
		default:
		}
		
		if (locked) {
			dice.setStroke(Color.RED);
			dice.strokeRect(cx - size * 0.4, cy - size * 0.4, size * 0.8, size * 0.8);
		}
	}
}
