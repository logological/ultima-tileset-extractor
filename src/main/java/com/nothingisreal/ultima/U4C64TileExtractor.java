package com.nothingisreal.ultima;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.google.common.io.Files;

/**
 * Extracts tiles from the C64 version of Ultima IV
 *
 * @author Tristan Miller <psychonaut@nothingisreal.com>
 *
 */
public class U4C64TileExtractor {

	@Argument(required = true, usage = "Ultima IV disk image", metaVar = "DISK_IMAGE", index = 0)
	private File diskImageFile;

	@Argument(required = true, usage = "directory to output graphics", metaVar = "OUTPUT_DIR", index = 1)
	private File outputDir;

	private byte[] diskImage;

	public static void main(String[] args) throws IOException, InterruptedException {
		new U4C64TileExtractor().doMain(args);
	}

	private void doMain(String[] args) throws IOException {
		CmdLineParser parser = new CmdLineParser(this);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			System.err
					.println("Usage: java " + this.getClass().getSimpleName() + " [options...] DISK_IMAGE OUTPUT_DIR");
			parser.printUsage(System.err);
			System.err.println();
			System.exit(1);
		}

		diskImage = Files.toByteArray(diskImageFile);

		extractTiles();
	}

	public static int findOffset(byte[] haystack, int[] needle) {
		byte[] byteNeedle = new byte[needle.length];
		for (int i = 0; i < needle.length; i++)
			byteNeedle[i] = (byte) needle[i];
		return findOffset(haystack, byteNeedle);
	}

	public static int findOffset(byte[] haystack, byte[] needle) {
		// FIXME: This doesn't actually work; I think assumes the String is
		// UTF-8 which has variable byte length
		return new String(haystack).indexOf(new String(needle));
	}

	private void extractTiles() throws IOException {
		int bitmapOffset = 0x16700;
		int colourOffset = 0x130b0;
		int i = bitmapOffset, x = 0, y = 0;
		BufferedImage tile = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		final int tileHeight = 2;
		final int tileWidth = 2;
		final int charHeight = 8;
		final int charWidth = 8;
		for (int tileIndex = 0; tileIndex < 256; tileIndex++) {
			for (int charRow = 0; charRow < tileHeight; charRow++) {
				for (int charCol = 0; charCol < tileWidth; charCol++) {
					for (int pixelRow = 0; pixelRow < charHeight; pixelRow++, i++) {
						for (int pixelCol = 0; pixelCol < charWidth; pixelCol++) {
							x = charCol * charWidth + charWidth - pixelCol - 1;
							y = charRow * charHeight + pixelRow;
							if ((diskImage[i + charRow * 4080] & (1 << pixelCol)) != 0) {
								tile.setRGB(x, y, C64Colour.values()[(diskImage[colourOffset] >> 4) & 0x0f].getRGB());
							} else {
								tile.setRGB(x, y, C64Colour.values()[diskImage[colourOffset] & 0x0f].getRGB());
							}
						}
					}
				}
			}
			colourOffset++;
			i -= 16;
			String filename = String.format("tile%03d.png", tileIndex);
			File outputFile = new File(outputDir, filename);
			ImageIO.write(tile, "png", outputFile);
		}
	}

}
