package com.xuecheng.base.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Lin
 * @date 2024/2/18 12:34
 */
public class Mp4VideoUtil extends VideoUtil {

    String ffmpegPath = "D:\\Environment\\ffmpeg\\bin\\ffmpeg.exe";
    String videoPath = "E:\\test\\2.avi";
    String mp4Name = "test1.mp4";
    String mp4FolderPath = "E:\\test\\Movies\\";

    public Mp4VideoUtil(String ffmpegPath, String videoPath, String mp4Name, String mp4FolderPath) {
        super(ffmpegPath);
        this.videoPath = videoPath;
        this.mp4Name = mp4Name;
        this.mp4FolderPath = mp4FolderPath;
    }

    public static void main(String[] args) {
        String ffmpegPath = "D:\\Environment\\ffmpeg\\bin\\ffmpeg.exe";
        String videoPath = "E:\\test\\2.avi";
        String mp4Name = "2.mp4";
        String mp4Path = "E:\\test\\Movies\\2.mp4";
        Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpegPath, videoPath, mp4Name, mp4Path);
        String s = videoUtil.generateMp4();
        System.out.println(s);
    }

    /**
     * 视频编码，生成mp4
     */
    public String generateMp4() {
        clearMp4(mp4FolderPath);
        /*
          ffmpeg -i 2.avi -c:v libx264 -s 1280x720 -pix_fmt yuv420p -b:a 63k -b:v 753k -r 18 .\2.mp4
         */
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(videoPath);
        command.add("-c:v");
        command.add("libx264");
        //覆盖输出文件
        command.add("-y");
        command.add("-s");
        command.add("1280x720");
        command.add("-pix_fmt");
        command.add("yuv420p");
        command.add("-b:a");
        command.add("63k");
        command.add("-b:v");
        command.add("753k");
        command.add("-r");
        command.add("18");
        command.add(mp4FolderPath);
        String outString = null;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            outString = waitFor(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean checkVideoTime = this.checkVideoTime(videoPath, mp4FolderPath);
        if (!checkVideoTime) {
            return outString;
        } else {
            return "success";
        }

    }

    /**
     * 清除已生成的mp4
     *
     * @param mp4Path mp4路径
     */
    private void clearMp4(String mp4Path) {
        File mp4File = new File(mp4Path);
        if (mp4File.exists() && mp4File.isFile()) {
            mp4File.delete();
        }
    }
}
