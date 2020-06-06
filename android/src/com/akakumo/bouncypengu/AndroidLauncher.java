package com.akakumo.bouncypengu;

import android.os.Bundle;

import android.view.View;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class AndroidLauncher extends AndroidApplication{

	protected AdView adView;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		RelativeLayout layout = new RelativeLayout(this);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
//		initialize(new bouncyPengu(), config);
		View gameView = initializeForView(new bouncyPengu(), config);
		layout.addView(gameView);

		MobileAds.initialize(this,"ca-app-pub-8939171793440266~5921965236");

		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId("ca-app-pub-8939171793440266/8261188119");

		AdRequest.Builder builder = new AdRequest.Builder();
//		builder.addTestDevice("F2F3EDF750838EAACE65B25D3FF23C52");
		RelativeLayout.LayoutParams adParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT
		);
		layout.addView(adView, adParams);

		adView.loadAd(builder.build());
		setContentView(layout);

	}

}
