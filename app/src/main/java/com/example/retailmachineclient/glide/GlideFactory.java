//package com.example.retailmachineclient.glide;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//
//import com.bumptech.glide.Glide;
//import com.example.retailmachineclient.R;
//import com.example.retailmachineclient.glide.ImageStrategy;
//
////import androidx.core.content.ContextCompat;
////import android.support.v4.content.ContextCompat;
//import androidx.core.content.ContextCompat;
//
//
///**
// *    time   : 2018/12/27
// *    desc   : 100dp 加工厂
// */
//public final class GlideFactory implements ImageFactory<GlideStrategy> {
//
//    @Override
//    public GlideStrategy createImageStrategy() {
//        return new GlideStrategy();
//    }
//
//    @Override
//    public Drawable createPlaceholder(Context context) {
////        return ContextCompat.getDrawable(context, R.mipmap.image_loading);
//        return null;
//    }
//
//    @Override
//    public Drawable createError(Context context) {
////        return ContextCompat.getDrawable(context, R.mipmap.image_load_err);
//        return null;
//    }
//
//    @Override
//    public void clearMemoryCache(Context context) {
//        // 清除内存缓存（必须在主线程）
//        Glide.get(context).clearMemory();
//    }
//
//    @Override
//    public void clearDiskCache(final Context context) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 清除本地缓存（必须在子线程）
//                Glide.get(context).clearDiskCache();
//            }
//        }).start();
//    }
//}