package gjarscan

/**
 * Created by tk on 19.4.2014.
 */
class ImportedClasses {
    def classList = []
    def File sourceFile
    def Closure importedClasses

    public ImportedClasses(File p_sourceFile, Closure p_importedClasses)
    {
        sourceFile = p_sourceFile
        importedClasses = p_importedClasses

        def text = sourceFile.getText()
        if (text == null || text.length() < 1)
            return

        def matcher = text =~ /(?s)import\s+(.*?);?\n/
        def int iCnt = 0
        def String strImport, newvalue
        def list = []
        while(matcher.find())
        {
            strImport = matcher[iCnt++][1].toString()
            newvalue = strImport.replaceAll("(?s)\\Astatic\\s+", "").replaceAll("[\\s\\t\\r]", "").trim()
            list.add newvalue
        }
        if (list.size() == 0)
            return
        classList = list
    }
}
