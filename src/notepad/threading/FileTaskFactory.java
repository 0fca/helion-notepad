package notepad.threading;

import javafx.concurrent.Task;
import notepad.FilesystemController;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class FileTaskFactory implements AbstractTaskFactory {
    private Object[] params;
    private int result = 0;
    private Operation o;
    private Class<?>[] paramTypes;

    public void setOperation(Operation o){
        this.o = o;
    }

    public void setParams(Object[] listOfParams){
        params = listOfParams;
        detectTypes();
    }

    @Override
    public Task<Object> createTask(int id) {
        return new Task<Object>(){
            @Override
            protected Object call() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                System.out.println("FileTask started...");
                System.out.println("Task "+id);
                Object ret = doOperation();
                System.out.println(ret.getClass());
                return ret;
            }

            private Object doOperation() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                System.out.println(o.getMethodName()+" : "+paramTypes[0]+" : "+params[0]);
                return FilesystemController.class.getDeclaredMethod(o.getMethodName(), paramTypes).invoke(null, params);
            }
        };
    }

    private void detectTypes(){
        paramTypes = new Class<?>[params.length];
        IntStream.range(0, params.length).forEach(i -> paramTypes[i] =  params[i].getClass());
    }

    @Override
    public String toString(){
        return this.getClass().getName()+" handling "+o.name()+" operation.";
    }
}
