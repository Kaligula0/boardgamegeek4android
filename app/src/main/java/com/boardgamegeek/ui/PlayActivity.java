package com.boardgamegeek.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;

import com.boardgamegeek.events.PlayDeletedEvent;
import com.boardgamegeek.events.PlaySelectedEvent;
import com.boardgamegeek.events.PlaySentEvent;
import com.boardgamegeek.provider.BggContract;
import com.boardgamegeek.service.SyncService;
import com.boardgamegeek.util.ActivityUtils;
import com.boardgamegeek.util.PreferencesUtils;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import icepick.Icepick;
import icepick.State;

public class PlayActivity extends SimpleSinglePaneActivity {
	private BroadcastReceiver broadcastReceiver;
	@State int playId = BggContract.INVALID_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Icepick.restoreInstanceState(this, savedInstanceState);

		EventBus.getDefault().removeStickyEvent(PlaySelectedEvent.class);

		final int originalPlayId = getPlayId();
		if (savedInstanceState == null) {
			final ContentViewEvent event = new ContentViewEvent().putContentType("Play");
			if (originalPlayId != BggContract.INVALID_ID) {
				event.putContentId(String.valueOf(originalPlayId));
			}
			Answers.getInstance().logContentView(event);
		}

		broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				int playId = PreferencesUtils.getNewPlayId(PlayActivity.this, originalPlayId);
				if (playId != BggContract.INVALID_ID) {
					newPlayId(playId);
				}
			}
		};
	}

	private int getPlayId() {
		if (playId != BggContract.INVALID_ID) {
			return playId;
		}
		return getIntent().getIntExtra(ActivityUtils.KEY_PLAY_ID, BggContract.INVALID_ID);
	}

	@Override
	protected void onStart() {
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver((broadcastReceiver), new IntentFilter(SyncService.ACTION_PLAY_ID_CHANGED));
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Icepick.saveInstanceState(this, outState);
	}

	@Override
	protected void onStop() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
		super.onStop();
	}

	private void newPlayId(int playId) {
		if (playId != BggContract.INVALID_ID) {
			this.playId = playId;
			((PlayFragment) getFragment()).setNewPlayId(playId);
			final ContentViewEvent event = new ContentViewEvent().putContentType("Play");
			if (this.playId != BggContract.INVALID_ID) {
				event.putContentId(String.valueOf(playId));
			}
			Answers.getInstance().logContentView(event);
		}
	}

	@Override
	protected Fragment onCreatePane(Intent intent) {
		return new PlayFragment();
	}

	@SuppressWarnings({ "unused", "UnusedParameters" })
	@Subscribe
	public void onEvent(PlaySentEvent event) {
		SyncService.sync(this, SyncService.FLAG_SYNC_PLAYS_UPLOAD);
	}

	@SuppressWarnings({ "unused", "UnusedParameters" })
	@Subscribe
	public void onEvent(PlayDeletedEvent event) {
		finish();
		SyncService.sync(this, SyncService.FLAG_SYNC_PLAYS_UPLOAD);
	}
}
