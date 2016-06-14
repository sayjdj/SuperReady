package com.haffle.superready.flyer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haffle.superready.R;
import com.haffle.superready.item.FlyerCampaign;
import com.haffle.superready.manager.ActivityManager;

public class GoodsFlyerActivity extends FragmentActivity implements  ActionBar.TabListener {

	ActivityManager activityManager = ActivityManager.getInstance();
	FlyerCampaign flyerCampaign;
	SectionsPagerAdapter mSectionsPagerAdapter;
	TextView textView_flyerName;
	RelativeLayout layout_back;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENT_THREE = 2;
	public static final int FRAGMENT_FOUR = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_flyer);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar_goods_flyer);

		activityManager.addActivity(this);

		// ImageURL을 받아온다
		Intent intent = getIntent();
		flyerCampaign = (FlyerCampaign)intent.getSerializableExtra("FLYERCAMPAIGN");

		setView();
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mSectionsPagerAdapter);
		textView_flyerName.setText(flyerCampaign.getName());
		layout_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		activityManager.deleteTopActivity();
	}

	void setView() {
		mViewPager = (ViewPager) findViewById(R.id.pager);
		textView_flyerName = (TextView)findViewById(R.id.titlebar_goodsFlyer_flyerName);
		layout_back = (RelativeLayout)findViewById(R.id.titlebar_goodsFlyer_back);
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class below).
			Fragment newFragment = null;
			Bundle bundle = new Bundle();

			switch (position) {
				case FRAGMENT_ONE:
					newFragment = new GoodsFlyerFragment();
					bundle.putString("IMAGE", flyerCampaign.getFlyer().get(0).getImage());
					newFragment.setArguments(bundle);
					break;
				case FRAGMENT_TWO:
					newFragment = new GoodsFlyerFragment();
					bundle.putString("IMAGE", flyerCampaign.getFlyer().get(1).getImage());
					newFragment.setArguments(bundle);
					break;
				case FRAGMENT_THREE:
					newFragment = new GoodsFlyerFragment();
					bundle.putString("IMAGE", flyerCampaign.getFlyer().get(2).getImage());
					newFragment.setArguments(bundle);
					break;
				case FRAGMENT_FOUR:
					newFragment = new GoodsFlyerFragment();
					bundle.putString("IMAGE", flyerCampaign.getFlyer().get(3).getImage());
					newFragment.setArguments(bundle);
					break;
				default:
					break;
			}

			return newFragment;
		}

		@Override
		public int getCount() {
			return flyerCampaign.getFlyer().size();
		}

	}
}
