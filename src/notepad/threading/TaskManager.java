package notepad.threading;

import javafx.concurrent.Task;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.*;
import java.util.stream.IntStream;

public class TaskManager {
    private volatile static TaskManager taskManager = new TaskManager();

    private ExecutorService executor = Executors.newCachedThreadPool();


    private TaskManager(){}

    @Contract(pure = true)
    public static synchronized TaskManager getTaskManager(){
        return taskManager;
    }
    private int id = 0;

    public Task submitTask(AbstractTaskFactory a){
        System.out.println("Starting task...");
        Task current = a.createTask(id++);
        executor.submit(current);
        return current;
    }

    public void kill(){
        executor.shutdownNow();
    }
}
