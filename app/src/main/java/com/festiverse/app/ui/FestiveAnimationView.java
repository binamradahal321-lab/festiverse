package com.festiverse.app.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;
import java.util.Random;

/**
 * Lightweight festival particle animations drawn on canvas — no libraries.
 * Each festival gets a themed effect: kites for Dashain, fireworks for Tihar,
 * rising diyos, Holi colors, monsoon rain, Himalayan snow, drifting petals,
 * or gentle starlight.
 */
public class FestiveAnimationView extends View {

    public enum Theme { KITES, FIREWORKS, DIYAS, PETALS, RAIN, SNOW, COLORS, STARS }

    private static final int COUNT = 22;
    private final Particle[] particles = new Particle[COUNT];
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path path = new Path();
    private final Random rnd = new Random();
    private Theme theme = Theme.STARS;
    private ValueAnimator animator;
    private float t; // global clock in seconds

    private static final int[] KITE_COLORS = {0xFFE63946, 0xFF457B9D, 0xFFF9A825, 0xFF2A9D8F};
    private static final int[] HOLI_COLORS = {0xFFE63946, 0xFFF9A825, 0xFF2A9D8F, 0xFF577590, 0xFFE76F51, 0xFF9D4EDD};

    public FestiveAnimationView(Context c) { super(c); }
    public FestiveAnimationView(Context c, AttributeSet a) { super(c, a); }

    /** Picks the right effect for a festival by its name. */
    public void setThemeFor(String festivalName) {
        String n = festivalName.toLowerCase(Locale.US);
        if (n.contains("dashain") || n.contains("dashami") || n.contains("ghatasthapana")
                || n.contains("fulpati") || n.contains("ashtami") || n.contains("nawami")
                || n.contains("kojagrat")) theme = Theme.KITES;
        else if (n.contains("tihar") || n.contains("laxmi") || n.contains("bhai")
                || n.contains("mha") || n.contains("indra") || n.contains("new year"))
            theme = Theme.FIREWORKS;
        else if (n.contains("shivaratri") || n.contains("chhath") || n.contains("jitiya")
                || n.contains("swasthani") || n.contains("bala")) theme = Theme.DIYAS;
        else if (n.contains("holi") || n.contains("gai jatra") || n.contains("bisket"))
            theme = Theme.COLORS;
        else if (n.contains("saune") || n.contains("machindranath") || n.contains("nag"))
            theme = Theme.RAIN;
        else if (n.contains("lhosar") || n.contains("rimdu") || n.contains("tiji")
                || n.contains("christmas")) theme = Theme.SNOW;
        else if (n.contains("teej") || n.contains("rishi") || n.contains("mata tirtha")
                || n.contains("kushe") || n.contains("bibaha") || n.contains("ram")
                || n.contains("saraswati") || n.contains("janai") || n.contains("ubhauli")
                || n.contains("udhauli") || n.contains("gaura")) theme = Theme.PETALS;
        else theme = Theme.STARS;
        reset();
    }

    private void reset() {
        for (int i = 0; i < COUNT; i++) particles[i] = newParticle(true);
        invalidate();
    }

    private Particle newParticle(boolean anywhere) {
        Particle p = new Particle();
        float w = Math.max(getWidth(), 1);
        float h = Math.max(getHeight(), 1);
        p.x = rnd.nextFloat() * w;
        p.phase = rnd.nextFloat() * 6.283f;
        p.speed = 0.5f + rnd.nextFloat();
        p.size = 0.5f + rnd.nextFloat();
        p.color = rnd.nextInt();
        switch (theme) {
            case RAIN: p.y = anywhere ? rnd.nextFloat() * h : -20; break;
            case SNOW: case PETALS: case COLORS:
                p.y = anywhere ? rnd.nextFloat() * h : -30; break;
            case DIYAS: case KITES:
                p.y = anywhere ? rnd.nextFloat() * h : h + 30; break;
            default: p.y = rnd.nextFloat() * h;
        }
        return p;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(1000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(a -> {
            t += 0.016f;
            step();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        if (animator != null) animator.cancel();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        reset();
    }

    private void step() {
        float w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        for (int i = 0; i < COUNT; i++) {
            Particle p = particles[i];
            if (p == null) { particles[i] = newParticle(true); continue; }
            switch (theme) {
                case KITES:
                    p.y -= p.speed * 0.45f;
                    p.x += (float) Math.sin(t * 1.3f + p.phase) * 0.6f;
                    if (p.y < -60) particles[i] = newParticle(false);
                    break;
                case DIYAS:
                    p.y -= p.speed * 0.7f;
                    p.x += (float) Math.sin(t * 2f + p.phase) * 0.35f;
                    if (p.y < -20) particles[i] = newParticle(false);
                    break;
                case RAIN:
                    p.y += p.speed * 9f;
                    if (p.y > h + 20) particles[i] = newParticle(false);
                    break;
                case SNOW:
                    p.y += p.speed * 1.1f;
                    p.x += (float) Math.sin(t * 1.2f + p.phase) * 0.7f;
                    if (p.y > h + 20) particles[i] = newParticle(false);
                    break;
                case PETALS:
                    p.y += p.speed * 0.9f;
                    p.x += (float) Math.sin(t * 1.6f + p.phase) * 1.1f + 0.3f;
                    if (p.y > h + 20) particles[i] = newParticle(false);
                    break;
                case COLORS:
                    p.y += (float) Math.sin(t * 1.4f + p.phase) * 0.8f + p.speed * 0.5f;
                    p.x += (float) Math.cos(t + p.phase) * 0.8f;
                    if (p.y > h + 20) particles[i] = newParticle(false);
                    break;
                default: // FIREWORKS handled per-draw via cycle; STARS static twinkle
                    break;
            }
            if (p.x < -60) p.x = w + 40;
            if (p.x > w + 60) p.x = -40;
        }
    }

    @Override
    protected void onDraw(Canvas c) {
        super.onDraw(c);
        float w = getWidth(), h = getHeight();
        if (w <= 0) return;
        switch (theme) {
            case KITES: drawKites(c); break;
            case FIREWORKS: drawFireworks(c, w, h); break;
            case DIYAS: drawDiyas(c); break;
            case RAIN: drawRain(c); break;
            case SNOW: drawSnow(c); break;
            case PETALS: drawPetals(c); break;
            case COLORS: drawColors(c); break;
            default: drawStars(c); break;
        }
    }

    private void drawKites(Canvas c) {
        for (int i = 0; i < 5; i++) {
            Particle p = particles[i];
            if (p == null) continue;
            float s = 14 + p.size * 16;
            int col = KITE_COLORS[i % KITE_COLORS.length];
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(col);
            paint.setAlpha(210);
            float tilt = (float) Math.sin(t * 1.3f + p.phase) * s * 0.3f;
            path.reset();
            path.moveTo(p.x, p.y - s);
            path.lineTo(p.x + s * 0.72f + tilt * 0.2f, p.y);
            path.lineTo(p.x, p.y + s);
            path.lineTo(p.x - s * 0.72f + tilt * 0.2f, p.y);
            path.close();
            c.drawPath(path, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2f);
            paint.setColor(Color.WHITE);
            paint.setAlpha(140);
            c.drawLine(p.x, p.y - s, p.x, p.y + s, paint);
            // flowing tail
            paint.setColor(col);
            paint.setAlpha(160);
            float px = p.x, py = p.y + s;
            for (int k = 1; k <= 5; k++) {
                float nx = p.x + (float) Math.sin(t * 2f + p.phase + k * 0.8f) * 8f;
                float ny = py + 12;
                c.drawLine(px, py, nx, ny, paint);
                px = nx; py = ny;
            }
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private void drawFireworks(Canvas c, float w, float h) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3f);
        for (int b = 0; b < 4; b++) {
            float cycle = (t * 0.5f + b * 0.37f) % 1.5f;
            if (cycle > 1f) continue;
            Random r = new Random(b * 991L + (long) ((t * 0.5f + b * 0.37f) / 1.5f));
            float cx = w * (0.15f + r.nextFloat() * 0.7f);
            float cy = h * (0.15f + r.nextFloat() * 0.5f);
            int col = HOLI_COLORS[r.nextInt(HOLI_COLORS.length)];
            float radius = cycle * (40 + r.nextFloat() * 36);
            int alpha = (int) (235 * (1f - cycle));
            paint.setColor(col);
            paint.setAlpha(alpha);
            for (int k = 0; k < 12; k++) {
                double ang = Math.PI * 2 * k / 12;
                float x2 = cx + (float) Math.cos(ang) * radius;
                float y2 = cy + (float) Math.sin(ang) * radius;
                c.drawLine(cx + (float) Math.cos(ang) * radius * 0.55f,
                        cy + (float) Math.sin(ang) * radius * 0.55f, x2, y2, paint);
                c.drawCircle(x2, y2, 3f, paint);
            }
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawDiyas(Canvas c) {
        for (Particle p : particles) {
            if (p == null) continue;
            float flicker = 0.7f + 0.3f * (float) Math.sin(t * 7f + p.phase);
            paint.setColor(0xFFFFB703);
            paint.setAlpha((int) (70 * flicker));
            c.drawCircle(p.x, p.y, 9 + p.size * 7, paint);
            paint.setAlpha((int) (210 * flicker));
            c.drawCircle(p.x, p.y, 3 + p.size * 2.4f, paint);
        }
    }

    private void drawRain(Canvas c) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.4f);
        paint.setColor(0xFFBFD7F2);
        for (Particle p : particles) {
            if (p == null) continue;
            paint.setAlpha(150);
            c.drawLine(p.x, p.y, p.x - 4, p.y + 13 + p.size * 7, paint);
        }
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawSnow(Canvas c) {
        paint.setColor(Color.WHITE);
        for (Particle p : particles) {
            if (p == null) continue;
            paint.setAlpha(200);
            c.drawCircle(p.x, p.y, 2.4f + p.size * 2.8f, paint);
        }
    }

    private void drawPetals(Canvas c) {
        for (Particle p : particles) {
            if (p == null) continue;
            paint.setColor((p.color & 1) == 0 ? 0xFFF48FB1 : 0xFFFFCC80);
            paint.setAlpha(200);
            c.save();
            c.rotate((t * 40f + p.phase * 57f) % 360f, p.x, p.y);
            c.drawOval(p.x - 5 - p.size * 3, p.y - 3, p.x + 5 + p.size * 3, p.y + 3, paint);
            c.restore();
        }
    }

    private void drawColors(Canvas c) {
        for (int i = 0; i < COUNT; i++) {
            Particle p = particles[i];
            if (p == null) continue;
            paint.setColor(HOLI_COLORS[i % HOLI_COLORS.length]);
            paint.setAlpha(190);
            c.drawCircle(p.x, p.y, 3 + p.size * 4.5f, paint);
        }
    }

    private void drawStars(Canvas c) {
        paint.setColor(Color.WHITE);
        for (Particle p : particles) {
            if (p == null) continue;
            float tw = 0.5f + 0.5f * (float) Math.sin(t * 2.4f + p.phase);
            paint.setAlpha((int) (190 * tw));
            c.drawCircle(p.x, p.y, 1.6f + p.size * 1.8f, paint);
        }
    }

    private static class Particle {
        float x, y, phase, speed, size;
        int color;
    }
}
