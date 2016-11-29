/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.windup.ui.internal.explorer;

import static org.jboss.tools.windup.model.domain.WindupMarker.ELEMENT_ID;

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.windup.runtime.model.Severity;
import org.jboss.tools.windup.ui.WindupUIPlugin;
import org.jboss.tools.windup.ui.internal.Messages;

import com.google.common.collect.Lists;

/**
 * Utility for working with resource markers.
 */
public class MarkerUtil {
	
	public static List<IMarker> getMarkers(String type, IResource resource, int depth) {
		if (resource.getType() == IResource.PROJECT) {
			if (!resource.isAccessible()) {
				return Lists.newArrayList();
			}
		}
		try {
			return Lists.newArrayList(resource.findMarkers(type, true, depth));
		} catch (CoreException e) {
			WindupUIPlugin.logError(NLS.bind(Messages.markerError_findingMarkers, resource.getName()), e);
		}
		return Lists.newArrayList();
	}
	
	public static IJavaElement findJavaElementForMarker(IMarker marker) {
        try {
            Object elementId = marker.getAttribute(ELEMENT_ID);
            if (elementId instanceof String) {
                return JavaCore.create((String) elementId);
            }
        } catch (CoreException e) {
            WindupUIPlugin.logError(Messages.markerError_findingJavaId, e);
            return null;
        }
        return null;
    }
	
	public static int convertSeverity(String severity) {
		if (severity == null) {
			return IMarker.SEVERITY_WARNING;
		}
		if (severity.equals(Severity.MANDATORY.toString())) {
			return IMarker.SEVERITY_ERROR;
		}
		else if (severity.equals(Severity.OPTIONAL.toString())) {
			return IMarker.SEVERITY_INFO;
		}
		else if (severity.equals(Severity.POTENTIAL.toString())) {
			return IMarker.SEVERITY_WARNING;
		}
		else {
			return IMarker.SEVERITY_ERROR;
		}
	}
}
