package com.festiverse.app;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.festiverse.app.model.Festival;
import com.festiverse.app.util.DateUtils;
import com.festiverse.app.util.NotificationHelper;
import com.festiverse.app.util.Prefs;

import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_FESTIVAL = "extra_festival";

    private Festival festival;

    private TextView cdDays, cdHours, cdMinutes, cdSeconds, cdLabel;
    private TextView description, btnEnglish, btnNepali;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            updateCountdown();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Prefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        festival = (Festival) getIntent().getSerializableExtra(EXTRA_FESTIVAL);
        if (festival == null) { finish(); return; }

        ImageView image = findViewById(R.id.detailImage);
        TextView name = findViewById(R.id.detailName);
        TextView nepali = findViewById(R.id.detailNepali);
        TextView date = findViewById(R.id.detailDate);
        TextView bs = findViewById(R.id.detailBs);
        TextView category = findViewById(R.id.detailCategory);
        TextView location = findViewById(R.id.detailLocation);
        TextView passedBadge = findViewById(R.id.detailPassedBadge);
        description = findViewById(R.id.detailDescription);
        btnEnglish = findViewById(R.id.btnEnglish);
        btnNepali = findViewById(R.id.btnNepali);
        View back = findViewById(R.id.backButton);
        View countdownCard = findViewById(R.id.countdownCard);

        cdDays = findViewById(R.id.dCdDays);
        cdHours = findViewById(R.id.dCdHours);
        cdMinutes = findViewById(R.id.dCdMinutes);
        cdSeconds = findViewById(R.id.dCdSeconds);
        cdLabel = findViewById(R.id.dCdLabel);

        name.setText(festival.getName());
        nepali.setText(festival.getNepaliName());
        date.setText(DateUtils.pretty(festival.getDate()));
        // Exact Bikram Sambat date, computed by the BS calendar engine
        bs.setText(com.festiverse.app.util.BsDate
                .fromIso(festival.getDate()).prettyNp());
        category.setText(festival.getCategory());
        location.setText(festival.getLocation());

        if (DateUtils.isPastBs(festival.getDate())) {
            passedBadge.setVisibility(View.VISIBLE);
        }

        GradientDrawable pill = new GradientDrawable();
        pill.setCornerRadius(dp(20));
        pill.setColor(withAlpha(festival.getColorHex(), 55));
        category.setBackground(pill);
        category.setTextColor(Color.WHITE);

        GradientDrawable cardBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{festival.getColorHex(), darken(festival.getColorHex())});
        cardBg.setCornerRadius(dp(24));
        countdownCard.setBackground(cardBg);

        image.setImageResource(festival.getImageRes());
        ((com.festiverse.app.ui.FestiveAnimationView) findViewById(R.id.detailFx))
                .setThemeFor(festival.getName());
        android.animation.ObjectAnimator dzx = android.animation.ObjectAnimator
                .ofFloat(image, "scaleX", 1f, 1.1f);
        android.animation.ObjectAnimator dzy = android.animation.ObjectAnimator
                .ofFloat(image, "scaleY", 1f, 1.1f);
        for (android.animation.ObjectAnimator a :
                new android.animation.ObjectAnimator[]{dzx, dzy}) {
            a.setDuration(10000);
            a.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            a.setRepeatMode(android.animation.ValueAnimator.REVERSE);
            a.start();
        }

        back.setOnClickListener(v -> finish());

        setupActions();

        btnEnglish.setOnClickListener(v -> setLanguage(false));
        btnNepali.setOnClickListener(v -> setLanguage(true));
        setLanguage(false);

        updateCountdown();
    }

    // ---------- Favorite + Notify ----------

    private void setupActions() {
        View favBtn = findViewById(R.id.detailFavBtn);
        View notifyBtn = findViewById(R.id.detailNotifyBtn);

        updateFavUi();
        updateNotifyUi();

        favBtn.setOnClickListener(v -> {
            Prefs.toggleFavorite(this, festival.getName());
            updateFavUi();
        });

        notifyBtn.setOnClickListener(v -> {
            if (Prefs.isNotifyOn(this, festival.getName())) {
                NotificationHelper.cancel(this, festival);
                android.widget.Toast.makeText(this,
                        "Reminder cancelled", android.widget.Toast.LENGTH_SHORT).show();
            } else {
                // Android 13+ needs runtime notification permission.
                if (android.os.Build.VERSION.SDK_INT >= 33
                        && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(
                            new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                }
                NotificationHelper.schedule(this, festival);

                long trigger = NotificationHelper.triggerTimeFor(festival);
                java.text.SimpleDateFormat dayFmt =
                        new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US);
                String triggerDay = dayFmt.format(new java.util.Date(trigger));
                String todayDay = dayFmt.format(new java.util.Date());

                String msg;
                if (DateUtils.isTodayBs(festival.getDate())) {
                    if (triggerDay.equals(todayDay)) {
                        // Festival is today and 7 AM hasn't passed yet
                        msg = festival.getName()
                                + " is happening today — reminder set for 7 AM!";
                    } else {
                        // Festival is today, already underway
                        msg = "It's happening today — enjoy the celebration! "
                                + "We'll remind you for next year ("
                                + com.festiverse.app.util.BsDate.np(
                                        DateUtils.nextOccurrenceBsYear(
                                                festival.getDate()) + 1) + ").";
                    }
                } else {
                    java.text.SimpleDateFormat df = new java.text.SimpleDateFormat(
                            "MMM d", java.util.Locale.US);
                    msg = "You'll be notified on "
                            + df.format(new java.util.Date(trigger)) + " at 7 AM";
                }
                android.widget.Toast.makeText(this, msg,
                        android.widget.Toast.LENGTH_LONG).show();
            }
            updateNotifyUi();
        });
    }

    private void updateFavUi() {
        android.widget.ImageView icon = findViewById(R.id.detailFavIcon);
        TextView text = findViewById(R.id.detailFavText);
        boolean fav = Prefs.isFavorite(this, festival.getName());
        icon.setImageResource(fav
                ? R.drawable.ic_heart_filled : R.drawable.ic_heart_outline_themed);
        text.setText(fav ? R.string.favorited : R.string.favorite);
    }

    private void updateNotifyUi() {
        android.widget.ImageView icon = findViewById(R.id.detailNotifyIcon);
        TextView text = findViewById(R.id.detailNotifyText);
        boolean on = Prefs.isNotifyOn(this, festival.getName());
        icon.setImageResource(on ? R.drawable.ic_bell_filled : R.drawable.ic_bell);
        if (on && DateUtils.isTodayBs(festival.getDate())) {
            // Happening right now: celebrate instead of "Reminder On"
            text.setText(R.string.happening_today_short);
        } else {
            text.setText(on ? R.string.reminder_on : R.string.notify_me);
        }
    }

    private void setLanguage(boolean nepali) {
        description.setText(nepali
                ? festival.getDescriptionNp()
                : festival.getDescriptionEn());
        styleLangButton(btnEnglish, !nepali);
        styleLangButton(btnNepali, nepali);
    }

    private void styleLangButton(TextView btn, boolean active) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(20));
        if (active) {
            bg.setColor(festival.getColorHex());
            btn.setTextColor(Color.WHITE);
        } else {
            bg.setColor(Color.TRANSPARENT);
            bg.setStroke(dp(1), withAlpha(festival.getColorHex(), 160));
            btn.setTextColor(festival.getColorHex());
        }
        btn.setBackground(bg);
    }

    private void updateCountdown() {
        boolean past = DateUtils.isPastBs(festival.getDate());
        if (DateUtils.isTodayBs(festival.getDate())) {
            cdLabel.setText("Happening today!");
        } else if (past) {
            cdLabel.setText("Passed · counting to "
                    + com.festiverse.app.util.BsDate.np(
                            DateUtils.nextOccurrenceBsYear(festival.getDate())));
        } else {
            cdLabel.setText("Time remaining");
        }
        long[] p = DateUtils.breakdown(DateUtils.millisUntilNext(festival.getDate()));
        cdDays.setText(String.format(Locale.US, "%02d", p[0]));
        cdHours.setText(String.format(Locale.US, "%02d", p[1]));
        cdMinutes.setText(String.format(Locale.US, "%02d", p[2]));
        cdSeconds.setText(String.format(Locale.US, "%02d", p[3]));
    }

    private int withAlpha(int color, int alpha) {
        return Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color));
    }

    private int darken(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.72f;
        return Color.HSVToColor(hsv);
    }

    private int dp(int v) {
        return (int) (v * getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(ticker);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
    }
}
