// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 22.3.2008 15:45:21
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package com.inetfeedback.jarscan;


// Referenced classes of package com.inetfeedback.jarscan:
//            SearchType

public final class SearchResult
{

    SearchResult(String s, SearchType searchtype, String s1, String s2, String s3, String s4)
    {
        searchCriteria = s;
        searchType = searchtype;
        libraryName = s1;
        libraryPath = s2;
        packageName = s3;
        className = s4;
    }

    public String getLibraryName()
    {
        return libraryName;
    }

    public String getLibraryPath()
    {
        return libraryPath;
    }

    public String getSearchCriteria()
    {
        return searchCriteria;
    }

    public SearchType getSearchType()
    {
        return searchType;
    }

    public String getPackageName()
    {
        return packageName;
    }

    public String getClassName()
    {
        return className;
    }

    private String libraryName;
    private String libraryPath;
    private SearchType searchType;
    private String searchCriteria;
    private String packageName;
    private String className;
}