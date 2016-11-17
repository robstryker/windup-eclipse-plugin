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
package org.jboss.tools.windup.ui.internal.explorer;

 import org.eclipse.core.resources.IResource;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.windup.runtime.model.IWindupConstants;
import org.jboss.tools.windup.ui.internal.Messages;
import org.jboss.tools.windup.windup.Hint;
import org.jboss.tools.windup.windup.QuickFix;

/**
 * Diff dialog for previewing Windup quick fixes.
 */
public class QuickFixDiffDialog extends DiffDialog {
	
	private TableViewer table;
	private Hint hint;
	private QuickFix quickFix;
	
	public QuickFixDiffDialog(Shell shell, IResource left, IResource right, Hint hint) {
		super(shell, left, right);
		this.hint = hint; 
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		if (hint.getQuickFixes().size() == 1) {
			this.quickFix = hint.getQuickFixes().get(0);
			return super.createDialogArea(parent);
		}
		Composite container = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).applyTo(container);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		table = new TableViewer(container, SWT.BORDER|SWT.FULL_SELECTION|SWT.H_SCROLL|SWT.V_SCROLL);
		buildColumns();
		table.getTable().setHeaderVisible(true);
		table.getTable().setLinesVisible(true);
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setInput(hint.getQuickFixes());
		GridDataFactory.fillDefaults().grab(true, false).hint(SWT.DEFAULT, 100).applyTo(table.getTable());
		table.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!event.getSelection().isEmpty()) {
					QuickFixDiffDialog.this.quickFix = (QuickFix)((StructuredSelection)event.getSelection()).getFirstElement();
					QuickFixDiffDialog.this.right = QuickFixUtil.getQuickFixedResource(left, quickFix, hint);
					loadPreview(left, right);
				}
			}
		});
		Control control = super.doCreateDialogArea(container);
		table.setSelection(new StructuredSelection(hint.getQuickFixes().get(0)));			
		return control;
	}
	
	public QuickFix getQuickFix() {
		return this.quickFix;
	}
	
	private void buildColumns() {
		// Type Column
		TableViewerColumn column = new TableViewerColumn(table, SWT.NONE);
		column.getColumn().setWidth(200);
		column.getColumn().setText(Messages.ComparePreviewer_quickFixType);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QuickFix fix = (QuickFix)element;
				return fix.getQuickFixType();
			}
		});
		// Text Column
		column = new TableViewerColumn(table, SWT.NONE);
		column.getColumn().setWidth(500);
		column.getColumn().setText(Messages.ComparePreviewer_quickFixText);
		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				QuickFix fix = (QuickFix)element;
				String label = "";
				if (IWindupConstants.QUICKFIX_REPLACE.toString().equals(fix.getQuickFixType())) {
					label = fix.getReplacementString();
				}
				else if (IWindupConstants.QUICKFIX_DELETE_LINE.toString().equals(fix.getQuickFixType())) {
					label = Messages.ComparePreviewer_quickFixNoText;
				}
				else if (IWindupConstants.QUICKFIX_INSERT_LINE.toString().equals(fix.getQuickFixType())) {
					label = fix.getNewLine();
				}
				return label;
			}
		});
	}
}
