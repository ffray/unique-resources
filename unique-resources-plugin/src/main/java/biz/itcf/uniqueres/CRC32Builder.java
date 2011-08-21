package biz.itcf.uniqueres;

import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CRC32Builder extends AbstractChecksumBuilder {

    @Override
    protected Checksum makeChecksum() {
        return new CRC32();
    }

}
