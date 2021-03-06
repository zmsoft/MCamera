package org.dlion.mycamera.ImgShow;

import android.graphics.Bitmap;

/**
 * @author dlion
 * 
 */
public class FileItem implements Comparable<FileItem> {
	private String name;
	private String path;
	private int fileType; // 0：文件，1：目录，2：上级目录
	private Bitmap image;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	@Override
	public int compareTo(FileItem another) {
		return this.name.compareTo(another.getName());
	}
}
