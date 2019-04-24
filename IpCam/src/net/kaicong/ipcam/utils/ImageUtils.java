package net.kaicong.ipcam.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

/**
 * Created by LingYan on 2014/9/4.
 */
public class ImageUtils {

    /**
     * 正常图片显示
     *
     * @param commonImage
     * @return
     */
    public static DisplayImageOptions getDisplayOptions(int commonImage, Map<String, String> headers) {
        DisplayImageOptions options;
        if (headers == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();
        } else {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .extraForDownloader(headers)//给有些访问图片的需要验证的链接添加验证头
                    .considerExifParams(true)
                    .build();
        }
        return options;
    }

    /**
     * 显示图片为圆形
     *
     * @param commonImage 默认图片
     * @return
     */
    public static DisplayImageOptions getRoundedDisplayOptions(int commonImage, Map<String, String> headers) {
        DisplayImageOptions options;
        if (headers == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new BitmapDisplayer() {
                        @Override
                        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                            Bitmap roundBitmap = toRoundBitmap(bitmap);
                            imageAware.setImageBitmap(roundBitmap);
                        }
                    })
                    .build();
        } else {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .extraForDownloader(headers)//给有些访问图片的需要验证的链接添加验证头
                    .considerExifParams(true)
                    .displayer(new BitmapDisplayer() {
                        @Override
                        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
                            Bitmap roundBitmap = toRoundBitmap(bitmap);
                            imageAware.setImageBitmap(roundBitmap);
                        }
                    })
                    .build();
        }
        return options;
    }

    /**
     * 显示为圆角图片
     *
     * @param radius      圆角半径
     * @param commonImage 默认图片
     * @return
     */
    public static DisplayImageOptions getFilletDisplayOptions(int radius, int commonImage, Map<String, String> headers) {
        DisplayImageOptions options;
        if (headers == null) {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(radius))
                    .build();
        } else {
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(commonImage)
                    .showImageForEmptyUri(commonImage)
                    .showImageOnFail(commonImage)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .extraForDownloader(headers)//给有些访问图片的需要验证的链接添加验证头
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(radius))
                    .build();
        }
        return options;
    }

    /**
     * 转换图片成圆形
     *
     * @param bitmap 传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    //bitmap转换成byte数组
    public static byte[] getByteArrayFromBitmap(Bitmap bitmap) {

        if (bitmap != null && !bitmap.isRecycled()) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
            return bos.toByteArray();
        } else {
            return null;
        }
    }

    //圆角图片
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        /**
         * 画一个圆角矩形
         * rectF: 矩形
         *  n 圆角在x轴上或y轴上的半径
         */
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        //设置两张图片相交时的模式
        //setXfermode前的是 dst 之后的是src
        //在正常的情况下，在已有的图像上绘图将会在其上面添加一层新的形状。
        //如果新的Paint是完全不透明的，那么它将完全遮挡住下面的Paint；
        //PorterDuffXfermode就可以来解决这个问题
        //canvas原有的图片 可以理解为背景 就是dst
        //新画上去的图片 可以理解为前景 就是src
//      paint.setXfermode(new PorterDuffXfermode(Mode.SRC_OUT));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * Drawable To Bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }

    //大小压缩
    private Bitmap getimage(String srcPath, float width, float height) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);//此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = width;//这里设置高度为800f
        float ww = height;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    //码流压缩
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    //小图制作
    private Bitmap img_comp_s(Bitmap image, float width, float height) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        be = (int) (newOpts.outWidth / width);
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    // 大小缩放
    private Bitmap img_comp_b(Bitmap image, float width) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (w > h && w < width) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (w / width);
        }
        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return bitmap;//不进行质量压缩
    }
}
