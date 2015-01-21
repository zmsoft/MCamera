package org.dlion.mycamera;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Main extends Activity {
	private int parentId = 3;
	private Button btnVideoBrowse;
	private Button btnImgBrowse;
	private Button btnVpn;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_camera);
		btnImgBrowse = (Button) findViewById(R.id.arc_hf_img_btnGridShow);
		btnImgBrowse.setOnClickListener(new btnListener());

		Button btnCamera = (Button) findViewById(R.id.btnVideo);
		btnCamera.setOnClickListener(new btnListener());

		btnVideoBrowse = (Button) findViewById(R.id.arc_hf_video_btnVideoBrowse);
		btnVideoBrowse.setOnClickListener(new btnListener());
		showVideoCount();
		showImgCount();

		btnVpn = (Button) findViewById(R.id.arc_hf_btnVpn);
		btnVpn.setOnClickListener(new btnListener());
	}

	// 按键监听
	class btnListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.arc_hf_img_btnGridShow:
				imgShow();
				break;
			case R.id.btnVideo:
				startRecorder();
				break;
			case R.id.arc_hf_video_btnVideoBrowse:
				videoShow();
				break;
			case R.id.arc_hf_btnVpn:
				startActivity(new Intent("android.net.vpn.SETTINGS"));
				break;
			default:
				break;
			}
		}

	}

	// 拍摄成功提示
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:// 拍照回来
			showImgCount();
			break;
		case 2:// 查看图片后回来
			showImgCount();
			break;
		case 3:// 录像回来
			showImgCount();
			showVideoCount();
			break;
		case 4:// 浏览录像回来
			showVideoCount();
		default:
			break;
		}
	}

	/*
	 * 消息提示
	 */
	private Toast toast;
	private String videoPath;
	private String imgPath;

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	// 浏览图片
	public void imgShow() {
		Intent intent = new Intent();
		intent.putExtra("path", imgPath);
		intent.setClass(this, FileShow.class);
		startActivityForResult(intent, 2);
	}

	// 图片数量
	private void showImgCount() {
		imgPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/hfdatabase/img/" + String.valueOf(parentId) + "/";
		File file = new File(imgPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File[] files = file.listFiles();
		int fileCount = files.length;
		if (fileCount == 0) {
			btnImgBrowse.setEnabled(false);
		} else {
			btnImgBrowse.setEnabled(true);
		}
		btnImgBrowse.setText("浏览图片(" + fileCount + ")");
	}

	/**
	 * 录像
	 */
	public void startRecorder() {
		Intent intent = new Intent();
		intent.setClass(Main.this, MCamera.class);
		intent.putExtra("parentId", parentId);
		startActivityForResult(intent, 3);
	}

	/**
	 * 浏览录像
	 */
	public void videoShow() {
		Intent intent = new Intent();
		intent.putExtra("path", videoPath);
		intent.setClass(Main.this, FileShow.class);
		startActivityForResult(intent, 4);
	}

	/**
	 * 录像数量
	 */
	public void showVideoCount() {
		videoPath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/hfdatabase/video/" + String.valueOf(parentId) + "/";
		File file = new File(videoPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		File[] files = file.listFiles();
		int fileCount = files.length;
		if (fileCount == 0) {
			btnVideoBrowse.setEnabled(false);
		} else {
			btnVideoBrowse.setEnabled(true);
		}
		btnVideoBrowse.setText("浏览录像(" + fileCount + ")");
	}
}