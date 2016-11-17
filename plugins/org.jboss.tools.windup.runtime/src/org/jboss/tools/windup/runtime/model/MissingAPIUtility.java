package org.jboss.tools.windup.runtime.model;

import org.eclipse.core.resources.IMarker;
import org.jboss.windup.tooling.data.Classification;
import org.jboss.windup.tooling.data.Hint;

/**
 * This is a class to isolate all API we see as missing or deficient, 
 * where the tools cannot work as currently envisioned without further 
 * changes upstream. 
 * 
 * This may include information we can't get based on current signatures,
 * or, information we cannot get without expanding our dependency 
 * tree substantially. 
 * 
 */
public class MissingAPIUtility {


    public static int getMarkerSeverity(Hint hint)
    {
        if (hint == null)
            return IMarker.SEVERITY_WARNING;

        // TODO ???
        // hint.getIssueCategory().???
        return IMarker.SEVERITY_ERROR;
    }

    public static int getMarkerSeverity(Classification c)
    {
        if (c == null)
            return IMarker.SEVERITY_WARNING;

        // TODO ???
        // hint.getIssueCategory().???
        return IMarker.SEVERITY_ERROR;
    }

}
