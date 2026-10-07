package com.festiverse.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.festiverse.app.R;
import com.festiverse.app.model.Festival;
import com.festiverse.app.util.DateUtils;
import com.festiverse.app.util.Prefs;

import java.util.List;

public class FestivalAdapter extends RecyclerView.Adapter<FestivalAdapter.VH> {

    public interface OnItemClick {
        void onClick(Festival festival);
    }

    /** Optional callback fired whenever a heart is toggled. */
    public interface OnFavoriteChanged {
        void onFavoriteChanged();
    }

    private OnFavoriteChanged favListener;

    public void setOnFavoriteChanged(OnFavoriteChanged l) { this.favListener = l; }

    private final Context context;
    private final List<Festival> items;
    private final OnItemClick listener;

    public FestivalAdapter(Context context, List<Festival> items, OnItemClick listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(LayoutInflater.from(context)
                .inflate(R.layout.item_festival, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Festival fest = items.get(pos);

        h.name.setText(fest.getName());
        h.nepaliName.setText(fest.getNepaliName());
        h.dateBs.setText(com.festiverse.app.util.BsDate
                .fromIso(fest.getDate()).prettyNp());
        h.dateAd.setText(DateUtils.pretty(fest.getDate()));
        h.category.setText(fest.getCategory());

        GradientDrawable pill = new GradientDrawable();
        pill.setCornerRadius(dp(20));
        pill.setColor(withAlpha(fest.getColorHex(), 40));
        h.category.setBackground(pill);
        h.category.setTextColor(fest.getColorHex());

        if (DateUtils.isPastBs(fest.getDate())) {
            long[] p = DateUtils.breakdown(DateUtils.millisUntilNext(fest.getDate()));
            h.countdown.setText("Passed · next in " + p[0] + "d ("
                    + com.festiverse.app.util.BsDate.np(
                            DateUtils.nextOccurrenceBsYear(fest.getDate())) + ")");
            h.countdown.setTextColor(Color.parseColor("#9AA0A6"));

            GradientDrawable passed = new GradientDrawable();
            passed.setCornerRadius(dp(20));
            passed.setColor(Color.parseColor("#339AA0A6"));
            h.passedBadge.setBackground(passed);
            h.passedBadge.setVisibility(View.VISIBLE);
        } else {
            h.passedBadge.setVisibility(View.GONE);
            long days = DateUtils.breakdown(
                    DateUtils.millisUntilNext(fest.getDate()))[0];
            if (DateUtils.isTodayBs(fest.getDate())) {
                h.countdown.setText("Today!");
            } else if (days == 1) {
                h.countdown.setText("Tomorrow");
            } else {
                h.countdown.setText(days + " days left");
            }
            h.countdown.setTextColor(fest.getColorHex());
        }

        h.accent.setBackgroundColor(fest.getColorHex());
        h.image.setImageResource(fest.getImageRes());

        // Favorite heart
        h.fav.setImageResource(Prefs.isFavorite(context, fest.getName())
                ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
        h.fav.setOnClickListener(v -> {
            boolean nowFav = Prefs.toggleFavorite(context, fest.getName());
            h.fav.setImageResource(nowFav
                    ? R.drawable.ic_heart_filled : R.drawable.ic_heart);
            if (favListener != null) favListener.onFavoriteChanged();
        });

        View.OnClickListener open = v -> {
            if (listener != null) listener.onClick(fest);
        };
        h.itemView.setOnClickListener(open);
        h.readAbout.setOnClickListener(open);
    }

    @Override
    public int getItemCount() { return items.size(); }

    private int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int dp(int v) {
        return (int) (v * context.getResources().getDisplayMetrics().density);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView image, fav;
        View accent;
        TextView name, nepaliName, dateBs, dateAd, category, countdown, passedBadge, readAbout;

        VH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.festImage);
            fav = v.findViewById(R.id.favBtn);
            accent = v.findViewById(R.id.accentStrip);
            name = v.findViewById(R.id.festName);
            nepaliName = v.findViewById(R.id.festNepaliName);
            dateBs = v.findViewById(R.id.festDateBs);
            dateAd = v.findViewById(R.id.festDateAd);
            category = v.findViewById(R.id.festCategory);
            countdown = v.findViewById(R.id.festCountdown);
            passedBadge = v.findViewById(R.id.passedBadge);
            readAbout = v.findViewById(R.id.readAboutBtn);
        }
    }
}
