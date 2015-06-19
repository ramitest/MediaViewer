package com.mycomp;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.util.List;
import org.json.simple.*;

public class ImageReader extends AbstractReader {

    ImageReader(String mediaRootDir, String urlRootPath) {
        super(mediaRootDir, urlRootPath);
    }

    public JSONArray getSubPathsOfPicsDirs() throws IOException {
        return getSubPathsDirs(getPicsDir());
    }

    public JSONArray getPicsFromDir(String imgDir, boolean fullPath) throws IOException {
        return getMediaFilesFromDir(imgDir, fullPath);
    }

    @Override
    protected List<String> getMediaFileEnds() {
        return Arrays.asList(".jpg", ".jpeg", ".png", ".tiff", ".gif");
    }

    @Override
    protected JSONObject getJsonObject(String fileName) throws IOException {
        JSONObject obj = new JSONObject();
        BufferedImage bimg = ImageIO.read(new File(fileName));
        obj.put("src", changeDiskPathToUrl(fileName));
        obj.put("width",    bimg.getWidth());
        obj.put("height",   bimg.getHeight());
        return obj;
    }
}

