package com.boardgamegeek.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.boardgamegeek.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerNumberRow extends LinearLayout {
	@BindView(R.id.poll_player_text) TextView mTextView;
	@BindView(R.id.best) View mBest;
	@BindView(R.id.recommended) View mRecommended;
	@BindView(R.id.no_votes) View mNoVotes;
	@BindView(R.id.not_recommended) View mNotRecommended;
	@BindView(R.id.votes) TextView votesView;

	public PlayerNumberRow(Context context) {
		super(context);
		init(context);
	}

	public PlayerNumberRow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		LayoutInflater li = LayoutInflater.from(context);
		li.inflate(R.layout.row_poll_players, this);
		ButterKnife.bind(this);
	}

	public void setText(CharSequence text) {
		mTextView.setText(text);
	}

	public void setTotal(int total) {
		mNoVotes.setTag(total);
		int votes = getVotes(mBest) + getVotes(mRecommended) + getVotes(mNotRecommended);
		mNoVotes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, total - votes));
	}

	public void setBest(int best) {
		mBest.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, best));
		mBest.setTag(best);
		int noVotes = getVotes(mNoVotes);
		int votes = best + getVotes(mRecommended) + getVotes(mNotRecommended);
		mNoVotes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, noVotes - votes));
		setVotes();
	}

	public void setRecommended(int recommended) {
		mRecommended.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, recommended));
		mRecommended.setTag(recommended);
		int noVotes = getVotes(mNoVotes);
		int votes = getVotes(mBest) + recommended + getVotes(mNotRecommended);
		mNoVotes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, noVotes - votes));
		setVotes();
	}

	public void setNotRecommended(int notRecommended) {
		mNotRecommended.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, notRecommended));
		mNotRecommended.setTag(notRecommended);
		int noVotes = getVotes(mNoVotes);
		int votes = getVotes(mBest) + getVotes(mRecommended) + notRecommended;
		mNoVotes.setLayoutParams(new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, noVotes - votes));
		setVotes();
	}

	private void setVotes() {
		int votes = getVotes(mBest) + getVotes(mRecommended) + getVotes(mNotRecommended);
		votesView.setText(String.valueOf(votes));
	}

	public void showNoVotes(boolean show) {
		mNoVotes.setVisibility(show ? View.VISIBLE : View.GONE);
		votesView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	public int[] getVotes() {
		int[] votes = new int[3];
		votes[0] = getVotes(mBest);
		votes[1] = getVotes(mRecommended);
		votes[2] = getVotes(mNotRecommended);
		return votes;
	}

	public void setHighlight() {
		mTextView.setBackgroundResource(R.drawable.highlight);
	}

	@SuppressWarnings("deprecation")
	public void clearHighlight() {
		mTextView.setBackgroundDrawable(null);
	}

	private int getVotes(View view) {
		Object votes = view.getTag();
		if (votes == null) {
			return 0;
		}
		return (int) votes;
	}
}
