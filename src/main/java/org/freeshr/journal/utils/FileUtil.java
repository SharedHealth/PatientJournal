package org.freeshr.journal.utils;


import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
    private static Logger logger = Logger.getLogger(FileUtil.class);

    public static String asString(String path) {
        try {
            return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
        } catch (IOException e) {
            logger.error(String.format("Could not read file %s, reason : %s", path, e.getMessage()));
            throw new RuntimeException("File not found", e);
        }
    }

    public static InputStream asStream(String path) {
        return new ByteArrayInputStream(asString(path).getBytes());
    }
}
