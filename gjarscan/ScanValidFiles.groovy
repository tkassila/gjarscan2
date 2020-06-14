package gjarscan
/**
 * Created by tk on 19.4.2014.
 */
class ScanValidFiles {
    def arrDirs
    def validFiles = []
    def Closure isValidFile
    def ImportedClasses importedClassInFile
    def importedClasses = [:]
    def Closure importedClassesFromFile

    public ScanValidFiles(p_arrDirs, Closure p_isValidFile, Closure p_importedClassesFromFile)
    {
        arrDirs = p_arrDirs
        isValidFile = p_isValidFile
        importedClassesFromFile = p_importedClassesFromFile
        seekFiles()
    }

    def void seekFiles()
    {
       validFiles = []
       def File f

       if (arrDirs) {
           if (arrDirs instanceof String[])
               for (String filename in arrDirs) {
                   f = new File(filename)
                   seekFileOrDir(f)
               }
       }
    }

    def void seekFileOrDir(File f)
    {
        // seekValidFile(f)
        println f.toString()

        if (f.isDirectory())
        {
            try {
                f.eachFileRecurse(groovy.io.FileType.FILES) { subfile ->
                    seekFileOrDir((File)subfile)
                }
            }catch (Exception e){
                if (e instanceof java.io.FileNotFoundException)
                    return
                print(e)
            }
        }
        else
        {
           if (isValidFile.call(f)) {
              importedClassInFile = new ImportedClasses(f, importedClassesFromFile)
              if (importedClassInFile.classList && importedClassInFile.classList.size() > 0) {
                   for (String strclass in importedClassInFile.classList) {
                        if (!importedClasses.keySet().contains(strclass))
                        { // new sourcelist:
                            def sourcelist = []
                            sourcelist.add f.toString()
                            importedClasses.put strclass, sourcelist
                        }
                        else
                        { // update sourcelist:
                            def sourcelist = importedClasses.get(strclass)
                            sourcelist.add f.toString()
                            importedClasses.put strclass, sourcelist
                        }
                   }
              }
           }
        }
    }

    /*
    def void seekValidFile(File f)
    {
        if (f.isFile())
        {
            if (!validFiles.contains(f) && isValidFile(f))
                validFiles.add f
        }
        else
        {
            for(File f2 in f.listFiles())
                seekFile(f2)
        }
    }
    */
}
