package com.example.cameraapp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {
	
	private Camera mCamera;
	private Preview mPreview;
	private static final String TAG = "MainActivity";
	//private PictureCallback mPicture;
	ImageButton ibCapture;
	FrameLayout preview;
	//TextView tvAccel, tvGyro;
	//SensorManager mSensorManager;
	//Sensor mAccel, mGyro;
	int stillCount = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//initializeSensorView();
		initializeCamera();
		
		ibCapture = (ImageButton) findViewById(R.id.ibCapture);
		ibCapture.setOnClickListener(this);
	}

	/*private void initializeSensorView() {
		tvAccel = (TextView) findViewById(R.id.tvAccel);
		tvGyro = (TextView) findViewById(R.id.tvGyro);
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL); //list all available sensors on the list deviceSensors
		//listSensors(deviceSensors, tvAccel);
		
		mGyro = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE); //Get an instance of default gyroscope sensor
	}*/
	
	@Override
	public void onClick(View v) {
		mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
		ibCapture.setEnabled(false);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mCamera != null) {
			//Release sensors when app is in background
			mCamera.release();
			//mSensorManager.unregisterListener(this);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		//ERROR HERE. ONRESUME THE APP CRASHES 
		//mCamera = getCameraInstance();
		//mSensorManager.registerListener(this, mAccel, SensorManager.SENSOR_DELAY_NORMAL);
		//mSensorManager.registerListener(this, mGyro, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	//Display all the available sensors the TextView
	/*private void listSensors(List<Sensor> deviceSensors, TextView tvDisplay) {
		for(int i = 1; i < deviceSensors.size(); i++) {
			tvDisplay.append("\n" + deviceSensors.get(i).getName());
		}
	}*/
	

	/*public void onAccuracyChanged(Sensor arg0, int arg1) {
		// Do something here if sensor accuracy changes
	}*/


	/*public void onSensorChanged(SensorEvent event) {
		// Accelerometer sensor has 3 values, one for each axis
		float x_accel = event.values[0];
		float y_accel = event.values[1];
		float z_accel = event.values[2];
		double force = Math.sqrt(x_accel*x_accel +y_accel*y_accel+z_accel*z_accel) - 9.81; //Linear acceleration index
		tvAccel.setText("Accelerometer: " + 
						"\n" +"x: " + x_accel + "\t m/s2" + 
						"\n" +"y: " + y_accel + "\t m/s2" + 
						"\n" +"z: " + z_accel + "\t m/s2" +
						"\n" +"Force: " + force);
		}
		//Gyroscope sensor has 3 values, one for each axis
		float x_gyro = event.values[0];
		float y_gyro = event.values[1];
		float z_gyro = event.values[2];
		tvGyro.setText("Gyroscope: " +
						"\n"+"x: " + x_gyro + "\trad/s" +
						"\n"+"y: " + y_gyro + "\trad/s" +
						"\n"+"y: " + z_gyro + "\trad/s");
		
	}*/

	private void initializeCamera() {
		//Create an instance of camera
		mCamera = getCameraInstance();
				
		//Create our Preview view and set it as the content of our activity
		mPreview = new Preview(this, mCamera);
		preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			Log.d(TAG, "onShutter'd");
		}
	};
	
	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			Log.d(TAG, "onPictureTaken - raw with data = " + ((data != null) ? data.length : " NULL"));
		}
	};
	
	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			FileOutputStream outStream = null;
			String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
			try {
				// write to local sandbox file system
				// Or write to sdcard
				outStream = new FileOutputStream(String.format("/storage/sdcard0/DCIM/Camera/IMG_" + timeStamp + "_" + stillCount + ".jpg",
						System.currentTimeMillis()));
				outStream.write(data);
				outStream.close();
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);			
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
			}
			Log.d(TAG, "onPictureTaken - jpeg");
			
			try {
				mCamera.startPreview();
				stillCount++;
				
				if(stillCount < 10) {
					mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
				} else {
					stillCount = 0;
					ibCapture.setEnabled(true);
				}
			} catch(Exception e) {
				Log.d(TAG, "Error starting preview: " + e.toString());
			}
		}
	};
	
	public static Camera getCameraInstance() {
		Camera c = null;
		
		try {
			c = Camera.open();// attempt to get the camera instance
		} catch (Exception e) {
			//Camera not available, in use or does not exist
		}
		return c; //return null if camera is unavailable
	}


}
