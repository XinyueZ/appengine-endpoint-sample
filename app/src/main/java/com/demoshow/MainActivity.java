package com.demoshow;

import java.io.IOException;

import android.app.ProgressDialog;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.os.AsyncTaskCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.demoshow.backend.myApi.MyApi;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;


public class MainActivity extends ActionBarActivity {
	private EditText mInputTv;
	private TextView mOutputTv;
	private ProgressDialog mPb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mInputTv = (EditText) findViewById(R.id.input_tv);
		mOutputTv = (TextView) findViewById(R.id.output_tv);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		dismissPb();
	}

	private void dismissPb() {
		if(mPb != null && mPb.isShowing()) {
			mPb.dismiss();
		}
	}

	public void call(View view) {
		mPb = ProgressDialog.show(this, null, null);
		Editable input = mInputTv.getText();
		EndpointsAsyncTask task = new EndpointsAsyncTask() {

			@Override
			protected void onPostExecute(String s) {
				super.onPostExecute(s);
				mOutputTv.setText(s);
				dismissPb();
			}

		};
		AsyncTaskCompat.executeParallel(task, input.toString());
	}
}

class EndpointsAsyncTask extends AsyncTask<String, String, String> {

	private static MyApi myApiService = null;

	@Override
	protected String doInBackground(String... params) {
		if (myApiService == null) {  // Only do this once
			MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(),
					null)
					;
//					.setRootUrl("http://10.0.2.2:8080/_ah/api/").setGoogleClientRequestInitializer(
//							new GoogleClientRequestInitializer() {
//								@Override
//								public void initialize(
//										AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
//									abstractGoogleClientRequest.setDisableGZipContent(true);
//								}
//							});

			myApiService = builder.build();
		}
		String name = params[0] ;
		try {
			return myApiService.sayHi(name).execute().getData();
		} catch (IOException e) {
			return e.getMessage();
		}
	}
}
