package org.opendataspace.android.storage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.opendataspace.android.object.Account;
import org.opendataspace.android.operation.OperationAccountConfig;

import java.io.File;

public class Storage {

    public static File getConfigFile(Context context, String name, Account account) {
        File ext = context.getExternalFilesDir(null);

        if (ext == null) {
            return null;
        }

        return new File(createFolder(ext.getParentFile(), "config" + File.separator + account.getFolderName()), name);
    }

    public static File createFolder(File f, String extendedPath) {
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

        if (name.equals(OperationAccountConfig.BRAND_ICON)) {
            bmp = Bitmap.createScaledBitmap(bmp, 192, 192, false);
        } else if (name.equals(OperationAccountConfig.BRAND_LARGE)) {
            bmp = Bitmap.createScaledBitmap(bmp, 1100, 250, false);
        }

        return new BitmapDrawable(context.getResources(), bmp);
    }
}
