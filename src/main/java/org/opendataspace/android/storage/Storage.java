package org.opendataspace.android.storage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import org.opendataspace.android.object.Account;
import org.opendataspace.android.object.Repo;
import org.opendataspace.android.operation.OperationAccountConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class Storage {

    public static final String CATEGORY_CONFIG = "config";
    public static final String CATEGORY_CACHE = "cache";

    public static File getAccountFolder(Context context, Account account) {
        if (account == null || context == null) {
            return null;
        }

        File ext = context.getExternalFilesDir(null);

        if (ext == null) {
            return null;
        }

        return new File(ext, account.getFolderName());
    }

    public static File getLocalFolder(Context context, Account account, Repo repo, String category) {
        File res = getAccountFolder(context, account);

        if (res == null) {
            return null;
        }

        if (repo != null) {
            res = new File(res, repo.getFolderName());
        }

        if (category != null) {
            res = new File(res, category);
        }

        if (!res.exists() && !res.mkdirs()) {
            return null;
        }

        return res;
    }

    public static Drawable getBrandingDrawable(Context context, Account account, String name) {
        File ext = getLocalFolder(context, account, null, CATEGORY_CONFIG);

        if (ext == null) {
            return null;
        }

        File f = new File(ext, name);

        if (!f.exists()) {
            return null;
        }

        Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());

        if (bmp == null) {
            return null;
        }

        Resources res = context.getResources();

        if (name.equals(OperationAccountConfig.BRAND_ICON)) {
            int sz = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, res.getDisplayMetrics());
            bmp = Bitmap.createScaledBitmap(bmp, sz, sz, false);
        } else if (name.equals(OperationAccountConfig.BRAND_LARGE)) {
            int w = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, res.getDisplayMetrics());
            int h = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, res.getDisplayMetrics());
            bmp = Bitmap.createScaledBitmap(bmp, w, h, false);
        }

        BitmapDrawable dr = new BitmapDrawable(res, bmp);
        dr.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
        return dr;
    }

    public static boolean copyFile(File source, File dest) throws IOException {
        if (source.isDirectory()) {
            if (!dest.mkdir()) {
                return false;
            }

            boolean res = true;
            File[] ls = source.listFiles(f -> !f.isHidden());

            if (ls != null) {
                for (File cur : ls) {
                    res = res && copyFile(cur, new File(dest, cur.getName()));
                }
            }

            return res;
        }

        if (!dest.createNewFile()) {
            return false;
        }

        FileChannel sc = null, dc = null;

        try {
            sc = new FileInputStream(source).getChannel();
            dc = new FileOutputStream(dest).getChannel();
            dc.transferFrom(sc, 0, sc.size());
        } finally {
            if (sc != null) {
                sc.close();
            }
            if (dc != null) {
                dc.close();
            }
        }

        return true;
    }

    public static boolean deleteTree(File file) {
        if (file == null) {
            return false;
        }

        if (file.isDirectory()) {
            File[] ls = file.listFiles();

            if (ls != null) {
                for (File cur : ls) {
                    deleteTree(cur);
                }
            }
        }

        return file.delete();
    }
}
