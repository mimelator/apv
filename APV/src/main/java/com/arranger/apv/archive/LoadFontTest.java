package com.arranger.apv.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.arranger.apv.test.APVPluginTest;

/**
 * Might not implement this route so no method is marked as a Test right now
 */
public class LoadFontTest extends APVPluginTest {

	public LoadFontTest() {
	}

	public void loadFont() throws Exception {
		UnZip unZip = new UnZip();
		unZip.unZipIt("data/ArialUnicodeMS.vlw.zip", "data/fonts");
	}

	@Override
	protected void setFrameIndexes() {

	}

	/**
	 * https://www.mkyong.com/java/how-to-decompress-files-from-a-zip-file/
	 */
	public class UnZip {
		List<String> fileList;

		public void unZipIt(String zipFile, String outputFolder) throws Exception {

			byte[] buffer = new byte[1024];

			// create output directory is not exists
			File folder = new File(outputFolder);
			if (!folder.exists()) {
				folder.mkdir();
			}
			
			File zipF = new File(zipFile);
			System.out.println(zipF.getAbsolutePath());

			// get the zip file content
			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipF));
			// get the zipped file list entry
			ZipEntry ze = zis.getNextEntry();

			while (ze != null) {

				String fileName = ze.getName();
				File newFile = new File(outputFolder + File.separator + fileName);

				System.out.println("file unzip : " + newFile.getAbsoluteFile());

				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();
			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		}
	}

}
