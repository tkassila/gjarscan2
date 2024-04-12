package com.inetfeedback.jarscan;

import java.lang.Class;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Modifier;

public class ListInnerGlasses {
    private Class parentClass;
    public ListInnerGlasses(Class p_parentClass)
    {
        parentClass = p_parentClass;
    }

    public Class [] getInnerClasses()
    {
        Class [] arrRet = null;
        if (parentClass != null)
        {
            int modifiers = 0;
            List<Class> listInnerClasses = new ArrayList<>();
            for (Class<?> cls : parentClass.getDeclaredClasses()) {
                if (cls == null)
                    continue;
                modifiers = cls.getModifiers();
                listInnerClasses.add(cls);
                /* if (Modifier.isStatic(modifiers)) {
                    // This is an inner class. Do your thing here.
                    listInnerClasses.add(cls);
                } else {
                    // This is a nested class. Not sure if you're interested in this.
                }
                 */
            }
            int size = listInnerClasses.size();
            if (size>0)
            {
                arrRet = new Class[size];
                arrRet = listInnerClasses.toArray(arrRet);
            }
        }
        return arrRet;
    }
}
