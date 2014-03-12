package com.okstate.ufas.tesing.helpers;

import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BinaryFileAccess {

	private FileOutputStream os;

	public BinaryFileAccess(String filePath, boolean append) {
		File root = Environment.getExternalStorageDirectory();
		File file = new File(root, filePath);

		try {
			file = new File(root, filePath);
			os = new FileOutputStream(file, append);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void WriteBytes(byte[] buffer) {
		try {
			os.write(buffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void CloseForBinary() {
		try {
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
