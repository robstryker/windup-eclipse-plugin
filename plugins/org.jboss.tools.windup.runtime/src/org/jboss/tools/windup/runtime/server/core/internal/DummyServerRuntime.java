package org.jboss.tools.windup.runtime.server.core.internal;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.model.RuntimeDelegate;
import org.jboss.tools.windup.runtime.WindupRuntimePlugin;

public class DummyServerRuntime extends RuntimeDelegate {

	public DummyServerRuntime() {
	}
	protected void initialize() {
		 File windupHome = WindupRuntimePlugin.findWindupHome();
		 getRuntimeWorkingCopy().setLocation(new Path(windupHome.getAbsolutePath()));
	}

}
