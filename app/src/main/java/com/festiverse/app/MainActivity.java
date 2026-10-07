package com.festiverse.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.festiverse.app.adapter.FestivalAdapter;
import com.festiverse.app.data.FestivalRepository;
import com.festiverse.app.data.RemoteSync;
import com.festiverse.app.model.Festival;
import com.festiverse.app.util.BsDate;
import com.festiverse.app.util.DateUtils;
import com.festiverse.app.util.Prefs;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private final List<Festival> allFestivals = new ArrayList<>();
    private final List<Festival> shown = new ArrayList<>();
    private final List<Festival> favShown = new ArrayList<>();
    private final List<Festival> dayShown = new ArrayList<>();

    private FestivalAdapter adapter, favAdapter, dayAdapter;

    // Sections
    private View homeSection, calendarSection, favoritesSection;

    // Home hero
    private ImageView heroImage;
    private TextView heroName, heroNepali, heroDate;
    private TextView cdDays, cdHours, cdMinutes, cdSeconds;
    private Festival nextFestival;

    // Favorites
    private View favEmptyState;

    // Calendar (Bikram Sambat)
    private GridLayout calGrid;
    private TextView calMonthLabel, calMonthSub, calSelectedTitle, calNoFestivals;
    private int bsYear, bsMonth;          // visible BS month
    private int selBsMonth, selBsDay;     // selected day (for bookmarks)
    private String selectedDateIso;       // selected day as AD iso (for highlight)
    private final Map<String, List<Festival>> dateMap = new HashMap<>();
    /** Festivals keyed by RECURRING BS date: month*100 + day (every year). */
    private final Map<Integer, List<Festival>> bsMap = new HashMap<>();
    private final List<Festival> monthShown = new ArrayList<>();
    private FestivalAdapter monthAdapter;
    private TextView calMonthFestTitle;
    private static final SimpleDateFormat ISO =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    private static final SimpleDateFormat AD_SUB =
            new SimpleDateFormat("MMM yyyy", Locale.US);

    private String tickerDay = "";

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            updateCountdown();
            // At midnight, the "which festival is next" order changes:
            // re-sort automatically without needing an app restart.
            String today = ISO.format(Calendar.getInstance().getTime());
            if (!today.equals(tickerDay)) {
                tickerDay = today;
                if (adapter != null) {
                    sortByUpcoming(allFestivals);
                    shown.clear();
                    shown.addAll(allFestivals);
                    adapter.notifyDataSetChanged();
                    setupHero();
                }
            }
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Prefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadData();
        bindTopBar();
        setupHome();
        setupFavorites();
        setupCalendar();
        setupBottomNav();
        setupHero();

        // Silent background sync of festival dates (keeps working offline).
        RemoteSync.fetch(updated -> {
            if (updated) {
                loadData();
                shown.clear();
                shown.addAll(allFestivals);
                adapter.notifyDataSetChanged();
                setupHero();
                renderCalendar();
            }
        });
    }

    /**
     * Soonest-upcoming first; passed ones (rolling to next BS year) sink last.
     * Keys are precomputed ONCE so the comparator is perfectly stable —
     * no live-clock drift can scramble the order.
     */
    private void sortByUpcoming(List<Festival> list) {
        final Map<String, Long> key = new HashMap<>();
        for (Festival f : list) {
            key.put(f.getName(), DateUtils.nextOccurrenceEpoch(f.getDate()));
        }
        java.util.Collections.sort(list, (a, b) -> {
            int c = Long.compare(key.get(a.getName()), key.get(b.getName()));
            return c != 0 ? c : a.getName().compareTo(b.getName());
        });
    }

    private void loadData() {
        allFestivals.clear();
        allFestivals.addAll(FestivalRepository.get().getAllFestivals());
        sortByUpcoming(allFestivals);
        dateMap.clear();
        bsMap.clear();
        for (Festival f : allFestivals) {
            List<Festival> list = dateMap.get(f.getDate());
            if (list == null) { list = new ArrayList<>(); dateMap.put(f.getDate(), list); }
            list.add(f);

            BsDate bd = BsDate.fromIso(f.getDate());
            int key = bd.month * 100 + bd.day;   // recurring: same day every BS year
            List<Festival> bl = bsMap.get(key);
            if (bl == null) { bl = new ArrayList<>(); bsMap.put(key, bl); }
            bl.add(f);
        }
    }

    // ================= Top bar =================

    private void bindTopBar() {
        TextView greeting = findViewById(R.id.greeting);
        greeting.setText("Namaste, " + Prefs.getUserName(this));

        findViewById(R.id.clearSearch).setVisibility(View.GONE);

        ImageView themeToggle = findViewById(R.id.themeToggle);
        themeToggle.setImageResource(Prefs.isDarkMode(this)
                ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode);
        themeToggle.setOnClickListener(v ->
                Prefs.setDarkMode(this, !Prefs.isDarkMode(this)));

        findViewById(R.id.logoutBtn).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Log out")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Log out", (d, w) -> {
                            Prefs.setLoggedIn(this, false);
                            Intent i = new Intent(this, LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        findViewById(R.id.feedbackBtn).setOnClickListener(v -> showFeedbackDialog());

        EditText searchBox = findViewById(R.id.searchBox);
        searchBox.setFocusable(false);
        searchBox.setClickable(true);
        searchBox.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, SearchActivity.class)));
    }

    // ================= Home =================

    private void setupHome() {
        // The swipe wrapper IS the home section (so tab switching hides both).
        homeSection = findViewById(R.id.swipeRefresh);

        SwipeRefreshLayout swipe = findViewById(R.id.swipeRefresh);
        swipe.setColorSchemeResources(R.color.primary, R.color.brand_magenta, R.color.accent_gold);
        swipe.setOnRefreshListener(() -> RemoteSync.fetch(updated -> {
            loadData();
            shown.clear();
            shown.addAll(allFestivals);
            adapter.notifyDataSetChanged();
            setupHero();
            refreshFavorites();
            renderCalendar();
            swipe.setRefreshing(false);
            android.widget.Toast.makeText(this, updated
                    ? "Festival dates updated"
                    : "You're up to date", android.widget.Toast.LENGTH_SHORT).show();
        }));

        heroImage = findViewById(R.id.heroImage);
        heroName = findViewById(R.id.heroName);
        heroNepali = findViewById(R.id.heroNepali);
        heroDate = findViewById(R.id.heroDate);
        cdDays = findViewById(R.id.cdDays);
        cdHours = findViewById(R.id.cdHours);
        cdMinutes = findViewById(R.id.cdMinutes);
        cdSeconds = findViewById(R.id.cdSeconds);

        shown.clear();
        shown.addAll(allFestivals);
        adapter = new FestivalAdapter(this, shown, this::openDetail);
        adapter.setOnFavoriteChanged(this::refreshFavorites);
        RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);
    }

    private void openDetail(Festival festival) {
        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra(DetailActivity.EXTRA_FESTIVAL, festival);
        startActivity(i);
    }

    private void setupHero() {
        nextFestival = allFestivals.isEmpty() ? null : allFestivals.get(0);
        if (nextFestival == null) return;

        heroName.setText(nextFestival.getName());
        heroNepali.setText(nextFestival.getNepaliName());
        heroDate.setText(DateUtils.pretty(nextFestival.getDate()));
        heroImage.setImageResource(nextFestival.getImageRes());
        heroImage.setOnClickListener(v -> openDetail(nextFestival));
        ((com.festiverse.app.ui.FestiveAnimationView) findViewById(R.id.heroFx))
                .setThemeFor(nextFestival.getName());

        // Dynamic festival animation: slow Ken Burns zoom on the hero art.
        heroImage.setScaleX(1f);
        heroImage.setScaleY(1f);
        android.animation.ObjectAnimator zx = android.animation.ObjectAnimator
                .ofFloat(heroImage, "scaleX", 1f, 1.12f);
        android.animation.ObjectAnimator zy = android.animation.ObjectAnimator
                .ofFloat(heroImage, "scaleY", 1f, 1.12f);
        for (android.animation.ObjectAnimator a :
                new android.animation.ObjectAnimator[]{zx, zy}) {
            a.setDuration(9000);
            a.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            a.setRepeatMode(android.animation.ValueAnimator.REVERSE);
            a.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
            a.start();
        }
        updateCountdown();
    }

    private void updateCountdown() {
        if (nextFestival == null) return;
        long[] p = DateUtils.breakdown(DateUtils.millisUntilNext(nextFestival.getDate()));
        cdDays.setText(String.format(Locale.US, "%02d", p[0]));
        cdHours.setText(String.format(Locale.US, "%02d", p[1]));
        cdMinutes.setText(String.format(Locale.US, "%02d", p[2]));
        cdSeconds.setText(String.format(Locale.US, "%02d", p[3]));
    }

    // ================= Favorites =================

    private void setupFavorites() {
        favoritesSection = findViewById(R.id.favoritesSection);
        favEmptyState = findViewById(R.id.favEmptyState);

        favAdapter = new FestivalAdapter(this, favShown, this::openDetail);
        favAdapter.setOnFavoriteChanged(this::refreshFavorites);
        RecyclerView favRecycler = findViewById(R.id.favRecycler);
        favRecycler.setLayoutManager(new LinearLayoutManager(this));
        favRecycler.setAdapter(favAdapter);
        refreshFavorites();
    }

    private void refreshFavorites() {
        Set<String> favs = Prefs.getFavorites(this);
        favShown.clear();
        for (Festival f : allFestivals) {
            if (favs.contains(f.getName())) favShown.add(f);
        }
        favAdapter.notifyDataSetChanged();
        // Hearts elsewhere may have changed too.
        adapter.notifyDataSetChanged();
        if (dayAdapter != null) dayAdapter.notifyDataSetChanged();
        boolean empty = favShown.isEmpty();
        favEmptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        findViewById(R.id.favRecycler).setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    // ================= Calendar =================

    private void setupCalendar() {
        calendarSection = findViewById(R.id.calendarSection);
        calGrid = findViewById(R.id.calGrid);
        calMonthLabel = findViewById(R.id.calMonthLabel);
        calMonthSub = findViewById(R.id.calMonthSub);
        calSelectedTitle = findViewById(R.id.calSelectedTitle);
        calNoFestivals = findViewById(R.id.calNoFestivals);

        dayAdapter = new FestivalAdapter(this, dayShown, this::openDetail);
        dayAdapter.setOnFavoriteChanged(this::refreshFavorites);
        RecyclerView dayRecycler = findViewById(R.id.calDayRecycler);
        dayRecycler.setLayoutManager(new LinearLayoutManager(this));
        dayRecycler.setAdapter(dayAdapter);

        findViewById(R.id.calAddEvent).setOnClickListener(v -> showAddEventDialog());

        calMonthFestTitle = findViewById(R.id.calMonthFestTitle);
        monthAdapter = new FestivalAdapter(this, monthShown, this::openDetail);
        monthAdapter.setOnFavoriteChanged(this::refreshFavorites);
        RecyclerView monthRecycler = findViewById(R.id.calMonthRecycler);
        monthRecycler.setLayoutManager(new LinearLayoutManager(this));
        monthRecycler.setAdapter(monthAdapter);

        // Nepali weekday header (Saturday red, as in Nepali calendars)
        LinearLayout weekRow = findViewById(R.id.calWeekdayRow);
        for (int i = 0; i < 7; i++) {
            TextView t = new TextView(this);
            t.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            t.setGravity(Gravity.CENTER);
            t.setText(BsDate.WEEKDAYS_NP[i]);
            t.setTextSize(12);
            t.setTextColor(i == 6
                    ? Color.parseColor("#E63946")
                    : getColorCompat(R.color.text_muted));
            t.setTypeface(null, android.graphics.Typeface.BOLD);
            weekRow.addView(t);
        }

        findViewById(R.id.calPrev).setOnClickListener(v -> {
            if (--bsMonth < 1) { bsMonth = 12; bsYear--; }
            if (bsYear < BsDate.START_YEAR) { bsYear = BsDate.START_YEAR; bsMonth = 1; }
            onMonthChanged();
            animateMonthSwipe(-1);
        });
        findViewById(R.id.calNext).setOnClickListener(v -> {
            if (++bsMonth > 12) { bsMonth = 1; bsYear++; }
            if (bsYear > BsDate.END_YEAR) { bsYear = BsDate.END_YEAR; bsMonth = 12; }
            onMonthChanged();
            animateMonthSwipe(1);
        });

        BsDate today = BsDate.today();
        bsYear = today.year;
        bsMonth = today.month;
        selBsMonth = today.month;
        selBsDay = today.day;
        selectedDateIso = ISO.format(Calendar.getInstance().getTime());
        renderCalendar();
        showDayFestivals(today.year, today.month, today.day);
    }

    /** Month changed: select today (if this month) or day 1, then redraw. */
    private void onMonthChanged() {
        BsDate today = BsDate.today();
        int selDay = (bsYear == today.year && bsMonth == today.month)
                ? today.day : 1;
        selDay = Math.min(selDay, BsDate.daysInMonth(bsYear, bsMonth));
        selBsMonth = bsMonth;
        selBsDay = selDay;
        selectedDateIso = ISO.format(BsDate.toAd(bsYear, bsMonth, selDay).getTime());
        renderCalendar();
        showDayFestivals(bsYear, bsMonth, selDay);
    }

    /** Smooth slide-and-fade when changing months. */
    private void animateMonthSwipe(int direction) {
        float density = getResources().getDisplayMetrics().density;
        calGrid.setAlpha(0f);
        calGrid.setTranslationX(direction * 48 * density);
        calGrid.animate().alpha(1f).translationX(0f).setDuration(240)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
        calMonthLabel.setAlpha(0f);
        calMonthLabel.animate().alpha(1f).setDuration(240).start();
    }

    private int getColorCompat(int res) {
        return androidx.core.content.ContextCompat.getColor(this, res);
    }

    /** Renders the visible BIKRAM SAMBAT month — Hamro Patro style. */
    private void renderCalendar() {
        calMonthLabel.setText(BsDate.MONTHS_NP[bsMonth - 1] + " " + BsDate.np(bsYear));

        int daysInMonth = BsDate.daysInMonth(bsYear, bsMonth);

        // AD range subtitle, e.g. "Sep/Oct 2026"
        Calendar first = BsDate.toAd(bsYear, bsMonth, 1);
        Calendar last = BsDate.toAd(bsYear, bsMonth, daysInMonth);
        String sub = AD_SUB.format(first.getTime());
        String subLast = AD_SUB.format(last.getTime());
        if (!sub.equals(subLast)) {
            sub = sub.split(" ")[0] + "/" + subLast;
        }
        calMonthSub.setText(sub);

        calGrid.removeAllViews();

        int firstDow = first.get(Calendar.DAY_OF_WEEK) - 1; // 0 = Sunday
        BsDate today = BsDate.today();
        float density = getResources().getDisplayMetrics().density;
        int cellH = (int) (58 * density);
        int cells = ((firstDow + daysInMonth + 6) / 7) * 7;

        for (int i = 0; i < cells; i++) {
            LinearLayout cell = new LinearLayout(this);
            cell.setOrientation(LinearLayout.VERTICAL);
            cell.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
            lp.width = 0;
            lp.height = cellH;
            lp.columnSpec = GridLayout.spec(i % 7, 1f);
            cell.setLayoutParams(lp);

            int dayNum = i - firstDow + 1;
            if (dayNum >= 1 && dayNum <= daysInMonth) {
                Calendar adCal = BsDate.toAd(bsYear, bsMonth, dayNum);
                final String iso = ISO.format(adCal.getTime());
                boolean isSaturday = (i % 7) == 6;

                TextView num = new TextView(this);
                int size = (int) (36 * density);
                num.setLayoutParams(new LinearLayout.LayoutParams(size, size));
                num.setGravity(Gravity.CENTER);
                num.setText(BsDate.np(dayNum));   // Nepali numerals
                num.setTextSize(15);
                num.setTypeface(null, android.graphics.Typeface.BOLD);

                boolean isToday = (bsYear == today.year
                        && bsMonth == today.month && dayNum == today.day);
                boolean isSelected = iso.equals(selectedDateIso);
                if (isSelected) {
                    num.setBackgroundResource(R.drawable.day_selected_bg);
                    num.setTextColor(Color.WHITE);
                } else if (isToday) {
                    num.setBackgroundResource(R.drawable.day_today_bg);
                    num.setTextColor(isSaturday
                            ? Color.parseColor("#E63946")
                            : getColorCompat(R.color.text_primary));
                } else {
                    num.setTextColor(isSaturday
                            ? Color.parseColor("#E63946")
                            : getColorCompat(R.color.text_primary));
                }
                cell.addView(num);

                // Small AD (English) date below, like Hamro Patro
                TextView adNum = new TextView(this);
                adNum.setText(String.valueOf(adCal.get(Calendar.DAY_OF_MONTH)));
                adNum.setTextSize(9);
                adNum.setGravity(Gravity.CENTER);
                adNum.setTextColor(getColorCompat(R.color.text_muted));
                cell.addView(adNum);

                // Festival dot (direct BS lookup)
                List<Festival> onDay = bsMap.get(bsMonth * 100 + dayNum);
                View dot = new View(this);
                int dotSize = (int) (6 * density);
                LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(dotSize, dotSize);
                dlp.topMargin = (int) (1 * density);
                dot.setLayoutParams(dlp);
                boolean hasEvent = !Prefs.eventsOn(this, bsMonth, dayNum).isEmpty();
                if (onDay != null && !onDay.isEmpty()) {
                    android.graphics.drawable.GradientDrawable gd =
                            new android.graphics.drawable.GradientDrawable();
                    gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                    gd.setColor(onDay.get(0).getColorHex());
                    dot.setBackground(gd);
                } else if (hasEvent) {
                    android.graphics.drawable.GradientDrawable gd =
                            new android.graphics.drawable.GradientDrawable();
                    gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
                    gd.setColor(getColorCompat(R.color.accent_gold));
                    dot.setBackground(gd);
                } else {
                    dot.setBackgroundColor(Color.TRANSPARENT);
                }
                cell.addView(dot);

                final int clickedDay = dayNum;
                cell.setOnClickListener(v -> {
                    selectedDateIso = iso;
                    selBsMonth = bsMonth;
                    selBsDay = clickedDay;
                    renderCalendar();
                    showDayFestivals(bsYear, bsMonth, clickedDay);
                });
            }
            calGrid.addView(cell);
        }

        // Month list: every festival this BS month, EXCEPT the selected day's
        // (those already show in the day panel above — no duplicates).
        monthShown.clear();
        for (int d2 = 1; d2 <= daysInMonth; d2++) {
            if (bsMonth == selBsMonth && d2 == selBsDay) continue;
            List<Festival> onDay = bsMap.get(bsMonth * 100 + d2);
            if (onDay != null) monthShown.addAll(onDay);
        }
        calMonthFestTitle.setText(BsDate.MONTHS_NP[bsMonth - 1] + " "
                + BsDate.np(bsYear) + " का अन्य चाडपर्वहरू ("
                + monthShown.size() + ")");
        calMonthFestTitle.setVisibility(monthShown.isEmpty() ? View.GONE : View.VISIBLE);
        if (monthAdapter != null) monthAdapter.notifyDataSetChanged();
    }

    private void showDayFestivals(int y, int m, int d) {
        selBsMonth = m;
        selBsDay = d;
        BsDate bs = new BsDate(y, m, d);
        calSelectedTitle.setText(bs.prettyNp() + " का चाडपर्वहरू");
        dayShown.clear();
        List<Festival> onDay = bsMap.get(m * 100 + d);
        if (onDay != null) dayShown.addAll(onDay);
        dayAdapter.notifyDataSetChanged();
        renderEventRows(m, d);
        calNoFestivals.setVisibility(dayShown.isEmpty()
                && Prefs.eventsOn(this, m, d).isEmpty() ? View.VISIBLE : View.GONE);
    }

    /** Renders the user's bookmarks (events and birthdays) for the day. */
    private void renderEventRows(int m, int d) {
        LinearLayout box = findViewById(R.id.calEventsBox);
        box.removeAllViews();
        float density = getResources().getDisplayMetrics().density;
        for (String entry : Prefs.eventsOn(this, m, d)) {
            String[] parts = entry.split("\\|", 4);
            if (parts.length < 4) continue;
            boolean birthday = "B".equals(parts[2]);

            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setBackgroundResource(R.drawable.card_bg);
            int pad = (int) (12 * density);
            row.setPadding(pad, pad, pad, pad);
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            rlp.bottomMargin = (int) (8 * density);
            row.setLayoutParams(rlp);

            ImageView icon = new ImageView(this);
            int is = (int) (20 * density);
            icon.setLayoutParams(new LinearLayout.LayoutParams(is, is));
            icon.setImageResource(birthday ? R.drawable.ic_cake : R.drawable.ic_event_star);
            row.addView(icon);

            TextView title = new TextView(this);
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tlp.leftMargin = (int) (10 * density);
            title.setLayoutParams(tlp);
            title.setText(parts[3]);
            title.setTextSize(14);
            title.setTextColor(getColorCompat(R.color.text_primary));
            title.setTypeface(null, android.graphics.Typeface.BOLD);
            row.addView(title);

            ImageView del = new ImageView(this);
            del.setLayoutParams(new LinearLayout.LayoutParams(is, is));
            del.setImageResource(R.drawable.ic_delete_small);
            final String entryF = entry;
            del.setOnClickListener(v -> {
                com.festiverse.app.util.NotificationHelper.cancelCustom(this, entryF);
                Prefs.removeEvent(this, entryF);
                renderCalendar();
                renderEventRows(m, d);
            });
            row.addView(del);

            box.addView(row);
        }
    }

    /** Dialog to bookmark an event or birthday on the selected BS day. */
    private void showAddEventDialog() {
        float density = getResources().getDisplayMetrics().density;
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * density);
        box.setPadding(pad, pad, pad, 0);

        final EditText input = new EditText(this);
        input.setHint(R.string.event_hint);
        input.setMaxLines(1);
        box.addView(input);

        final android.widget.RadioGroup group = new android.widget.RadioGroup(this);
        group.setOrientation(LinearLayout.HORIZONTAL);
        android.widget.RadioButton rbEvent = new android.widget.RadioButton(this);
        rbEvent.setText(R.string.type_event);
        rbEvent.setId(View.generateViewId());
        android.widget.RadioButton rbBirthday = new android.widget.RadioButton(this);
        rbBirthday.setText(R.string.type_birthday);
        rbBirthday.setId(View.generateViewId());
        group.addView(rbEvent);
        group.addView(rbBirthday);
        group.check(rbEvent.getId());
        box.addView(group);

        BsDate sel = new BsDate(bsYear, selBsMonth, selBsDay);
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.add_event) + " — " + sel.prettyNp())
                .setView(box)
                .setPositiveButton("Save", (dlg, w) -> {
                    String title = input.getText().toString().trim();
                    if (title.isEmpty()) return;
                    String type = group.getCheckedRadioButtonId() == rbBirthday.getId()
                            ? "B" : "E";
                    Prefs.addEvent(this, selBsMonth, selBsDay, type, title);

                    // Schedule the reminder notification for this bookmark.
                    if (android.os.Build.VERSION.SDK_INT >= 33
                            && checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                            != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{
                                android.Manifest.permission.POST_NOTIFICATIONS}, 102);
                    }
                    String entry = selBsMonth + "|" + selBsDay + "|" + type + "|"
                            + title.replace("|", "/");
                    com.festiverse.app.util.NotificationHelper.scheduleCustom(this, entry);
                    android.widget.Toast.makeText(this,
                            "Saved. You will be notified at 7 AM that day.",
                            android.widget.Toast.LENGTH_SHORT).show();

                    renderCalendar();
                    renderEventRows(selBsMonth, selBsDay);
                    calNoFestivals.setVisibility(View.GONE);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /** Styled feedback form: name, email and message, sent via email app. */
    private void showFeedbackDialog() {
        View form = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
        final EditText name = form.findViewById(R.id.fbName);
        final EditText email = form.findViewById(R.id.fbEmail);
        final EditText message = form.findViewById(R.id.fbMessage);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(form)
                .setPositiveButton("Send", null)   // set below so we can validate
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String n = name.getText().toString().trim();
            String e = email.getText().toString().trim();
            String m = message.getText().toString().trim();
            if (n.isEmpty()) { name.setError("Please enter your name"); return; }
            if (e.isEmpty()
                    || !android.util.Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
                email.setError("Please enter a valid email");
                return;
            }
            if (m.isEmpty()) { message.setError("Please write a message"); return; }

            dialog.dismiss();
            android.widget.Toast.makeText(this,
                    "Thanks for the feedback!",
                    android.widget.Toast.LENGTH_SHORT).show();

            if (com.festiverse.app.data.SupabaseClient.isConfigured()) {
                com.festiverse.app.data.SupabaseClient.sendFeedback(n, e, m, ok -> {
                    if (!ok) Prefs.saveFeedback(this, n, e, m); // keep a local backup
                });
            } else {
                Prefs.saveFeedback(this, n, e, m);
            }
        });
    }

    // ================= Bottom navigation =================

    private void setupBottomNav() {
        BottomNavigationView nav = findViewById(R.id.bottomNav);
        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            homeSection.setVisibility(id == R.id.nav_home ? View.VISIBLE : View.GONE);
            calendarSection.setVisibility(id == R.id.nav_calendar ? View.VISIBLE : View.GONE);
            favoritesSection.setVisibility(id == R.id.nav_favorites ? View.VISIBLE : View.GONE);
            if (id == R.id.nav_favorites) refreshFavorites();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.post(ticker);
        if (adapter != null) {
            sortByUpcoming(allFestivals);
            shown.clear();
            shown.addAll(allFestivals);
            adapter.notifyDataSetChanged();
            setupHero();
        }
        if (favAdapter != null) refreshFavorites();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(ticker);
    }
}
