package org.andglkmod.hunkypunk;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.content.Context;
import android.util.Log;

/*
Troubleshooting this issue:
https://github.com/eclipsesource/J2V8/issues/76
Example of same problem?
https://forums.xamarin.com/discussion/71591/problem-including-native-code-in-xamarin-android
 */
public class SharedLibraryLoader
{
    private static Context context;
    private static String libDir = "lib";
    private static String shortLibName;
    private static String fullLibName;

    static public boolean loadLibrary(String libName, Context ctx)
    {
        context = ctx;
        shortLibName = libName;
        fullLibName = "lib" + libName + ".so";

        try
        {
            Log.d("SharedLibraryLoader", "Trying to load library");
            System.loadLibrary(shortLibName);
            Log.d("SharedLibraryLoader", "Library was loaded from default location");
            return true;
        }
        catch(UnsatisfiedLinkError e)
        {
            Log.d("SharedLibraryLoader","Lib wasn't found at default location. Trying to find in application private storage");
            String path = null;
            path = findInAppStorage(fullLibName);
            if (path != null)
            {
                Log.d("SharedLibraryLoader","Lib was found in application private storage. Loading lib...");
                System.load(path);
                return true;
            }
            else
            {
                Log.d("SharedLibraryLoader","Lib was not found in application private storage. Trying to find in apk...");
                path = findInApkAndCopyToAppStorage(fullLibName);

                if (path != null)
                {
                    Log.d("SharedLibraryLoader","Lib was found in apk and copied to application private storage. Loading lib...");
                    System.load(path);
                    return true;
                }
                else
                {
                    Log.d("SharedLibraryLoader", "FAILED TO LOAD LIBRARY");
                    return false;
                }
            }
        }
    }

    static public String findInAppStorage(String libName)
    {
        Log.d("SharedLibraryLoader", "enter findInAppStorage()");
        String basePath = context.getApplicationInfo().dataDir;
        File dataDir = new File(basePath);

        String[] listFiles;
        String  lib = null;
        listFiles = dataDir.list();


        for (int i=0; i < listFiles.length; i++)
        {
            lib = findInStorage(basePath + "/" +listFiles[i], libName);

            if (lib != null)
            {
                return lib;
            }
        }

        Log.w("SharedLibraryLoader", "Lib wasn't found. libName: " + libName);
        return null;
    }

    static private String findInStorage(String path, String nameOfLib)
    {
        File file = new File(path);
        if (file.isDirectory())
        {
            Log.d("SharedLibraryLoader","Strorage__dir: " + path + "/");
            String[]    list = file.list();
            String      target = null;
            for (int i = 0; i < list.length; i++)
            {
                target = findInStorage(path + "/" + list[i], nameOfLib);
                if (target != null)
                {
                    return target;
                }
            }
        }
        else
        {
            Log.d("SharedLibraryLoader","Strorage_file: " + path);
            if(path.contains(nameOfLib))
            {
                Log.d("SharedLibraryLoader","Lib was found in: " + path);
                return path;
            }
        }
        return null;
    }

    static private String findInApkAndCopyToAppStorage(String libName)
    {
        Log.d("SharedLibraryLoader", "Enter findInApkAndCopyToStorage()");

        // ---------------- ZIP - find path to .so  inside .apk ------------------
        String apkPath = context.getPackageResourcePath();
        Log.d("SharedLibraryLoader", String.format("Path to Package resource is: %s", apkPath));

        try
        {
            ZipFile zf = new ZipFile(apkPath);

            Enumeration<ZipEntry> zipFiles = (Enumeration<ZipEntry>) zf.entries();
            ZipEntry    soZipEntry = null;
            ZipEntry    tempZipEntry;
            String      tmpString;
            for ( ; zipFiles.hasMoreElements();)
            {
                tempZipEntry = zipFiles.nextElement();
                tmpString = tempZipEntry.getName();

                if (tmpString.contains(libName))
                {
                    Log.d("SharedLibraryLoader", "Library " + fullLibName + " was found in: " + tmpString);
                    soZipEntry = tempZipEntry;
                }
            }

            //----------now copy library---------------
            Log.d("SharedLibraryLoader", "soZipEntry = " + soZipEntry.toString());

            if (soZipEntry != null)
            {
                InputStream soInputStream = zf.getInputStream(soZipEntry);

                File fileDir;
                File soFile;
                OutputStream outStream;
                fileDir = context.getApplicationContext().getDir(libDir, Context.MODE_PRIVATE); // but "app_lib" was created!
                String fullSoFilePath = fileDir.getAbsolutePath() + "/" + libName;
                Log.d("SharedLibraryLoader", "New libpath is "+ fullSoFilePath);
                soFile = new File(fullSoFilePath);

                Log.d("SharedLibraryLoader", "Is file already exists? - " + soFile.exists());

                outStream = new BufferedOutputStream(new FileOutputStream(soFile));

                Log.d("SharedLibraryLoader", "Start copying library...");
                byte[] byteArray = new byte[256];
                int copiedBytes = 0;

                while ((copiedBytes = soInputStream.read(byteArray)) != -1)
                {
                    outStream.write(byteArray, 0, copiedBytes);
                }

                Log.d("SharedLibraryLoader", "Finish copying library");
                outStream.close();

                soInputStream.close();
                return fullSoFilePath;
            }
            else
            {
                Log.d("SharedLibraryLoader", "Library not Found in APK");
                return null;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
