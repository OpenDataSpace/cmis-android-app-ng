package org.opendataspace.android.storage;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountConfig;

import java.io.File;

public class Storage {

    public static File getConfigFile(Context context, String name, Account account) {
        if (account == null) {
            return null;
        }

        File ext = context.getExternalFilesDir(null);

        if (ext == null) {
            return null;
        }

        return new File(createFolder(ext.getParentFile(), "config" + File.separator + account.getFolderName()), name);
    }

    private static File createFolder(File f, String extendedPath) {
        File tmpFolder = new File(f, extendedPath);

        if (!tmpFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            tmpFolder.mkdirs();
        }

        return tmpFolder;
    }

    public static Drawable getBrandingDrawable(Context context, String name, Account account) {
        File f = getConfigFile(context, name, account);

        if (f == null || !f.exists()) {
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
}
