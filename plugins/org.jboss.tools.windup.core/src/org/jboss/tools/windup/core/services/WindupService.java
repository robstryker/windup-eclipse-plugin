/*******************************************************************************
 * Copyright (c) 2016 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.windup.core.services;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.windup.core.IWindupListener;
import org.jboss.tools.windup.core.WindupCorePlugin;
import org.jboss.tools.windup.core.internal.Messages;
import org.jboss.tools.windup.core.utils.FileUtils;
import org.jboss.tools.windup.model.domain.ModelService;
import org.jboss.tools.windup.model.domain.WorkspaceResourceUtils;
import org.jboss.tools.windup.runtime.WindupExecutioner;
import org.jboss.tools.windup.runtime.WindupRuntimePlugin;
import org.jboss.tools.windup.runtime.model.IWindupConstants;
import org.jboss.tools.windup.runtime.model.OptionDescription;
import org.jboss.tools.windup.runtime.model.OptionFacades;
import org.jboss.tools.windup.runtime.model.OptionFacades.OptionTypeFacade;
import org.jboss.tools.windup.runtime.model.OptionFacades.OptionsFacadeManager;
import org.jboss.tools.windup.windup.ConfigurationElement;
import org.jboss.tools.windup.windup.Input;
import org.jboss.tools.windup.windup.MigrationPath;
import org.jboss.tools.windup.windup.Pair;
import org.jboss.windup.tooling.ExecutionResults;
import org.jboss.windup.tooling.data.Classification;
import org.jboss.windup.tooling.data.Hint;
import org.jboss.windup.tooling.data.ReportLink;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * <p>
 * Service used to perform Windup operations inside of Eclipse.
 * </p>
 */
@Singleton
@Creatable
public class WindupService
{
    private List<IWindupListener> windupListeners = new ArrayList<IWindupListener>();
    private Map<IProject, ExecutionResults> projectToResults = new HashMap<>();
    
    @Inject private ModelService modelService;

    /**
     * Returns an {@link Iterable} with all {@link Hint}s returned by Windup during the last run.
     * 
     * NOTE: This will return an empty list if Windup has not yet been run on this project.
     */
    public Iterable<Hint> getHints(IResource resource)
    {
        ExecutionResults results = projectToResults.get(resource.getProject());
        if (results == null)
            return Collections.emptyList();
        else
            return results.getHints();
    }

    /**
     * Returns an {@link Iterable} with all {@link Classification}s returned by Windup during the last run.
     * 
     * NOTE: This will return an empty list if Windup has not yet been run on this project.
     */
    public Iterable<Classification> getClassifications(IResource resource, IProgressMonitor monitor)
    {
        ExecutionResults results = projectToResults.get(resource.getProject());
        if (results == null)
            return Collections.emptyList();
        else
            return results.getClassifications();
    }
    
    /**
     * <p>
     * Generate a Windup report for the given projects.
     * </p>
     * 
     * <p>
     * This can be a long running operation, it should be run in a Job.
     * </p>
     * 
     * @param projects Generate a Windup reports for these {@link IProject}s
     * @param monitor {@link IProgressMonitor} to report progress to
     */
    public IStatus generateGraph(IProject[] projects, IProgressMonitor monitor)
    {
        // protect against a null given for the progress monitor
        IProgressMonitor progress;
        if (monitor != null)
        {
            progress = monitor;
        }
        else
        {
            progress = new NullProgressMonitor();
        }

        // start the task
        progress.beginTask(Messages.generate_windup_reports, projects.length);
        IStatus status = null;

        try
        {
            // for each project generate a report
            for (IProject project : projects)
            {
                // if not canceled, continue generating reports for the given projects
                if (!progress.isCanceled())
                {
                    status = this.generateGraph(project, progress);

                    // if not an okay status stop generating reports
                    if (!status.equals(Status.OK_STATUS))
                    {
                        break;
                    }
                }
                else
                {
                    status = Status.CANCEL_STATUS;
                    break;
                }
            }
        }
        finally
        {
            progress.done();
        }

        return status;
    }
    
    public IStatus generateGraph(IProject project) {
    	return generateGraph(project, null);
    }
    
    public IStatus generateGraph(ConfigurationElement configuration, IProgressMonitor progress) {
    	progress.subTask(Messages.startingWindup);
    	modelService.synch(configuration);
    	IPath basePath = modelService.getGeneratedReportsBaseLocation(configuration);
    	File baseOutputDir = basePath.toFile();

    	progress.subTask(Messages.removing_old_report);
        FileUtils.delete(baseOutputDir, true);
        IStatus status = null;

        try {
        	for (Input input : configuration.getInputs()) {
            	
                Path projectPath = WorkspaceResourceUtils.computePath(input.getUri());
                progress.beginTask(NLS.bind(Messages.generate_windup_graph_for, input.getName()), IProgressMonitor.UNKNOWN);
                
                IPath outputPath = modelService.getGeneratedReportBaseLocation(configuration, input);
                
                HashMap<String, Object> options2 = new HashMap<String, Object>();
                fillOptionsMap(configuration, options2);
                Path windupHome = WindupRuntimePlugin.findWindupHome().toPath();
                ExecutionResults results = execute(windupHome, projectPath, outputPath.toFile().toPath(), options2, progress);
                
                modelService.populateConfiguration(configuration, input, results);
        	}
        	modelService.save();
            status = Status.OK_STATUS;
        }
        catch (Exception e)
        {
        	WindupCorePlugin.log(e);
            throw new RuntimeException(e);
        }
        finally
        {
            // mark the monitor as complete
            progress.done();
        }

        return status;
    }
    
    
    private ExecutionResults execute(Path windupHome, Path projectPath, Path outputPath, 
    		HashMap<String, Object> options, IProgressMonitor monitor) {
    	return WindupExecutioner.get().execute(windupHome, projectPath, outputPath, options, monitor);
    }
    
    private void fillOptionsMap(ConfigurationElement configuration, HashMap<String, Object> windupOptions) {
        windupOptions.put(IWindupConstants.SOURCE_MODE_OPTION, true);
        
        MigrationPath path = configuration.getMigrationPath();
        windupOptions.put( IWindupConstants.TARGET_OPTION, Lists.newArrayList(path.getTarget().getId()));
        if (path.getSource() != null) {
        	windupOptions.put(IWindupConstants.SOURCE_OPTION, Lists.newArrayList(path.getSource().getId()));
        }
        if (!configuration.isGenerateReport()) {
        	windupOptions.put(IWindupConstants.SKIP_REPORTS_RENDERING_OPTION, true);
        }
        if (!configuration.getPackages().isEmpty()) {
        	windupOptions.put(IWindupConstants.SCAN_PACKAGES_OPTION, configuration.getPackages());
        }
        if (!configuration.getUserRulesDirectories().isEmpty()) {
        	File file = new File(configuration.getUserRulesDirectories().get(0));
        	windupOptions.put(IWindupConstants.USER_RULES_DIRECTORY_OPTION, file);
        }
        
        OptionsFacadeManager facadeMgr = modelService.getOptionFacadeManager();
        
        Multimap<String, String> optionMap = ArrayListMultimap.create();
        for (Pair pair : configuration.getOptions()) {
        	String name = pair.getKey();
        	String value = pair.getValue();
        	optionMap.put(name, value);
        }
        
        for (String name : optionMap.keySet()) {
        	List<String> values = (List<String>)optionMap.get(name);
        	OptionDescription desc = facadeMgr.findOptionDescription(name); 
        	OptionTypeFacade<?> typeFacade = facadeMgr.getFacade(desc, OptionTypeFacade.class);
        	if (OptionFacades.isSingleValued(desc)) {
        		Object optionValue = typeFacade.newInstance(values.get(0));
        		windupOptions.put(name, optionValue);
        	}
        	else {
        		List<?> optionValues = typeFacade.newInstance(values);
        		windupOptions.put(name, optionValues);
        	}
        }
        
        
        
        // Now set tooling-specific k/v pairs
        windupOptions.put(IWindupConstants.T_IGNORE, "\\.class$");
    }
    
    /**
     * <p>
     * Generate a Windup report for the given project.
     * </p>
     * 
     * <p>
     * <b>NOTE:</b> This can be a long running operation, it should be run in a Job.
     * </p>
     * 
     * @param project Generate a Windup report for this project
     * @param monitor {@link IProgressMonitor} to report progress to
     */
    public IStatus generateGraph(IProject project, IProgressMonitor monitor)
    {
        // protect against a null given for the progress monitor
        IProgressMonitor progress;
        if (monitor != null)
        {
            progress = new SubProgressMonitor(monitor, 1);
        }
        else
        {
            progress = new NullProgressMonitor();
        }

        String projectName = project.getName();

        // start the task
        progress.beginTask(NLS.bind(Messages.generate_windup_graph_for, projectName), IProgressMonitor.UNKNOWN);
        IStatus status = null;

        try
        {
            File inputDir = project.getLocation().toFile();
            IPath outputPath = getProjectReportPath(project);
            File outputDir = outputPath.toFile();

            // clear out existing report
            progress.subTask(Messages.removing_old_report);
            FileUtils.delete(outputDir, true);

            // create new graph
            progress.subTask(NLS.bind(Messages.generate_windup_graph_for, projectName));
            
            Path wHome = WindupRuntimePlugin.findWindupHome().toPath();
            HashMap<String, Object> opts = new HashMap<String, Object>();
            opts.put(IWindupConstants.SOURCE_MODE_OPTION, true);
            opts.put(IWindupConstants.TARGET_OPTION, Lists.newArrayList("eap"));
            ExecutionResults results = execute(wHome, inputDir.toPath(), outputDir.toPath(), opts, monitor);
            projectToResults.put(project, results);

            // notify listeners that a graph was just generated
            this.notifyGraphGenerated(project);
            status = Status.OK_STATUS;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            // mark the monitor as complete
            progress.done();
        }

        return status;
    }

    /**
     * <p>
     * Determines if a report exists for the {@link IProject} containing the given {@link IResource}.
     * </p>
     * 
     * @param resource determine if a report exists for the {@link IProject} containing this {@link IResource}
     * 
     * @return <code>true</code> if a report exists for the {@link IProject} containing the given {@link IResource}, <code>false</code> otherwise.
     */
    public boolean reportExists(IResource resource)
    {
        IPath reportPath = getProjectReportPath(resource);
        File reportDir = new File(reportPath.toString());

        return reportDir.exists();
    }

    /**
     * <p>
     * Get the Windup report for the given resource.
     * </p>
     * 
     * @param resource get the location of the Windup report for this {@link IResource}
     * 
     * @return location of the Windup report for the given {@link IResource}
     */
    public IPath getReportLocation(IResource resource)
    {

        IPath projectReportPath = getProjectReportPath(resource);

        IPath reportPath = null;
        switch (resource.getType())
        {
        // if selected resource is a file get the file specific report page
        case IResource.FILE:
        {
            File resourceAsFile = resource.getLocation().toFile().getAbsoluteFile();
            ExecutionResults executionResults = projectToResults.get(resource.getProject());
            if (executionResults == null)
                break;
            for (ReportLink reportLink : executionResults.getReportLinks())
            {
                if (resourceAsFile.equals(reportLink.getInputFile()))
                {
                    String reportFile = reportLink.getReportFile();
                    Path projectPath = resource.getProject().getLocation().toFile().toPath();
                    Path reportFileRelativeToProject = projectPath.relativize(new File(reportFile).toPath());
                    IPath projectLocation = resource.getProject().getLocation();
                    reportPath = projectLocation.append(reportFileRelativeToProject.toString());
                    break;
                }
            }
            break;
        }

        /*
         * if selected resource is the project then get the Windup report home page for that project
         */
        case IResource.PROJECT:
        {
            reportPath = projectReportPath.append(ModelService.PROJECT_REPORT_HOME_PAGE);
            break;
        }

        default:
        {
            break;
        }
        }

        // determine if the report of the given file exists, if it doesn't return null
        if (reportPath != null)
        {
            File reportFile = new File(reportPath.toString());
            if (!reportFile.exists())
            {
                reportPath = null;
            }
        }

        return reportPath;
    }

    /**
     * <p>
     * Get the Windup report parent directory for the given resource.
     * </p>
     * 
     * @param resource get the location of the Windup report parent directory for this {@link IResource}
     * 
     * @return location of the Windup report parent directory for the given {@link IResource}
     */
    public IPath getReportParentDirectoryLocation(IResource resource)
    {
        return getProjectReportPath(resource);
    }

	/**
     * <p>
     * Registers a {@link IWindupListener}.
     * </p>
     * 
     * @param listener {@link IWindupListener} to register
     */
    public void addWindupListener(IWindupListener listener)
    {
        this.windupListeners.add(listener);
    }

    /**
     * <p>
     * Removes an already registered {@link IWindupListener}.
     * </p>
     * 
     * @param listener {@link IWindupListener} to unregister
     */
    public void removeWindupListener(IWindupListener listener)
    {
        this.windupListeners.remove(listener);
    }

    /**
     * <p>
     * Notifies all of the registered {@link IWindupListener} that a Windup report was just generated for the given {@link IProject}.
     * </p>
     * 
     * @param project Notify all registered {@link IWindupListener} that a Windup report was just generated for this {@link IProject}
     */
    private void notifyGraphGenerated(IProject project)
    {
        for (IWindupListener listener : WindupService.this.windupListeners)
        {
            listener.graphGenerated(project);
        }
    }

    /**
     * <p>
     * Get the location where the report should be stored for the {@link IProject} containing the given {@link IResource}
     * </p>
     * 
     * @param resource get the location where the report should be stored for the {@link IProject} containing this {@link IResource}
     * 
     * @return location where the report should be stored for the {@link IProject} containing the given {@link IResource}
     */
    private IPath getProjectReportPath(IResource resource)
    {
        return ModelService.reportsDir.append(resource.getProject().getName());
    }
    
}