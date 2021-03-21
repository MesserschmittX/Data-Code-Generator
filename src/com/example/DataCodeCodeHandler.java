package com.example;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.sun.istack.internal.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;


public class DataCodeCodeHandler {
	public static final int maxDataCodeDataLength = 2953;
	
	// Function to create the QR code
    private static BufferedImage createDataCode(String data, @Nullable String path, String fileFormat,
												String charset, Map<EncodeHintType, String> hasMhap,
												BarcodeFormat barcodeFormat,
												int height, int width){

        BitMatrix matrix;
		try {
			matrix = new MultiFormatWriter().encode(new String(data.getBytes(charset), charset), barcodeFormat, width, height, hasMhap);

			if(path != null){
				MatrixToImageWriter.writeToPath(matrix, fileFormat, new File(path + "." + fileFormat).toPath());
				CodeGeneratorWindow.logCodeGen("Location: " + path + "." + fileFormat, Color.YELLOW);
			}
			CodeGeneratorWindow.logCodeGen("QR Code Generated!!! data length: " + data.length(), Color.BLUE);

	        return MatrixToImageWriter.toBufferedImage(matrix);
		} catch (Exception e) {
			CodeGeneratorWindow.showDialog(e.getMessage());
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}
        return null;
    }
    
    private static String readDataCode(String path){
    	BinaryBitmap binaryBitmap = null;
		try {
			binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path)))));
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}

    	Result result = null;
		try {
			result = new MultiFormatReader().decode(binaryBitmap);
		} catch (Exception e) {
			CodeGeneratorWindow.logCodeGen(e.getMessage(), Color.RED);
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			e.printStackTrace();
			CodeGeneratorWindow.logCodeGen(sw.toString(), Color.RED);
		}

    	return (result != null) ? result.getText() : null;
    	}
   
    // Driver code
    public static BufferedImage generateGAMSetupDataCode(String data, @Nullable String path, int height, int width) {
 
        // Encoding charset
        String charset = "UTF-8";
        
        Map<EncodeHintType, String> hashMap = null;
        BarcodeFormat barcodeFormat = BarcodeFormat.QR_CODE;
        
        String barcodeFormatSetting = CodeGeneratorWindow.codeFormatChoice.getSelectedItem();
        String errorCorrectionLevelSetting = CodeGeneratorWindow.errorCorrectionLevelChoice.getSelectedItem();
        
        if(barcodeFormatSetting.contains("AZTEC")) {
        	barcodeFormat = BarcodeFormat.AZTEC;
        	String errorCorrectionLevel = "25";
        	if(errorCorrectionLevelSetting.contains("25")) {
            	errorCorrectionLevel = "25";
            } else if(errorCorrectionLevelSetting.contains("40")) {
            	errorCorrectionLevel = "40";
            } else if(errorCorrectionLevelSetting.contains("55")) {
            	errorCorrectionLevel = "55";
            } else if(errorCorrectionLevelSetting.contains("70")) {
            	errorCorrectionLevel = "70";
            } else if(errorCorrectionLevelSetting.contains("85")) {
            	errorCorrectionLevel = "85";
            } else if(errorCorrectionLevelSetting.contains("100")) {
            	errorCorrectionLevel = "100";
            }
        	hashMap = new HashMap<>();
        	hashMap.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        } else if(barcodeFormatSetting.contains("CODABAR")) {
        	barcodeFormat = BarcodeFormat.CODABAR;
        } else if(barcodeFormatSetting.contains("CODE 128")) {
        	barcodeFormat = BarcodeFormat.CODE_128;
        } else if(barcodeFormatSetting.contains("Code 39")) {
        	barcodeFormat = BarcodeFormat.CODE_39;
        } else if(barcodeFormatSetting.contains("Code 93")) {
        	barcodeFormat = BarcodeFormat.CODE_93;
        } else if(barcodeFormatSetting.contains("Data Matrix")) {
        	barcodeFormat = BarcodeFormat.DATA_MATRIX;
        } else if(barcodeFormatSetting.contains("EAN-13")) {
        	barcodeFormat = BarcodeFormat.EAN_13;
        } else if(barcodeFormatSetting.contains("EAN-8")) {
        	barcodeFormat = BarcodeFormat.EAN_8;
        } else if(barcodeFormatSetting.contains("ITF")) {
        	barcodeFormat = BarcodeFormat.ITF;
        } else if(barcodeFormatSetting.contains("MaxiCode")) {
        	barcodeFormat = BarcodeFormat.MAXICODE;
        } else if(barcodeFormatSetting.contains("PDF417")) {
        	barcodeFormat = BarcodeFormat.PDF_417;
        	String errorCorrectionLevel = "0";
        	if(errorCorrectionLevelSetting.contains("0")) {
            	errorCorrectionLevel = "0";
            } else if(errorCorrectionLevelSetting.contains("1")) {
            	errorCorrectionLevel = "1";
            } else if(errorCorrectionLevelSetting.contains("2")) {
            	errorCorrectionLevel = "2";
            } else if(errorCorrectionLevelSetting.contains("3")) {
            	errorCorrectionLevel = "3";
            } else if(errorCorrectionLevelSetting.contains("4")) {
            	errorCorrectionLevel = "4";
            } else if(errorCorrectionLevelSetting.contains("5")) {
            	errorCorrectionLevel = "5";
            } else if(errorCorrectionLevelSetting.contains("6")) {
            	errorCorrectionLevel = "6";
            } else if(errorCorrectionLevelSetting.contains("7")) {
            	errorCorrectionLevel = "7";
            } else if(errorCorrectionLevelSetting.contains("8")) {
            	errorCorrectionLevel = "8";
            }
        	hashMap = new HashMap<>();
        	hashMap.put(EncodeHintType.ERROR_CORRECTION,errorCorrectionLevel);
        } else if(barcodeFormatSetting.contains("QR Code")) {
			String errorCorrectionLevel = ErrorCorrectionLevel.L.toString();
        	hashMap = new HashMap<>();
            if(errorCorrectionLevelSetting.contains("H")) {
            	errorCorrectionLevel = ErrorCorrectionLevel.H.toString(); //possible length 1273 ErrorCorrectionLevel.H	~30% correction
            } else if(errorCorrectionLevelSetting.contains("Q")) {
            	errorCorrectionLevel = ErrorCorrectionLevel.Q.toString(); //possible length 1663 ErrorCorrectionLevel.Q	~25% correction
            } else if(errorCorrectionLevelSetting.contains("M")) {
            	errorCorrectionLevel = ErrorCorrectionLevel.M.toString(); //possible length 2331 ErrorCorrectionLevel.M	~15% correction
            } else if(errorCorrectionLevelSetting.contains("L")) {
            	errorCorrectionLevel = ErrorCorrectionLevel.L.toString(); //possible length 2953 ErrorCorrectionLevel.L	~7% correction
            }
            
            hashMap.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
        } else if(barcodeFormatSetting.contains("RSS 14")) {
        	barcodeFormat = BarcodeFormat.RSS_14;
        } else if(barcodeFormatSetting.contains("RSS EXPANDED")) {
        	barcodeFormat = BarcodeFormat.RSS_EXPANDED;
        } else if(barcodeFormatSetting.contains("UPC-A")) {
        	barcodeFormat = BarcodeFormat.UPC_A;
        } else if(barcodeFormatSetting.contains("UPC-E")) {
        	barcodeFormat = BarcodeFormat.UPC_E;
        } else if(barcodeFormatSetting.contains("UPC/EAN")) {
        	barcodeFormat = BarcodeFormat.UPC_EAN_EXTENSION;
        }
        
        
        
        
        String fileFormat = "png";
        
        return createDataCode(data, path, fileFormat, charset, hashMap, barcodeFormat, height, width);
    }
    public static String readGAMSetupDataCode(String filePath) {
        String readString = readDataCode(filePath);
 
        System.out.println("QRCode output: " + readString);
        return readString;
    }
    
    /*	supported codes
     		AZTEC
			Aztec 2D barcode format.
			CODABAR
			CODABAR 1D format.
			CODE_128
			Code 128 1D format.
			CODE_39
			Code 39 1D format.
			CODE_93
			Code 93 1D format.
			DATA_MATRIX
			Data Matrix 2D barcode format.
			EAN_13
			EAN-13 1D format.
			EAN_8
			EAN-8 1D format.
			ITF
			ITF (Interleaved Two of Five) 1D format.
			MAXICODE
			MaxiCode 2D barcode format.
			PDF_417
			PDF417 format.
			QR_CODE
			QR Code 2D barcode format.
			RSS_14
			RSS 14
			RSS_EXPANDED
			RSS EXPANDED
			UPC_A
			UPC-A 1D format.
			UPC_E
			UPC-E 1D format.
			UPC_EAN_EXTENSION
			UPC/EAN extension format.
     */
    
}
