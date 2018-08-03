/*
 * The MIT License
 *
 * Copyright 2016 jvanek.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.fakekoji.xmlrpc.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Logger;

/**
 *
 * @author jvanek
 */
public class IsFailedBuild {

    private static final Logger LOGGER = Logger.getLogger(JavaServerConstants.FAKE_KOJI_LOGGER);

    private final File dir;
    boolean lastResult = false;

    public IsFailedBuild(File dir) {
        this.dir = dir;
    }

    public boolean getLastResult() {
        return lastResult;
    }

    public IsFailedBuild reCheck() {
        try {
            mayBeFailedImpl();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return this;
    }

    private void mayBeFailedImpl() throws IOException {
        Files.walkFileTree(dir.toPath(), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (!file.toFile().isDirectory()) {
                    if (file.getFileName().endsWith("FAILED")) {
                        LOGGER.info("Found file ending with FAILED - " + file.toFile().getAbsoluteFile());
                        lastResult = true;
                    }
                    if (file.getFileName().endsWith("ERROR")) {
                        LOGGER.info("Found file ending with ERROR - " + file.toFile().getAbsoluteFile());
                        lastResult = true;
                    }
                    //most files have VERY long names. Shjortest known name is hg.log, anything shorter is probably error
                    if (file.getFileName().toString().length() < 5) {
                        LOGGER.info("Found filename shorter then 5 chars - " + file.toFile().getAbsoluteFile());
                        lastResult = true;
                    }
                    //most files have huge. Even logs.  Empty files are mostly just touches signalizing something wrong
                    if (file.toFile().length() <= 5) {
                        LOGGER.info("Found file size smaller then 5 bytes - " + file.toFile().getAbsoluteFile());
                        lastResult = true;
                    }
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

}
