package notepad.threading;

public enum Operation {
    OPEN("openFile"),
    CLOSE("closeFile"),
    CREATE("newFile"),
    SAVE("saveFile");

    private String methodName;

    Operation(String methodName){
        this.methodName = methodName;
    }

    public String getMethodName(){
        return methodName;
    }
}
