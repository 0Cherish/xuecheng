package com.xuecheng.media;

import org.junit.jupiter.api.Test;
import org.springframework.util.DigestUtils;

import javax.swing.plaf.OptionPaneUI;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 大文件处理测试
 *
 * @author Lin
 * @date 2024/2/17 12:06
 */
public class BigFileTest {

    @Test
    public void testChunk() throws IOException {
        File sourceFile = new File("E:\\test\\1.mp4");
        String chunkPath = "E:\\test\\chunk\\";
        File chunkFolder = new File(chunkPath);
        if (!chunkFolder.exists()) {
            chunkFolder.mkdirs();
        }
        // 分块大小
        long chunkSize = 1024 * 1024 * 5;
        // 分块数量
        long chunkNum = (long) Math.ceil(sourceFile.length() * 1.0 / chunkSize);
        System.out.println("分块总数：" + chunkNum);
        // 缓冲区大小
        byte[] b = new byte[1024];
        // 使用RandomAccessFile访问文件
        RandomAccessFile rafRead = new RandomAccessFile(sourceFile, "r");
        // 分块
        for (int i = 0; i < chunkNum; i++) {
            // 创建分块文件
            File file = new File(chunkPath + i);
            if (file.exists()) {
                file.delete();
            }
            boolean newFile = file.createNewFile();
            if (newFile) {
                // 向分块中写入数据
                RandomAccessFile rafWrite = new RandomAccessFile(file, "rw");
                int len;
                while ((len = rafRead.read(b)) != -1) {
                    rafWrite.write(b, 0, len);
                    if (file.length() >= chunkSize) {
                        break;
                    }
                }
                rafWrite.close();
                System.out.println("完成分块" + i);
            }
        }
        rafRead.close();
    }

    @Test
    public void testMerge() throws IOException {
        // 块文件目录
        File chunkFolder = new File("E:\\test\\chunk\\");
        // 原始文件
        File originalFile = new File("E:\\test\\1.mp4");
        // 合并文件
        File mergeFile = new File("E:\\test\\1_1.mp4");
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建新的合并文件
        mergeFile.createNewFile();
        // 用于写文件
        RandomAccessFile rafWrite = new RandomAccessFile(mergeFile, "rw");
        // 指针指向文件顶端
        rafWrite.seek(0);
        // 缓冲区
        byte[] b = new byte[1024];
        // 分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        fileList.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
        // 合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile rafRead = new RandomAccessFile(chunkFile, "r");
            int len;
            while ((len = rafRead.read(b)) != -1) {
                rafWrite.write(b, 0, len);
            }
            rafRead.close();
        }
        rafWrite.close();

        // 校验文件
        try (
                FileInputStream fileInputStream = new FileInputStream(originalFile);
                FileInputStream mergeFilStream = new FileInputStream(mergeFile);
        ) {
            String originalMd5 = DigestUtils.md5DigestAsHex(fileInputStream);
            String mergeFileMd5 = DigestUtils.md5DigestAsHex(mergeFilStream);
            if (originalMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
            } else {
                System.out.println("合并文件失败");
            }

        }
    }
}
