/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.mpimp.golm.robin.rnaseq.parser;

import de.mpimp.golm.robin.rnaseq.qual.QualityEncoding.VERSION;
import java.io.File;
import java.net.URI;

/**
 *
 * @author marc
 */
public class FastQFile extends File {
    
    private long readCount;
    private long filteredReadCount;
    private VERSION qualityEncoding;
    private String comment;

    public FastQFile(URI uri) {
        super(uri);
    }

    public FastQFile(File parent, String child) {
        super(parent, child);
    }

    public FastQFile(String parent, String child) {
        super(parent, child);
    }

    public FastQFile(String pathname) {
        super(pathname);
    }

    public VERSION getQualityEncoding() {
        return qualityEncoding;
    }

    public long getReadCount() {
        return readCount;
    }

    public void setQualityEncoding(VERSION qualityEncoding) {
        this.qualityEncoding = qualityEncoding;
    }

    public void setReadCount(long readCount) {
        this.readCount = readCount;
    }

    public void setComment(String string) {
        this.comment = string;
    }

    public String getComment() {
        return comment;
    }

    public long getFilteredReadCount() {
        return filteredReadCount;
    }

    public void setFilteredReadCount(long filteredReadCount) {
        this.filteredReadCount = filteredReadCount;
    }

    
}
