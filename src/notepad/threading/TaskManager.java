package notepad.threading;

import javafx.concurrent.Task;
import org.jetbrains.annotations.Contract;
import java.util.concurrent.*;

public class TaskManager {
    private volatile static TaskManager taskManager = new TaskManager();
    private ThreadPoolExecutor tpm = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    private TaskManager(){}

    @Contract(pure = true)
    public static synchronized TaskManager getTaskManager(){
        return taskManager;
    }

    public Task submitTask(AbstractTaskFactory a){
        System.out.println("Starting task...");
        Task current = a.createTask(tpm.getActiveCount() + 1);
        tpm.submit(current);
        return current;
    }

    public void kill(){
        tpm.shutdownNow();
    }
}
