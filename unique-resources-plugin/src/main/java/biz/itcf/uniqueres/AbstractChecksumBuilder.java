package biz.itcf.uniqueres;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.Checksum;

public abstract class AbstractChecksumBuilder implements UniqueIDBuilder {

    protected abstract Checksum makeChecksum();

    @Override
    public String buildID(File f) {
        FileInputStream fin = null;

        try {
            fin = new FileInputStream(f);

            Checksum checksum = makeChecksum();

            byte[] buffer = new byte[4 * 1024];

            int read;

            while ((read = fin.read(buffer)) != -1) {
                checksum.update(buffer, 0, read);
            }

            return Long.toString(checksum.getValue());
        } catch (IOException e) {
            throw new RuntimeException("Unrecoverable IOException.", e);
        } finally {
            if (fin != null) {
                try {
                    fin.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
