package org.jboss.tools.windup.server.ui.internal;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;
import org.jboss.tools.windup.runtime.WindupRuntimePlugin;
import org.jboss.tools.windup.runtime.server.core.internal.WindupServer;

public class WindupServerWizardFragment extends WizardFragment {

	public WindupServerWizardFragment() {
		// TODO Auto-generated constructor stub
	}

	private IWizardHandle handle;
	private String homeDir;
	private Text homeText;
	private Button browseButton;
	
	
	@Override
	public boolean hasComposite() {
		return true;
	}
	

	@Override
	public boolean isComplete() {
		// Only one instance created per workspace, so we need to workaround this
		boolean b = browseButton != null && !browseButton.isDisposed() && findError() == null && super.isComplete();
		return b;
	}
	
	public ImageDescriptor getImageDescriptor() {
		return null; // TODO
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		this.handle = handle;
		Composite main = new Composite(parent, SWT.NONE);
		handle.setTitle("Windup Server");
		handle.setDescription("A server that launches windup for on-the-fly migration advice.");
		handle.setImageDescriptor(getImageDescriptor());
		main.setLayout(new GridLayout(3, false));
		
		
		Label l = new Label(main, SWT.NONE);
		l.setText("Windup Home: ");
		GridData homeData = new GridData();
		homeData.grabExcessHorizontalSpace = true;
		homeData.horizontalAlignment = SWT.FILL;
		
		GridData homeTextData = new GridData();
		homeTextData.grabExcessHorizontalSpace = true;
		homeTextData.horizontalAlignment = SWT.FILL;
		homeTextData.widthHint = 200;
		homeText = new Text(main, SWT.BORDER);
		homeText.setLayoutData(homeTextData);
		browseButton = new Button(main, SWT.PUSH);
		browseButton.setText("Browse...");
		

		homeText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				homeDir = homeText.getText();
				validate();
			}
		});
		browseButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				widgetSelected(e);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				browseHomeDirClicked();
				validate();
			}

		});
		
		IServer s = (IServer)getTaskModel().getObject(TaskModel.TASK_SERVER);
		if( s != null ) {
			homeDir = s.getAttribute(WindupServer.PROP_WINDUP_HOME, WindupRuntimePlugin.findWindupHome().getAbsolutePath());
		}
		
		if( homeDir != null ) {
			homeText.setText(homeDir);
		}
		
		String err = findError();
		setComplete(err == null);
		handle.update();
		main.pack(true);
		return main;
	}
	
	private void validate() {
		String err = findError();
		if( err != null ) {
			handle.setMessage(err, IMessageProvider.ERROR);
			setComplete(false);
		} else {
			setComplete(true);
			String warn = findWarning();
			if( warn != null ) {
				handle.setMessage(warn, IMessageProvider.WARNING);
			} else {
				handle.setMessage(null, IMessageProvider.NONE);
			}
		}
		handle.update();
	}

	private String findWarning() {
		return null;
	}
	
	private String findError() {
		if( homeDir == null || !(new File(homeDir)).exists()) {
			return "The selected folder does not exist.";
		}
		// TODO make sure it's a windup installation we can launch without terminating
		return null;
	}
	
	protected void browseHomeDirClicked() {
		File file = homeDir == null ? null : new File(homeDir);
		if (file != null && !file.exists()) {
			file = null;
		}

		File directory = getDirectory(file, homeText.getShell());
		if (directory != null) {
			homeDir = directory.getAbsolutePath();
			homeText.setText(homeDir);
		}
	}
	

	protected static File getDirectory(File startingDirectory, Shell shell) {
		DirectoryDialog fileDialog = new DirectoryDialog(shell, SWT.OPEN);
		if (startingDirectory != null) {
			fileDialog.setFilterPath(startingDirectory.getPath());
		}

		String dir = fileDialog.open();
		if (dir != null) {
			dir = dir.trim();
			if (dir.length() > 0) {
				return new File(dir);
			}
		}
		return null;
	}


	protected IServer getServerFromTaskModel() {
		IServer wc = (IServer) getTaskModel().getObject(TaskModel.TASK_SERVER);
		return wc;
	}
	

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		exit();
		IServer s = getServerFromTaskModel();
		if( s instanceof IServerWorkingCopy ) {
			IServerWorkingCopy swc = (IServerWorkingCopy) s;
			swc.setAttribute(WindupServer.PROP_WINDUP_HOME, homeDir);
		}
	}
	
}
