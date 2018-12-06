package Main;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {
	private static final double DEF_FLAT = 2.3;
	private static final double MIN_FLAT = 1.6;
	private static final double MAX_FLAT = 2.5;

	public Main(double flatness) {
		Wrapper w3d = new Wrapper(DEF_FLAT);

		Container c = getContentPane();
		c.setLayout(new BorderLayout());
		c.add(w3d, BorderLayout.CENTER);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(true);
		setVisible(true);
	}

	public static void main(String[] args) {
		new Main(2.3);
	}

}
