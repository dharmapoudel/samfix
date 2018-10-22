package com.dharmapoudel.samfix.autobackup;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class BackupTask extends AsyncTask<String, Void, Boolean> {


    private final Context mContext;
    private PackageManager mPackageManager;
    private static final String APP_DIR = "backups/apps/";

    public BackupTask(Context context) {
        mContext = context.getApplicationContext();
        mPackageManager = mContext.getPackageManager();
        createDirectory();
    }

    @Override
    protected Boolean doInBackground(final String... pkgs) {
        if (pkgs == null || pkgs.length < 1)
            return backupApps();
        else {
            backupApp(pkgs[0]);
            return true;
        }
    }

    private static void createDirectory(){
        String fileName = ".nomedia";
        File f1 = new File(Environment.getExternalStorageDirectory() + File.separator + APP_DIR, fileName);
        if (!f1.exists()) {
            f1.mkdirs();
        }
    }


    private boolean backupApps() {
        List<PackageInfo> packageInfos = mPackageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packageInfos) {
            backupApp(packageInfo);
        }
        return true;
    }

    private void backupApp(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;

        if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 &&
                (applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
            return;
        }

        final String name = applicationInfo.loadLabel(mPackageManager).toString().trim();
        final String pkg = applicationInfo.packageName.trim();
        final String version = packageInfo.versionName.trim();

        String filename = (name + "-" + pkg + "-" + version + ".apk").replaceAll("[?<>\\:*|\"]", "_");
        File output = new File(Environment.getExternalStorageDirectory() + File.separator + APP_DIR, filename);
        File input = new File(applicationInfo.publicSourceDir);

        try {
            copy(input, output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void backupApp(String pkg) {
        try {
            backupApp(mPackageManager.getPackageInfo(pkg, 0));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void copy(File input, File output) throws IOException {
        FileOutputStream outStream = new FileOutputStream(output.getAbsolutePath());
        InputStream inStream = new FileInputStream(input);

        byte[] buf = new byte[16 * 1024];
        int size;
        while ((size = inStream.read(buf)) != -1) {
            outStream.write(buf, 0, size);
        }
    }

}
