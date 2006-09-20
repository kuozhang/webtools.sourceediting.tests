/*******************************************************************************
 * Copyright (c) 2001, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.xsd.ui.internal.common.properties.sections;

import java.util.List;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.PageBook;
import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.INodeNotifier;
import org.eclipse.wst.xsd.ui.internal.common.commands.AddExtensionCommand;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.AddExtensionsComponentDialog;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.DOMExtensionDetailsContentProvider;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.DOMExtensionItemMenuListener;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionDetailsViewer;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.ExtensionsSchemasRegistry;
import org.eclipse.wst.xsd.ui.internal.common.properties.sections.appinfo.SpecificationForExtensionsSchema;
import org.eclipse.wst.xsd.ui.internal.common.util.Messages;
import org.eclipse.wst.xsd.ui.internal.editor.XSDEditorCSHelpIds;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public abstract class AbstractExtensionsSection extends AbstractSection
{
  protected ExtensionDetailsViewer extensionDetailsViewer;
  protected TreeViewer extensionTreeViewer;
  protected ITreeContentProvider extensionTreeContentProvider;
  protected ILabelProvider extensionTreeLabelProvider;
  protected Label contentLabel;
  protected ISelectionChangedListener elementSelectionChangedListener;
  protected IDocumentChangedNotifier documentChangeNotifier;
  protected INodeAdapter internalNodeAdapter = new InternalNodeAdapter();

  private Composite page, pageBook2;
  protected Button addButton, removeButton;
  private PageBook pageBook;
  private Object prevInput;
  private SpecificationForExtensionsSchema prevCategory;

  /**
   * 
   */
  public AbstractExtensionsSection()
  {
    super();    
  }
  
  class InternalNodeAdapter implements INodeAdapter
  {

    public boolean isAdapterForType(Object type)
    {
      // this method should never be called
      // we don't use objects of this class as 'standard' adapters
      return true;
    }

    public void notifyChanged(INodeNotifier notifier, int eventType, Object changedFeature, Object oldValue, Object newValue, int pos)
    {     
      boolean isRoot = false;
      if (notifier instanceof Element)
      {
        if (isTreeViewerInputElement((Element)notifier))// TODO
        {
          isRoot = true;
          extensionTreeViewer.refresh(extensionTreeViewer.getInput());
        }  
      }
      if (!isRoot)
      {  
        extensionTreeViewer.refresh(notifier);
        if ( newValue instanceof Element)
        {
          extensionTreeViewer.expandToLevel(notifier, 1);
          extensionTreeViewer.setSelection(new StructuredSelection(newValue));
        }
      }  
    }    
  }
  
  protected boolean isTreeViewerInputElement(Element element)
  {
    return false;
  }

  public void createContents(Composite parent)
  {
    // TODO (cs) add assertion
    if (extensionTreeContentProvider == null)
       return;
    
    IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
    documentChangeNotifier = (IDocumentChangedNotifier)editor.getAdapter(IDocumentChangedNotifier.class);
    
    if (documentChangeNotifier != null)
    {
      documentChangeNotifier.addListener(internalNodeAdapter);
    }  
    
    composite = getWidgetFactory().createFlatFormComposite(parent);

    GridLayout gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    composite.setLayout(gridLayout);

    GridData gridData = new GridData();

    page = getWidgetFactory().createComposite(composite);
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    page.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    page.setLayoutData(gridData);

    pageBook = new PageBook(page, SWT.FLAT);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    pageBook.setLayoutData(gridData);

    pageBook2 = getWidgetFactory().createComposite(pageBook, SWT.FLAT);

    gridLayout = new GridLayout();
    gridLayout.marginHeight = 2;
    gridLayout.marginWidth = 2;
    gridLayout.numColumns = 1;
    pageBook2.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    pageBook2.setLayoutData(gridData);

    SashForm sashForm = new SashForm(pageBook2, SWT.HORIZONTAL);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    sashForm.setLayoutData(gridData);
    sashForm.setForeground(ColorConstants.white);
    sashForm.setBackground(ColorConstants.white);
    Control[] children = sashForm.getChildren();
    for (int i = 0; i < children.length; i++)
    {
      children[i].setVisible(false);
    }
    Composite leftContent = getWidgetFactory().createComposite(sashForm, SWT.FLAT);
    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    leftContent.setLayout(gridLayout);

    Section section = getWidgetFactory().createSection(leftContent, SWT.FLAT | ExpandableComposite.TITLE_BAR);
    section.setText(Messages._UI_LABEL_EXTENSIONS);
    section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Composite tableAndButtonComposite = getWidgetFactory().createComposite(leftContent, SWT.FLAT);
    tableAndButtonComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
    gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    tableAndButtonComposite.setLayout(gridLayout);    
    
    extensionTreeViewer = new TreeViewer(tableAndButtonComposite, SWT.FLAT | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.LINE_SOLID);
    MenuManager menuManager = new MenuManager();    
    extensionTreeViewer.getTree().setMenu(menuManager.createContextMenu(extensionTreeViewer.getTree()));
    menuManager.addMenuListener(new DOMExtensionItemMenuListener(extensionTreeViewer));
    
    gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    extensionTreeViewer.getTree().setLayout(gridLayout);
    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
   
    extensionTreeViewer.getTree().setLayoutData(gridData);
    extensionTreeViewer.setContentProvider(extensionTreeContentProvider);
    extensionTreeViewer.setLabelProvider(extensionTreeLabelProvider);
    elementSelectionChangedListener = new ElementSelectionChangedListener();
    extensionTreeViewer.addSelectionChangedListener(elementSelectionChangedListener);
    extensionTreeViewer.getTree().addMouseTrackListener(new MouseTrackAdapter()
    {
      public void mouseHover(org.eclipse.swt.events.MouseEvent e)
      {
        ISelection selection = extensionTreeViewer.getSelection();
        if (selection instanceof StructuredSelection)
        {
          Object obj = ((StructuredSelection) selection).getFirstElement();
          if (obj instanceof Element)
          {
            Element element = (Element) obj;
            ExtensionsSchemasRegistry registry = getExtensionsSchemasRegistry();
            // ApplicationSpecificSchemaProperties[] properties =
            // registry.getAllApplicationSpecificSchemaProperties();
            // ApplicationSpecificSchemaProperties[] properties =
            // (ApplicationSpecificSchemaProperties[])
            // registry.getAllApplicationSpecificSchemaProperties().toArray(new
            // ApplicationSpecificSchemaProperties[0]);
            List properties = registry.getAllExtensionsSchemasContribution();

            int length = properties.size();
            for (int i = 0; i < length; i++)
            {
              SpecificationForExtensionsSchema current = (SpecificationForExtensionsSchema) properties.get(i);
              if (current.getNamespaceURI().equals(element.getNamespaceURI()))
              {
                extensionTreeViewer.getTree().setToolTipText(current.getDescription());
                break;
              }
            }
          }
        }
      }

    });
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(extensionTreeViewer.getControl(),
    		XSDEditorCSHelpIds.EXTENSIONS_TAB__EXTENSIONS); 
    
    Composite buttonComposite = getWidgetFactory().createComposite(tableAndButtonComposite, SWT.FLAT);
    //ColumnLayout columnLayout = new ColumnLayout();
    //buttonComposite.setLayout(columnLayout);
    buttonComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.numColumns = 1;
    gridLayout.makeColumnsEqualWidth = true;
    buttonComposite.setLayout(gridLayout);
    
    addButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_ADD_WITH_DOTS, SWT.FLAT);   
    addButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    addButton.addSelectionListener(this);
    addButton.setToolTipText(Messages._UI_ACTION_ADD_EXTENSION_COMPONENT);
    //addButton.setLayoutData(new ColumnLayoutData(ColumnLayoutData.FILL));
    addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(addButton,
    		XSDEditorCSHelpIds.EXTENSIONS_TAB__ADD);     
    
    removeButton = getWidgetFactory().createButton(buttonComposite, Messages._UI_ACTION_DELETE, SWT.FLAT);
    removeButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    removeButton.addSelectionListener(this);
    removeButton.setToolTipText(Messages._UI_ACTION_DELETE_EXTENSION_COMPONENT);
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(removeButton,
    		XSDEditorCSHelpIds.EXTENSIONS_TAB__DELETE);   
    
    //removeButton.setLayoutData(new ColumnLayoutData(ColumnLayoutData.FILL));

    // TODO (cs) uncomment the up/down button when we have time to implement
    //
    //Button up = getWidgetFactory().createButton(buttonComposite, Messages._UI_LABEL_UP, SWT.FLAT);
    //up.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    
    //Button down = getWidgetFactory().createButton(buttonComposite, Messages._UI_LABEL_DOWN, SWT.FLAT);
    //down.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    Composite rightContent = getWidgetFactory().createComposite(sashForm, SWT.FLAT);
    Section section2 = getWidgetFactory().createSection(rightContent, SWT.FLAT | ExpandableComposite.TITLE_BAR);
    section2.setText(Messages._UI_LABEL_EXTENSION_DETAILS);
    section2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    //contentLabel = getWidgetFactory().createLabel(rightContent, "Content");

    Composite testComp = getWidgetFactory().createComposite(rightContent, SWT.FLAT);

    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginBottom = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginRight = 0;
    gridLayout.numColumns = 1;
    gridLayout.marginHeight = 3;
    gridLayout.marginWidth = 3;
    rightContent.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    rightContent.setLayoutData(gridData);

    gridLayout = new GridLayout();
    gridLayout.marginTop = 0;
    gridLayout.marginLeft = 0;
    gridLayout.marginRight = 0;
    gridLayout.marginBottom = 0;
    gridLayout.marginHeight = 3;
    gridLayout.marginWidth = 3;
    gridLayout.numColumns = 2;
    testComp.setLayout(gridLayout);

    gridData = new GridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    gridData.horizontalAlignment = GridData.FILL;
    testComp.setLayoutData(gridData);

    createElementContentWidget(testComp);

    int[] weights = { 50, 50 };
    sashForm.setWeights(weights);

    pageBook.showPage(pageBook2);
  }

  protected void createElementContentWidget(Composite parent)
  {
    extensionDetailsViewer = new ExtensionDetailsViewer(parent, getWidgetFactory());
    extensionDetailsViewer.setContentProvider(new DOMExtensionDetailsContentProvider());    
    extensionDetailsViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    
    PlatformUI.getWorkbench().getHelpSystem().setHelp(extensionDetailsViewer.getControl(),
    		XSDEditorCSHelpIds.EXTENSIONS_TAB__EXTENSIONS_DETAILS);   
  }
  

  /*
   * @see org.eclipse.wst.common.ui.properties.internal.provisional.view.ITabbedPropertySection#refresh()
   */
  public void refresh()
  {
    setListenerEnabled(false);
    if (input != null)
    {
      if ( prevInput == input)
    	  return;
      else
    	  prevInput = input;
      
      Tree tree = extensionTreeViewer.getTree();
      extensionDetailsViewer.setInput(null);
      tree.removeAll();
      
      extensionTreeViewer.setInput(input);
      if (tree.getSelectionCount() == 0 && tree.getItemCount() > 0)
      {       
        TreeItem treeItem = tree.getItem(0);     
        extensionTreeViewer.setSelection(new StructuredSelection(treeItem.getData()));
      }
      removeButton.setEnabled(tree.getSelectionCount() > 0);      
    }
    setListenerEnabled(true);
  }

  public Composite getPage()
  {
    return page;
  }

  protected abstract AddExtensionCommand getAddExtensionCommand(Object o);
  protected abstract Command getRemoveExtensionCommand(Object o);  
  protected abstract ExtensionsSchemasRegistry getExtensionsSchemasRegistry();
  
  protected AddExtensionsComponentDialog createAddExtensionsComponentDialog()
  {
    return new AddExtensionsComponentDialog(composite.getShell(), getExtensionsSchemasRegistry());
  }
    
  public void widgetSelected(SelectionEvent event)
  {
    if (event.widget == addButton)
    {
      ExtensionsSchemasRegistry registry = getExtensionsSchemasRegistry();
      AddExtensionsComponentDialog dialog = createAddExtensionsComponentDialog();

      List properties = registry.getAllExtensionsSchemasContribution();

      dialog.setInput(properties);
      dialog.setBlockOnOpen(true);
      dialog.setPrefStore( getPrefStore() );
      
      if (prevCategory != null)
    	  dialog.setInitialCategorySelection(prevCategory);

      if (dialog.open() == Window.OK)
      {
        Object newSelection = null;          
        Object[] result = dialog.getResult();      
        if (result != null)
        {
          SpecificationForExtensionsSchema extensionsSchemaSpec = (SpecificationForExtensionsSchema) result[1];
          AddExtensionCommand addExtensionCommand = getAddExtensionCommand(result[0]);
          if (addExtensionCommand != null)
          {  
            addExtensionCommand.setSchemaProperties(extensionsSchemaSpec);
            if (getCommandStack() != null)
            {
              getCommandStack().execute(addExtensionCommand);
              newSelection = addExtensionCommand.getNewObject();
            }
          }
        }
        //refresh();
        if (newSelection != null)
        {
          extensionTreeViewer.setSelection(new StructuredSelection(newSelection));
        }
      }

      prevCategory = dialog.getSelectedCategory();
    }
    else if (event.widget == removeButton)
    {
      ISelection selection = extensionTreeViewer.getSelection();
      
      if (selection instanceof StructuredSelection)
      {
        Object o = ((StructuredSelection) selection).getFirstElement();
        Command command = getRemoveExtensionCommand(o);            
        if (getCommandStack() != null)
        {
          getCommandStack().execute(command);
        }
      }
    }
    else if (event.widget == extensionTreeViewer.getTree())
    {

    }
  }

  // TODO make this one an abstract method. XSDEditor and WSDLEditor should return
  // diferent IpreferenceStore objects
  protected IPreferenceStore getPrefStore() {
	return null;
  }

  public void widgetDefaultSelected(SelectionEvent event)
  {

  }

  public boolean shouldUseExtraSpace()
  {
    return true;
  }

  public void dispose()
  {
    if (documentChangeNotifier != null)
      documentChangeNotifier.removeListener(internalNodeAdapter);
  }
 

  Node selectedNode;

  class ElementSelectionChangedListener implements ISelectionChangedListener
  {
    public void selectionChanged(SelectionChangedEvent event)
    {   
      boolean isDeleteEnabled = false;
      ISelection selection = event.getSelection();
      if (selection instanceof StructuredSelection)
      {
        StructuredSelection structuredSelection = (StructuredSelection)selection;
        if (structuredSelection.size() > 0)
        {  
          Object obj = structuredSelection.getFirstElement();
          if (obj instanceof Node)
          {
            selectedNode = (Node) obj;
            extensionDetailsViewer.setInput(obj);
            isDeleteEnabled = true;         
          }
        }  
        else
        {
          // if nothing is selected then don't show any details
          //
          extensionDetailsViewer.setInput(null);
        }  
      }
      removeButton.setEnabled(isDeleteEnabled);
    }
  }

  public ITreeContentProvider getExtensionTreeContentProvider()
  {
    return extensionTreeContentProvider;
  }

  public void setExtensionTreeContentProvider(ITreeContentProvider extensionTreeContentProvider)
  {
    this.extensionTreeContentProvider = extensionTreeContentProvider;
  }

  public ILabelProvider getExtensionTreeLabelProvider()
  {
    return extensionTreeLabelProvider;
  }

  public void setExtensionTreeLabelProvider(ILabelProvider extensionTreeLabelProvider)
  {
    this.extensionTreeLabelProvider = extensionTreeLabelProvider;
  }
}