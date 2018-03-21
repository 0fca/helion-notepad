package notepad.threading;

import javafx.concurrent.Task;

import java.util.Arrays;

public class SettingsTaskFactory implements AbstractTaskFactory {
    private String textToParse,returnValue;

    public SettingsTaskFactory(String text){
        this.textToParse = text;
    }

    @Override
    public Task createTask(int id) {
        //System.out.println("Creating task");
        return new Task(){
            @Override
            protected String call(){
                System.out.println("Started parsing...");

                System.out.println("Task "+id+" : "+returnValue);
                return returnValue;
            }
        };
    }
}
