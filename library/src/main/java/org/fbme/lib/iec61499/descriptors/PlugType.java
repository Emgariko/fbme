package org.fbme.lib.iec61499.descriptors;


import org.fbme.lib.iec61499.declarations.AdapterTypeDeclaration;
import org.fbme.lib.iec61499.declarations.NamedDeclaration;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class PlugType implements FBTypeDescriptor {

    private final AdapterTypeDeclaration myDeclaration;

    public PlugType(AdapterTypeDeclaration declaration) {
        myDeclaration = declaration;
    }

    @Override
    public String getTypeName() {
        return myDeclaration.getName();
    }

    @Nullable
    @Override
    public NamedDeclaration getDeclaration() {
        return myDeclaration;
    }

    @Override
    public List<FBPortDescriptor> getEventInputPorts() {
        return FBTypeDescriptorUtils.getEventOutputPorts(myDeclaration);
    }

    @Override
    public List<FBPortDescriptor> getEventOutputPorts() {
        return FBTypeDescriptorUtils.getEventInputPorts(myDeclaration);
    }

    @Override
    public List<FBPortDescriptor> getDataInputPorts() {
        return FBTypeDescriptorUtils.getDataOutputPorts(myDeclaration);
    }

    @Override
    public List<FBPortDescriptor> getDataOutputPorts() {
        return FBTypeDescriptorUtils.getDataInputPorts(myDeclaration);
    }

    @Override
    public List<FBPortDescriptor> getSocketPorts() {
        return Collections.emptyList();
    }

    @Override
    public List<FBPortDescriptor> getPlugPorts() {
        return Collections.emptyList();
    }

    @Override
    public List<Integer> getAssociatedVariablesForInputEvent(int eventNumber) {
        return FBTypeDescriptorUtils.getAssociatedVariablesForOutputEvent(myDeclaration, eventNumber);
    }

    @Override
    public List<Integer> getAssociatedVariablesForOutputEvent(int eventNumber) {
        return FBTypeDescriptorUtils.getAssociatedVariablesForInputEvent(myDeclaration, eventNumber);
    }
}
