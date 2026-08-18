package com.ridvan.planim;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private FrameLayout host;
    private String screen = "HOME";
    private int weekOffset = 0;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
        ReminderScheduler.scheduleAll(this);
        buildShell();
        home();
    }

    @Override public void onBackPressed() {
        if (!"HOME".equals(screen)) home(); else super.onBackPressed();
    }

    private void buildShell() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(10), dp(14), dp(10));
        root.setBackgroundColor(Color.rgb(248,248,248));
        host = new FrameLayout(this);
        root.addView(host, new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout nav = row();
        Button h = btn("Ana Sayfa"), r = btn("Raporlar");
        h.setOnClickListener(v -> home()); r.setOnClickListener(v -> report());
        nav.addView(h, new LinearLayout.LayoutParams(0,dp(48),1)); spaceH(nav,8);
        nav.addView(r, new LinearLayout.LayoutParams(0,dp(48),1));
        root.addView(nav);
        setContentView(root);
    }

    private void home() {
        screen="HOME";
        LinearLayout p=page();
        p.addView(title("Planım"));
        p.addView(text(TimeUtils.formatDate(System.currentTimeMillis())));
        space(p,18);
        Button t=primary("YAPILACAKLAR\nGünlük, haftalık veya aylık tekrarlar");
        Button g=primary("HEDEFLER\nKısa, orta ve uzun vadeli hedefler");
        t.setOnClickListener(v->tasks()); g.setOnClickListener(v->goals());
        p.addView(t); space(p,12); p.addView(g); space(p,18);
        List<TaskItem> ts=AppStore.loadTasks(this); List<GoalItem> gs=AppStore.loadGoals(this);
        int ok=0, active=0; for(TaskItem x:ts) if(TimeUtils.currentCount(x)>=x.requiredCount) ok++;
        for(GoalItem x:gs) if(!x.completed) active++;
        p.addView(cardText("Bugünkü görünüm\nYapılacak: "+ts.size()+"\nPeriyot hedefi tamamlanan: "+ok+"\nAktif hedef: "+active));
        show(p);
    }

    private void tasks() {
        screen="TASKS"; LinearLayout p=page(); p.addView(title("Yapılacaklar")); space(p,8);
        List<TaskItem> list=AppStore.loadTasks(this);
        if(list.isEmpty()) p.addView(text("Henüz yapılacak eklenmedi."));
        for(TaskItem t:list){ p.addView(taskCard(t)); space(p,10); }
        Button add=primary(list.isEmpty()?"+ İlk Yapılacağı Ekle":"+ Ekle"); add.setOnClickListener(v->taskTitle()); p.addView(add); show(p);
    }

    private View taskCard(TaskItem t) {
        LinearLayout c=card(); int n=TimeUtils.currentCount(t);
        TextView a=text(t.title); a.setTextSize(18); a.setTypeface(null,Typeface.BOLD); c.addView(a);
        c.addView(text(TimeUtils.periodLabel(t.period)+" • Hedef "+t.requiredCount+" tekrar"));
        TextView pr=text(n+"/"+t.requiredCount); pr.setTypeface(null,Typeface.BOLD); c.addView(pr);
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); bar.setMax(t.requiredCount); bar.setProgress(Math.min(n,t.requiredCount)); c.addView(bar,new LinearLayout.LayoutParams(-1,dp(12)));
        if(t.reminderEnabled)c.addView(text(String.format(Locale.getDefault(),"Hatırlatma: %02d:%02d",t.reminderHour,t.reminderMinute)));
        space(c,8); LinearLayout rr=row();
        Button plus=btn("+1"), undo=btn("Geri al"), del=btn("Sil"); plus.setEnabled(n<t.requiredCount); undo.setEnabled(n>0);
        plus.setOnClickListener(v->{List<TaskItem> all=AppStore.loadTasks(this); TaskItem x=findTask(all,t.id); if(x!=null&&TimeUtils.currentCount(x)<x.requiredCount){x.completions.add(System.currentTimeMillis());AppStore.saveTasks(this,all);} tasks();});
        undo.setOnClickListener(v->{List<TaskItem> all=AppStore.loadTasks(this); TaskItem x=findTask(all,t.id); if(x!=null){long[] q=TimeUtils.currentPeriod(x);for(int i=x.completions.size()-1;i>=0;i--){long z=x.completions.get(i);if(z>=q[0]&&z<=q[1]){x.completions.remove(i);break;}}AppStore.saveTasks(this,all);}tasks();});
        del.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Silinsin mi?").setMessage(t.title).setNegativeButton("Vazgeç",null).setPositiveButton("Sil",(d,w)->{List<TaskItem> all=AppStore.loadTasks(this);all.removeIf(x->x.id.equals(t.id));AppStore.saveTasks(this,all);ReminderScheduler.cancel(this,t.id);tasks();}).show());
        rr.addView(plus,new LinearLayout.LayoutParams(0,dp(44),1));spaceH(rr,5);rr.addView(undo,new LinearLayout.LayoutParams(0,dp(44),1));spaceH(rr,5);rr.addView(del,new LinearLayout.LayoutParams(0,dp(44),1));c.addView(rr); return c;
    }

    private void taskTitle(){ TaskItem d=new TaskItem(); EditText e=new EditText(this);e.setHint("Başlık"); AlertDialog a=new AlertDialog.Builder(this).setTitle("1/4 • Başlık").setView(e).setNegativeButton("İptal",null).setPositiveButton("Devam",null).create(); a.setOnShowListener(x->a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{String s=e.getText().toString().trim();if(s.isEmpty()){e.setError("Başlık gerekli");return;}d.title=s;a.dismiss();taskPeriod(d);}));a.show(); }
    private void taskPeriod(TaskItem d){String[] x={"Günlük","Haftalık","Aylık"};new AlertDialog.Builder(this).setTitle("2/4 • Periyot").setItems(x,(a,i)->{d.period=i==1?TaskItem.WEEKLY:i==2?TaskItem.MONTHLY:TaskItem.DAILY;taskRepeat(d);}).setNegativeButton("İptal",null).show();}
    private void taskRepeat(TaskItem d){NumberPicker n=new NumberPicker(this);n.setMinValue(1);n.setMaxValue(10);n.setValue(1);new AlertDialog.Builder(this).setTitle("3/4 • Tekrar").setMessage("Seçilen periyot içinde kaç kez yapılmalı?").setView(n).setNegativeButton("İptal",null).setPositiveButton("Devam",(a,w)->{d.requiredCount=n.getValue();taskReminder(d);}).show();}
    private void taskReminder(TaskItem d){new AlertDialog.Builder(this).setTitle("4/4 • Hatırlatma").setMessage("Hatırlatma eklemek ister misiniz?").setNegativeButton("Hayır",(a,w)->saveTask(d)).setPositiveButton("Saat seç",(a,w)->new TimePickerDialog(this,(v,h,m)->{d.reminderEnabled=true;d.reminderHour=h;d.reminderMinute=m;saveTask(d);},9,0,true).show()).show();}
    private void saveTask(TaskItem d){List<TaskItem> x=AppStore.loadTasks(this);x.add(d);AppStore.saveTasks(this,x);if(d.reminderEnabled)ReminderScheduler.schedule(this,d);tasks();}

    private void goals(){screen="GOALS";LinearLayout p=page();p.addView(title("Hedefler"));space(p,8);List<GoalItem> all=AppStore.loadGoals(this);String[] cats={"Kısa Vadeli • 0–3 ay","Orta Vadeli • 3–12 ay","Uzun Vadeli • 12+ ay"};for(int c=0;c<3;c++){TextView h=text(cats[c]);h.setTextSize(18);h.setTypeface(null,Typeface.BOLD);p.addView(h);boolean any=false;for(GoalItem g:all)if(goalCat(g)==c){p.addView(goalCard(g));space(p,7);any=true;}if(!any)p.addView(text("Henüz hedef yok."));space(p,14);}Button add=primary("+ Hedef Ekle");add.setOnClickListener(v->goalTitle());p.addView(add);show(p);}
    private int goalCat(GoalItem g){long m=ChronoUnit.MONTHS.between(LocalDate.now(),TimeUtils.toDate(g.targetDate));if(m<3)return 0;if(m<12)return 1;return 2;}
    private View goalCard(GoalItem g){LinearLayout c=card();TextView t=text((g.completed?"✓ ":"")+g.title);t.setTypeface(null,Typeface.BOLD);c.addView(t);c.addView(text("Hedef tarihi: "+TimeUtils.formatDate(g.targetDate)));LinearLayout r=row();Button done=btn(g.completed?"Geri al":"Tamamlandı"),del=btn("Sil");done.setOnClickListener(v->{List<GoalItem> a=AppStore.loadGoals(this);GoalItem x=findGoal(a,g.id);if(x!=null){x.completed=!x.completed;x.completedAt=x.completed?System.currentTimeMillis():0;AppStore.saveGoals(this,a);}goals();});del.setOnClickListener(v->{List<GoalItem>a=AppStore.loadGoals(this);a.removeIf(x->x.id.equals(g.id));AppStore.saveGoals(this,a);goals();});r.addView(done,new LinearLayout.LayoutParams(0,dp(44),1));spaceH(r,6);r.addView(del,new LinearLayout.LayoutParams(0,dp(44),1));c.addView(r);return c;}
    private void goalTitle(){GoalItem g=new GoalItem();EditText e=new EditText(this);e.setHint("Hedef");AlertDialog a=new AlertDialog.Builder(this).setTitle("Hedef başlığı").setView(e).setNegativeButton("İptal",null).setPositiveButton("Tarih seç",null).create();a.setOnShowListener(x->a.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v->{String s=e.getText().toString().trim();if(s.isEmpty()){e.setError("Başlık gerekli");return;}g.title=s;a.dismiss();LocalDate n=LocalDate.now();new DatePickerDialog(this,(q,y,m,d)->{LocalDate ld=LocalDate.of(y,m+1,d);g.targetDate=TimeUtils.atStart(ld);List<GoalItem>z=AppStore.loadGoals(this);z.add(g);AppStore.saveGoals(this,z);goals();},n.getYear(),n.getMonthValue()-1,n.getDayOfMonth()).show();}));a.show();}

    private void report(){screen="REPORT";LinearLayout p=page();p.addView(title("Haftalık Rapor"));LocalDate mon=TimeUtils.mondayForOffset(weekOffset),sun=mon.plusDays(6);p.addView(text(TimeUtils.formatShort(mon)+" – "+TimeUtils.formatShort(sun)));LinearLayout r=row();Button prev=btn("← Önceki"),next=btn(weekOffset<0?"Sonraki →":"Bu Hafta");next.setEnabled(weekOffset<0);prev.setOnClickListener(v->{weekOffset--;report();});next.setOnClickListener(v->{if(weekOffset<0)weekOffset++;report();});r.addView(prev,new LinearLayout.LayoutParams(0,dp(44),1));spaceH(r,8);r.addView(next,new LinearLayout.LayoutParams(0,dp(44),1));p.addView(r);space(p,12);
        List<TaskItem> ts=AppStore.loadTasks(this);List<GoalItem> gs=AppStore.loadGoals(this);long ws=TimeUtils.atStart(mon),we=TimeUtils.atEnd(sun);int entered=0,gentered=0,gdone=0;for(TaskItem t:ts)if(t.createdAt>=ws&&t.createdAt<=we)entered++;for(GoalItem g:gs){if(g.createdAt>=ws&&g.createdAt<=we)gentered++;if(g.completedAt>=ws&&g.completedAt<=we)gdone++;}int ok=0,fail=0,ongoing=0;LocalDate today=LocalDate.now();for(TaskItem t:ts){if(TimeUtils.toDate(t.createdAt).isAfter(sun))continue;if(TaskItem.DAILY.equals(t.period)){for(LocalDate d=mon;!d.isAfter(sun);d=d.plusDays(1)){if(d.isBefore(TimeUtils.toDate(t.createdAt)))continue;if(weekOffset==0&&!d.isBefore(today)){if(d.equals(today))ongoing++;continue;}int c=TimeUtils.count(t.completions,TimeUtils.atStart(d),TimeUtils.atEnd(d));if(c>=t.requiredCount)ok++;else fail++;}}else if(TaskItem.WEEKLY.equals(t.period)){if(weekOffset==0)ongoing++;else{int c=TimeUtils.count(t.completions,ws,we);if(c>=t.requiredCount)ok++;else fail++;}}else ongoing++;}
        int den=ok+fail,rate=den==0?0:(int)Math.round(ok*100.0/den);p.addView(cardText("Yapılacaklar\nBu hafta girilen: "+entered+"\nYeterli tekrara ulaşan periyot: "+ok+"\nYetersiz kalan periyot: "+fail+"\nDevam eden periyot: "+ongoing+"\nBaşarı oranı: %"+rate));space(p,10);int active=0;for(GoalItem g:gs)if(!g.completed)active++;p.addView(cardText("Hedefler\nBu hafta girilen: "+gentered+"\nBu hafta tamamlanan: "+gdone+"\nToplam aktif hedef: "+active));show(p);}

    private TaskItem findTask(List<TaskItem>a,String id){for(TaskItem x:a)if(x.id.equals(id))return x;return null;} private GoalItem findGoal(List<GoalItem>a,String id){for(GoalItem x:a)if(x.id.equals(id))return x;return null;}
    private LinearLayout page(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(4),dp(10),dp(4),dp(22));return x;} private LinearLayout row(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.HORIZONTAL);return x;}
    private LinearLayout card(){LinearLayout x=new LinearLayout(this);x.setOrientation(LinearLayout.VERTICAL);x.setPadding(dp(15),dp(13),dp(15),dp(13));x.setBackgroundColor(Color.WHITE);return x;} private View cardText(String s){LinearLayout c=card();c.addView(text(s));return c;}
    private TextView title(String s){TextView x=text(s);x.setTextSize(28);x.setTypeface(null,Typeface.BOLD);return x;} private TextView text(String s){TextView x=new TextView(this);x.setText(s);x.setTextSize(15);x.setTextColor(Color.rgb(30,30,30));x.setPadding(0,dp(3),0,dp(3));return x;}
    private Button btn(String s){Button b=new Button(this);b.setAllCaps(false);b.setText(s);return b;} private Button primary(String s){Button b=btn(s);b.setTextSize(17);b.setGravity(Gravity.CENTER);b.setMinHeight(dp(72));return b;}
    private void show(LinearLayout p){ScrollView s=new ScrollView(this);s.addView(p,new ScrollView.LayoutParams(-1,-2));host.removeAllViews();host.addView(s,new FrameLayout.LayoutParams(-1,-1));}
    private void space(LinearLayout x,int n){View v=new View(this);x.addView(v,new LinearLayout.LayoutParams(1,dp(n)));} private void spaceH(LinearLayout x,int n){View v=new View(this);x.addView(v,new LinearLayout.LayoutParams(dp(n),1));} private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);} private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
