package org.jboss.tools.windup.runtime.server.core.internal.controllers;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IModule;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.AbstractSubsystemController;
import org.jboss.ide.eclipse.as.wtp.core.server.behavior.IPublishController;

public class WindupNullPublishController extends AbstractSubsystemController implements IPublishController {

	@Override
	public IStatus canPublish() {
		// TODO Auto-generated method stub
		return Status.CANCEL_STATUS;
	}

	@Override
	public boolean canPublishModule(IModule[] module) {
		return false;
	}

	@Override
	public void publishStart(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishFinish(IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int publishModule(int kind, int deltaKind, IModule[] module, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void publishServer(int kind, IProgressMonitor monitor) throws CoreException {
		// TODO Auto-generated method stub
		
	}
	

}
