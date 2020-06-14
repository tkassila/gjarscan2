
package com.inetfeedback.jarscan;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

//Referenced classes of package com.inetfeedback.jarscan:
//         SearchResult, SearchType

public final class JarScan21
{

 public JarScan21()
 {
     filecounter = 0;
     dircounter = 0;
     jarfiles = new ArrayList();
     searchfiles = new ArrayList();
     showProgress = false;
     progressCountRun = false;
     searchResults = new ArrayList();
     startPath = ".";
     bScanSubDirs = true;
     bGUI = false;     
 }

 private static StringBuffer quisb = new StringBuffer();
 private static void createGuisb() { quisb = new StringBuffer(); }
 
 private static String usage()
 {
     StringBuffer stringbuffer = new StringBuffer();
     stringbuffer.append("Usage: java -jar jarscan.jar [-help | /?]\n                    [-dir directory name]\n                    [-zip]\n                    [-showProgress]\n                    [-nosearchlist]\n                    [-nosubdirs]\n                    <-files | -class | -package>\n                    <search string 1> [search string 2]\n                    [search string n]\n");
     stringbuffer.append("\n");
     stringbuffer.append("This version of Jarscan application scans jar or java .zip files and\n");
     stringbuffer.append("searches given class(ses) or packages as v. 2.0 but also asking if a user\n");
     stringbuffer.append("will to search with another parameter values from same jar file collection.\n");
     stringbuffer.append("\n");
     stringbuffer.append("\n");
     stringbuffer.append("Help: optional between [] charachthers \n");
     stringbuffer.append("  -help or /?           Displays this message.\n\n");
     stringbuffer.append("  -dir                  The directory to start searching\n                        from default is \".\"\n\n");
     stringbuffer.append("  -zip                  Also search Zip files\n\n");
     stringbuffer.append("  -showP[rogress]       Show a running count of files read in\n\n");
     stringbuffer.append("  -fil[es] or -class    Search for a file or Java class\n                        contained in some library.\n                        i.e. HttpServlet\n\n");
     stringbuffer.append("  -pack[age]            Search for a Java package\n                        contained in some library.\n                        i.e. javax.servlet.http\n\n");
     stringbuffer.append("  -nose[archlist]       no search list of readed jar files\n"+
    		 "                        and displays finded jars as <number>start\n" +
    		 "                        and <number> end markerings\n\n");                    
     stringbuffer.append("  -nosub[dirs]          Do not search in subdirs\n\n");
     stringbuffer.append("  search string         The file or package to\n                        search for.\n                        i.e. see examples above\n\n");
     stringbuffer.append("  -donotask[tocontinue]  Do not ask to continue after a execution.\n\n");
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
                     println("file count:" + filecounter + "; directory count: " + dircounter);
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
             if(bScanSubDirs && afile[i].isDirectory())
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
	 int i = 0;
     for(Iterator iterator = searchResults.iterator(); iterator.hasNext(); println(" "))
     {
         SearchResult searchresult = (SearchResult)iterator.next();
         print(++i +" start ");
         if (searchlist || (!searchlist && bGUI))
        	 print("===============================================");
         println("");
         println("Found: " + searchresult.getSearchCriteria());
         if(classSearch)
             println("Class: " + searchresult.getClassName());
         println("Package: " + searchresult.getPackageName());
         println("Library Name: " + searchresult.getLibraryName());
         println("Library Path: " + searchresult.getLibraryPath());
         print(i +" end ");
         if (searchlist || (!searchlist && bGUI))
        	 print("===============================================");
         println("");
     }

 }

 private void searchLibrary()
     throws Exception
 {
     boolean flag = false;
     boolean flag1 = false;
label0:
     for(int i = 0; i < jarfiles.size(); i++)
     {
         File file = (File)jarfiles.get(i);
         try
         {
             ZipFile zipfile = new ZipFile(file);
             Enumeration enumeration = zipfile.entries();
             do
             {
                 if(!enumeration.hasMoreElements())
                     break;
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
             } 
             while(true);
         }
         catch(Exception exception)
         {
             println(" ");
             println("*** problem opening archive: ");
             println(file.getName() + " [" + file.getCanonicalPath() + "]");
             println("\n\n" + exception);
             println(" ");
             //i++;
         }
     }

     if(!flag && classSearch)
     {
         println(" ");
         println("*** could not find the class(es) you were looking for");
     } else
     if(!flag1 && !classSearch)
     {
         println(" ");
         println("*** could not find the package(s) you were looking for");
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

 private static boolean bAskContinueValue = true;
 
 private static boolean getAskContinueValue()
 {
	 return bAskContinueValue;
 }
 
 public StringBuffer getGuiStringBuffer() { return quisb; }
 
 public void readcommanargs(String [] args)
 throws Exception
 {
	 createGuisb();
	 searchlist = true; // another wise: a bug with re-executing

     long l = System.currentTimeMillis();
     try {
    	 
    /*	 
    if(args.length == 1 && args[0].startsWith("-gui"))
     {
	 bGUI = true;
         // initGui();
	 return;
     }
     */

     if(args.length < 4)
     {
         println(usage());
         return;
     }
     
     for(int i = 0; i < args.length; i++) // so that println
     {
    	    if(args[i].startsWith("-gui") )
            {
           	 bGUI = true;
           	 // initGui();
                break;
            }         	 
     }     
 
     println(" ");
     println("=========================");
     println("JarScan");
     println("written by Geoff Yaworski and T. Kassila");
     println("gyaworski@hotmail.com");
     println("Version " + versionNum);
     println("=========================");
     println(" ");
     
label0:
     for(int i = 0; i < args.length; i++)
     {
         if(args[i] == null)
             continue;
         if(args[i].startsWith("-fil") || args[i].startsWith("-cla"))
         {
             classSearch = true;
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
                 searchfiles.add(getClassWithPackageFromDirClassString(args[i]));
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
                 searchfiles.add(args[i]);
                 i++;
             } while(true);
         }
         if(args[i].startsWith("-dir"))
         {
             startPath = args[++i];
             println("Will start search from: " + startPath);
             continue;
         }
         if(args[i].startsWith("-h") || args[i].equalsIgnoreCase("/?"))
         {
             println(usage());
             return;
         }         
         if(args[i].startsWith("-nosub") )
         {
        	 bScanSubDirs = false;
             continue;
         }
         if(args[i].startsWith("-gui") )
         {
        	 bGUI = true;
        	 // initGui();
             continue;
         }         
         if(args[i].startsWith("-z"))
         {
             includeZip = true;
             println("JarScan will include Zip files in search");
             continue;
         }
         if(args[i].startsWith("-nosub"))
         {
             bScanSubDirs = false;
             println("JarScan will include Zip files in search");
             continue;
         }
         if(args[i].startsWith("-showP"))
         {
             showProgress = true;
             println("JarScan display a count of files read in");
             continue;
         }
    	 else
    	 if(args[i].startsWith("-nosearchlist") || args[i].startsWith("-nose"))
    	 {
    	     setSearchlist(false);
    	     println("JarScan do not display searched jar files //=" + args[i]);
    	     continue;
    	 } 
    	 else
    	 if(args[i].startsWith("-donotasktocontinue") || args[i].startsWith("-donotask"))
    	 {
    		 bAskContinueValue = false;
    	     println("JarScan do not ask for searched jar files //=" + args[i]);
    	     continue;
    	 }          
    	 else
         {
             println("JarScan does not recognize the argument: " + args[i]);
             println(usage());
             return;
         }
     }

     if(searchfiles.size() == 0)
     {
         println("Please specify at least one search string.\n");
         println(usage());
         return;
     }
     
     println(" ");
     if(classSearch)
         println("Looking for class(es): " + searchfiles.toString());
     else
         println("Looking for package(s): " + searchfiles.toString());
     println(" ");
     File file = new File(startPath);
     if(!file.exists())
     {
         println("The search path specified does not exist.  [" + startPath + "]");
         return;
     }
     showProgress();
     findLibrary(file);
     progressCountRun = false;
     if(file.isDirectory())
         println("Processed " + dircounter + " directories containing " + filecounter + " files");
	 String strUnder = ( bScanSubDirs ? "under" : "in");
     if(jarfiles.size() > 0)
     {
    	 String msg = "found " + jarfiles.size() + " libraries " + strUnder +" the directory: " + file.getCanonicalPath();
         println(msg);
     } else
     {
         if(file.isDirectory())
         {
        	 String msg = "No libraries were found to search " + strUnder +" the directory: " + file.getCanonicalPath() + " ... select another directory to search";
             println(msg);
         }
         else
             println("No libraries were found at: " + file.getCanonicalPath());
         return;
     }
     
     if (searchlist)
     {
		 println(" ");
		 for(int j = 0; j < jarfiles.size(); j++)
		 {
		     File file1 = (File)jarfiles.get(j);
		     println((j + 1) + ") " + file1.getName() + " [" + file1.getCanonicalPath() + "]");
		 }
     }

     println(" ");
     println("searching these jarfiles now ....");
     println(" ");
     searchLibrary();
     printResults();
     println("Search took: " + (System.currentTimeMillis() - l) + " milliseconds.");
     
     boolean bAskToContinue = getAskContinueValue();
     String params [];
     int max;
     
     while(bAskToContinue)
     {
    	 bAskToContinue = getAnswer("continue to search of another class or package? ");
    	 if (bAskToContinue)
    	 {
    		 params = getNewSearchValues(args);
    		 if (params == null)
    			 break;
    		 l = System.currentTimeMillis();
    		 searchfiles.clear();
    		 searchResults.clear();
    		 max = params.length;
    		 for(int i = 0; i < max; i++)
    			 searchfiles.add(params[i]);
    	     searchLibrary();
    	     printResults();
    	     println("Search took: " + (System.currentTimeMillis() - l) + " milliseconds.");
    	 }
    	 else
    	 {
    		 if (bGUI)
    			 gui.close();
    		 System.exit(0);
    	 }
     }
   } catch (Exception e){
	   println("Error: " +e.getMessage());
	   throw e;
   }

 }
 
 public void searchWithInSameJarFiles(String [] args)
 throws Exception
 {
	 if (args == null)
		 return;
	 
	 long l = System.currentTimeMillis();
	 searchfiles.clear();
	 searchResults.clear();
	 int max = args.length;
	 for(int i = 0; i < max; i++)
		 searchfiles.add(args[i]);
     searchLibrary();
     printResults();
     println("Search took: " + (System.currentTimeMillis() - l) + " milliseconds.");
 }
 
 public static void main(String args[])
     throws Exception
 {
     JarScan21 jarscan = new JarScan21();
     jarscan.readcommanargs(args);     
 }

 private static String
 getClassWithPackageFromDirClassString(String value)
 {
	 //if (ret.contains("/"))
	 return value.replace('/', '.');
 }
 
 private static boolean getAnswer(String msg)
 throws IOException
 {
	 Character ch;
	 while(true)
	 {
	 	ch = new Character((char)read(msg));
	 	if ( ch.equals( new Character('y')) || ch.equals( new Character('Y')) )
	 		return true;
	 	if ( ch.equals( new Character('n')) || ch.equals( new Character('N')) )
	 		return false;
	 }
 }
 
 private static int read(String msg)
 throws IOException
 {
	 if (bGUI)
		 return gui.read(msg);
	 System.out.print(msg);
	 return System.in.read();
 }
 
 private static String [] getNewSearchValues(String [] args)
 throws IOException
 {		
		clearAnswer();
		changeBackGroundColor();
		println("\n\n");
		println("////////////////////////////////////////////////////////////");
		println("////////////////////////////////////////////////////////////");
		String msg = "Give new search parameters (-class/package class/package)?";
		println("\n" + msg);
		String strParams = readParams(msg);
		println("");
		if (strParams != null)
		{
			if ( strParams.trim().equals("n") || strParams.trim().equals( "e") )
			{
				return null;
			}
				
			StringTokenizer tokens = new StringTokenizer(strParams);
			int iParams = tokens.countTokens();
			int iAlloc = args.length + iParams;
			String[] params = new String[iAlloc];
			int i = 0, ind = 0;
			boolean prevDirParam = false, bSkip = false;
			
			for(; i < args.length; i++)
			{
				println(args[i]);
				if (args[i].startsWith("-pac") ||
						args[i].startsWith("-PAC"))
				{
					bSkip = true;
					continue;
				}
					
				if (args[i].startsWith("-cla") ||
						args[i].startsWith("-CLA"))
				{
					bSkip = true;
					continue;
				}
				if (bSkip)
				{
					bSkip = false;
					continue;
				}					
				if (args[i].startsWith("-"))
					params[ind++] = args[i];
				else
				if (prevDirParam)
				{
					params[ind++] = args[i];
				}
				if (args[i].startsWith("-dir") ||
					args[i].startsWith("-DIR"))
					prevDirParam = true;
				else
					prevDirParam = false;
			}
			String strValue;
		
			String[] params2 = new String[ind + iParams];
			for(i = 0 ; i < ind; i++)
			{
				params2[i] = params[i];
			}
			
			while(tokens.hasMoreElements())
			{
				strValue = tokens.nextToken();
				println(strValue);
				params2[i++] = strValue;
			}
			return params2;
		}
		return null;
 }
 
 private static void changeBackGroundColor()
 {
	 bBackGroundColor = ! bBackGroundColor;
	 if (bGUI)
	 {
		 gui.changeBackGroundColor(bBackGroundColor);
	 }	 
 }
 
 private static String readParams(String msg)
 throws IOException
 {
	if (bGUI)
	{
		return gui.readParams(msg);
	}
	int iBytesReaded = System.in.read(readBytes);
	if (iBytesReaded < 3) // with /r/n
		return null;
	String strParams = new String(readBytes).substring(0, iBytesReaded-2);
	return strParams;
	
 }
 
 private static void clearAnswer()
 throws IOException
 {
	if (bGUI)
	{
		gui.clearAnswer();
		return ;
	}
	while(System.in.available() > 0)
		System.in.read();	 
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
     println("Search took: " + l1 + " milliseconds.");
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
     if (afile == null)
    	 return ;
     
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

 public void setSearchlist(boolean value)
 {
	 searchlist = value;	 
 }
 
 void test()
 {
 }

 private static void println(String msg)
 {
	 if (bGUI)
		 quiPrintln(msg);
	 else
		 System.out.println(msg);
 }

 private static void print(String msg)
 {
	 if (bGUI)
		 quiPrint(msg);
	 else
		 System.out.print(msg);
 }

 private static void quiPrintln(String msg)
 {
	 // System.out.println("gui: " + msg);
	 // gui.insertString(msg);
	 quisb.append(msg +"\n");
 }

 private static void quiPrint(String msg)
 {
	 // System.out.println("gui: " + msg);
	 // gui.insertString(msg);
	 quisb.append(msg);
 }

 private static void initGui()
 {
	 gui = new JarscanGui ();
	 System.out.println("gui initialized: " );
 }

 private int filecounter;
 private int dircounter;
 private List jarfiles;
 private List searchfiles;
 private boolean classSearch;
 private boolean includeZip;
 private static boolean searchlist = true;
 private boolean showProgress;
 private boolean progressCountRun;
 private ArrayList searchResults;
 private String startPath;
 private static String versionNum = "2.3tk";
 private static boolean bScanSubDirs = true;
 private static boolean bGUI = false;
 private static JarscanGui gui;
 private static byte [] readBytes = new byte[81];
 private static boolean bBackGroundColor = false;
 private static boolean bSaveJarFileLIstUntilNextCall = false;
 
 public static boolean getSaveJarFileListUntilNextCall()
 {
	 return bSaveJarFileLIstUntilNextCall;
 }
 
 public static void setSaveJarFileListUntilNextCall(boolean value)
 {
	 bSaveJarFileLIstUntilNextCall = value;
 }
 
 public List getJarfiles() { return jarfiles; }
 public ArrayList getSearchResults() { return searchResults; }

}
