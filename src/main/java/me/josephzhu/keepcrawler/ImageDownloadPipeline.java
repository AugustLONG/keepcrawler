package me.josephzhu.keepcrawler;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by joseph on 16/4/22.
 */
public class ImageDownloadPipeline implements Pipeline
{
    private static final String imageDirectory = Consts.current().ImageLocation;
    private static Logger logger = LoggerFactory.getLogger("ImageDownloadPipeline");
    private static ExecutorService downloadThreadPool = new ThreadPoolExecutor(
            100, 500, 30, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100000),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static String downloadImage(Image image)
            throws Exception
    {
        URL imageUrl = new URL(image.remoteUrl);
        String filePath = imageDirectory + File.separator+ image.localPath;
        String dirPath = getParentDirPath(filePath);
        File dir = new File(dirPath);
        if (!dir.exists())
            dir.mkdirs();
        File file = new File(filePath);
        if (!file.exists())
        {
            try (
                    InputStream imageReader = new BufferedInputStream(imageUrl.openStream());
                    OutputStream imageWriter = new BufferedOutputStream(new FileOutputStream(filePath))
            )
            {
                int readByte;

                while ((readByte = imageReader.read()) != -1)
                {
                    imageWriter.write(readByte);
                }
            }
        }
        else
        {
            filePath += "(已存在)";
        }
        return filePath;
    }

    public static String getParentDirPath(String fileOrDirPath) {
        boolean endsWithSlash = fileOrDirPath.endsWith(File.separator);
        return fileOrDirPath.substring(0, fileOrDirPath.lastIndexOf(File.separatorChar,
                endsWithSlash ? fileOrDirPath.length() - 2 : fileOrDirPath.length() - 1));
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Image> imageList = resultItems.get("images");
        if (imageList != null) {
            for (Image image : imageList) {
                if (StringUtils.isNotEmpty(image.remoteUrl) && StringUtils.isNotEmpty(image.localPath)) {
                    downloadThreadPool.execute(() ->
                    {
                        try {
                            String filePath = downloadImage(image);
                            logger.info(String.format("图片下载成功,%s->%s", image.remoteUrl, filePath));
                        } catch (Exception ex) {
                            logger.warn(String.format("图片%s下载失败,原因:%s", image.remoteUrl, ex.getMessage()));
                        }
                    });
                }
            }
        }
    }
}
