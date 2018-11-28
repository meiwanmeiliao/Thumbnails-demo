import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author zhuolin
 * @program: Thumbnails-demo
 * @date 2018/11/28
 * @description: ${description}
 **/
public class ThumbnailsDemo {
    /**
     * 对图片按照比例缩放
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @param scale       缩放比 0-1为缩小 >1 放大
     * @throws IOException
     */
    public static void thumbnails (String picturePath, String targetPath, Double scale) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).scale(scale).toFile(targetFile);
    }

    /**
     * 随宽度等比缩放
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @param width       宽度大小
     * @throws IOException
     */
    public static void thumbnailsWithWidth (String picturePath, String targetPath, int width) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).width(width).toFile(targetFile);
    }

    /**
     * 随高度等比缩放
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @param height      高度大小
     * @throws IOException
     */
    public static void thumbnailsWithHeight (String picturePath, String targetPath, int height) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).height(height).toFile(targetFile);
    }

    /**
     * 按照指定大小进行缩放 不保持原有比例
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @param width       宽度
     * @param height      高度
     * @throws IOException
     */
    public static void thumbnailsWithSize (String picturePath, String targetPath, int width, int height) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).size(width, height)
                .keepAspectRatio(false).toFile(targetFile);
        // 或者Thumbnails.of(file).forceSize(width,height).toFile(targetFile);
    }

    /**
     * 对图片进行旋转
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @param rotate      角度
     * @throws IOException
     */
    public static void rotate (String picturePath, String targetPath, Double rotate) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }

        Thumbnails.of(file).size(1000, 1000).rotate(rotate)
                .keepAspectRatio(false).toFile(targetFile);
    }

    /**
     * 对图片加水印
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @throws IOException
     */
    public static void watermark (String picturePath, String targetPath) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        BufferedImage bufferedImage = Thumbnails.of(ThumbnailsDemo.class.getClassLoader().getResource("mark.jpg").getPath())
                .size(1500, 1500).keepAspectRatio(false).asBufferedImage();
        Thumbnails.of(file).size(1000, 1000).watermark(bufferedImage, 0.9f)
                .toFile(targetFile);
    }


    /**
     * 对图片裁剪
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @throws IOException
     */
    public static void sourceRegion (String picturePath, String targetPath) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).sourceRegion(100, 100, 840, 800).size(100, 100)
                .toFile(targetFile);
    }

    /**
     * 转换图片格式
     *
     * @param picturePath 原文件路径
     * @param targetPath  目标图片路径
     * @throws IOException
     */
    public static void outputFormat (String picturePath, String targetPath) throws IOException {
        File file = new File(picturePath);
        File targetFile = new File(targetPath);
        if (!targetFile.getParentFile().exists() || targetFile.getParentFile().isFile()) {
            targetFile.getParentFile().mkdirs();
        }
        Thumbnails.of(file).scale(1).outputFormat("png").toFile(targetFile);
    }


    public static void main (String[] args) throws IOException {
        String picturePath = ThumbnailsDemo.class.getClassLoader().getResource("1024-966.jpg").getPath();
        String targetPath = System.getProperty("user.dir") + File.separator + "images" + File.separator;
        thumbnails(picturePath, targetPath + "scale_picture.jpg", 1.1);
        thumbnailsWithWidth(picturePath, targetPath + "width_picture.jpg", 100);
        thumbnailsWithHeight(picturePath, targetPath + "height_picture.jpg", 500);
        thumbnailsWithSize(picturePath, targetPath + "size_picture.jpg", 500, 250);
        rotate(picturePath, targetPath + "rotate_picture.jpg", 130.0);
        watermark(picturePath, targetPath + "waterMark_picture.jpg");
        sourceRegion(picturePath, targetPath + "sourceRegion_picture.jpg");
        outputFormat(picturePath, targetPath + "outputFormat_picture.png");
    }


}
