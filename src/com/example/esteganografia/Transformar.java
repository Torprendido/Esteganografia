package com.example.esteganografia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.IntBuffer;

import org.jtransforms.fft.DoubleFFT_1D;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class Transformar {
	
	private final static String MENSAJE_AUTENTICO = "rico_y_delicioso";
	//private final static int N = 256;
	//private final static int MAX_LADO = 640;
	private final Bitmap imagen;
	private Bitmap imagen2;
	//private final int largo;
	//private final int alto;
	//private double[][] f;
	//private final DoubleFFT_2D fft = new DoubleFFT_2D(N, N);
	//private final DoubleDCT_2D dct = new DoubleDCT_2D(N, N);
	private double[] f;
	private final DoubleFFT_1D fft;
	private final int largo;
	private int indexF = 0;
	
	public Transformar(Bitmap imagen) {
		this.imagen = imagen;
		imagen2 = Bitmap.createBitmap(
				imagen.getWidth(),
				imagen.getHeight(),
				imagen.getConfig()
		);
		largo = imagen.getHeight()*imagen.getWidth();
		fft = new DoubleFFT_1D(largo);
		rellenarF();
	}
	
	private void comprobarMensaje() {
		indexF = 0;
		for (int i = 0; i < MENSAJE_AUTENTICO.length(); i ++) {
			if (!iLsb(MENSAJE_AUTENTICO.charAt(i))) {
				MainActivity.etiketa.append("La imagen <<NO ES AUTENTICA>> !!\n");
				return;
			}
		}
	}
	
	public void destransformar() {
		fft.realInverse(f, true);
		comprobarMensaje();
	}
	
	int borrame = 0;
	private void guardarImagenTransformada() {
		File file = new File(MainActivity.folder,"chois" + borrame++ + MainActivity.rutaImagen.getName());
	    try {	
	    	FileOutputStream fos = new FileOutputStream(file);
	    	imagen2.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    fos.flush();
		    fos.close();
		} catch (FileNotFoundException e) {
			Log.i("chois", e.getMessage());
		} catch (IOException e) {
			Log.i("chois", e.getMessage());
		}
	    
	}
	
	
	private boolean iLsb(char c) {
		String charBin = Integer.toBinaryString(c);
		Log.i("chois", charBin);
		for (int i = 0; i < charBin.length(); i ++) {
			long aux = (f[indexF] < 0 ? -1*Math.round(f[indexF]) : Math.round(f[indexF])) % 2; 
			Log.i(charBin.charAt(i) + "", aux + "");
			if (charBin.charAt(i) - 48 != aux)
				return false;
			indexF ++;
		}
		return true;
	}
	
	private void insertarMensaje() {
		for (int i = 0; i < MENSAJE_AUTENTICO.length(); i ++)
			lsb(MENSAJE_AUTENTICO.charAt(i));
	}
	
	private void lsb(char c) {
		String charBin = Integer.toBinaryString(c);
		for (int i = 0; i < charBin.length(); i ++) {
			long aux = Math.round(f[indexF]);
			if (charBin.charAt(i) == '1' & aux % 2 == 0)
				f[indexF] = aux < 0 ? aux + 1 : aux - 1;
			else if (charBin.charAt(i) == '0' & aux % 2 == 1)
				f[indexF] = aux - 1;
			else if (charBin.charAt(i) == '0' & aux % 2 == -1)
				f[indexF] = aux + 1;
			else
				f[indexF] = aux;
			indexF ++;
		}
		for (int i = indexF; i < largo; i ++)
			f[i] = Math.round(f[i]);
	}
	
	/*
	private void perenme() {
		IntBuffer sourceData2 = IntBuffer.allocate(largo);
		imagen2.copyPixelsToBuffer(sourceData2);
		for (int i = 0; i < largo; i ++)
			if (sourceData2.array()[i] != Math.round(f[i])) {
				Log.i("hip" + i, sourceData2.array()[i] + ", " + (int) Math.round(f[i]));
			}
	}*/
	
	private void redondearF() {
		for (int x = 0; x < largo; x ++)
			f[x] = Math.round(f[x]);
	}
	
	private void rellenarF() {
		IntBuffer sourceData = IntBuffer.allocate(largo);
		imagen.copyPixelsToBuffer(sourceData);
		f = new double[largo];
		for (int x = 0; x < largo; x ++)
			f[x] = sourceData.array()[x];
	}
	
	public void trasformar() {
		fft.realForward(f);
		insertarMensaje();
		fft.realInverse(f, true);
		int[] ff = new int[largo];
		for (int i = 0; i < largo; i ++) {
			ff[i] = (int) Math.round(f[i]);
		}
		IntBuffer data = IntBuffer.wrap(ff);
		imagen2.copyPixelsFromBuffer(data);
		guardarImagenTransformada();
	}
	
	private void perenme() {
		int[] ff = new int[largo];
		for (int i = 0; i < largo; i ++) {
			ff[i] = (int) Math.round(f[i]);
		}
		IntBuffer data = IntBuffer.wrap(ff);
		imagen2 = Bitmap.createBitmap(
				imagen.getWidth(),
				imagen.getHeight(),
				imagen.getConfig()
		);
		imagen2.copyPixelsFromBuffer(data);
		guardarImagenTransformada();
	}
	
	
	
	
	/*
	public Transformar(Bitmap imagen) {
		this.imagen = imagen;
		imagen2 = Bitmap.createBitmap(imagen.getWidth(), imagen.getHeight(), imagen.getConfig());
		imagen2.setPremultiplied(false);
		largo = imagen.getWidth();
		alto = imagen.getHeight();
	}

	private boolean comprobarDimenciones() {
		if (largo < N | alto < N) {
			MainActivity.etiketa.append("La imagen es muy pequenia < (128 x 128)px\n");
			return false;
		}
		if (largo > MAX_LADO | alto > MAX_LADO) {
			MainActivity.etiketa.append("La imagen es muy grande < (640 x 640)px\n");
			return false;
		}
		return true;
	}
	
	private boolean comprobarCaracter(char c) {
		Log.i("O", f[N/2][N/2] + "");
		int colorSignificativo = ((int) f[N/2][N/2]) % 256;
		return colorSignificativo == c;
	}
	
	public void destransformarPorBloques() {
		if (comprobarDimenciones()) {
			int indiceMensaje = 0;
			final int complementoLargo = largo % N == 0 ? 0 : N - (largo % N);
			final int complementoAlto = alto % N == 0 ? 0 : N - (alto % N);
			for (int i = 0; i < largo + complementoLargo; i += N)
				for (int j = 0; j < alto + complementoAlto; j += N) {
					rellenarF(i, j);
					Log.i("N", f[N/2][N/2] + "");
					fft.realForward(f);
					//dct.forward(f, true);
					redondearF();
					if (!comprobarCaracter(MENSAJE_AUTENTICO.charAt(indiceMensaje ++))) {
						MainActivity.etiketa.append("La imagen no es <<AUTENTICA>>!!\n");
						return;
					}
					indiceMensaje ++;
					if (indiceMensaje == MENSAJE_AUTENTICO.length()) indiceMensaje = 0;
				}
		}
	}
	
	private void insertarCaracter(char c) {
		int colorSignificativo = ((int) f[N/2][N/2]) % 256;
		f[N/2][N/2] = f[N/2][N/2] - colorSignificativo;
		f[N/2][N/2] = f[N/2][N/2] + c;
	}
	
	private void guardarImagenTransformada() {
	    File file = new File(MainActivity.folder,"chois" + MainActivity.rutaImagen.getName());
	    try {
	    	FileOutputStream fOut = new FileOutputStream(file);
			imagen2.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		    fOut.flush();
		    fOut.close();
		} catch (FileNotFoundException e) {
			Log.i("chois", e.getMessage());
		} catch (IOException e) {
			Log.i("chois", e.getMessage());
		}
	}
	
	private void redondearF() {
		for (int x = 0; x < N; x ++)
			for (int y = 0; y < N; y ++)
				f[x][y] = Math.round(f[x][y]);
	}
	
	private void rellenarF(int i, int j) {
		f = new double[N][N];
		for (int x = i; x < i + N; x ++)
			for (int y = j; y < j + N; y ++)
				f[x - i][y - j] = x >= largo | y >= alto ? 0 : imagen.getPixel(x, y);
	}
	
	public void trasformarPorBloques() {
		if (comprobarDimenciones()) {
			final int complementoLargo = largo % N == 0 ? 0 : N - (largo % N);
			final int complementoAlto = alto % N == 0 ? 0 : N - (alto % N);
			int indiceMensaje = 0;
			for (int i = 0; i < largo + complementoLargo; i += N)
				for (int j = 0; j < alto + complementoAlto; j += N) {
					rellenarF(i, j);
					Log.i("psps0", f[N/2][N/2] + "");
					fft.realForward(f);
					//dct.forward(f, true);
					Log.i("bsbs1", f[N/2][N/2] + "");
					redondearF();
					Log.i("bsbs2", f[N/2][N/2] + "");
					insertarCaracter(MENSAJE_AUTENTICO.charAt(indiceMensaje ++));
					Log.i("dsds3", f[N/2][N/2] + "");
					if (indiceMensaje == MENSAJE_AUTENTICO.length()) indiceMensaje = 0;
					fft.realInverse(f, true);
					//dct.inverse(f, true);
					for (int x = i; x < i + N; x ++)
						for (int y = j; y < j + N; y ++)
							if (x < largo & y < alto) {
								imagen2.setPixel(x, y, (int) Math.round(Math.round(f[x - i][y - j])));
								if (x == i + N/2 & y == j + N/2) {
									Log.i("Flaw", f[N/2][N/2] + "");
									Log.i("Flip", Math.round(f[N/2][N/2]) + "");
									Log.i("Flip2", (int) Math.round(f[N/2][N/2]) + "");
									Log.i("chois", x + ", " + y);
									Log.i("hap", imagen2.getPixel(x, y) + "");
								}
							}
				}
			guardarImagenTransformada();
		}
	}
	*/
	
	
}
