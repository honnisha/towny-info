package honnisha.townyinfo.utils;

import honnisha.townyinfo.Townyinfo;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Task {
    private long lastRunTime;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    private final String timeToExecute;
    private final ITask taskRunner;

    public Task(String timeToExecute, ITask taskRunner) {
        this.timeToExecute = timeToExecute;
        this.taskRunner = taskRunner;
        Townyinfo.logger.info(String.format("Task registered for execute at %s (now:%s)", this.timeToExecute, Task.dateFormat.format(new Date())));
    }

    public boolean hasTime() {
        if(Task.dateFormat.format(new Date()).equals(this.timeToExecute)) {
            if(lastRunTime == 0) return true;
            return System.currentTimeMillis() - lastRunTime > 1000 * 60;
        }
        return false;
    }

    public void run () {
        lastRunTime = System.currentTimeMillis();
        this.taskRunner.runTask();
    }
}
