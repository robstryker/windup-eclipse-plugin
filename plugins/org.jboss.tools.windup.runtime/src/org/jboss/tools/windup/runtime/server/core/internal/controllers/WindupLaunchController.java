package org.jboss.tools.windup.runtime.server.core.internal.controllers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfigurator;
import org.jboss.ide.eclipse.as.core.server.ILaunchConfigConfiguratorProvider;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.ILaunchServerController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IServerShutdownController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IShutdownControllerDelegate;

public class WindupLaunchController extends AbstractSubsystemController 
	implements ILaunchServerController, ILaunchConfigurationDelegate2, IShutdownControllerDelegate, ILaunchConfigConfiguratorProvider {

	private WindupLaunchControllerDelegate launchDelegate;
	private WindupLaunchControllerDelegate getLaunchDelegate() {
		if( launchDelegate == null ) {
			launchDelegate = new WindupLaunchControllerDelegate();
		}
		return launchDelegate;
	}
	
	
	@Override
	public IStatus canStart(String launchMode) {
		return Status.OK_STATUS;
	}

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy workingCopy,
			IProgressMonitor monitor) throws CoreException {
		ILaunchConfigConfigurator configurator = getConfigurator();
		if( configurator != null ) {
			configurator.configure(workingCopy);
		}
	}

	private ILaunchConfigConfigurator getConfigurator() throws CoreException {
		return new WindupLaunchConfigurator(getServer());
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.debug.core.model.ILaunchConfigurationDelegate#launch(org.eclipse.debug.core.ILaunchConfiguration, java.lang.String, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
		ILaunch launch, IProgressMonitor monitor) throws CoreException {
		// FOr this method we assume everything has already been set up properly
		// and we just launch with our standard local launch delegate
		// which checks things like if a server is up already, or 
		// provides profiling integration with wtp's profiling for servers
		getLaunchDelegate().launch(configuration, mode, launch, monitor);
	}

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode)
			throws CoreException {
		// TODO Auto-generated method stub
		return getLaunchDelegate().getLaunch(configuration, mode);
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().buildForLaunch(configuration, mode, monitor);
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().finalLaunchCheck(configuration, mode, monitor);
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration,
			String mode, IProgressMonitor monitor) throws CoreException {
		return getLaunchDelegate().preLaunchCheck(configuration, mode, monitor);
	}


	@Override
	public IServerShutdownController getShutdownController() {
		IServerShutdownController c = new WindupShutdownController();
		c.initialize(getServer(), null, null);
		return c;
	}


	@Override
	public ILaunchConfigConfigurator getLaunchConfigurator()
			throws CoreException {
		return getConfigurator();
	}

}
