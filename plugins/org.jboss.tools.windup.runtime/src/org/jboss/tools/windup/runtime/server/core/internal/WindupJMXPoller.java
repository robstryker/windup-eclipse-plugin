package org.jboss.tools.windup.runtime.server.core.internal;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.jmx.integration.JBossJMXConnectionProviderModel;
import org.jboss.ide.eclipse.as.jmx.integration.JMXPoller;
import org.jboss.ide.eclipse.as.jmx.integration.JMXPollerModel;
import org.jboss.tools.jmx.core.ExtensionManager;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.local.JVMConnectionUtility;

public class WindupJMXPoller extends JMXPoller {
	private static final String POLLER_ID = "org.jboss.tools.windup.runtime.server.core.internal.WindupJMXPoller";
	
	// MBEAN methods
	private static final String STARTED_METHOD = "started"; //$NON-NLS-1$
	public static final String SHUTDOWN_METHOD = "shutdownServer"; //$NON-NLS-1$
	public static final String SYSTEM_MBEAN = "org.jboss.windup.bootstrap.server:type=WindupServer"; //$NON-NLS-1$
	public static final String TOOLING_MBEAN = "org.jboss.windup.tooling:type=Tooling"; //$NON-NLS-1$


	@Override
	protected JMXPollerModel createModel() {
		return new JMXPollerModel() {
			@Override
			protected IConnectionWrapper findConnection(IServer server) {
				// TODO clean up, this is here to ensure it's initialized
				ExtensionManager.getProviders();  
				IActiveJvm jvm  = JVMFacadeUtility.findJvmForServer(server);
				if( jvm != null ) {
					return JVMConnectionUtility.findConnectionForJvm(jvm);
				}
				return null;
			}
			@Override
			public void beginTransaction(IServer server, Object lock) {
				// do nothing
			}

			@Override
			public void endTransaction(IServer server, Object lock) {
				// do nothing
			}
		};
	}
	protected IJMXRunnable createJMXRunnable() {
		return new WindupJMXPollerRunnable();
	}
	protected int getStateFromRunnable(IJMXRunnable runnable) {
		return ((WindupJMXPollerRunnable)runnable).result ? STATE_STARTED : STATE_TRANSITION;
	}
	
	private static class WindupJMXPollerRunnable implements IJMXRunnable {
		private boolean result;
		public void run(MBeanServerConnection connection) throws Exception {
			Object attInfo = connection.invoke(
					new ObjectName(SYSTEM_MBEAN),STARTED_METHOD, new Object[]{}, new String[]{});
			result = ((Boolean) attInfo).booleanValue();
		}
	}
}
