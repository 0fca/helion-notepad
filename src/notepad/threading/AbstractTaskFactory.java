package notepad.threading;

import javafx.concurrent.Task;

@FunctionalInterface
public interface AbstractTaskFactory<T> {
    Task<T> createTask(int id);
}
