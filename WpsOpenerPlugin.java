package id.snbtku.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import java.io.File;

@CapacitorPlugin(name = "WpsOpener")
public class WpsOpenerPlugin extends Plugin {
    private static final String[] WPS = {"cn.wps.moffice_eng", "cn.wps.moffice", "cn.wps.moffice_eng.xiaomi.lite"};

    @PluginMethod
    public void open(PluginCall call) {
        String path = call.getString("path");
        if (path == null || path.isEmpty()) { call.reject("path kosong"); return; }
        try {
            if (path.startsWith("file://")) path = Uri.parse(path).getPath();
            File f = new File(path);
            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", f);
            JSObject r = new JSObject();
            for (String pkg : WPS) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(uri, "application/pdf");
                i.setPackage(pkg);
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    getContext().startActivity(i);
                    r.put("app", "wps");
                    call.resolve(r);
                    return;
                } catch (ActivityNotFoundException e) { /* coba paket berikutnya */ }
            }
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(uri, "application/pdf");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Intent ch = Intent.createChooser(i, "Buka PDF dengan");
            ch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(ch);
            r.put("app", "chooser");
            call.resolve(r);
        } catch (Exception e) {
            call.reject(String.valueOf(e.getMessage()));
        }
    }
}
