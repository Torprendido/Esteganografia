package com.example.esteganografia;

import java.io.File;
import java.util.Date;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {

	public static TextView etiketa;
	public static Button boton;
	public static Button boton2;
	public static File rutaImagen;
	public static File folder;
	
	static {
		folder = new File(
        		Environment.getExternalStorageDirectory(),
        		"Esteganografia"
		);
        if (!folder.exists()) folder.mkdirs();
	} 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		boton = (Button) findViewById(R.id.boton);
		boton2 = (Button) findViewById(R.id.boton2);
		etiketa = (TextView) findViewById(R.id.etiketa);
	}
	
	public void esteganografia(View arg) {
		rutaImagen = prepararArchivo();
		generarImagen(rutaImagen);
	}
	
	public void checarImagen(View arg) {
		etiketa.setText("");
		Intent chooseFile;
		Intent intent;
		chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
		chooseFile.setType("file/*");
		intent = Intent.createChooser(chooseFile, "Choose a file");
		startActivityForResult(intent, 1);
	}
	
	private File prepararArchivo() {
		String momento = new Date().toString().replaceAll(" ", "_");
        return new File(
        		folder,
        		"hipFlaw_" + momento.replaceAll(":", "_") + ".png"
		);
	}

	private void generarImagen(File archivoLimpio) {
		Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		etiketa.setText("");
        etiketa.append(archivoLimpio.toString() + "\n");
        Uri uri = Uri.fromFile(archivoLimpio);
        camara.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(camara, 1337);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		final Bitmap imagen;
		final Transformar trans;
		if (requestCode == 1 & resultCode == RESULT_OK) {
			final Uri uri = data.getData();
			etiketa.append(uri.getPath());
			imagen = BitmapFactory.decodeFile(uri.getPath());
			trans = new Transformar(imagen);
			trans.destransformar();
		} else if (requestCode == 1337)
			if (rutaImagen.exists()) {
				imagen = BitmapFactory.decodeFile(rutaImagen.toString());
				trans = new Transformar(imagen);
				trans.trasformar();
			}	
	}
}
