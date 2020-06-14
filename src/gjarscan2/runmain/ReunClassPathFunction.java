package gjarscan2.runmain;

import java.io.File;

public class ReunClassPathFunction {
    public static void main(String args[]) {
        ReunClassPathFunction run = new ReunClassPathFunction();
      //  run.correctSpaceContainsClassPath("C:\\java\\project\\javafx\\gjarscan2\\out\\production\\gjarscan2;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\groovyHotSwap\\gragent.jar;C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\ captureAgent\\debugger-agent.jar");
        String ret = run.correctSpaceContainsClassPath("C:\\Users\\tkassila\\.IdeaIC2019.3\\system\\  captureAgent\\debugger-agent.jar");
        System.out.println(ret);
    }

    public String correctSpaceContainsClassPath(String classpath)
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
            return classPathSeparator +getCorrectedOneClassPath(classpath) +classPathSeparator;

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

    public String getCorrectedOneClassPath(String oneClassPathValue)
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
