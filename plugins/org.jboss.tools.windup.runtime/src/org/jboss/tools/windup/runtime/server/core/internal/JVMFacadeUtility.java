package org.jboss.tools.windup.runtime.server.core.internal;

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.util.JBossServerBehaviorUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.jvmmonitor.core.IHost;
import org.jboss.tools.jmx.jvmmonitor.core.JvmModel;

/*
 * Copied from KarafJVMFacadeUtility, but modified
 */
public class JVMFacadeUtility {
	public static IActiveJvm findJvmForServer(IServer server) {
		String progArgs, main;
		main = progArgs = null;
		ILaunchConfiguration lc = null;
		IControllableServerBehavior beh = JBossServerBehaviorUtils.getControllableBehavior(server);
		if (beh == null) {
			return null; // Absolutely no way to find an activejvm for this
		}

		if (server.getServerState() == IServer.STATE_STARTED) {
			Object o = beh.getSharedData(AbstractStartJavaServerLaunchDelegate.PROCESS);
			if (o instanceof IProcess) {
				IProcess proc = (IProcess) o;
				ILaunch launch = proc.getLaunch();
				lc = launch.getLaunchConfiguration();
			}
		} else {
			try {
				lc = server.getLaunchConfiguration(true, new NullProgressMonitor());
			} catch(CoreException ce) {
				ce.printStackTrace(); // TODO log
			}
		}

		if (lc != null) {
			// Find the
			try {
				progArgs = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS, (String) null);
				main = lc.getAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String) null);
			} catch (CoreException ce) {
				ce.printStackTrace(); // TODO log this so user knows there's a problem
			}
		} else {
			// error immediately, we have no launch config. No way to find a jvm
			return null;
		}

		StringBuffer target2 = new StringBuffer();
		if (main != null) {
			target2.append(main);
			target2.append(" ");
		}
		if (progArgs != null)
			target2.append(progArgs);

		String target = target2.toString();
		target = target.replaceAll("\"", "").trim();
		JvmModel model = JvmModel.getInstance();
		List<IHost> hosts = model.getHosts();
		for (IHost host : hosts) {
			List<IActiveJvm> jvms = host.getActiveJvms();
			for (IActiveJvm jvm : jvms) {
				String command = jvm.getLaunchCommand();
				if (command.equals(target)) {
					return jvm;
				}
				if( command.contains(main) && command.contains(progArgs)) {
					return jvm; // Most likely already running
				}
			}
		}
		return null;
	}

}
