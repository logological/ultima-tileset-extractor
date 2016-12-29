package ultimavtiles;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * A class to extract the tileset from the Commodore 64/128 version of Ultima V
 * 
 * @author Tristan Miller <psychonaut@nothingisreal.com>
 *
 */
public class Ultima5C64TileExtractor {

	final static String ULTIMA_V_PROGRAM_DISK = "/home/psy/games/C64/c64/ULTIMA5/PROGRAM.D64";

	public static void main(String[] args) throws IOException, InterruptedException {
		int width = 1409;
		int height = 1009;
		JFrame frame = new JFrame("Direct draw demo");

		BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DataInputStream dataStream = new DataInputStream(
				new BufferedInputStream(new FileInputStream(new File(ULTIMA_V_PROGRAM_DISK))));

		// dataStream.skipBytes(8);
		int tileRows = height / 16;
		int tileCols = width / 16;

		close:
			for (int tileRow = 0; tileRow < tileRows; tileRow++) {
			for (int tileCol = 0; tileCol < tileCols; tileCol++) {
				frame.repaint();
				for (int charRow = 0; charRow < 2; charRow++) {
					for (int charCol = 0; charCol < 2; charCol++) {
						for (int pixelRow = 0; pixelRow < 8; pixelRow++) {
							int theByte = dataStream.read();
							if (theByte == -1) {
								break close;
							}
							for (int pixelCol = 7; pixelCol >= 0; pixelCol--) {
								int x = tileCol * 16 + charCol * 8 + 8 - pixelCol;
								int y = tileRow * 16 + charRow * 8 + pixelRow;
								if ((theByte & (1 << pixelCol)) != 0) {
									canvas.setRGB(x, y, Color.WHITE.getRGB());
								} else
									canvas.setRGB(x, y, Color.BLACK.getRGB());
							}
						}
					}
				}
			}
		}

		dataStream.close();

	}

}