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
package org.eclipse.wst.xsd.ui.internal.common.commands;

import org.eclipse.gef.commands.Command;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xsd.ui.internal.refactor.PerformUnsavedRefactoringOperation;
import org.eclipse.wst.xsd.ui.internal.refactor.RefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.XMLRefactoringComponent;
import org.eclipse.wst.xsd.ui.internal.refactor.rename.RenameComponentProcessor;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

public class UpdateNameCommand extends Command
{
//  private String oldName;
  private String newName;
  private XSDNamedComponent component;

  public UpdateNameCommand(String label, XSDNamedComponent component, String newName)
  {
    super(label);

    if (component instanceof XSDComplexTypeDefinition && component.getName() == null && component.eContainer() instanceof XSDNamedComponent && ((XSDNamedComponent) component.eContainer()).getName() != null)
    {
      component = (XSDNamedComponent) component.eContainer();
    }

    this.component = component;
    this.newName = newName;
//    this.oldName = component.getName();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.eclipse.gef.commands.Command#execute()
   */
  public void execute()
  {
    renameComponent(newName);
  }
  
  /**
   * Performs a rename refactoring to rename the component and all the
   * references to it within the current schema.
   * 
   * @param newName the new component name.
   */
  private void renameComponent(String newName)
  {
    // this is a 'globally' defined component (e.g. global element)    
    if (component.eContainer() instanceof XSDSchema)
    {  
      RefactoringComponent refactoringComponent = new XMLRefactoringComponent(
          component,
          (IDOMElement)component.getElement(), 
          component.getName(),
          component.getTargetNamespace());

      RenameComponentProcessor processor = new RenameComponentProcessor(refactoringComponent, newName, true);    
      RenameRefactoring refactoring = new RenameRefactoring(processor);
      PerformUnsavedRefactoringOperation operation = new PerformUnsavedRefactoringOperation(refactoring);
      operation.run(null);
    } 
    else
    {
      // this is a 'locally' defined component (e.g. local element)
      // no need to call refactoring since this component can't be referenced      
      component.setName(newName);
    }  
  }
}
