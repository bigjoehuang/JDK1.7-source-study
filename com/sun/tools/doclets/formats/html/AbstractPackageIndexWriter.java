/*
 * Copyright (c) 1998, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.tools.doclets.formats.html;

import java.io.*;
import java.util.*;
import com.sun.javadoc.*;
import com.sun.tools.doclets.formats.html.markup.*;
import com.sun.tools.doclets.internal.toolkit.*;

/**
 * Abstract class to generate the overview files in
 * Frame and Non-Frame format. This will be sub-classed by to
 * generate overview-frame.html as well as overview-summary.html.
 *
 * @author Atul M Dambalkar
 * @author Bhavesh Patel (Modified)
 */
public abstract class AbstractPackageIndexWriter extends HtmlDocletWriter {

    /**
     * Array of Packages to be documented.
     */
    protected PackageDoc[] packages;

    /**
     * Constructor. Also initialises the packages variable.
     *
     * @param filename Name of the package index file to be generated.
     */
    public AbstractPackageIndexWriter(ConfigurationImpl configuration,
                                      String filename) throws IOException {
        super(configuration, filename);
        this.relativepathNoSlash = ".";
        packages = configuration.packages;
    }

    /**
     * Adds the navigation bar header to the documentation tree.
     *
     * @param body the document tree to which the navigation bar header will be added
     */
    protected abstract void addNavigationBarHeader(Content body);

    /**
     * Adds the navigation bar footer to the documentation tree.
     *
     * @param body the document tree to which the navigation bar footer will be added
     */
    protected abstract void addNavigationBarFooter(Content body);

    /**
     * Adds the overview header to the documentation tree.
     *
     * @param body the document tree to which the overview header will be added
     */
    protected abstract void addOverviewHeader(Content body);

    /**
     * Adds the packages list to the documentation tree.
     *
     * @param packages an array of packagedoc objects
     * @param text caption for the table
     * @param tableSummary summary for the table
     * @param body the document tree to which the packages list will be added
     */
    protected abstract void addPackagesList(PackageDoc[] packages, String text,
            String tableSummary, Content body);

    /**
     * Generate and prints the contents in the package index file. Call appropriate
     * methods from the sub-class in order to generate Frame or Non
     * Frame format.
     *
     * @param title the title of the window.
     * @param includeScript boolean set true if windowtitle script is to be included
     */
    protected void buildPackageIndexFile(String title, boolean includeScript) throws IOException {
        String windowOverview = configuration.getText(title);
        Content body = getBody(includeScript, getWindowTitle(windowOverview));
        addNavigationBarHeader(body);
        addOverviewHeader(body);
        addIndex(body);
        addOverview(body);
        addNavigationBarFooter(body);
        printHtmlDocument(configuration.metakeywords.getOverviewMetaKeywords(title,
                configuration.doctitle), includeScript, body);
    }

    /**
     * Default to no overview, override to add overview.
     *
     * @param body the document tree to which the overview will be added
     */
    protected void addOverview(Content body) throws IOException {
    }

    /**
     * Adds the frame or non-frame package index to the documentation tree.
     *
     * @param body the document tree to which the index will be added
     */
    protected void addIndex(Content body) {
        addIndexContents(packages, "doclet.Package_Summary",
                configuration.getText("doclet.Member_Table_Summary",
                configuration.getText("doclet.Package_Summary"),
                configuration.getText("doclet.packages")), body);
    }

    /**
     * Adds package index contents. Call appropriate methods from
     * the sub-classes. Adds it to the body HtmlTree
     *
     * @param packages array of packages to be documented
     * @param text string which will be used as the heading
     * @param tableSummary summary for the table
     * @param body the document tree to which the index contents will be added
     */
    protected void addIndexContents(PackageDoc[] packages, String text,
            String tableSummary, Content body) {
        if (packages.length > 0) {
            Arrays.sort(packages);
            addAllClassesLink(body);
            addPackagesList(packages, text, tableSummary, body);
        }
    }

    /**
     * Adds the doctitle to the documentation tree, if it is specified on the command line.
     *
     * @param body the document tree to which the title will be added
     */
    protected void addConfigurationTitle(Content body) {
        if (configuration.doctitle.length() > 0) {
            Content title = new RawHtml(configuration.doctitle);
            Content heading = HtmlTree.HEADING(HtmlConstants.TITLE_HEADING,
                    HtmlStyle.title, title);
            Content div = HtmlTree.DIV(HtmlStyle.header, heading);
            body.addContent(div);
        }
    }

    /**
     * Returns highlighted "Overview", in the navigation bar as this is the
     * overview page.
     *
     * @return a Content object to be added to the documentation tree
     */
    protected Content getNavLinkContents() {
        Content li = HtmlTree.LI(HtmlStyle.navBarCell1Rev, overviewLabel);
        return li;
    }

    /**
     * Do nothing. This will be overridden in PackageIndexFrameWriter.
     *
     * @param body the document tree to which the all classes link will be added
     */
    protected void addAllClassesLink(Content body) {
    }
}
