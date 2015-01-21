package org.dlion.mycamera;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MCamera extends Activity {
	private Button mVideoStartBtn;
	private SurfaceView mSurfaceview;
	private MediaRecorder mMediaRecorder;
	private SurfaceHolder mSurfaceHolder;
	private File mRecVedioPath;
	private File mRecAudioFile;
	private TextView timer;
	private int hour = 0;
	private int minute = 0;
	private int second = 0;
	private boolean bool;
	private int parentId;
	protected Camera camera;
	protected boolean isPreview;
	private Drawable iconStart;
	private Drawable iconStop;
	private boolean isRecording = true; // true��ʾû��¼�񣬵����ʼ��false��ʾ����¼�񣬵����ͣ
	private SurfaceHolder holder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * ȫ����ʾ
		 */
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.map_video);
		iconStart = getResources().getDrawable(
				R.drawable.arc_hf_btn_video_start);
		iconStop = getResources().getDrawable(R.drawable.arc_hf_btn_video_stop);

		parentId = getIntent().getIntExtra("parentId", 0);
		timer = (TextView) findViewById(R.id.arc_hf_video_timer);
		mVideoStartBtn = (Button) findViewById(R.id.arc_hf_video_start);
		mSurfaceview = (SurfaceView) this.findViewById(R.id.arc_hf_video_view);

		// ���ü�ʱ�����ɼ�
		timer.setVisibility(View.GONE);

		// ���û���·��
		mRecVedioPath = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/hfdatabase/video/temp/");
		if (!mRecVedioPath.exists()) {
			mRecVedioPath.mkdirs();
		}

		// ��Ԥ����ͼ
		holder = mSurfaceview.getHolder();
		holder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				if (camera != null) {
					if (isPreview) {
						camera.stopPreview();
						isPreview = false;
					}
					camera.release();
					camera = null; // �ǵ��ͷ�
				}
				mSurfaceview = null;
				mSurfaceHolder = null;
				mMediaRecorder = null;
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				try {
					camera = Camera.open();
					Camera.Parameters parameters = camera.getParameters();
					parameters.setPreviewFrameRate(5); // ÿ��5֡
					parameters.setPictureFormat(PixelFormat.JPEG);// ������Ƭ�������ʽ
					parameters.set("jpeg-quality", 85);// ��Ƭ����
					camera.setParameters(parameters);
					camera.setPreviewDisplay(holder);
					camera.startPreview();
					isPreview = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
				mSurfaceHolder = holder;
			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format,
					int width, int height) {
				mSurfaceHolder = holder;
			}
		});
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mVideoStartBtn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isRecording) {
					/*
					 * �����ʼ¼��
					 */
					if (isPreview) {
						camera.stopPreview();
						camera.release();
						camera = null;
					}
					second = 0;
					minute = 0;
					hour = 0;
					bool = true;
					if (mMediaRecorder == null)
						mMediaRecorder = new MediaRecorder();
					else
						mMediaRecorder.reset();
					mMediaRecorder.setPreviewDisplay(mSurfaceHolder
							.getSurface());
					mMediaRecorder
					.setVideoSource(MediaRecorder.VideoSource.CAMERA);
					mMediaRecorder
					.setAudioSource(MediaRecorder.AudioSource.MIC);
					mMediaRecorder
					.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
					mMediaRecorder
					.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
					mMediaRecorder
					.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					mMediaRecorder.setVideoSize(320, 240);
					mMediaRecorder.setVideoFrameRate(15);
					try {
						mRecAudioFile = File.createTempFile("Vedio", ".3gp",
								mRecVedioPath);
					} catch (IOException e) {
						e.printStackTrace();
					}
					mMediaRecorder.setOutputFile(mRecAudioFile
							.getAbsolutePath());
					try {
						mMediaRecorder.prepare();
						timer.setVisibility(View.VISIBLE);
						handler.postDelayed(task, 1000);
						mMediaRecorder.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					showMsg("��ʼ¼��");
					mVideoStartBtn.setBackgroundDrawable(iconStop);
					isRecording = !isRecording;
				} else {
					/*
					 * ���ֹͣ
					 */
					try {
						bool = false;
						mMediaRecorder.stop();
						timer.setText(format(hour) + ":" + format(minute) + ":"
								+ format(second));
						mMediaRecorder.release();
						mMediaRecorder = null;
						videoRename();
					} catch (Exception e) {
						e.printStackTrace();
					}
					isRecording = !isRecording;
					mVideoStartBtn.setBackgroundDrawable(iconStart);
					showMsg("¼����ɣ��ѱ���");

					try {
						camera = Camera.open();
						Camera.Parameters parameters = camera.getParameters();
						parameters.setPreviewFrameRate(5); // ÿ��5֡
						parameters.setPictureFormat(PixelFormat.JPEG);// ������Ƭ�������ʽ
						parameters.set("jpeg-quality", 85);// ��Ƭ����
						camera.setParameters(parameters);
						camera.setPreviewDisplay(mSurfaceHolder);
						camera.startPreview();
						isPreview = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		Button btnImgStart = (Button) findViewById(R.id.arc_hf_img_start);
		btnImgStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mMediaRecorder != null) {
					try {
						bool = false;
						mMediaRecorder.stop();
						timer.setText(format(hour) + ":" + format(minute) + ":"
								+ format(second));
						mMediaRecorder.release();
						mMediaRecorder = null;
						videoRename();
					} catch (Exception e) {
						e.printStackTrace();
					}
					isRecording = !isRecording;
					mVideoStartBtn.setBackgroundDrawable(iconStart);
					showMsg("¼����ɣ��ѱ���");

					try {
						camera = Camera.open();
						Camera.Parameters parameters = camera.getParameters();
						parameters.setPreviewFrameRate(5); // ÿ��5֡
						parameters.setPictureFormat(PixelFormat.JPEG);// ������Ƭ�������ʽ
						parameters.set("jpeg-quality", 85);// ��Ƭ����
						camera.setParameters(parameters);
						camera.setPreviewDisplay(mSurfaceHolder);
						camera.startPreview();
						isPreview = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (camera != null) {
					camera.autoFocus(null);
					camera.takePicture(null, null, new PictureCallback() {
						@Override
						public void onPictureTaken(byte[] data, Camera camera) {
							Bitmap bitmap = BitmapFactory.decodeByteArray(data,	0, data.length);
							//Matrix matrix = new Matrix();
							// ��������
							//matrix.postScale(5f, 4f);
							/*bitmap = Bitmap.createBitmap(bitmap, 0, 0,
									bitmap.getWidth(), bitmap.getHeight(),
									matrix, true);*/
							String path = Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ "/DCIM/Camera/";
							/*+ String.valueOf(parentId)*/ 
							//+ "/";
							String fileName = new SimpleDateFormat(
									"yyyyMMddHHmmss").format(new Date())
									+ ".jpg";
							Log.d("qxq", "path = "+path+" filename = "+fileName);
							File out = new File(path);
							if (!out.exists()) {
								out.mkdirs();
							}
							out = new File(path, fileName);
							try {
								FileOutputStream outStream = new FileOutputStream(out);
								bitmap.compress(CompressFormat.JPEG, 100, outStream);
								outStream.close();
								camera.startPreview();
							} catch (Exception e) {
								e.printStackTrace();
							}
							showMsg("���ճɹ�");
						}
					}); 
				}
			}
		});
	}

	/*
	 * ��Ϣ��ʾ
	 */
	private Toast toast;

	public void showMsg(String arg) {
		if (toast == null) {
			toast = Toast.makeText(this, arg, Toast.LENGTH_SHORT);
		} else {
			toast.cancel();
			toast.setText(arg);
		}
		toast.show();
	}

	/*
	 * ����video�ļ�����
	 */
	protected void videoRename() {
		String path = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/hfdatabase/video/"
				+ String.valueOf(parentId) + "/";
		String fileName = new SimpleDateFormat("yyyyMMddHHmmss")
		.format(new Date()) + ".3gp";
		File out = new File(path);
		if (!out.exists()) {
			out.mkdirs();
		}
		out = new File(path, fileName);
		if (mRecAudioFile.exists())
			mRecAudioFile.renameTo(out);
	}

	/*
	 * ��ʱ�����ã�ʵ�ּ�ʱ
	 */
	private Handler handler = new Handler();
	private Runnable task = new Runnable() {
		public void run() {
			if (bool) {
				handler.postDelayed(this, 1000);
				second++;
				if (second >= 60) {
					minute++;
					second = second % 60;
				}
				if (minute >= 60) {
					hour++;
					minute = minute % 60;
				}
				timer.setText(format(hour) + ":" + format(minute) + ":"
						+ format(second));
			}
		}
	};

	/*
	 * ��ʽ��ʱ��
	 */
	public String format(int i) {
		String s = i + "";
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}

	/*
	 * ��д���ؼ�����
	 */
	@Override
	public void onBackPressed() {
		if (mMediaRecorder != null) {
			mMediaRecorder.stop();
			mMediaRecorder.release();
			mMediaRecorder = null;
			videoRename();
		}
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		onBackPressed();
	}
}