package org.jboss.tools.windup.runtime.server.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.model.ServerDelegate;
import org.jboss.ide.eclipse.as.core.util.IJBossToolingConstants;
import org.jboss.tools.windup.runtime.WindupRuntimePlugin;

public class WindupServer extends ServerDelegate {
	public static final String PROP_WINDUP_HOME = "org.jboss.tools.windup.runtime.server.core.internal.WINDUP_HOME";
	private static final String POLLER_ID="org.jboss.tools.windup.runtime.server.core.internal.WindupJMXPoller"; 
	
	
	public WindupServer() {
		// TODO Auto-generated constructor stub
	}
	public void setDefaults(IProgressMonitor monitor) {
		setAttribute(PROP_WINDUP_HOME, WindupRuntimePlugin.findWindupHome().getAbsolutePath());
		setAttribute(IJBossToolingConstants.STARTUP_POLLER_KEY, POLLER_ID);
		setAttribute(IJBossToolingConstants.SHUTDOWN_POLLER_KEY, POLLER_ID);
		
	}
	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		return Status.CANCEL_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		return new IModule[0];
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {
		return new IModule[0];
	}

	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {
	}
}
