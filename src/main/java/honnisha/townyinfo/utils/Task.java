package honnisha.townyinfo.utils;

import honnisha.townyinfo.Townyinfo;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;

public class Task {
    private long lastRunTime;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final String timeToExecute;
    private final ITask taskRunner;
    private final String dayOfWeek;

    public Task(String timeToExecute, ITask taskRunner) {
        String[] args = timeToExecute.split("-");
        this.dayOfWeek = args[0];
        this.timeToExecute = args[1];

        this.taskRunner = taskRunner;

        final DayOfWeek currentDayOfWeek = LocalDateTime.now().getDayOfWeek();
        String currentDayOfWeekName = currentDayOfWeek.getDisplayName(TextStyle.FULL, Locale.US);
        Townyinfo.logger.info(String.format(
                "Task registered for execute at %s day: %s (now:%s day:%s)",
                this.timeToExecute, this.dayOfWeek, Task.dateFormat.format(new Date()), currentDayOfWeekName
        ));
    }

    public boolean hasTime() {

        final DayOfWeek currentDayOfWeek = LocalDateTime.now().getDayOfWeek();
        String currentDayOfWeekName = currentDayOfWeek.getDisplayName(TextStyle.FULL, Locale.US);

        if (this.dayOfWeek.equalsIgnoreCase("everyday") || this.dayOfWeek.equalsIgnoreCase(currentDayOfWeekName)) {
            if(Task.dateFormat.format(new Date()).equals(this.timeToExecute)) {
                if(lastRunTime == 0) return true;
                return System.currentTimeMillis() - lastRunTime > 1000 * 60;
            }
        }
        return false;
    }

    public void run () {
        lastRunTime = System.currentTimeMillis();
        this.taskRunner.runTask();
    }
}
