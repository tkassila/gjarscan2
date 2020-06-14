package gjarscan

import com.inetfeedback.jarscan.JarScan21
import com.inetfeedback.jarscan.SearchResult

/**
 * Created by tk on 19.4.2014.
 */
class SeekJarClasses {

        def jarFiles = []
        def ScanValidFiles scanValidFiles
        def jarScan = new JarScan21()
        def bFirstTimeJarScanCall = true
        def String [] jarScanAnotherParams
        def String [] arrJarDirs
        def String [] arrSourceDirs
        def String currentJarDir
        def results = []

    public static void main(String[] args) {

        def jarScanAnotherParams = ""
        def arrSourceDirs = {"src"}
        def arrJarDirs = {"%JAVA_HOME%"}
        def isValidFile = { file -> file.getName().toLowerCase().endsWith(".groovy") || file.getName().toLowerCase().endsWith(".java")
        }

        def importedClassesFromFile =  { text ->
            if (text == null || text.length() < 1)
                return null
            def matcher = text =~ /import\s+(.*?);?\n/
            def int iCnt = 0
            def strImport
            def list = []
            while(matcher.find())
            {
                strImport = matcher[iCnt++][1]
                list.add strImport
            }
            if (list.size() == 0)
                return null
            list
        }
        SeekJarClasses sjc = new SeekJarClasses(jarScanAnotherParams, arrSourceDirs, arrJarDirs,
                                                isValidFile, importedClassesFromFile)
    }

    public SeekJarClasses(String [] p_jarScanAnotherParams, String [] p_arrSourceDirs, String [] p_arrJarDirs,
                          Closure p_isValidFile, Closure p_importedClassesFromFile)
    {
            jarScanAnotherParams = p_jarScanAnotherParams
            arrJarDirs = p_arrJarDirs
            arrSourceDirs = p_arrSourceDirs

            if (arrSourceDirs && arrSourceDirs.size() > 0 && arrJarDirs && arrJarDirs.size() > 0)
            {
                scanValidFiles = new ScanValidFiles(arrSourceDirs, p_isValidFile, p_importedClassesFromFile)
                if (scanValidFiles.importedClasses && scanValidFiles.importedClasses.size() > 0)
                {
                    for(String jarfile in arrJarDirs)
                    {
                        bFirstTimeJarScanCall = true
                        this.currentJarDir = jarfile.replaceAll("(?s)\\\$(.*)", "%\$1%")
                        for(hmclass in scanValidFiles.importedClasses)
                        {
                            seekJarFiles(hmclass.key)
                        }
                    }
                }
            }
    }

    def seekJarFiles(String strclass)
    {
        //  -gui  -dir %JAVA_HOME% -donotask  -class java.lang.String
        def args = "-gui " +jarScanAnotherParams +" -dir " + getEnvironVariableValue(currentJarDir) + " -donotask -class " + strclass
        jarScanCall(args.split(" "))
    }

    def jarScanCall(String [] args)
    {
        if (bFirstTimeJarScanCall)
        {
            jarScan.readcommanargs(args)
            bFirstTimeJarScanCall = false
        }
        else
            jarScan.searchWithInSameJarFiles(args)

        SearchResult [] tmp_results = jarScan.getSearchResults()
        if (tmp_results)
            results.addAll tmp_results
        /*
            for(SearchResult sr in results)
            {

            }
         */
    }

    def static public getSearchValue(String value)
    {
        if (!value)
            return value
        value = value.trim()
        if (!value.contains("%"))
        {
            if (value.contains(" "))
                return value.split(" ")
            return value
        }

        if (value.contains(" "))
        {
            def values = value.split(" ")
            def newvalues = []
            def v2
            for(v in values)
            {
                v2 = getEnvironVariableValue(v)
                if (!v2)
                    continue
                newvalues.add v2
            }
            return newvalues
        }
        return getEnvironVariableValue(value)
    }

    def static String getEnvironVariableValue(String value)
    {
        if (!value)
            return null
        if (!value.contains("%"))
            return value
        if (!value.startsWith("%") && !value.endsWith("%"))
            return value
        if (value.length() < 2)
            return value
        return System.getenv(value.substring(1, value.length()-1))
    }

}
