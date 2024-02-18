package com.xuecheng.base.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频文件处理父类
 *
 * @author Lin
 * @date 2024/2/18 12:35
 */
public class VideoUtil {

    /**
     * ffmpeg的安装位置
     */
    String ffmpegPath = "D:\\Environment\\ffmpeg\\bin\\ffmpeg.exe";

    public VideoUtil(String ffmpegPath) {
        this.ffmpegPath = ffmpegPath;
    }

    /**
     * 检查视频时间是否一致
     *
     * @param source 源路径
     * @param target 目标路径
     * @return 返回结果
     */
    public Boolean checkVideoTime(String source, String target) {
        String sourceTime = getVideoTime(source);
        String targetTime = getVideoTime(target);
        if (sourceTime == null || targetTime == null) {
            return false;
        }
        sourceTime = sourceTime.substring(0, sourceTime.lastIndexOf("."));
        targetTime = targetTime.substring(0, targetTime.lastIndexOf("."));

        return sourceTime.equals(targetTime);
    }

    /**
     * 获取视频时间（时：分：秒：毫秒）
     *
     * @param videoPath 视频路径
     * @return 视频时间
     */
    public String getVideoTime(String videoPath) {
        /*
          ffmpeg -i 2.avi
         */
        List<String> command = new ArrayList<>();
        command.add(ffmpegPath);
        command.add("-i");
        command.add(videoPath);

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            // 将标准输入流和错误输出流合并，通过标准输入流读取信息
            builder.redirectErrorStream(true);
            Process p = builder.start();
            String outString = waitFor(p);
            System.out.println(outString);
            int start = outString.trim().indexOf("Duration: ");
            if (start >= 0) {
                int end = outString.trim().indexOf(", start:");
                if (end >= 0) {
                    String time = outString.substring(start + 10, end);
                    if (!time.isEmpty()) {
                        return time.trim();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String waitFor(Process p) {
        InputStream in = null;
        InputStream error;
        String result = "error";
        int exitValue = -1;
        StringBuilder outputString = new StringBuilder();
        try {
            in = p.getInputStream();
            error = p.getErrorStream();
            boolean finished = false;
            // 每次休眠一秒，最长执行时间10分钟
            int maxRetry = 600;
            int retry = 0;
            while (!finished) {
                if (retry > maxRetry) {
                    return "error";
                }
                try {
                    while (in.available() > 0) {
                        Character c = (char) in.read();
                        outputString.append(c);
                        System.out.print(c);
                    }
                    while (error.available() > 0) {
                        Character c = (char) in.read();
                        outputString.append(c);
                        System.out.print(c);
                    }
                    // 进程未结束时调用exitValue将抛出异常
                    exitValue = p.exitValue();
                    finished = true;
                } catch (IllegalThreadStateException e) {
                    Thread.sleep(1000);
                    retry++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return outputString.toString();
    }
}
