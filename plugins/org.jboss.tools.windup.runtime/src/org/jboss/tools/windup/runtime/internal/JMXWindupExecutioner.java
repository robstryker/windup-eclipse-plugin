package org.jboss.tools.windup.runtime.internal;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;
import org.jboss.tools.jmx.core.IConnectionWrapper;
import org.jboss.tools.jmx.core.IJMXRunnable;
import org.jboss.tools.jmx.core.JMXException;
import org.jboss.tools.jmx.jvmmonitor.core.IActiveJvm;
import org.jboss.tools.jmx.local.JVMConnectionUtility;
import org.jboss.tools.windup.runtime.IRemoteWindupExecutioner;
import org.jboss.tools.windup.runtime.server.core.internal.JVMFacadeUtility;
import org.jboss.tools.windup.runtime.server.core.internal.WindupJMXPoller;
import org.jboss.tools.windup.runtime.server.core.internal.WindupServer;
import org.jboss.windup.tooling.ExecutionResults;

public class JMXWindupExecutioner implements IRemoteWindupExecutioner {

	@Override
	public ExecutionResults execute(Path windupHome, Path projectPath, Path outputPath, HashMap<String, Object> options,
			IProgressMonitor monitor) {

		IServer found = null;
		IServer[] all = ServerCore.getServers();
		for (int i = 0; i < all.length && found == null; i++) {
			if (all[i].getServerType().getId().equals("org.jboss.tools.windup.runtime.server.type")) {
				found = all[i];
			}
		}

		if (found != null) {
			WindupServer ws = (WindupServer) found.loadAdapter(WindupServer.class, new NullProgressMonitor());
			if (ws != null) {
				IActiveJvm jvm = JVMFacadeUtility.findJvmForServer(found);
				boolean success = false;
				if (jvm != null) {
					IConnectionWrapper jmx = JVMConnectionUtility.findConnectionForJvm(jvm);
					if (jmx != null) {
						IJMXRunnable run = new IJMXRunnable() {
							public void run(MBeanServerConnection connection) throws Exception {
								Map.class.getName();
								Object opParams[] = { windupHome.toFile().getAbsolutePath(),
										projectPath.toFile().getAbsolutePath(), outputPath.toFile().getAbsolutePath(),
										options };

								String opSig[] = { String.class.getName(), String.class.getName(),
										String.class.getName(), 
										Map.class.getName()};

								Object result = connection.invoke(new ObjectName(WindupJMXPoller.TOOLING_MBEAN),
										"execute", opParams, opSig);
								System.out.println(result);
							}
						};
						try {
							jmx.run(run);
							success = true;
						} catch (JMXException jmxe) {
							jmxe.printStackTrace(); // TODO LOG
						}
					}
				}
			}
		}

		//
		// WindupProgressMonitor windupProgressMonitor = new
		// WindupProgressMonitorAdapter(monitor);
		// ExecutionBuilder execBuilder =
		// WindupService.getServiceFromFurnace(ExecutionBuilder.class,
		// windupProgressMonitor);
		// ExecutionBuilderSetOptions options2 = execBuilder.begin(windupHome)
		// .setInput(projectPath)
		// .setOutput(outputPath.toFile().toPath())
		// .setProgressMonitor(windupProgressMonitor);
		//
		// Iterator<String> optsIt = options.keySet().iterator();
		// while(optsIt.hasNext()) {
		// String k = optsIt.next();
		// if( !k.startsWith(IWindupOptions.TOOLING_PREFIX)) {
		// options2 = options2.setOption(k, options.get(k));
		// } else {
		// if( IWindupOptions.T_IGNORE.equals(k)) {
		// options2 = options2.ignore((String)options.get(k));
		// }
		// }
		// }
		//
		// return options2.execute();
		return null;
	}

}
