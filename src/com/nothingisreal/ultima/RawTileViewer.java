package com.nothingisreal.ultima;

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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

/**
 * A class to extract the tileset from the Commodore 64/128 version of Ultima V
 * 
 * @author Tristan Miller <psychonaut@nothingisreal.com>
 *
 */
public class RawTileViewer {

	@Option(name = "-f", aliases = { "--framewidth" }, usage = "viewer frame width in tiles", metaVar = "N")
	private int frameWidth = 88;

	@Option(name = "-g", aliases = { "--frameheight" }, usage = "viewer frame height in tiles", metaVar = "N")
	private int frameHeight = 63;

	@Option(name = "-t", aliases = { "--tilewidth" }, usage = "tile width in characters", metaVar = "N")
	private int tileWidth = 2;

	@Option(name = "-u", aliases = { "--tileheight" }, usage = "tile height in characters", metaVar = "N")
	private int tileHeight = 2;

	@Option(name = "-c", aliases = { "--charwidth" }, usage = "character width in pixels", metaVar = "N")
	private int charWidth = 8;

	@Option(name = "-d", aliases = { "--charheight" }, usage = "character height in pixels", metaVar = "N")
	private int charHeight = 8;

	@Option(name = "-o", aliases = { "--offset" }, usage = "offset", metaVar = "N")
	private int offset = 0;

	@Argument(required = true, usage = "disk image", metaVar = "FILENAME")
	private File diskImage;

	public static void main(String[] args) throws IOException, InterruptedException {
		new RawTileViewer().doMain(args);
	}

	public void doMain(String[] args) throws IOException, InterruptedException {

		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err.println("java RawTileViewer [options...] FILENAME");
			parser.printUsage(System.err);
			System.err.println();
			System.exit(1);
		}

		JFrame frame = new JFrame("Direct draw demo");

		BufferedImage canvas = new BufferedImage(frameWidth * tileWidth * charWidth,
				frameHeight * tileHeight * charHeight, BufferedImage.TYPE_INT_ARGB);

		frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		DataInputStream dataStream = new DataInputStream(new BufferedInputStream(new FileInputStream(diskImage)));

		dataStream.skipBytes(offset);

		eof: for (int tileRow = 0; tileRow < frameHeight; tileRow++) {
			for (int tileCol = 0; tileCol < frameWidth; tileCol++) {
				for (int charRow = 0; charRow < tileHeight; charRow++) {
					for (int charCol = 0; charCol < tileWidth; charCol++) {
						for (int pixelRow = 0; pixelRow < charHeight; pixelRow++) {
							int theByte = dataStream.read();
							if (theByte == -1) {
								break eof;
							}
							for (int pixelCol = 0; pixelCol < charWidth; pixelCol++) {
								int x = tileCol * tileWidth * charWidth + charCol * charWidth + charWidth - pixelCol
										- 1;
								int y = tileRow * tileHeight * charHeight + charRow * charHeight + pixelRow;
								if ((theByte & (1 << pixelCol)) != 0) {
									canvas.setRGB(x, y, Color.WHITE.getRGB());
								} else
									canvas.setRGB(x, y, Color.BLACK.getRGB());
							}
						}
					}
				}
				frame.repaint();
			}
		}

		dataStream.close();
	}

}