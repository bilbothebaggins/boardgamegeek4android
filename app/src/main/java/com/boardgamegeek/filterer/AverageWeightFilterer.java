package com.boardgamegeek.filterer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.boardgamegeek.R;
import com.boardgamegeek.provider.BggContract.Games;
import com.boardgamegeek.util.MathUtils;
import com.boardgamegeek.util.StringUtils;

import java.util.Locale;

public class AverageWeightFilterer extends CollectionFilterer {
	public static final double MIN_RANGE = 1.0;
	public static final double MAX_RANGE = 5.0;

	private double min;
	private double max;
	private boolean includeUndefined;

	public AverageWeightFilterer(Context context) {
		super(context);
	}

	public AverageWeightFilterer(@NonNull Context context, double min, double max, boolean includeUndefined) {
		super(context);
		this.min = min;
		this.max = max;
		this.includeUndefined = includeUndefined;
	}

	@Override
	public void setData(@NonNull String data) {
		String[] d = data.split(DELIMITER);
		min = d.length > 0 ? MathUtils.constrain(StringUtils.parseDouble(d[0], MIN_RANGE), MIN_RANGE, MAX_RANGE) : MIN_RANGE;
		max = d.length > 1 ? MathUtils.constrain(StringUtils.parseDouble(d[1], MAX_RANGE), MIN_RANGE, MAX_RANGE) : MAX_RANGE;
		includeUndefined = d.length > 2 && (d[2].equals("1"));
	}

	@Override
	public int getTypeResourceId() {
		return R.string.collection_filter_type_average_weight;
	}

	@Override
	public String getDisplayText() {
		return context.getString(R.string.weight) + " " + describeRange(R.string.undefined_abbr);
	}

	@Override
	public String getDescription() {
		return context.getString(R.string.average_weight) + " " + describeRange(R.string.undefined);
	}

	private String describeRange(@StringRes int unratedResId) {
		String text = min == max ?
			String.format(Locale.getDefault(), "%.1f", max) :
			String.format(Locale.getDefault(), "%.1f-%.1f", min, max);
		if (includeUndefined) text += String.format(" (+%s)", context.getString(unratedResId));
		return text;
	}

	@Override
	public String getSelection() {
		String format = min == max ? "%1$s=?" : "(%1$s>=? AND %1$s<=?)";
		if (includeUndefined) format += " OR %1$s=0 OR %1$s IS NULL";
		return String.format(Locale.getDefault(), format, Games.STATS_AVERAGE_WEIGHT);
	}

	@Override
	public String[] getSelectionArgs() {
		return min == max ?
			new String[] { String.valueOf(max) } :
			new String[] { String.valueOf(min), String.valueOf(max) };
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public boolean includeUndefined() {
		return includeUndefined;
	}

	@NonNull
	@Override
	public String flatten() {
		return String.valueOf(min) + DELIMITER + String.valueOf(max) + DELIMITER + (includeUndefined ? "1" : "0");
	}
}
