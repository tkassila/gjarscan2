package gjarscan2;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class JarScanProcesses extends Service<String> {
    private static final String NEWLINE = System.getProperty("line.separator");

    private Process process = null;
    private int exitValue = -1;
  //  private StringBuilder result;
    private StringBuilder resultError;
    private String m_strWorkingDir = null;
    private String m_strExecute = null;
    private String strMessage = null;

    private StringProperty result = new SimpleStringProperty();

    public final void setResult(String value) {
        result.set(value);
    }

    public final String getResult() {
        return result.get();
    }

    public final StringProperty resultProperty() {
        return url;
    }

    public String getStrMessage() { return this.strMessage; }
    public void setExecutionData(String p_strWorkingDir, String p_strExecute)
    {
        this.m_strWorkingDir = p_strWorkingDir;
        this.m_strExecute = p_strExecute;
    }

    public int getExitValue() { return exitValue; }
    public String getResultString() {
        if (result == null)
            return null;
        return result.toString();
    }

    public String getErrorString() {
        if (resultError == null)
            return null;
        return resultError.toString();
    }

    public boolean cancelProcess()
    {
        if (this.process == null)
            return false;
        process.destroy();
        return true;
    }

    /**
     * @param command the command to run
     * @return the output of the command
     * @throws IOException if an I/O error occurs
     */
    protected final String runJava(String strWorkingDir, String strCommand)
            throws IOException, InterruptedException
    {
        OutputStream stdin = null;
        InputStream stderr = null;
        InputStream stdout = null;
        strMessage = null;
        this.exitValue = -1;
        result.setValue("");
        StringBuffer sb = new StringBuffer();
        resultError = new StringBuilder(80);
        try {
            if (Main.bDebug)
                System.out.println(System.getProperty("java.home"));
            String strJavaHome = System.getProperty("java.home");
            String classpath = System.getProperty("java.class.path");
            classpath = correctSpaceContainsClassPath(classpath);
            // classpath = ""; // classpath.replaceAll("\n", "");
            String javaBin = strJavaHome + File.separator + "bin" + File.separator + "java";
            List<String> jvmArgs = new ArrayList<String>();
            String className = "com.inetfeedback.jarscan.JarScan21";
            String strUpdatedCommand = strCommand +" -donotask";
            List<String> java_app_args = Arrays.asList(strUpdatedCommand.split(" "));
            List<String> command = new ArrayList<String>();
            command.add(javaBin);
            command.addAll(jvmArgs);
            command.add("-cp");

            File workingDir = new File(".");
            /*
            String preCP = new String(getClassPathSeparator()
                +workingDir.getAbsolutePath() +getClassPathSeparator());
            command.add("." +preCP +classpath);
            */
            String preCP = new String("." +getClassPathSeparator());
            command.add(preCP +classpath);
            command.add(className);
            command.addAll(java_app_args);

            String line = null;
            String strExec = strJavaHome +"\\bin\\java -version";
            Map<java.lang.String, java.lang.String> mapEnv = System.getenv();
            String [] arrEnv = new String[mapEnv.size()];
            String value;
            int i = 0;
            for (String key : mapEnv.keySet())
            {
                value = mapEnv.get(key);
                arrEnv[i] = "" +key +"=" +value;
                i++;
            }
            System.out.println(arrEnv.toString());
            strExec = command.stream().collect(Collectors.joining(" "));
           // strExec = "C:\\java\\jdk1.8.0\\jre\\bin\\java -cp .;C:\\java\\project\\javafx\\gjarscan2\\out\\production\\gjarscan2;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\groovyHotSwap\\gragent.jar;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\captureAgent\\debugger-agent.jar com.inetfeedback.jarscan.JarScan21 -dir \\java\\project\\javafx\\gjarscan2 -class JarScan -donotask";
            System.out.println(strExec);
            // ProcessBuilder builder = new ProcessBuilder(command);
            // Process process = Runtime.getRuntime().exec(strExec, arrEnv, workingDir);
            Process process = Runtime.getRuntime().exec(strExec, arrEnv, workingDir);
            // -> builder.directory(workingDir);
            // Process process = builder.start();
            //  Map<String, String> environ = builder.environment();
            // builder.directory(new File("C:\\java\\project\\javafx\\gjarscan2\\out\\production\\gjarscan2"));

            // System.out.println("Directory : " + System.getenv("temp") );
            // process = builder.start();
            stdin = process.getOutputStream ();
            stderr = process.getErrorStream ();
            stdout = process.getInputStream ();
            BufferedReader bri = new BufferedReader
                    (new InputStreamReader(stdout));
            BufferedReader bre = new BufferedReader
                    (new InputStreamReader(stderr));
            while ((line = bri.readLine()) != null) {
                System.out.println(line);
                if (!line.contains(NEWLINE))
                    sb.append(line).append(NEWLINE);
                else
                    sb.append(line);
            }
            bri.close();
            while ((line = bre.readLine()) != null) {
                System.out.println(line);
                if (!line.contains(NEWLINE))
                    resultError.append(line).append(NEWLINE);
                else
                    resultError.append(line);
            }
            bre.close();
            result.setValue(sb.toString());
            exitValue = process.waitFor();

            System.out.println("exitValue: " +process.exitValue());
            // exitValue = process.exitValue();
            System.out.println("Done.");
            process = null;
        }
        catch (IOException err) {
            err.printStackTrace();
            exitValue = process.exitValue();
            strMessage = err.getMessage();
            process = null;
            throw err;
        } catch (InterruptedException ie){
            process = null;
            throw ie;
        }

        /*
        try {
            Map<String, String> mapEnvironments = System.getenv();
            System.out.println(System.getProperty("java.home"));
            String strJavaHome = System.getProperty("java.home");
            if (strJavaHome.contains(" ") && File.separatorChar == '\\')
            {
                strJavaHome = '"' + strJavaHome.trim() + '"';
            }
            command = command.replaceAll("/", "\\\\");
            command = new String("c:\\Java\\jdk1.8.0\\bin\\java -version");
            mapEnvironments.entrySet().forEach(System.out::println);
            ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
            // pb.strWorkingDir(strWorkingDir);
          //  pb.directory(new File(strWorkingDir));
            Process process = pb.start();
             result= new StringBuilder(80);
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while (true)
            {
                    String line = in.readLine();
                    if (line == null)
                        break;
                    result.append(line).append(NEWLINE);
            }
            exitValue = process.exitValue();
        }catch (Exception e){
            if (process != null)
                exitValue = process.exitValue();
            if (result != null)
                result.append(e.getMessage());
            throw e;
        }
         */
        return sb.toString();
    }

    /**
     * Prevent construction.
     */
    public JarScanProcesses()
    {
        super();
    }

    private StringProperty url = new SimpleStringProperty();
    public final String getWorkingDir() {
        return m_strWorkingDir;
    }
    public final String getExecuteString() {
        return this.m_strExecute;
    }

    public final StringProperty urlProperty() {
        return url;
    }

    protected Task<String> createTask() {
        final String _workingdir = getWorkingDir();
        final String _strExecution = getExecuteString();
        return new Task<String>() {
            protected String call()
                    throws IOException, InterruptedException {
         //           try {
                        return runJava(_workingdir, _strExecution);
           /*         }catch (Exception e){
                        cancelled();
                        throw e;
                    }finally {
                        succeeded();
                    }
                    */
            }
        };
    }

    @Override protected void succeeded() {
        super.succeeded();
        this.strMessage = "Done!";
    }

    @Override protected void cancelled() {
        super.cancelled();
        this.strMessage = "Cancelled!";
    }

    public static String getClassPathSeparator()
    {
        return (File.separatorChar == '\\' ? ";": ":");
    }

    public static String correctSpaceContainsClassPath(String classpath)
    {
        if (classpath == null)
            return classpath;
        if (classpath.trim().length() == 0)
            return classpath;
        if (!classpath.contains(" "))
            return classpath;
        int ind = classpath.indexOf(' ');
        if (ind == -1)
            return classpath;
        String classPathSeparator = (File.separatorChar == '\\' ? ";" : ":");
        int indclassPathSeparator = classpath.indexOf(classPathSeparator);
        if (indclassPathSeparator == -1)
            return classPathSeparator +JarScanProcesses.getCorrectedOneClassPath(classpath) +classPathSeparator;

        String regexSeparator = "(" + classPathSeparator +"|$)";
        String [] arrClassPath = classpath.split(regexSeparator);
        if (arrClassPath == null || arrClassPath.length == 0)
            return classpath;
        StringBuffer sb = new StringBuffer();
        for(String oneCPath : arrClassPath)
        {
            if (oneCPath == null || oneCPath.trim().length() == 0)
                continue;
            sb.append(classPathSeparator +getCorrectedOneClassPath(oneCPath));
        }
        String ret = sb.toString();
        return ret;
    }

    public static String getCorrectedOneClassPath(String oneClassPathValue)
    {
        if (oneClassPathValue == null)
            return oneClassPathValue;
        if (oneClassPathValue.trim().length() == 0)
            return oneClassPathValue.trim();
        int ind = oneClassPathValue.indexOf(' ');
        if (ind == -1)
            return oneClassPathValue;
        return "\"" +oneClassPathValue +"\"";
    }
}
