// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 22.3.2008 15:46:19
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package com.inetfeedback.jarscan;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

// Referenced classes of package com.inetfeedback.jarscan:
//            SearchResult, SearchType

public final class JarScan
{

    public JarScan()
    {
        filecounter = 0;
        dircounter = 0;
        jarfiles = new ArrayList();
        searchfiles = new ArrayList();
        showProgress = false;
        progressCountRun = false;
        searchResults = new ArrayList();
        startPath = ".";
    }

    private static String usage()
    {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append("Usage: java -jar jarscan.jar [-help | /?]\n                    [-dir directory name]\n                    [-zip]\n                    [-showProgress]\n                    <-files | -class | -package>\n                    <search string 1> [search string 2]\n                    [search string n]\n");
        stringbuffer.append("\n");
        stringbuffer.append("Help:\n");
        stringbuffer.append("  -help or /?           Displays this message.\n\n");
        stringbuffer.append("  -dir                  The directory to start searching\n                        from default is \".\"\n\n");
        stringbuffer.append("  -zip                  Also search Zip files\n\n");
        stringbuffer.append("  -showProgress         Show a running count of files read in\n\n");
        stringbuffer.append("  -files or -class      Search for a file or Java class\n                        contained in some library.\n                        i.e. HttpServlet\n\n");
        stringbuffer.append("  -package              Search for a Java package\n                        contained in some library.\n                        i.e. javax.servlet.http\n\n");
        stringbuffer.append("  search string         The file or package to\n                        search for.\n                        i.e. see examples above\n\n");
        return stringbuffer.toString();
    }

    private void showProgress()
    {
        if(showProgress)
        {
            progressCountRun = true;
            (new Thread() {

                public void run()
                {
                    while(progressCountRun) 
                    {
                        System.out.println("file count:" + filecounter + "; directory count: " + dircounter);
                        try
                        {
                            sleep(2000L);
                        }
                        catch(InterruptedException interruptedexception) { }
                    }
                }

            }
).start();
        }
    }

    private void findLibrary(File file)
        throws Exception
    {
        if(file.isDirectory())
        {
            File afile[] = file.listFiles();
            for(int i = 0; i < afile.length; i++)
                if(afile[i].isDirectory())
                {
                    dircounter++;
                    dirParser(afile[i]);
                } else
                {
                    if(afile[i].getName().endsWith(".jar") || afile[i].getName().endsWith(".JAR"))
                        jarfiles.add(afile[i]);
                    else
                    if(includeZip && (afile[i].getName().endsWith(".zip") || afile[i].getName().endsWith(".ZIP")))
                        jarfiles.add(afile[i]);
                    filecounter++;
                }

        } else
        if(file.getName().endsWith(".jar") || file.getName().endsWith(".JAR"))
        {
            jarfiles.add(file);
            filecounter++;
        } else
        if(includeZip && (file.getName().endsWith(".zip") || file.getName().endsWith(".ZIP")))
            jarfiles.add(file);
    }

    private void printResults()
    {
    	int i  = 0;
        for(Iterator iterator = searchResults.iterator(); iterator.hasNext(); System.out.println(" "))
        {
            SearchResult searchresult = (SearchResult)iterator.next();
            System.out.println(++i +" start ===============================================");
            System.out.println("Found: " + searchresult.getSearchCriteria());
            if(classSearch)
                System.out.println("Class: " + searchresult.getClassName());
            System.out.println("Package: " + searchresult.getPackageName());
            System.out.println("Library Name: " + searchresult.getLibraryName());
            System.out.println("Library Path: " + searchresult.getLibraryPath());
            System.out.println(i +" end ===============================================");
        }

    }

    private void searchLibrary()
        throws Exception
    {
        boolean flag = false;
        boolean flag1 = false;
label0:
        for(int i = 0; i < jarfiles.size();)
        {
            File file = (File)jarfiles.get(i);
            try
            {
                ZipFile zipfile = new ZipFile(file);
                Enumeration enumeration = zipfile.entries();
                do
                {
                    if(!enumeration.hasMoreElements())
                        continue label0;
                    ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
                    String s = zipentry.getName();
                    s = s.replace('/', '.');
                    int j = 0;
                    while(j < searchfiles.size()) 
                    {
                        String s1 = (String)searchfiles.get(j);
                        if(classSearch)
                        {
                            if(!zipentry.isDirectory() && s != null && s.indexOf(".class") != -1 && s.indexOf(s1) != -1)
                            {
                                String s2 = s.substring(0, s.indexOf(".class"));
                                String s3 = s2.substring(0, s2.lastIndexOf("."));
                                flag = true;
                                SearchResult searchresult1 = new SearchResult(s1, SearchType.CLASS, file.getName(), file.getCanonicalPath(), s3, s2);
                                searchResults.add(searchresult1);
                            }
                        } else
                        if(zipentry.isDirectory())
                        {
                            if(s.substring(s.length() - 1).equals("."))
                                s = s.substring(0, s.length() - 1);
                            if(s.equals(s1))
                            {
                                flag1 = true;
                                SearchResult searchresult = new SearchResult(s1, SearchType.PACKAGE, file.getName(), file.getCanonicalPath(), s, null);
                                searchResults.add(searchresult);
                            }
                        }
                        j++;
                    }
                } while(true);
            }
            catch(Exception exception)
            {
                System.out.println(" ");
                System.out.println("*** problem opening archive: ");
                System.out.println(file.getName() + " [" + file.getCanonicalPath() + "]");
                System.out.println("\n\n" + exception);
                System.out.println(" ");
                i++;
            }
        }

        if(!flag && classSearch)
        {
            System.out.println(" ");
            System.out.println("*** could not find the class(es) you were looking for");
        } else
        if(!flag1 && !classSearch)
        {
            System.out.println(" ");
            System.out.println("*** could not find the package(s) you were looking for");
        }
    }

    private void buildSearch(String as[])
        throws Exception
    {
        for(int i = 0; i < as.length; i++)
            if(as[i] != null && as[i].length() != 0 && !searchfiles.contains(as[i]))
                searchfiles.add(as[i]);

        if(searchfiles.isEmpty())
            throw new Exception("You must specify at least one search criteria.");
        else
            return;
    }

    public static void main(String args[])
        throws Exception
    {
        long l = System.currentTimeMillis();
        System.out.println(" ");
        System.out.println("=========================");
        System.out.println("JarScan");
        System.out.println("written by Geoff Yaworski");
        System.out.println("gyaworski@hotmail.com");
        System.out.println("Version " + versionNum);
        System.out.println("=========================");
        System.out.println(" ");
        JarScan jarscan = new JarScan();
        if(args.length < 4)
        {
            System.out.println(usage());
            return;
        }
label0:
        for(int i = 0; i < args.length; i++)
        {
            if(args[i] == null)
                continue;
            if(args[i].startsWith("-fil") || args[i].startsWith("-cla"))
            {
                jarscan.classSearch = true;
                i++;
                do
                {
                    if(i >= args.length)
                        continue label0;
                    if(args[i].startsWith("-"))
                    {
                        i--;
                        continue label0;
                    }
                    jarscan.searchfiles.add(args[i]);
                    i++;
                } while(true);
            }
            if(args[i].startsWith("-pac"))
            {
                i++;
                do
                {
                    if(i >= args.length)
                        continue label0;
                    if(args[i].startsWith("-"))
                    {
                        i--;
                        continue label0;
                    }
                    jarscan.searchfiles.add(args[i]);
                    i++;
                } while(true);
            }
            if(args[i].startsWith("-dir"))
            {
                jarscan.startPath = args[++i];
                System.out.println("Will start search from: " + jarscan.startPath);
                continue;
            }
            if(args[i].startsWith("-h") || args[i].equalsIgnoreCase("/?"))
            {
                System.out.println(usage());
                return;
            }
            if(args[i].startsWith("-z"))
            {
                jarscan.includeZip = true;
                System.out.println("JarScan will include Zip files in search");
                continue;
            }
            if(args[i].startsWith("-showP"))
            {
                jarscan.showProgress = true;
                System.out.println("JarScan display a count of files read in");
            } else
            {
                System.out.println("JarScan does not recognize the argument: " + args[i]);
                System.out.println(usage());
                return;
            }
        }

        if(jarscan.searchfiles.size() == 0)
        {
            System.out.println("Please specify at least one search string.\n");
            System.out.println(usage());
            return;
        }
        System.out.println(" ");
        if(jarscan.classSearch)
            System.out.println("Looking for class(es): " + jarscan.searchfiles.toString());
        else
            System.out.println("Looking for package(s): " + jarscan.searchfiles.toString());
        System.out.println(" ");
        File file = new File(jarscan.startPath);
        if(!file.exists())
        {
            System.out.println("The search path specified does not exist.  [" + jarscan.startPath + "]");
            return;
        }
        jarscan.showProgress();
        jarscan.findLibrary(file);
        jarscan.progressCountRun = false;
        if(file.isDirectory())
            System.out.println("Processed " + jarscan.dircounter + " directories containing " + jarscan.filecounter + " files");
        if(jarscan.jarfiles.size() > 0)
        {
            System.out.println("found " + jarscan.jarfiles.size() + " libraries under the directory: " + file.getCanonicalPath());
        } else
        {
            if(file.isDirectory())
                System.out.println("No libraries were found to search under the directory: " + file.getCanonicalPath() + " ... select another directory to search");
            else
                System.out.println("No libraries were found at: " + file.getCanonicalPath());
            return;
        }
        System.out.println(" ");
        for(int j = 0; j < jarscan.jarfiles.size(); j++)
        {
            File file1 = (File)jarscan.jarfiles.get(j);
            System.out.println((j + 1) + ") " + file1.getName() + " [" + file1.getCanonicalPath() + "]");
        }

        System.out.println(" ");
        System.out.println("searching these jarfiles now ....");
        System.out.println(" ");
        jarscan.searchLibrary();
        jarscan.printResults();
        System.out.println("Search took: " + (System.currentTimeMillis() - l) + " milliseconds.");
    }

    SearchResult[] search(String as[], String s, SearchType searchtype)
        throws Exception
    {
        long l = System.currentTimeMillis();
        validateArguments(as, s, searchtype);
        File file = new File(s);
        try
        {
            file.getCanonicalPath();
            if(!file.exists())
                throw new Exception("The search path: \"" + s + "\" does not exist.");
        }
        catch(IOException ioexception)
        {
            throw ioexception;
        }
        catch(Exception exception)
        {
            throw new Exception("The path you specified was invalid: " + exception.getMessage());
        }
        startPath = s;
        if(searchtype == SearchType.CLASS)
            classSearch = true;
        buildSearch(as);
        findLibrary(file);
        searchLibrary();
        long l1 = System.currentTimeMillis() - l;
        System.out.println("Search took: " + l1 + " milliseconds.");
        return (SearchResult[])searchResults.toArray(new SearchResult[0]);
    }

    private void validateArguments(String as[], String s, SearchType searchtype)
        throws Exception
    {
        if(as == null)
            throw new Exception("Your search criteria is null, you must specify something to search for.");
        if(as.length == 0)
            throw new Exception("Your search criteria is empty, you must specify something to search for.");
        if(s == null)
            throw new Exception("Your search path is null, you must specify a starting point.");
        if(s.length() == 0)
            throw new Exception("Your search path is empty, you must specify a starting point.");
        if(searchtype == null)
            throw new Exception("The type of search cannot be null.");
        else
            return;
    }

    private void dirParser(File file)
        throws Exception
    {
        File afile[] = file.listFiles();
        for(int i = 0; i < afile.length; i++)
        {
            if(afile[i].isDirectory())
            {
                dircounter++;
                dirParser(afile[i]);
                continue;
            }
            if(afile[i].getName().endsWith(".jar") || afile[i].getName().endsWith(".JAR"))
                jarfiles.add(afile[i]);
            filecounter++;
        }

    }

    void searchZipFiles(boolean flag)
    {
        includeZip = flag;
    }

    void test()
    {
    }

    private int filecounter;
    private int dircounter;
    private List jarfiles;
    private List searchfiles;
    private boolean classSearch;
    private boolean includeZip;
    private boolean showProgress;
    private boolean searchlist = true;
    private boolean progressCountRun;
    private ArrayList searchResults;
    private String startPath;
    private static String versionNum = "2.0";




}
