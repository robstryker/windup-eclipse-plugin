package org.jboss.tools.windup.runtime.server.core.internal.controllers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.JBossServerCorePlugin;
import org.jboss.ide.eclipse.as.core.server.IServerStatePoller;
import org.jboss.ide.eclipse.as.core.server.IUserPrompter;
import org.jboss.ide.eclipse.as.core.server.internal.PollThread;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.core.util.PollThreadUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;
import org.jboss.tools.as.core.server.controllable.IDeployableServerBehaviorProperties;

public class WindupLaunchControllerDelegate extends AbstractStartJavaServerLaunchDelegate {

	protected void logStatus(IServer server, IStatus stat) {
		// TODO ignore
	}

	protected void initiatePolling(IServer server) {
		// Initiate Polling!
		PollThreadUtils.pollServer(server, IServerStatePoller.SERVER_UP);
	}

	/*
	 * A solution needs to be found here. Should ideally use the poller that the
	 * server says is its poller, but some pollers such as timeout poller
	 */
	protected IStatus isServerStarted(IServer server) {
		return PollThreadUtils.isServerStarted(server);
	}

	@Override
	protected void cancelPolling(IServer server) {
		IControllableServerBehavior jbsBehavior = JBossServerBehaviorUtils.getControllableBehavior(server);
		Object pt = ((ControllableServerBehavior) jbsBehavior)
				.getSharedData(IDeployableServerBehaviorProperties.POLL_THREAD);
		if (pt != null) {
			PollThreadUtils.cancelPolling(null, (PollThread) pt);
		}
	}

	protected void validateServerStructure(IServer server) throws CoreException {
		// do nothing
	}


	protected IUserPrompter getPrompter() {
		return JBossServerCorePlugin.getDefault().getPrompter();
	}

	protected boolean addCustomHotcodeReplaceLogic(IServer server) {
		return false;
	}
}