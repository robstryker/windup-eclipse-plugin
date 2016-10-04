package org.jboss.tools.windup.runtime.server.core.internal.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.internal.launching.environments.EnvironmentsManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.wst.server.core.IServer;
import org.jboss.ide.eclipse.as.core.server.IJBossServerRuntime;
import org.jboss.ide.eclipse.as.core.server.internal.launch.configuration.JBossLaunchConfigProperties;
import org.jboss.ide.eclipse.as.core.util.LaunchConfigUtils;
import org.jboss.ide.eclipse.as.wtp.core.server.launch.LaunchConfiguratorWithOverrides;
import org.jboss.ide.eclipse.as.wtp.core.util.VMInstallUtil;

public class WindupLaunchConfigurator extends LaunchConfiguratorWithOverrides {

	public WindupLaunchConfigurator(IServer server) throws CoreException {
		super(server);
	}
	
	JBossLaunchConfigProperties props;

	private JBossLaunchConfigProperties properties = null;
	protected JBossLaunchConfigProperties getProperties() {
		if( properties == null )
			properties = createProperties();
		return properties;
	}

	protected JBossLaunchConfigProperties createProperties() {
		return new JBossLaunchConfigProperties();
	}
	
	@Override
	protected void doConfigure(ILaunchConfigurationWorkingCopy launchConfig) throws CoreException {
		
		
		// TODO In current impl, we need to pass all this stuff on startup
		// But I believe once we trim down the BootstrapServer class, we'll only
		// need this stuff on JMX commands
		
		// -Djava.rmi.server.hostname=localhost 
		// --input /home/rob/tmp/windup/demo 
		// --output /home/rob/tmp/windup/output 
		// --target eap --overwrite
		
		// hard-code non-bundled windup since we're making changes to windup core and need the local build
		String windupHome = "/home/rob/code/jbtools/other/windup/windup-distribution/target/windup-distribution-3.0.0-SNAPSHOT"; 
		

		
		getProperties().setProgramArguments(getDefaultProgramArguments(windupHome), launchConfig);
		getProperties().setVmArguments(getDefaultVMArguments(), launchConfig);
		getProperties().setJreContainer(getJreContainerPath(), launchConfig);
		getProperties().setMainType(getMainType(), launchConfig);
		getProperties().setWorkingDirectory(getWorkingDirectory(windupHome), launchConfig);
		getProperties().setClasspath(getClasspath(getProperties().getClasspath(launchConfig), windupHome), launchConfig);
		getProperties().setUseDefaultClassPath(isUseDefaultClasspath(), launchConfig);
		getProperties().setServerId(getServerId(server), launchConfig);
		
		
		
	}

	protected List<String> getClasspath(List<String> currentClasspath, String windupHome) throws CoreException {
		IExecutionEnvironment env = EnvironmentsManager.getDefault().getEnvironment("JavaSE-1.7");
		IVMInstall vmInstall = VMInstallUtil.findVMInstall(env);
		
		IRuntimeClasspathEntry[] libEntries = getClasspath(new Path(windupHome));
		
		IRuntimeClasspathEntry jreEntry = LaunchConfigUtils.getJREEntry(vmInstall);
		String jreMemento = jreEntry == null ? null : jreEntry.getMemento();
		
		// Remove all entries that represent JREs here. There should only be one jre entry and we'll add that. 
		Iterator<String> i = currentClasspath.iterator();
		String t = null;
		while(i.hasNext()) {
			t = i.next();
			if( t.contains("org.eclipse.jdt.launching.JRE_CONTAINER/"))  { //$NON-NLS-1$
				i.remove();
			}
		}
		
		
		List<String> classpath = new ArrayList<String>();
		classpath.addAll(currentClasspath);
		for( int j = 0; j < libEntries.length; j++ ) {
			classpath.add(libEntries[j].getMemento());
		}
		if( jreMemento != null && !classpath.contains(jreMemento))
			classpath.add(jreMemento);
		return classpath;
	}
	protected boolean isUseDefaultClasspath() {
		return false;
	}

	private String getDefaultVMArguments() {
		return "-Djava.rmi.server.hostname=localhost ";
	}

	private String getDefaultProgramArguments(String windupHome) {
		String progArgs = "--input /home/rob/tmp/windup/demo --output /home/rob/tmp/windup/output --target eap --overwrite " + 
				"--immutableAddonDir " + windupHome + "/addons/";
		return progArgs;
	}

	private String getWorkingDirectory(String windupHome) {
		return new Path(windupHome).append("bin").toFile().getAbsolutePath();
	}

	private String getMainType() {
		return "org.jboss.windup.bootstrap.BootstrapServer";
	}

	protected String getJreContainerPath() {
		IExecutionEnvironment env = EnvironmentsManager.getDefault().getEnvironment("JavaSE-1.7");
		IVMInstall vmInstall = VMInstallUtil.findVMInstall(env);
		if (vmInstall == null) {
			return null;
		}
		return JavaRuntime.newJREContainerPath(vmInstall).toPortableString();
	}
	
	public static IRuntimeClasspathEntry[] getClasspath(IPath windupHome) throws CoreException {
		ArrayList<IRuntimeClasspathEntry> ar = new ArrayList<IRuntimeClasspathEntry>();
		File[] children = windupHome.append("lib").toFile().listFiles();
		for( int i = 0; i < children.length; i++ ) {
			ar.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(children[i].getAbsolutePath())));
		}
		return (IRuntimeClasspathEntry[]) ar.toArray(new IRuntimeClasspathEntry[ar.size()]);
	}
	
	@Override
	protected void doOverrides(ILaunchConfigurationWorkingCopy launchConfig) throws CoreException {
		// TODO In the future we may wish to not allow users to change some details in the launch config
		// for now, since it's still testing / dev, let's jsut call doConfigure again
		doConfigure(launchConfig);
	}

}
