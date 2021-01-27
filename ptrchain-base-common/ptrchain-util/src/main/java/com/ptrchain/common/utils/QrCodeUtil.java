package com.ptrchain.common.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.EncodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/**
 * 
 * @author simmon 20161122
 *
 */
public class QrCodeUtil {
	private static Integer onColor = 0xFF000000;  //前景色  
	private static Integer offColor = 0xFFFFFFFF; //背景色  
	public static void createQrCodeFile(String content, int width, int height, String filePath) {
		try {
			if (width == 0) {
				width = 500;
			}
			if (height == 0) {
				height = 500;
			}
			QrCodeUtil zp = new QrCodeUtil();
			BufferedImage bi = null;
			BufferedImage bim = zp.getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, width, height,
					zp.getDecodeHintType());
			bi = bim;
			ImageIO.write(bi, "jpeg", new File(filePath));
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	public static BufferedImage createQrCode(String content, int width, int height) {
		try {
			if (width == 0) {
				width = 500;
			}
			if (height == 0) {
				height = 500;
			}
			QrCodeUtil zp = new QrCodeUtil();
			BufferedImage bim = zp.getQR_CODEBufferedImage(content, BarcodeFormat.QR_CODE, width, height,
					zp.getDecodeHintType());
			return bim;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 二维码的解析
	 */
	public static void parseQR_CODEImage(BufferedImage image_) {
		try {
			MultiFormatReader formatReader = new MultiFormatReader();

			BufferedImage image = image_;

			LuminanceSource source = new BufferedImageLuminanceSource(image);
			Binarizer binarizer = new HybridBinarizer(source);
			BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

			Map hints = new HashMap();
			hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

			Result result = formatReader.decode(binaryBitmap, hints);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 将二维码生成为文件
	 * 
	 * @param bm
	 * @param imageFormat
	 * @param file
	 */
	public void decodeQR_CODE2ImageFile(BitMatrix bm, String imageFormat, File file) {
		try {
			if (null == file || file.getName().trim().isEmpty()) {
				throw new IllegalArgumentException("文件异常，或扩展名有问题！");
			}

			BufferedImage bi = fileToBufferedImage(bm);
			ImageIO.write(bi, "jpeg", file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建初始化二维码
	 * 
	 * @param bm
	 * @return
	 */
	public BufferedImage fileToBufferedImage(BitMatrix bm) {
		BufferedImage image = null;
		try {
			int w = bm.getWidth(), h = bm.getHeight();
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					image.setRGB(x, y, bm.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * 生成二维码bufferedImage图片
	 * 
	 * @param content
	 *            编码内容
	 * @param barcodeFormat
	 *            编码类型
	 * @param width
	 *            图片宽度
	 * @param height
	 *            图片高度
	 * @param hints
	 *            设置参数
	 * @return
	 */
	public BufferedImage getQR_CODEBufferedImage(String content, BarcodeFormat barcodeFormat, int width, int height,
			Map<EncodeHintType, ?> hints) {
		MultiFormatWriter multiFormatWriter = null;
		BitMatrix bm = null;
		BufferedImage image = null;
		try {
			multiFormatWriter = new MultiFormatWriter();

			// 参数顺序分别为：编码内容，编码类型，生成图片宽度，生成图片高度，设置参数
			bm = multiFormatWriter.encode(content, barcodeFormat, width, height, hints);

			int w = bm.getWidth();
			int h = bm.getHeight();
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

			// 开始利用二维码数据创建Bitmap图片，分别设为黑（0xFFFFFFFF）白（0xFF000000）两色
			double r =  Math.sqrt(Math.pow(width,2)+Math.pow(height,2))/2;
			int bColor = 255;
			double halfW = width/2;
			double halfH = height/2;
			
			for (int x = 0; x < w; x++) {
				for (int y = 0; y < h; y++) {
					double cr  = Math.sqrt(Math.pow(x-halfW,2)+Math.pow(y-halfH,2));
					Double crpD =bColor- bColor*cr/r;
					int crpoint =crpD.intValue();
					image.setRGB(x, y, bm.get(x, y) ? onColor : offColor);
				}
			}
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return image;
	}

	/**
	 * 设置二维码的格式参数
	 * 
	 * @return
	 */
	public Map<EncodeHintType, Object> getDecodeHintType() {
		// 用于设置QR二维码参数
		Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
		// 设置QR二维码的纠错级别（H为最高级别）具体级别信息
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
		// 设置编码方式
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.MAX_SIZE, 350);
		hints.put(EncodeHintType.MIN_SIZE, 100);

		return hints;
	}

}
