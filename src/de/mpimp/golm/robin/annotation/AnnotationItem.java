/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.mpimp.golm.robin.annotation;

import de.rzpd.mapman.data.Bin;
import de.rzpd.mapman.data.BinItem;
import java.util.Vector;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author marc
 */
public class AnnotationItem {

    BinItem item;
    Vector<Bin> bins;

    public AnnotationItem() {
        bins = new Vector<Bin>();
    }

    public Vector<Bin> getBins() {
        return bins;
    }

    public void setBins(Vector<Bin> bins) {
        this.bins = bins;
    }

    public String getDescription() {
        return this.getItem().getDescription();
    }

    public String getBinsAsString() {
        Vector<String> binsvector = new Vector<String>();

        for (Bin bin : this.getBins()) {
            binsvector.add(bin.getBinCode() + " " + bin.getName());
        }
        return StringUtils.join(binsvector, " AND ");
    }

    public void addBin(Bin newbin) {
        this.bins.add(newbin);
    }

    public BinItem getItem() {
        return item;
    }

    public void setItem(BinItem item) {
        this.item = item;
    }
}
