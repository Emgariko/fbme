package org.fbme.lib.iec61499.declarations;

import org.fbme.lib.iec61499.Identifier;
import org.fbme.lib.iec61499.fbnetwork.FunctionBlockDeclarationBase;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface SocketDeclaration extends FunctionBlockDeclarationBase {

    void setAdapterType(@NotNull Identifier identifier);

    void setAdapterType(@NotNull ParameterDeclaration parameter);

    List<ParameterAssignment> getParameters();
}
