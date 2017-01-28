package com.nothingisreal.ultima;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.common.io.Files;

/**
 * Visualizes an entire disk image as bitmapped tiles
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
	private File diskImageFile;

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

		viewImage(diskImageFile);
	}

	private void viewImage(File diskImageFile) throws IOException {
		final byte[] diskImage = Files.toByteArray(diskImageFile);

		final JFrame frame = new JFrame(diskImageFile.getName());

		final BufferedImage canvas = new BufferedImage(frameWidth * tileWidth * charWidth,
				frameHeight * tileHeight * charHeight, BufferedImage.TYPE_INT_ARGB);

		frame.getContentPane().add(new JLabel(new ImageIcon(canvas)));
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		drawImage(frame, canvas, diskImage, offset);

		frame.addKeyListener(new KeyListener() {
	        public void keyTyped(KeyEvent e) {
	            //System.out.println("Key typed code=" + e.getKeyCode() + ", char=" + e.getKeyChar());
	        	if (e.getKeyCode() == 0 && e.getKeyChar() == 0x1b)
	        		System.exit(0);
	        }

	        public void keyPressed(KeyEvent e) {
	        	switch (e.getKeyCode()) {
	        	case 37: // Left
	        		offset -= 1;
	        		break;
	        	case 39: // Right
	        		offset += 1;
	        		break;
	        	case 38: // Up
	        		offset -= charHeight;
	        		break;
	        	case 40: // Down
	        		offset += charHeight;
	        		break;
	        	case 33: // PgUp
	        		offset -= charHeight * tileHeight * tileWidth;
	        		break;
	        	case 34: // PgDn
	        		offset += charHeight * tileHeight * tileWidth;
	        		break;
	        	case 36: // Home
	        	    offset = 0;
	        	    break;
	        	case 35: // End
	        	    offset = diskImage.length - canvas.getHeight() * canvas.getWidth() / charWidth;
	        	    break;
	        	}
	        	if (offset < 0) {
                    offset = 0;
                }
	        	if (offset >= diskImage.length) {
                    offset = diskImage.length;
                }
	        	System.out.println("Offset = " + offset);
	        	drawImage(frame, canvas, diskImage, offset);
	        }

	        public void keyReleased(KeyEvent e) {
	        }

	    });
	}

	private void drawImage(JFrame frame, BufferedImage canvas, byte[] diskImage, int offset) {
		if (offset < 0 || offset >= diskImage.length) {
            return;
        }
		int i = offset;
		int x = 0, y = 0;
		for (int tileRow = 0; tileRow < frameHeight; tileRow++) {
			for (int tileCol = 0; tileCol < frameWidth; tileCol++) {
				for (int charRow = 0; charRow < tileHeight; charRow++) {
					for (int charCol = 0; charCol < tileWidth; charCol++) {
						for (int pixelRow = 0; pixelRow < charHeight; pixelRow++, i++) {
							for (int pixelCol = 0; pixelCol < charWidth; pixelCol++) {
								x = tileCol * tileWidth * charWidth + charCol * charWidth + charWidth - pixelCol
										- 1;
								y = tileRow * tileHeight * charHeight + charRow * charHeight + pixelRow;
								if (i >= diskImage.length) {
									canvas.setRGB(x, y, Color.GRAY.getRGB());
	                            }
								else if ((diskImage[i] & (1 << pixelCol)) != 0) {
									canvas.setRGB(x, y, Color.WHITE.getRGB());
								}
                                else {
                                    canvas.setRGB(x, y, Color.BLACK.getRGB());
                                }
							}
						}
					}
				}
			}
		}
		frame.repaint();
	}

}