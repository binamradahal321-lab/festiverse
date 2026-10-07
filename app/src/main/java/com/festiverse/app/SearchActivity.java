package com.festiverse.app;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.festiverse.app.adapter.FestivalAdapter;
import com.festiverse.app.data.FestivalRepository;
import com.festiverse.app.model.Festival;
import com.festiverse.app.util.Prefs;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Dedicated search page: shows ONLY the festivals matching the query. */
public class SearchActivity extends AppCompatActivity {

    public static final String EXTRA_QUERY = "extra_query";

    private final List<Festival> all = new ArrayList<>();
    private final List<Festival> results = new ArrayList<>();

    private FestivalAdapter adapter;
    private RecyclerView recycler;
    private View emptyState;
    private TextView resultCount;
    private EditText searchInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Prefs.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        all.addAll(FestivalRepository.get().getAllFestivals());


        // Order results by how soon each festival occurs (soonest first),
        // exactly like the home list — precomputed keys keep it stable.
        final java.util.Map<String, Long> nextKey = new java.util.HashMap<>();
        for (Festival f : all) {
            nextKey.put(f.getName(),
                    com.festiverse.app.util.DateUtils.nextOccurrenceEpoch(f.getDate()));
        }
        java.util.Collections.sort(all, (a, b) -> {
            int c = Long.compare(nextKey.get(a.getName()), nextKey.get(b.getName()));
            return c != 0 ? c : a.getName().compareTo(b.getName());
        });

        recycler = findViewById(R.id.searchRecycler);
        emptyState = findViewById(R.id.searchEmptyState);
        resultCount = findViewById(R.id.resultCount);
        searchInput = findViewById(R.id.searchInput);

        adapter = new FestivalAdapter(this, results, festival -> {
            Intent i = new Intent(SearchActivity.this, DetailActivity.class);
            i.putExtra(DetailActivity.EXTRA_FESTIVAL, festival);
            startActivity(i);
        });
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(adapter);

        findViewById(R.id.searchBack).setOnClickListener(v -> finish());
        findViewById(R.id.searchClear).setOnClickListener(v -> searchInput.setText(""));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) {}
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) {
                filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        String initial = getIntent().getStringExtra(EXTRA_QUERY);
        if (initial != null && !initial.isEmpty()) {
            searchInput.setText(initial);
            searchInput.setSelection(initial.length());
        } else {
            filter("");
        }

        searchInput.requestFocus();
        searchInput.postDelayed(() -> {
            InputMethodManager imm =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
        }, 150);
    }

    private void filter(String query) {
        String q = query.trim().toLowerCase(Locale.getDefault());
        results.clear();
        if (!q.isEmpty()) {
            for (Festival f : all) {
                if (f.getName().toLowerCase(Locale.getDefault()).contains(q)
                        || f.getNepaliName().contains(query.trim())
                        || f.getCategory().toLowerCase(Locale.getDefault()).contains(q)
                        || f.getLocation().toLowerCase(Locale.getDefault()).contains(q)) {
                    results.add(f);
                }
            }
        }
        adapter.notifyDataSetChanged();

        if (q.isEmpty()) {
            resultCount.setText("Type to search festivals");
            emptyState.setVisibility(View.GONE);
            recycler.setVisibility(View.GONE);
        } else if (results.isEmpty()) {
            resultCount.setText("No results for \u201C" + query.trim() + "\u201D");
            emptyState.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        } else {
            resultCount.setText(results.size()
                    + (results.size() == 1 ? " result for \u201C" : " results for \u201C")
                    + query.trim() + "\u201D");
            emptyState.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
        }
    }
}
