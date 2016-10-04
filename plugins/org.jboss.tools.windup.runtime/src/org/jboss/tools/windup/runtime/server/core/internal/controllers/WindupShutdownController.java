package org.jboss.tools.windup.runtime.server.core.internal.controllers;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ControllableServerBehavior;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IServerShutdownController;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.AbstractStartJavaServerLaunchDelegate;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.local.JVMConnectionUtility;
import org.jboss.tools.windup.runtime.server.core.internal.JVMFacadeUtility;
import org.jboss.tools.windup.runtime.server.core.internal.WindupJMXPoller;

public class WindupShutdownController extends AbstractSubsystemController implements IServerShutdownController {

	public WindupShutdownController() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus canStop() {
		if( getServer().getServerState() == IServer.STATE_STARTED)
			return Status.OK_STATUS;
		return Status.CANCEL_STATUS;
	}

	@Override
	public void stop(boolean force) {
		// TODO Auto-generated method stub
		ControllableServerBehavior controllable = ((ControllableServerBehavior)getControllableBehavior());
		controllable.setServerStopping();
		ExtensionManager.getProviders();  
		IActiveJvm jvm  = JVMFacadeUtility.findJvmForServer(getServer());
		boolean success = false;
		if( jvm != null ) {
			IConnectionWrapper jmx = JVMConnectionUtility.findConnectionForJvm(jvm);
			if( jmx != null ) {
				IJMXRunnable run = new IJMXRunnable(){
					public void run(MBeanServerConnection connection) throws Exception {
						Object attInfo = connection.invoke(
								new ObjectName(WindupJMXPoller.SYSTEM_MBEAN),WindupJMXPoller.SHUTDOWN_METHOD, new Object[]{}, new String[]{});
					}
				};
				try {
					jmx.run(run);
					success = true;
				} catch(JMXException jmxe) {
					jmxe.printStackTrace(); // TODO LOG
				}
			}
		}
		if( !success ) {
			// We never managed to call shutdown, or call failed
			controllable.setServerStarted();
		} else {
			// The server may have been launched externally. If it was launched externally, we should set it to stopped.
			Object proc = controllable.getSharedData(	AbstractStartJavaServerLaunchDelegate.PROCESS);
			if( proc == null ) {
				// externally launched or launched in a previous eclipse session and never shut down
				controllable.setServerStopped();
			} else {
				// If we launched it, we should let our process listener handle and wait for process termination
				// do nothing
			}
		}

	}

}
