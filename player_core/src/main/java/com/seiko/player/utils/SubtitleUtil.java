package com.seiko.player.utils;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SubtitleUtil {

    /**
     * 根据视频文件获取同文件夹下字幕
     * 视频名：test.mp4
     * 字幕名：test.ass | test.sc.ass
     * 过滤：test1.ass
     */
    public static String getSubtitlePath(String videoPath) {
        if (TextUtils.isEmpty(videoPath) || !videoPath.contains("."))
            return "";

        File videoFile = new File(videoPath);
        if (!videoFile.exists())
            return "";

        //可加载的字幕格式
        List<String> extensionList = new ArrayList<>();
        extensionList.add("ASS");
        extensionList.add("SCC");
        extensionList.add("SRT");
        extensionList.add("STL");
        extensionList.add("TTML");

        //无后缀文件路径
        String videoPathNoExt = videoFile.getAbsolutePath();
        int pointIndex = videoPathNoExt.lastIndexOf(".");
        videoPathNoExt = videoPathNoExt.substring(0, pointIndex);

        List<String> subtitlePathList = new ArrayList<>();
        File folderFile = videoFile.getParentFile();

        for (File childFile : folderFile.listFiles()) {
            String childFilePath = childFile.getAbsolutePath();
            //文件路径头与视频路径头相同
            if (childFilePath.startsWith(videoPathNoExt)) {
                String extension = getFileExtension(childFilePath);
                //文件结尾存在与可用字幕格式中
                if (extensionList.contains(extension.toUpperCase())) {
                    //存在xxx.ass直接返回
                    if (childFilePath.length() == videoPathNoExt.length() + extension.length() + 1)
                        return childFilePath;
                    subtitlePathList.add(childFilePath);
                }
            }
        }
        if (subtitlePathList.size() < 1) {
            return "";
        } else if (subtitlePathList.size() == 1) {
            return subtitlePathList.get(0);
        } else {
            for (String subtitlePath : subtitlePathList) {
                String extension = getFileExtension(subtitlePath);
                String centerContent = subtitlePath.substring(videoPathNoExt.length(), subtitlePath.length() - extension.length() - 1);
                //与必须包含“.”，如“.sc”
                if (centerContent.contains("."))
                    return subtitlePath;
            }
        }
        return "";
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    private static String getFileExtension(final String filePath) {
        if (isSpace(filePath)) return "";
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

}
