import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Assert;
import net.coobird.thumbnailator.Thumbnails;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author zhuolin
 * @program: ZUremote
 * @date 2018/11/28
 * @description: ${description}
 **/
public class ThumbnailBigImage {

    private static final int CUT_WIDTH = 5000;

    private static final int CUT_HEIGHT = 5000;

    /**
     * 按照像素0.01进行缩放
     *
     * @param sourceImagePath 原文件路径
     * @param targetImagePath 目标文件路径
     * @return 目标文件地址
     */
    public static String thumbnail (String sourceImagePath, String targetImagePath) throws IOException {
        return thumbnailForScale(sourceImagePath, targetImagePath, 0.01);
    }

    /**
     * 按照指定缩放备输进行缩放
     *
     * @param sourceImagePath 原文件路径
     * @param targetImagePath 目标文件路径
     * @param scale           缩放倍数
     * @return 目标文件地址
     */
    public static String thumbnailForScale (String sourceImagePath, String targetImagePath, Double scale) throws IOException {
        return thumbnail(sourceImagePath, targetImagePath, scale, null, null);
    }

    /**
     * 按照近似长宽进行缩放
     *
     * @param sourceImagePath 原文件路径
     * @param targetImagePath 目标文件路径
     * @param width           近似宽度像素
     * @param height          近似高度像素
     * @return 目标文件地址
     */
    public static String thumbnailForSize (String sourceImagePath, String targetImagePath, int width, int height) throws IOException {
        return thumbnail(sourceImagePath, targetImagePath, null, width, height);
    }

    /**
     * 获取图片的ImageReader
     *
     * @param sourceImagePath 图片路径
     * @return ImageReader
     * @throws IOException IOException
     */
    private static ImageReader getPictureImageReader (String sourceImagePath) throws IOException {
        Assert.isTrue(sourceImagePath != null && sourceImagePath.trim().length() != 0, "图片路径必须存在");
        File srcFile = new File(sourceImagePath);
        Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(FileUtil.getType(srcFile));
        ImageReader reader = (ImageReader) readers.next();
        ImageInputStream iis = ImageIO.createImageInputStream(srcFile);
        reader.setInput(iis, true);
        return reader;
    }

    /**
     * 获取图片宽度
     *
     * @param reader reader
     * @return 宽度
     * @throws IOException IOException
     */
    private static int getPictureWidth (ImageReader reader) throws IOException {
        return reader.getWidth(0);
    }

    /**
     * 获取图片高度
     *
     * @param reader
     * @return
     * @throws IOException
     */
    private static int getPictureHeight (ImageReader reader) throws IOException {
        return reader.getHeight(0);
    }

    /**
     * 生成缩略图
     *
     * @param sourceImagePath 原文件路径
     * @param targetImagePath 目标文件路径
     * @param scale           缩放值
     * @param thumbnailWidth  近似宽度
     * @param thumbnailHeight 近似高度
     * @return 图片路径
     * @throws IOException IOException
     */
    private static String thumbnail (String sourceImagePath, String targetImagePath, Double scale, Integer thumbnailWidth, Integer thumbnailHeight) throws IOException {
        Long startTime = System.currentTimeMillis();
        Assert.isTrue(sourceImagePath != null && sourceImagePath.trim().length() != 0, "图片路径必须存在");
        Assert.isTrue(targetImagePath != null && targetImagePath.trim().length() != 0, "目标路径必须存在");
        File srcFile = new File(sourceImagePath);
        ImageReader reader = getPictureImageReader(sourceImagePath);
        int width = getPictureWidth(reader);
        int height = getPictureHeight(reader);
        List<List<BufferedImage>> bufferedImageList = new ArrayList<List<BufferedImage>>();
        for (int i = 0; i * CUT_HEIGHT < height; i++) {
            List<BufferedImage> bufferedImages = new ArrayList<BufferedImage>();
            for (int j = 0; j * CUT_WIDTH < width; j++) {
                Thumbnails.Builder<File> builder = Thumbnails.of(srcFile).sourceRegion(j * CUT_WIDTH, i * CUT_HEIGHT, CUT_WIDTH, CUT_HEIGHT);
                if (scale != null) {
                    builder.scale(scale);
                }
                if (thumbnailWidth != null && thumbnailHeight != null) {
                    Integer perWidth = null;
                    if (CUT_WIDTH > width) {
                        perWidth = thumbnailWidth;
                    } else {
                        perWidth = CUT_WIDTH * thumbnailWidth / width;
                    }
                    Integer perHeight = null;
                    if (CUT_HEIGHT > width) {
                        perHeight = thumbnailHeight;
                    } else {
                        perHeight = CUT_HEIGHT * thumbnailHeight / height;
                    }
                    builder.size(perWidth, perHeight).keepAspectRatio(false);
                }
                bufferedImages.add(builder.asBufferedImage());
            }
            bufferedImageList.add(bufferedImages);
        }
        List<BufferedImage> newBufferedImages = new ArrayList<BufferedImage>();
        for (List<BufferedImage> bufferedImages : bufferedImageList) {
            newBufferedImages.add(mergeImage(true, bufferedImages));
        }
        BufferedImage bufferedImage = mergeImage(false, newBufferedImages);
        String suffix = targetImagePath.substring(targetImagePath.lastIndexOf(".") + 1);
        File file = new File(targetImagePath);
        if (!file.getParentFile().exists() || file.getParentFile().isFile()) {
            file.getParentFile().mkdirs();
        }
        ImageIO.write(bufferedImage, suffix, new File(targetImagePath));
        System.out.println(System.currentTimeMillis() - startTime);
        return targetImagePath;
    }

    /**
     * 合并任数量的图片成一张图片
     *
     * @param isHorizontal   true代表水平合并，false代表垂直合并
     * @param bufferedImages 待合并的图片数组
     * @return BufferedImage
     */
    private static BufferedImage mergeImage (boolean isHorizontal, List<BufferedImage> bufferedImages) {
        // 生成新图片
        BufferedImage destImage = null;
        // 计算新图片的长和高
        int allw = 0, allh = 0, allwMax = 0, allhMax = 0;
        // 获取总长、总宽、最长、最宽
        for (int i = 0; i < bufferedImages.size(); i++) {
            BufferedImage img = bufferedImages.get(i);
            allw += img.getWidth();
            allh += img.getHeight();
            if (img.getWidth() > allwMax) {
                allwMax = img.getWidth();
            }
            if (img.getHeight() > allhMax) {
                allhMax = img.getHeight();
            }
        }
        // 创建新图片
        if (isHorizontal) {
            destImage = new BufferedImage(allw, allhMax, BufferedImage.TYPE_INT_RGB);
        } else {
            destImage = new BufferedImage(allwMax, allh, BufferedImage.TYPE_INT_RGB);
        }
        // 合并所有子图片到新图片
        int wx = 0, wy = 0;
        for (int i = 0; i < bufferedImages.size(); i++) {
            BufferedImage img = bufferedImages.get(i);
            int w1 = img.getWidth();
            int h1 = img.getHeight();
            // 从图片中读取RGB
            int[] ImageArrayOne = new int[w1 * h1];
            // 逐行扫描图像中各个像素的RGB到数组中
            ImageArrayOne = img.getRGB(0, 0, w1, h1, ImageArrayOne, 0, w1);
            // 水平方向合并
            if (isHorizontal) {
                // 设置上半部分或左半部分的RGB
                destImage.setRGB(wx, 0, w1, h1, ImageArrayOne, 0, w1);
            }
            // 垂直方向合并
            else {
                // 设置上半部分或左半部分的RGB
                destImage.setRGB(0, wy, w1, h1, ImageArrayOne, 0, w1);
            }
            wx += w1;
            wy += h1;
        }
        return destImage;
    }


}
