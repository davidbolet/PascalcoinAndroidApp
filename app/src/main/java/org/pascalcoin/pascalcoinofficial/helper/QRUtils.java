package org.pascalcoin.pascalcoinofficial.helper;

import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRUtils {

	public static Bitmap encodeAsBitmap(String str, final int width) throws WriterException {
		BitMatrix result;
		try {
			result = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, width, width);
		} catch (IllegalArgumentException iae) {
			// Unsupported format
			return null;
		}
		int w = result.getWidth();
		int h = result.getHeight();
		int[] pixels = new int[w * h];
		for (int y = 0; y < h; y++) {
			int offset = y * w;
			for (int x = 0; x < w; x++) {
				pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
			}
		}
		Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
		return bitmap;
	}

}
