package ca.ubc.cs.cpsc210.translink.providers;

import android.content.Context;
import android.content.res.Resources;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Data provider where data source is a file in Android environment
 */
public class AndroidFileDataProvider extends AbstractFileDataProvider {
    private Context cxt;
    private String fileName;

    public AndroidFileDataProvider(Context cxt, String fileName) {
        this.fileName = fileName;
        this.cxt = cxt;
    }

    @Override
    public String dataSourceToString() throws IOException {
        Resources resources = cxt.getResources();
        InputStream is = resources.openRawResource(
                resources.getIdentifier("raw/" + fileName, "raw", cxt.getPackageName()));
        return readSource(is);
    }

    @Override
    public byte[] dataSourceToBytes() throws IOException {
        Resources resources = cxt.getResources();
        InputStream is = resources.openRawResource(
                resources.getIdentifier("raw/" + fileName, "raw", cxt.getPackageName()));
        return readSourceRaw(is);
    }
}
