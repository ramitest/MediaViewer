/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycomp;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.json.simple.*;

public abstract class AbstractReader {

    private static String PICS_DIR = "pics";
    private static String VIDS_DIR = "vids";
//    private static String imgRootDir  = System.getProperty("my.imgviewer.imgdir");
//    private static String urlRootPath = System.getProperty("my.imgviewer.urlpathroot");
    private static String imgRootDir;
    private static String urlRootPath;

    AbstractReader(String mediaRootDir, String urlRootPath) {
        this.imgRootDir  = mediaRootDir;
        this.urlRootPath = urlRootPath;
    }

    protected static String getPicsDir(){
        return PICS_DIR;
    }

    protected static String getVidsDir(){
        return VIDS_DIR;
    }

    protected static String getMediaFilesRootDir(){
        return imgRootDir;
    }

    protected static String getURLRootPath(){
        return urlRootPath;
    }

    protected static String addBSlashToEnd(String str) {
        return addStrToEndIfNotExists(str, "\\");
    }

    protected static String addSlashToEnd(String str) {
        return addStrToEndIfNotExists(str, "/");
    }

    protected static String addStrToEndIfNotExists(String str, String endStr) {
        if (str.endsWith(endStr))
            return str;
        return str.concat(endStr);
    }

    protected static String replaceSlashtoBSlash(String str) {
        System.out.println("replaceSlashtoBSlash:::: " +str);
        str = str.replace("/", "\\");
        return str.replace("\\\\", "\\");
    }

    protected static String replaceBSlashtoSlash(String str) {
        System.out.println("replaceBSlashtoSlash:::: " +str);
        str = str.replace("\\", "/");
        return str.replace("//", "/");
    }

    protected static String getSubPathFromDiskFullPath(String fileName, String strRootPath) {
        String subPath = fileName.substring(strRootPath.length() + 1);
        return replaceBSlashtoSlash(subPath);
    }

    protected static boolean isDir(Path rootDirPath) {
        if (Files.exists(rootDirPath, LinkOption.NOFOLLOW_LINKS)) {
            if (Files.isDirectory(rootDirPath, LinkOption.NOFOLLOW_LINKS)) {
                return true;
            }
        }
        return false;
    }

    protected static DirectoryStream<Path> getDirStream(Path rootDirPath) throws IOException {
        return Files.newDirectoryStream(rootDirPath, getDirsFilter());
    }

    protected static DirectoryStream.Filter<Path> getDirsFilter() {
        return new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                return Files.isDirectory(file);
            }
        };
    }

    protected static JSONArray getDirListing(Path dir) throws IOException {
        JSONArray list = new JSONArray();
        for (Path subDir : getDirStream(dir)) {
            list.add(getSubPathFromDiskFullPath(subDir.toString(), getMediaFilesRootDir()));
        }
        return list;
    }

    protected static String changeDiskPathToUrl(String fileName) {
        String subPath = getSubPathFromDiskFullPath(fileName, getMediaFilesRootDir());
//        "http://"+ localhost+":"+8080+"/imgviewer/";
        
        return getURLRootPath() + subPath;
    }

    public static JSONArray getSubPathsDirs(String subDir) throws IOException {
        System.out.println("imgRootDir:" + getMediaFilesRootDir());
        System.out.println("urlRootPath:" + getURLRootPath());
        if (isDir(Paths.get(getMediaFilesRootDir()))) {
            for (Path dir : getDirStream(Paths.get(getMediaFilesRootDir()))) {
                System.out.println(dir);
                if (dir.endsWith(subDir)) {
                    return getDirListing(dir);
                }
            }
        }
        return null;
    }

    public static boolean strEndsWith(String full, List<String> ends) {
        for (String end : ends)
            if ( full.endsWith(end) )
                return true;
        return false;
    }

    protected static DirectoryStream.Filter<Path> getMediaFilter(final List<String> fileTypes) {
        return new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path file) throws IOException {
                String fileStr = file.toString();
                return strEndsWith(fileStr, fileTypes);
            }
        };
    }
    
    protected abstract JSONObject getJsonObject(String fileName) throws IOException;
    protected abstract List<String> getMediaFileEnds();

    public JSONArray getMediaFilesFromDir(String mediaDir, boolean fullPath) throws IOException {
        System.out.println("getMediaFilesFromDir::" + mediaDir);
        if (false == fullPath) {
            mediaDir = getFullMediaDir(mediaDir);
        }
        System.out.println("getMediaFilesFromFullDir::" + mediaDir);
        JSONArray mediaFilesJson = new JSONArray();
        if (isDir(Paths.get(mediaDir))) {
            for (Path imgPath : getStream(Paths.get(mediaDir))) {
                mediaFilesJson.add(getJsonObject(imgPath.toString()));
            }
        }
        return mediaFilesJson;
    }
    
    protected String getFullMediaDir(String relativePath) {
        String osName = System.getProperty("os.name").toLowerCase();
        if(osName.indexOf("win") >= 0)
            return addBSlashToEnd(getMediaFilesRootDir()) + replaceSlashtoBSlash(relativePath);
        return addSlashToEnd(getMediaFilesRootDir()) + replaceBSlashtoSlash(relativePath);
    }

    protected DirectoryStream<Path> getStream(Path dirPath) throws IOException {
        return Files.newDirectoryStream(dirPath, getMediaFilter(getMediaFileEnds()));
    }

    
}
