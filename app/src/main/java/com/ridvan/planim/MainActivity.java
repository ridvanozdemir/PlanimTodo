package com.ridvan.planim;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final String TASKS = "TASKS";
    private static final String GOALS = "GOALS";
    private static final String REPORT = "REPORT";
    private static final String TASK_FORM = "TASK_FORM";
    private static final String GOAL_FORM = "GOAL_FORM";

    private final int BG = Color.rgb(250, 248, 244);
    private final int SURFACE = Color.WHITE;
    private final int TEXT = Color.rgb(23, 43, 66);
    private final int MUTED = Color.rgb(101, 116, 139);
    private final int BORDER = Color.rgb(229, 235, 240);
    private final int TEAL = Color.rgb(20, 184, 178);
    private final int TEAL_DARK = Color.rgb(14, 145, 141);
    private final int TEAL_SOFT = Color.rgb(223, 247, 244);
    private final int BLUE = Color.rgb(46, 141, 235);
    private final int BLUE_SOFT = Color.rgb(232, 243, 255);
    private final int PURPLE = Color.rgb(122, 95, 234);
    private final int PURPLE_SOFT = Color.rgb(239, 234, 255);
    private final int AMBER = Color.rgb(245, 169, 35);
    private final int AMBER_SOFT = Color.rgb(255, 244, 216);
    private final int DANGER = Color.rgb(220, 91, 91);
    private final int DANGER_SOFT = Color.rgb(255, 236, 236);

    private FrameLayout host;
    private String screen = TASKS;
    private int weekOffset = 0;
    private Button navTasks;
    private Button navGoals;
    private Button navReport;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (Build.VERSION.SDK_INT >= 30) {
            getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                int lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;
                controller.setSystemBarsAppearance(lightBars, lightBars);
            }
        }

        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
        }

        ReminderScheduler.scheduleAll(this);
        shell();
        tasks();
    }

    @Override
    public void onBackPressed() {
        if (TASK_FORM.equals(screen)) {
            tasks();
        } else if (GOAL_FORM.equals(screen)) {
            goals();
        } else if (!TASKS.equals(screen)) {
            tasks();
        } else {
            super.onBackPressed();
        }
    }

    private void shell() {
        LinearLayout root = col();
        root.setBackgroundColor(BG);
        if (Build.VERSION.SDK_INT >= 30) {
            root.setOnApplyWindowInsetsListener((view, insets) -> {
                Insets safe = insets.getInsets(
                        WindowInsets.Type.systemBars() | WindowInsets.Type.displayCutout()
                );
                view.setPadding(safe.left, safe.top, safe.right, safe.bottom);
                return insets;
            });
            root.requestApplyInsets();
        } else {
            root.setFitsSystemWindows(true);
        }

        host = new FrameLayout(this);
        host.setClipChildren(false);
        root.addView(host, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ));

        LinearLayout nav = row();
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(8), dp(7), dp(8), dp(7));
        nav.setBackground(bg(SURFACE, BORDER, 24));
        nav.setElevation(dp(8));

        navTasks = navButton("☷\nGörevler");
        navGoals = navButton("◎\nHedefler");
        navReport = navButton("▥\nRapor");

        navTasks.setOnClickListener(v -> tasks());
        navGoals.setOnClickListener(v -> goals());
        navReport.setOnClickListener(v -> report());

        nav.addView(navTasks, new LinearLayout.LayoutParams(0, dp(62), 1f));
        spaceH(nav, 4);
        nav.addView(navGoals, new LinearLayout.LayoutParams(0, dp(62), 1f));
        spaceH(nav, 4);
        nav.addView(navReport, new LinearLayout.LayoutParams(0, dp(62), 1f));

        LinearLayout.LayoutParams navParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        navParams.setMargins(dp(14), dp(6), dp(14), dp(12));
        root.addView(nav, navParams);

        setContentView(root);
    }

    private void navState() {
        boolean tasksOn = TASKS.equals(screen) || TASK_FORM.equals(screen);
        boolean goalsOn = GOALS.equals(screen) || GOAL_FORM.equals(screen);
        styleNav(navTasks, tasksOn);
        styleNav(navGoals, goalsOn);
        styleNav(navReport, REPORT.equals(screen));
    }

    private void tasks() {
        screen = TASKS;
        LinearLayout page = page();

        addBrandHeader(page, "Görevlerini düzenle, küçük adımlarla ilerle.");
        page.addView(segmentTabs(true));
        space(page, 18);

        List<TaskItem> items = AppStore.loadTasks(this);
        LinearLayout titleRow = row();
        titleRow.setGravity(Gravity.CENTER_VERTICAL);
        TextView section = sectionTitle("Bugün");
        titleRow.addView(section, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        titleRow.addView(chip(items.size() + " görev", Color.rgb(244, 247, 249), MUTED));
        page.addView(titleRow);
        space(page, 10);

        if (items.isEmpty()) {
            LinearLayout empty = surfaceCard();
            TextView icon = iconBadge("✓", TEAL_SOFT, TEAL, 54);
            LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(54), dp(54));
            ip.gravity = Gravity.CENTER_HORIZONTAL;
            empty.addView(icon, ip);
            space(empty, 10);

            TextView h = head("İlk görevini ekle");
            h.setGravity(Gravity.CENTER);
            empty.addView(h);

            TextView sub = body("Günlük, haftalık veya aylık bir görev oluşturup tekrar hedefini takip edebilirsin.");
            sub.setTextColor(MUTED);
            sub.setGravity(Gravity.CENTER);
            empty.addView(sub);
            space(empty, 12);

            Button add = primary("+ Görev oluştur");
            add.setOnClickListener(v -> taskForm());
            empty.addView(add);
            page.addView(empty);
        } else {
            for (TaskItem item : items) {
                page.addView(taskCard(item));
                space(page, 12);
            }
        }

        LinearLayout motivation = softCard(TEAL_SOFT);
        LinearLayout mr = row();
        mr.setGravity(Gravity.CENTER_VERTICAL);
        mr.addView(iconBadge("🎯", SURFACE, TEAL, 44), new LinearLayout.LayoutParams(dp(44), dp(44)));
        spaceH(mr, 12);
        LinearLayout mt = col();
        TextView mh = head("Küçük adımlar, büyük hedeflere ulaşır.");
        mh.setTextSize(15);
        mt.addView(mh);
        TextView ms = caption("Bugün bir görevi tamamlamak bile ilerlemedir.");
        mt.addView(ms);
        mr.addView(mt, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        motivation.addView(mr);
        page.addView(motivation);

        show(page, this::taskForm);
        navState();
    }

    private View taskCard(TaskItem task) {
        int count = TimeUtils.currentCount(task);
        int accent = taskAccent(task);
        int soft = taskSoft(task);
        int progress = task.requiredCount == 0 ? 0 :
                Math.min(100, (int) Math.round(count * 100.0 / task.requiredCount));

        LinearLayout card = surfaceCard();

        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);

        String icon = TaskItem.WEEKLY.equals(task.period) ? "↻" :
                TaskItem.MONTHLY.equals(task.period) ? "▦" : "✓";
        top.addView(iconBadge(icon, soft, accent, 50),
                new LinearLayout.LayoutParams(dp(50), dp(50)));
        spaceH(top, 12);

        LinearLayout main = col();
        TextView title = head(task.title);
        title.setTextSize(18);
        main.addView(title);

        LinearLayout meta = row();
        meta.setGravity(Gravity.CENTER_VERTICAL);
        meta.addView(chip(TimeUtils.periodLabel(task.period), Color.rgb(246, 248, 250), MUTED));
        if (task.reminderEnabled) {
            spaceH(meta, 7);
            meta.addView(chip(String.format(Locale.getDefault(), "⏰ %02d:%02d",
                    task.reminderHour, task.reminderMinute), BLUE_SOFT, BLUE));
        }
        main.addView(meta);

        top.addView(main, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        TextView done = chip(count >= task.requiredCount ? "Tamamlandı" : "%" + progress,
                count >= task.requiredCount ? TEAL_SOFT : Color.rgb(246, 248, 250),
                count >= task.requiredCount ? TEAL_DARK : TEXT);
        top.addView(done);

        card.addView(top);
        space(card, 13);

        LinearLayout progressText = row();
        progressText.setGravity(Gravity.CENTER_VERTICAL);
        TextView p1 = body(count + " / " + task.requiredCount + " tamamlandı");
        p1.setTextColor(accent);
        p1.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        progressText.addView(p1, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        TextView p2 = caption("Hedef: " + task.requiredCount + " tekrar");
        progressText.addView(p2);
        card.addView(progressText);
        space(card, 7);

        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(Math.max(1, task.requiredCount));
        bar.setProgress(Math.min(count, Math.max(1, task.requiredCount)));
        bar.setProgressTintList(ColorStateList.valueOf(accent));
        bar.setProgressBackgroundTintList(ColorStateList.valueOf(Color.rgb(239, 242, 245)));
        card.addView(bar, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(7)
        ));

        space(card, 13);
        LinearLayout actions = row();
        Button complete = compactButton(count >= task.requiredCount ? "✓ Tamam" : "+1 Tamamla", soft, accent);
        Button undo = compactButton("Geri al", SURFACE, MUTED);
        Button delete = compactButton("Sil", DANGER_SOFT, DANGER);

        complete.setEnabled(count < task.requiredCount);
        undo.setEnabled(count > 0);

        complete.setOnClickListener(v -> {
            List<TaskItem> all = AppStore.loadTasks(this);
            TaskItem current = findTask(all, task.id);
            if (current != null && TimeUtils.currentCount(current) < current.requiredCount) {
                current.completions.add(System.currentTimeMillis());
                AppStore.saveTasks(this, all);
            }
            tasks();
        });

        undo.setOnClickListener(v -> {
            List<TaskItem> all = AppStore.loadTasks(this);
            TaskItem current = findTask(all, task.id);
            if (current != null) {
                long[] range = TimeUtils.currentPeriod(current);
                for (int i = current.completions.size() - 1; i >= 0; i--) {
                    long ts = current.completions.get(i);
                    if (ts >= range[0] && ts <= range[1]) {
                        current.completions.remove(i);
                        break;
                    }
                }
                AppStore.saveTasks(this, all);
            }
            tasks();
        });

        delete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Görev silinsin mi?")
                .setMessage(task.title)
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton("Sil", (dialog, which) -> {
                    List<TaskItem> all = AppStore.loadTasks(this);
                    all.removeIf(x -> x.id.equals(task.id));
                    AppStore.saveTasks(this, all);
                    ReminderScheduler.cancel(this, task.id);
                    tasks();
                }).show());

        actions.addView(complete, new LinearLayout.LayoutParams(0, dp(42), 1.25f));
        spaceH(actions, 7);
        actions.addView(undo, new LinearLayout.LayoutParams(0, dp(42), 1f));
        spaceH(actions, 7);
        actions.addView(delete, new LinearLayout.LayoutParams(0, dp(42), .75f));
        card.addView(actions);

        return card;
    }

    private void taskForm() {
        screen = TASK_FORM;
        LinearLayout page = page();

        page.addView(formHeader("Yeni görev", this::tasks));
        space(page, 14);

        final TaskItem draft = new TaskItem();
        final int[] periodIndex = {0};
        final int[] repeat = {1};
        final boolean[] reminderEnabled = {false};
        final int[] reminderTime = {9, 0};

        LinearLayout form = surfaceCard();

        form.addView(label("Başlık"));
        EditText title = edit("Örn: Su iç, kitap oku, yüzme...");
        form.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(54)
        ));

        space(form, 18);
        form.addView(label("Periyot"));

        LinearLayout periodRow = row();
        Button daily = segmentButton("Günlük", true);
        Button weekly = segmentButton("Haftalık", false);
        Button monthly = segmentButton("Aylık", false);
        Button[] periodButtons = {daily, weekly, monthly};

        View.OnClickListener periodClick = v -> {
            int index = v == daily ? 0 : v == weekly ? 1 : 2;
            periodIndex[0] = index;
            for (int i = 0; i < periodButtons.length; i++) {
                styleSegmentButton(periodButtons[i], i == index);
            }
        };
        daily.setOnClickListener(periodClick);
        weekly.setOnClickListener(periodClick);
        monthly.setOnClickListener(periodClick);

        periodRow.addView(daily, new LinearLayout.LayoutParams(0, dp(46), 1f));
        spaceH(periodRow, 6);
        periodRow.addView(weekly, new LinearLayout.LayoutParams(0, dp(46), 1f));
        spaceH(periodRow, 6);
        periodRow.addView(monthly, new LinearLayout.LayoutParams(0, dp(46), 1f));
        form.addView(periodRow);

        space(form, 18);
        form.addView(label("Tekrar sayısı"));

        LinearLayout repeatRow = row();
        repeatRow.setGravity(Gravity.CENTER_VERTICAL);
        Button minus = compactButton("−", Color.rgb(247, 249, 250), TEXT);
        Button plus = compactButton("+", Color.rgb(247, 249, 250), TEXT);
        TextView repeatValue = valueText("1");

        minus.setOnClickListener(v -> {
            if (repeat[0] > 1) repeat[0]--;
            repeatValue.setText(String.valueOf(repeat[0]));
        });
        plus.setOnClickListener(v -> {
            if (repeat[0] < 10) repeat[0]++;
            repeatValue.setText(String.valueOf(repeat[0]));
        });

        repeatRow.addView(minus, new LinearLayout.LayoutParams(0, dp(48), 1f));
        repeatRow.addView(repeatValue, new LinearLayout.LayoutParams(0, dp(48), 1f));
        repeatRow.addView(plus, new LinearLayout.LayoutParams(0, dp(48), 1f));
        form.addView(repeatRow);

        TextView repeatHint = caption("Seçtiğin periyot içinde kaç kez tamamlamak istediğini belirle.");
        form.addView(repeatHint);

        space(form, 18);
        LinearLayout reminderHeader = row();
        reminderHeader.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout reminderText = col();
        reminderText.addView(label("Hatırlatıcı"));
        TextView reminderSub = caption("İstersen tek bir hatırlatma saati seç.");
        reminderText.addView(reminderSub);
        reminderHeader.addView(reminderText,
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Button toggle = compactButton("Kapalı", Color.rgb(247, 249, 250), MUTED);
        reminderHeader.addView(toggle, new LinearLayout.LayoutParams(dp(90), dp(42)));
        form.addView(reminderHeader);

        Button timeButton = compactButton("⏰ 09:00", BLUE_SOFT, BLUE);
        LinearLayout.LayoutParams timeLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(46)
        );
        timeLp.topMargin = dp(10);
        form.addView(timeButton, timeLp);
        timeButton.setEnabled(false);
        timeButton.setAlpha(.45f);

        Runnable refreshReminder = () -> {
            boolean on = reminderEnabled[0];
            toggle.setText(on ? "Açık" : "Kapalı");
            toggle.setTextColor(on ? TEAL_DARK : MUTED);
            toggle.setBackground(bg(on ? TEAL_SOFT : Color.rgb(247, 249, 250),
                    on ? TEAL_SOFT : BORDER, 13));
            timeButton.setEnabled(on);
            timeButton.setAlpha(on ? 1f : .45f);
            timeButton.setText(String.format(Locale.getDefault(), "⏰ %02d:%02d",
                    reminderTime[0], reminderTime[1]));
        };

        toggle.setOnClickListener(v -> {
            reminderEnabled[0] = !reminderEnabled[0];
            refreshReminder.run();
        });

        timeButton.setOnClickListener(v -> new TimePickerDialog(this, (view, hour, minute) -> {
            reminderTime[0] = hour;
            reminderTime[1] = minute;
            reminderEnabled[0] = true;
            refreshReminder.run();
        }, reminderTime[0], reminderTime[1], true).show());

        space(form, 20);
        Button save = primary("Görevi kaydet");
        save.setOnClickListener(v -> {
            String taskTitle = title.getText().toString().trim();
            if (taskTitle.isEmpty()) {
                title.setError("Başlık gerekli");
                return;
            }

            draft.title = taskTitle;
            draft.period = periodIndex[0] == 1 ? TaskItem.WEEKLY :
                    periodIndex[0] == 2 ? TaskItem.MONTHLY : TaskItem.DAILY;
            draft.requiredCount = repeat[0];
            draft.reminderEnabled = reminderEnabled[0];
            draft.reminderHour = reminderTime[0];
            draft.reminderMinute = reminderTime[1];

            List<TaskItem> all = AppStore.loadTasks(this);
            all.add(draft);
            AppStore.saveTasks(this, all);
            if (draft.reminderEnabled) {
                ReminderScheduler.schedule(this, draft);
            }
            Toast.makeText(this, "Görev eklendi", Toast.LENGTH_SHORT).show();
            tasks();
        });
        form.addView(save);
        page.addView(form);

        space(page, 14);
        LinearLayout preview = softCard(TEAL_SOFT);
        preview.addView(head("Önizleme"));
        TextView pv = body("Görev kaydedildiğinde listede ilerleme çubuğu, tekrar hedefi ve varsa hatırlatma saatiyle görünecek.");
        pv.setTextColor(MUTED);
        preview.addView(pv);
        page.addView(preview);

        show(page, null);
        navState();
    }

    private void goals() {
        screen = GOALS;
        LinearLayout page = page();

        addBrandHeader(page, "Kısa, orta ve uzun vadeli hedeflerini tek yerde gör.");
        page.addView(segmentTabs(false));
        space(page, 18);

        List<GoalItem> all = AppStore.loadGoals(this);
        String[] names = {"Kısa vade", "Orta vade", "Uzun vade"};
        String[] descriptions = {"0–3 ay", "3–12 ay", "12+ ay"};

        for (int category = 0; category < 3; category++) {
            int count = 0;
            for (GoalItem goal : all) if (goalCat(goal) == category) count++;

            LinearLayout sectionRow = row();
            sectionRow.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout texts = col();
            TextView h = sectionTitle(names[category]);
            texts.addView(h);
            texts.addView(caption(descriptions[category]));
            sectionRow.addView(texts,
                    new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            sectionRow.addView(chip(count + " hedef", Color.rgb(244, 247, 249), MUTED));
            page.addView(sectionRow);
            space(page, 9);

            boolean any = false;
            for (GoalItem goal : all) {
                if (goalCat(goal) == category) {
                    page.addView(goalCard(goal, category));
                    space(page, 11);
                    any = true;
                }
            }

            if (!any) {
                TextView none = caption("Bu vadede henüz hedef yok.");
                none.setPadding(dp(4), dp(3), 0, dp(6));
                page.addView(none);
            }
            space(page, 10);
        }

        show(page, this::goalForm);
        navState();
    }

    private View goalCard(GoalItem goal, int category) {
        int accent = category == 0 ? TEAL : category == 1 ? BLUE : PURPLE;
        int soft = category == 0 ? TEAL_SOFT : category == 1 ? BLUE_SOFT : PURPLE_SOFT;

        LinearLayout card = surfaceCard();
        LinearLayout top = row();
        top.setGravity(Gravity.CENTER_VERTICAL);

        String statusIcon = goal.completed ? "✓" : "◎";
        top.addView(iconBadge(statusIcon, soft, accent, 52),
                new LinearLayout.LayoutParams(dp(52), dp(52)));
        spaceH(top, 12);

        LinearLayout main = col();
        TextView title = head(goal.title);
        if (goal.completed) {
            title.setTextColor(MUTED);
        }
        main.addView(title);

        TextView date = caption("Hedef tarihi: " + TimeUtils.formatDate(goal.targetDate));
        main.addView(date);

        LocalDate target = TimeUtils.toDate(goal.targetDate);
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), target);
        String status;
        if (goal.completed) {
            status = "Tamamlandı";
        } else if (days < 0) {
            status = "Hedef tarihi geçti";
        } else if (days == 0) {
            status = "Bugün";
        } else {
            status = days + " gün kaldı";
        }
        TextView state = chip(status, soft, accent);
        LinearLayout.LayoutParams stateLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        stateLp.topMargin = dp(5);
        main.addView(state, stateLp);

        top.addView(main, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        card.addView(top);

        space(card, 13);
        LinearLayout actions = row();
        Button done = compactButton(goal.completed ? "Geri al" : "✓ Tamamlandı", soft, accent);
        Button delete = compactButton("Sil", DANGER_SOFT, DANGER);

        done.setOnClickListener(v -> {
            List<GoalItem> items = AppStore.loadGoals(this);
            GoalItem current = findGoal(items, goal.id);
            if (current != null) {
                current.completed = !current.completed;
                current.completedAt = current.completed ? System.currentTimeMillis() : 0L;
                AppStore.saveGoals(this, items);
            }
            goals();
        });

        delete.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle("Hedef silinsin mi?")
                .setMessage(goal.title)
                .setNegativeButton("Vazgeç", null)
                .setPositiveButton("Sil", (dialog, which) -> {
                    List<GoalItem> items = AppStore.loadGoals(this);
                    items.removeIf(x -> x.id.equals(goal.id));
                    AppStore.saveGoals(this, items);
                    goals();
                }).show());

        actions.addView(done, new LinearLayout.LayoutParams(0, dp(42), 1f));
        spaceH(actions, 8);
        actions.addView(delete, new LinearLayout.LayoutParams(0, dp(42), .65f));
        card.addView(actions);
        return card;
    }

    private void goalForm() {
        screen = GOAL_FORM;
        LinearLayout page = page();

        page.addView(formHeader("Yeni hedef", this::goals));
        space(page, 14);

        final long[] targetDate = {TimeUtils.atStart(LocalDate.now().plusMonths(1))};

        LinearLayout form = surfaceCard();
        form.addView(label("Hedef başlığı"));
        EditText title = edit("Örn: İngilizce B2 seviyesine ulaş");
        form.addView(title, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(54)
        ));

        space(form, 18);
        form.addView(label("Hedef tarihi"));
        Button dateButton = compactButton(TimeUtils.formatDate(targetDate[0]), BLUE_SOFT, BLUE);
        form.addView(dateButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(50)
        ));

        dateButton.setOnClickListener(v -> {
            LocalDate selected = TimeUtils.toDate(targetDate[0]);
            DatePickerDialog picker = new DatePickerDialog(this, (view, year, month, day) -> {
                LocalDate date = LocalDate.of(year, month + 1, day);
                targetDate[0] = TimeUtils.atStart(date);
                dateButton.setText(TimeUtils.formatDate(targetDate[0]));
            }, selected.getYear(), selected.getMonthValue() - 1, selected.getDayOfMonth());
            picker.getDatePicker().setMinDate(TimeUtils.atStart(LocalDate.now()));
            picker.show();
        });

        space(form, 12);
        TextView info = caption("Planım, hedefini seçtiğin tarihe göre kısa, orta veya uzun vadeli olarak otomatik gruplandırır.");
        form.addView(info);

        space(form, 20);
        Button save = primary("Hedefi kaydet");
        save.setOnClickListener(v -> {
            String goalTitle = title.getText().toString().trim();
            if (goalTitle.isEmpty()) {
                title.setError("Başlık gerekli");
                return;
            }

            GoalItem goal = new GoalItem();
            goal.title = goalTitle;
            goal.targetDate = targetDate[0];

            List<GoalItem> all = AppStore.loadGoals(this);
            all.add(goal);
            AppStore.saveGoals(this, all);
            Toast.makeText(this, "Hedef eklendi", Toast.LENGTH_SHORT).show();
            goals();
        });
        form.addView(save);

        page.addView(form);
        show(page, null);
        navState();
    }

    private void report() {
        screen = REPORT;
        LinearLayout page = page();

        TextView title = title("Haftalık Analiz");
        page.addView(title);
        TextView subtitle = body("Tamamlanan görevlerini ve hedef performansını raporlarla gör.");
        subtitle.setTextColor(MUTED);
        page.addView(subtitle);
        space(page, 14);

        LocalDate monday = TimeUtils.mondayForOffset(weekOffset);
        LocalDate sunday = monday.plusDays(6);

        LinearLayout rangeCard = surfaceCard();
        rangeCard.setPadding(dp(10), dp(9), dp(10), dp(9));
        LinearLayout rangeRow = row();
        rangeRow.setGravity(Gravity.CENTER_VERTICAL);

        Button prev = compactButton("‹", Color.rgb(247, 249, 250), TEXT);
        Button next = compactButton("›", Color.rgb(247, 249, 250), TEXT);
        next.setEnabled(weekOffset < 0);

        TextView range = body(TimeUtils.formatShort(monday) + " – " + TimeUtils.formatShort(sunday));
        range.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        range.setGravity(Gravity.CENTER);

        prev.setOnClickListener(v -> {
            weekOffset--;
            report();
        });
        next.setOnClickListener(v -> {
            if (weekOffset < 0) weekOffset++;
            report();
        });

        rangeRow.addView(prev, new LinearLayout.LayoutParams(dp(44), dp(42)));
        rangeRow.addView(range, new LinearLayout.LayoutParams(0, dp(42), 1f));
        rangeRow.addView(next, new LinearLayout.LayoutParams(dp(44), dp(42)));
        rangeCard.addView(rangeRow);
        page.addView(rangeCard);

        space(page, 14);

        List<TaskItem> tasks = AppStore.loadTasks(this);
        List<GoalItem> goals = AppStore.loadGoals(this);
        long weekStart = TimeUtils.atStart(monday);
        long weekEnd = TimeUtils.atEnd(sunday);

        int entered = 0;
        int goalsEntered = 0;
        int goalsDone = 0;
        for (TaskItem task : tasks) {
            if (task.createdAt >= weekStart && task.createdAt <= weekEnd) entered++;
        }
        for (GoalItem goal : goals) {
            if (goal.createdAt >= weekStart && goal.createdAt <= weekEnd) goalsEntered++;
            if (goal.completedAt >= weekStart && goal.completedAt <= weekEnd) goalsDone++;
        }

        int ok = 0;
        int fail = 0;
        int ongoing = 0;
        LocalDate today = LocalDate.now();

        for (TaskItem task : tasks) {
            if (TimeUtils.toDate(task.createdAt).isAfter(sunday)) continue;

            if (TaskItem.DAILY.equals(task.period)) {
                for (LocalDate day = monday; !day.isAfter(sunday); day = day.plusDays(1)) {
                    if (day.isBefore(TimeUtils.toDate(task.createdAt))) continue;
                    if (weekOffset == 0 && !day.isBefore(today)) {
                        if (day.equals(today)) ongoing++;
                        continue;
                    }
                    int count = TimeUtils.count(task.completions,
                            TimeUtils.atStart(day), TimeUtils.atEnd(day));
                    if (count >= task.requiredCount) ok++;
                    else fail++;
                }
            } else if (TaskItem.WEEKLY.equals(task.period)) {
                if (weekOffset == 0) {
                    ongoing++;
                } else {
                    int count = TimeUtils.count(task.completions, weekStart, weekEnd);
                    if (count >= task.requiredCount) ok++;
                    else fail++;
                }
            } else {
                ongoing++;
            }
        }

        int denominator = ok + fail;
        int rate = denominator == 0 ? 0 : (int) Math.round(ok * 100.0 / denominator);

        int activeGoals = 0;
        for (GoalItem goal : goals) if (!goal.completed) activeGoals++;

        int[] dayCounts = new int[7];
        for (TaskItem task : tasks) {
            for (Long timestamp : task.completions) {
                LocalDate day = TimeUtils.toDate(timestamp);
                if (!day.isBefore(monday) && !day.isAfter(sunday)) {
                    int index = day.getDayOfWeek().getValue() - 1;
                    if (index >= 0 && index < 7) dayCounts[index]++;
                }
            }
        }
        int activeDays = 0;
        for (int count : dayCounts) if (count > 0) activeDays++;

        LinearLayout stats1 = row();
        stats1.addView(statCard("✓", "Tamamlanan", String.valueOf(ok),
                "görev periyodu", TEAL), new LinearLayout.LayoutParams(0, dp(126), 1f));
        spaceH(stats1, 10);
        stats1.addView(statCard("−", "Eksik kalan", String.valueOf(fail),
                "görev periyodu", AMBER), new LinearLayout.LayoutParams(0, dp(126), 1f));
        page.addView(stats1);

        space(page, 10);

        LinearLayout stats2 = row();
        stats2.addView(statCard("◎", "Başarı", "%" + rate,
                "tamamlanma oranı", BLUE), new LinearLayout.LayoutParams(0, dp(126), 1f));
        spaceH(stats2, 10);
        stats2.addView(statCard("▦", "Bu hafta", activeDays + " gün",
                "aktif kullanım", PURPLE), new LinearLayout.LayoutParams(0, dp(126), 1f));
        page.addView(stats2);

        space(page, 14);
        LinearLayout chartCard = surfaceCard();
        chartCard.addView(head("Günlere göre tamamlananlar"));
        TextView chartSub = caption("Bu hafta yapılan görev tekrarlarının dağılımı");
        chartCard.addView(chartSub);
        space(chartCard, 8);

        WeeklyBarsView bars = new WeeklyBarsView(dayCounts);
        chartCard.addView(bars, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(205)
        ));
        page.addView(chartCard);

        space(page, 14);

        LinearLayout focus = surfaceCard();
        LinearLayout focusRow = row();
        focusRow.setGravity(Gravity.CENTER_VERTICAL);

        RingProgressView ring = new RingProgressView(rate);
        focusRow.addView(ring, new LinearLayout.LayoutParams(dp(112), dp(112)));
        spaceH(focusRow, 14);

        LinearLayout focusText = col();
        focusText.addView(head("Haftanın özeti"));
        focusText.addView(body("Bu hafta " + entered + " yeni görev ve " +
                goalsEntered + " yeni hedef eklendi."));
        TextView goalText = caption("Tamamlanan hedef: " + goalsDone +
                "  •  Aktif hedef: " + activeGoals +
                "  •  Devam eden görev periyodu: " + ongoing);
        focusText.addView(goalText);
        focusRow.addView(focusText,
                new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        focus.addView(focusRow);
        page.addView(focus);

        show(page, null);
        navState();
    }

    private LinearLayout statCard(String icon, String label, String value,
                                  String detail, int accent) {
        LinearLayout card = surfaceCard();
        card.setPadding(dp(14), dp(12), dp(14), dp(12));

        LinearLayout header = row();
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.addView(iconBadge(icon, softForAccent(accent), accent, 34),
                new LinearLayout.LayoutParams(dp(34), dp(34)));
        spaceH(header, 8);
        TextView labelView = caption(label);
        labelView.setTextColor(TEXT);
        header.addView(labelView, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ));
        card.addView(header);

        TextView valueView = head(value);
        valueView.setTextSize(25);
        valueView.setTextColor(TEXT);
        card.addView(valueView);

        TextView detailView = caption(detail);
        detailView.setTextSize(12);
        card.addView(detailView);
        return card;
    }

    private LinearLayout segmentTabs(boolean tasksActive) {
        LinearLayout outer = row();
        outer.setPadding(dp(4), dp(4), dp(4), dp(4));
        outer.setBackground(bg(SURFACE, BORDER, 18));

        Button tasksButton = segmentButton("☷  Yapılacaklar", tasksActive);
        Button goalsButton = segmentButton("◎  Hedefler", !tasksActive);

        tasksButton.setOnClickListener(v -> tasks());
        goalsButton.setOnClickListener(v -> goals());

        outer.addView(tasksButton, new LinearLayout.LayoutParams(0, dp(48), 1f));
        spaceH(outer, 5);
        outer.addView(goalsButton, new LinearLayout.LayoutParams(0, dp(48), 1f));
        return outer;
    }

    private void addBrandHeader(LinearLayout page, String subtitleText) {
        TextView brand = title("Planım");
        brand.setTextSize(32);
        page.addView(brand);
        TextView subtitle = body(subtitleText);
        subtitle.setTextColor(MUTED);
        page.addView(subtitle);
        space(page, 14);
    }

    private LinearLayout formHeader(String titleText, Runnable backAction) {
        LinearLayout row = row();
        row.setGravity(Gravity.CENTER_VERTICAL);

        Button back = compactButton("←", Color.TRANSPARENT, TEXT);
        back.setOnClickListener(v -> backAction.run());
        row.addView(back, new LinearLayout.LayoutParams(dp(48), dp(46)));

        TextView title = head(titleText);
        title.setTextSize(23);
        row.addView(title, new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f
        ));

        TextView mark = iconBadge("✓", TEAL_SOFT, TEAL, 42);
        row.addView(mark, new LinearLayout.LayoutParams(dp(42), dp(42)));
        return row;
    }

    private int goalCat(GoalItem goal) {
        LocalDate today = LocalDate.now();
        LocalDate target = TimeUtils.toDate(goal.targetDate);
        if (!target.isAfter(today.plusMonths(3))) return 0;
        if (target.isBefore(today.plusMonths(12))) return 1;
        return 2;
    }

    private int taskAccent(TaskItem task) {
        if (TaskItem.WEEKLY.equals(task.period)) return BLUE;
        if (TaskItem.MONTHLY.equals(task.period)) return AMBER;
        return TEAL;
    }

    private int taskSoft(TaskItem task) {
        if (TaskItem.WEEKLY.equals(task.period)) return BLUE_SOFT;
        if (TaskItem.MONTHLY.equals(task.period)) return AMBER_SOFT;
        return TEAL_SOFT;
    }

    private int softForAccent(int accent) {
        if (accent == BLUE) return BLUE_SOFT;
        if (accent == PURPLE) return PURPLE_SOFT;
        if (accent == AMBER) return AMBER_SOFT;
        return TEAL_SOFT;
    }

    private TaskItem findTask(List<TaskItem> items, String id) {
        for (TaskItem item : items) if (item.id.equals(id)) return item;
        return null;
    }

    private GoalItem findGoal(List<GoalItem> items, String id) {
        for (GoalItem item : items) if (item.id.equals(id)) return item;
        return null;
    }

    private LinearLayout col() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        return view;
    }

    private LinearLayout row() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.HORIZONTAL);
        return view;
    }

    private LinearLayout page() {
        LinearLayout view = col();
        view.setPadding(dp(18), dp(14), dp(18), dp(24));
        return view;
    }

    private LinearLayout surfaceCard() {
        LinearLayout card = col();
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.setBackground(bg(SURFACE, BORDER, 18));
        card.setElevation(dp(3));
        return card;
    }

    private LinearLayout softCard(int color) {
        LinearLayout card = col();
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.setBackground(bg(color, color, 18));
        return card;
    }

    private GradientDrawable bg(int color, int stroke, int radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radius));
        drawable.setStroke(dp(1), stroke);
        return drawable;
    }

    private TextView title(String value) {
        TextView view = text(value);
        view.setTextSize(30);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(TEXT);
        return view;
    }

    private TextView sectionTitle(String value) {
        TextView view = text(value);
        view.setTextSize(20);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(TEXT);
        return view;
    }

    private TextView head(String value) {
        TextView view = text(value);
        view.setTextSize(17);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(TEXT);
        return view;
    }

    private TextView body(String value) {
        TextView view = text(value);
        view.setTextSize(15);
        view.setLineSpacing(0, 1.08f);
        return view;
    }

    private TextView caption(String value) {
        TextView view = text(value);
        view.setTextSize(13);
        view.setTextColor(MUTED);
        return view;
    }

    private TextView label(String value) {
        TextView view = text(value);
        view.setTextSize(14);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setTextColor(TEXT);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.bottomMargin = dp(7);
        view.setLayoutParams(lp);
        return view;
    }

    private TextView text(String value) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextColor(TEXT);
        view.setIncludeFontPadding(false);
        return view;
    }

    private TextView chip(String value, int color, int textColor) {
        TextView view = text(value);
        view.setTextSize(12);
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(10), dp(5), dp(10), dp(5));
        view.setBackground(bg(color, color, 14));
        return view;
    }

    private TextView iconBadge(String value, int color, int textColor, int size) {
        TextView view = text(value);
        view.setTextSize(size >= 50 ? 22 : 17);
        view.setTextColor(textColor);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setBackground(bg(color, color, size / 2));
        return view;
    }

    private TextView valueText(String value) {
        TextView view = text(value);
        view.setTextSize(18);
        view.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        view.setGravity(Gravity.CENTER);
        view.setBackground(bg(SURFACE, BORDER, 0));
        return view;
    }

    private EditText edit(String hint) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setHintTextColor(Color.rgb(148, 163, 184));
        edit.setTextColor(TEXT);
        edit.setTextSize(15);
        edit.setSingleLine(true);
        edit.setPadding(dp(14), 0, dp(14), 0);
        edit.setBackground(bg(SURFACE, BORDER, 14));
        return edit;
    }

    private Button primary(String value) {
        Button button = baseButton(value);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setTextSize(15);
        button.setBackground(bg(TEAL, TEAL_DARK, 15));
        button.setMinHeight(dp(52));
        return button;
    }

    private Button compactButton(String value, int color, int textColor) {
        Button button = baseButton(value);
        button.setTextColor(textColor);
        button.setTextSize(13);
        button.setBackground(bg(color, color == Color.TRANSPARENT ? Color.TRANSPARENT : BORDER, 13));
        return button;
    }

    private Button segmentButton(String value, boolean active) {
        Button button = baseButton(value);
        button.setTextSize(13);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        styleSegmentButton(button, active);
        return button;
    }

    private void styleSegmentButton(Button button, boolean active) {
        button.setTextColor(active ? TEAL_DARK : MUTED);
        button.setBackground(bg(active ? TEAL_SOFT : SURFACE,
                active ? TEAL_SOFT : SURFACE, 14));
    }

    private Button navButton(String value) {
        Button button = baseButton(value);
        button.setTextSize(12);
        button.setGravity(Gravity.CENTER);
        button.setLineSpacing(0, .9f);
        button.setPadding(0, dp(3), 0, dp(2));
        return button;
    }

    private void styleNav(Button button, boolean active) {
        button.setTextColor(active ? TEAL_DARK : MUTED);
        button.setTypeface(Typeface.DEFAULT, active ? Typeface.BOLD : Typeface.NORMAL);
        button.setBackground(bg(active ? TEAL_SOFT : SURFACE,
                active ? TEAL_SOFT : SURFACE, 17));
    }

    private Button baseButton(String value) {
        Button button = new Button(this);
        button.setAllCaps(false);
        button.setText(value);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setPadding(dp(9), 0, dp(9), 0);
        if (Build.VERSION.SDK_INT >= 21) button.setStateListAnimator(null);
        return button;
    }

    private void show(LinearLayout page, Runnable fabAction) {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        scroll.setPadding(0, 0, 0, fabAction != null ? dp(76) : dp(12));
        scroll.addView(page, new ScrollView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        host.removeAllViews();
        host.addView(scroll, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        ));

        if (fabAction != null) {
            TextView fab = iconBadge("+", TEAL, Color.WHITE, 58);
            fab.setTextSize(32);
            fab.setElevation(dp(10));
            fab.setOnClickListener(v -> fabAction.run());

            FrameLayout.LayoutParams fabLp = new FrameLayout.LayoutParams(dp(58), dp(58));
            fabLp.gravity = Gravity.END | Gravity.BOTTOM;
            fabLp.setMargins(0, 0, dp(22), dp(16));
            host.addView(fab, fabLp);
        }
    }

    private void space(LinearLayout parent, int size) {
        View view = new View(this);
        parent.addView(view, new LinearLayout.LayoutParams(1, dp(size)));
    }

    private void spaceH(LinearLayout parent, int size) {
        View view = new View(this);
        parent.addView(view, new LinearLayout.LayoutParams(dp(size), 1));
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + .5f);
    }

    private final class WeeklyBarsView extends View {
        private final int[] values;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String[] labels = {"Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"};

        WeeklyBarsView(int[] values) {
            super(MainActivity.this);
            this.values = values.clone();
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            int width = getWidth();
            int height = getHeight();
            float left = dp(22);
            float right = width - dp(8);
            float top = dp(14);
            float bottom = height - dp(32);
            float plotHeight = bottom - top;

            int max = 1;
            for (int value : values) max = Math.max(max, value);

            paint.setStrokeWidth(dp(1));
            paint.setColor(BORDER);
            for (int i = 0; i <= 3; i++) {
                float y = top + plotHeight * i / 3f;
                canvas.drawLine(left, y, right, y, paint);
            }

            float slot = (right - left) / 7f;
            float barWidth = slot * .46f;
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT);

            for (int i = 0; i < 7; i++) {
                float center = left + slot * (i + .5f);
                float barHeight = values[i] == 0 ? dp(3) : plotHeight * values[i] / max;
                float barTop = bottom - barHeight;

                paint.setColor(TEAL);
                RectF rect = new RectF(center - barWidth / 2f, barTop,
                        center + barWidth / 2f, bottom);
                canvas.drawRoundRect(rect, dp(6), dp(6), paint);

                if (values[i] > 0) {
                    paint.setColor(TEXT);
                    paint.setTextSize(dp(11));
                    canvas.drawText(String.valueOf(values[i]), center, barTop - dp(6), paint);
                }

                paint.setColor(MUTED);
                paint.setTextSize(dp(11));
                canvas.drawText(labels[i], center, height - dp(9), paint);
            }
        }
    }

    private final class RingProgressView extends View {
        private final int percent;
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        RingProgressView(int percent) {
            super(MainActivity.this);
            this.percent = Math.max(0, Math.min(100, percent));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            float stroke = dp(11);
            float padding = stroke + dp(5);
            RectF rect = new RectF(padding, padding,
                    getWidth() - padding, getHeight() - padding);

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(stroke);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(Color.rgb(235, 239, 242));
            canvas.drawArc(rect, 0, 360, false, paint);

            paint.setColor(TEAL);
            canvas.drawArc(rect, -90, 360f * percent / 100f, false, paint);

            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setTextSize(dp(19));
            paint.setColor(TEXT);
            canvas.drawText("%" + percent, getWidth() / 2f,
                    getHeight() / 2f + dp(5), paint);
        }
    }
}
