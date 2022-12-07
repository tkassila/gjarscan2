package gjarscan2.runmain;

import gjarscan2.Main;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class RunMain {
    public static void main(String args[]) {
//        Main.runArgs = args;
//        String classPath = Main.getClassPathRunArg();
        OutputStream stdin = null;
            InputStream stderr = null;
            InputStream stdout = null;
            try {
                System.out.println(System.getProperty("java.home"));
                String strJavaHome = System.getProperty("java.home");
                String classpath = System.getProperty("java.class.path");
                String javaBin = strJavaHome + File.separator + "bin" + File.separator + "java";
                List<String> jvmArgs = new ArrayList<String>();
                String className = "com.inetfeedback.jarscan.JarScan21";
                List<String> java_app_args = new ArrayList<String>();;
                java_app_args.add("-help");
                List<String> command = new ArrayList<String>();
                command.add(javaBin);
                command.addAll(jvmArgs);
                command.add("-cp");
                command.add(classpath);
                command.add(className);
                command.addAll(java_app_args);

                String line;
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
                ProcessBuilder builder = new ProcessBuilder(command);
                File workingDir = new File(".");
                // Process process = Runtime.getRuntime().exec(strExec, arrEnv, workingDir);
                builder.directory(workingDir);
                Process process = builder.start();
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
                }
                bri.close();
                while ((line = bre.readLine()) != null) {
                    System.out.println(line);
                }
                bre.close();
                process.waitFor();
                System.out.println("exitValue: " + process.exitValue());
                System.out.println("Done.");
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
