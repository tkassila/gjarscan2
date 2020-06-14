// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 22.3.2008 15:45:43
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package com.inetfeedback.jarscan;


public final class SearchType
{

    private SearchType(String s)
    {
        type = s;
    }

    public String toString()
    {
        return type;
    }

    private final String type;
    public static final SearchType CLASS = new SearchType("class");
    public static final SearchType PACKAGE = new SearchType("package");

}