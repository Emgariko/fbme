package org.fbme.ide.richediting.adapters.fbnetwork.layout;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.elk.alg.layered.LayeredLayoutProvider;
import org.eclipse.elk.alg.layered.options.LayeredMetaDataProvider;
import org.eclipse.elk.core.IGraphLayoutEngine;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutMetaDataService;
import org.eclipse.elk.core.util.NullElkProgressMonitor;
import org.eclipse.elk.graph.*;
import org.eclipse.elk.graph.util.ElkGraphUtil;
import org.fbme.ide.richediting.adapters.fbnetwork.FBConnectionCursor;
import org.fbme.ide.richediting.adapters.fbnetwork.FBConnectionPath;
import org.fbme.ide.richediting.viewmodel.*;
import org.fbme.lib.iec61499.fbnetwork.ConnectionPath;
import org.fbme.scenes.controllers.components.ComponentController;
import org.fbme.scenes.controllers.components.ComponentSynchronizer;
import org.fbme.scenes.controllers.components.ComponentsFacility;
import org.fbme.scenes.controllers.diagram.*;

import java.awt.*;
import java.util.List;
import java.util.*;

import static org.junit.Assert.assertTrue;

public class FBElkLayoutProvider {
    private static final Logger LOG = LogManager.getLogger(FBElkLayoutProvider.class);

    private final DiagramFacility<NetworkComponentView, NetworkPortView, NetworkConnectionView, Point> diagramFacility;
    private final ComponentsFacility<NetworkComponentView, Point> componentsFacility;
    private final ConnectionsFacility<NetworkComponentView, NetworkPortView, NetworkConnectionView, FBConnectionCursor, FBConnectionPath> connectionsFacility;
    private final DiagramController<NetworkComponentView, NetworkPortView, NetworkConnectionView> diagramController;
    private final ComponentSynchronizer<NetworkComponentView, Point> componentSynchronizer;
    private final ConnectionPathSyncronizer<NetworkConnectionView, FBConnectionPath> connectionSynchronizer;

    private final IGraphLayoutEngine layoutEngine = new LayeredLayoutProvider(); // use RecursiveGraphLayoutEngine() for recursive graph
    private final List<ILayoutMetaDataProvider> layoutProviders = Collections.singletonList(new LayeredMetaDataProvider());
    private final FBElkProperties layoutPropertiesProvider = new FBElkProperties();

    public FBElkLayoutProvider(
            DiagramFacility<NetworkComponentView, NetworkPortView, NetworkConnectionView, Point> diagramFacility,
            ComponentsFacility<NetworkComponentView, Point> componentsFacility,
            ConnectionsFacility<NetworkComponentView, NetworkPortView, NetworkConnectionView, FBConnectionCursor, FBConnectionPath> connectionsFacility
    ) {
        this.diagramFacility = diagramFacility;
        this.componentsFacility = componentsFacility;
        this.connectionsFacility = connectionsFacility;
        this.diagramController = diagramFacility.getDiagramController();
        this.componentSynchronizer = componentsFacility.getComponentSyncronizer();
        this.connectionSynchronizer = connectionsFacility.getConnectionSynchronizer();
    }

    public void relayout() {
        try {
            LayoutMetaDataService.getInstance().registerLayoutMetaDataProviders(layoutProviders.toArray(new ILayoutMetaDataProvider[0]));
            Map<NetworkComponentView, ElkNode> mapViewNode = new HashMap<>();
            Map<NetworkPortView, ElkPort> mapViewPort = new HashMap<>();
            ElkNode root = createElkGraph(mapViewNode, mapViewPort);
            layoutEngine.layout(root, new NullElkProgressMonitor());
            applyLayout(mapViewNode, mapViewPort);
        } catch (Throwable t) {
            LOG.error("error when relayout");
        }
    }

    private ElkNode createElkGraph(Map<NetworkComponentView, ElkNode> mapViewNode, Map<NetworkPortView, ElkPort> mapViewPort) {
        ElkNode node = ElkGraphUtil.createGraph();
        layoutPropertiesProvider.setRootProperties(node);

        processComponents(node, mapViewNode, mapViewPort);
        processConnections(node, mapViewPort);

        return node;
    }

    private void processConnections(ElkNode parent, Map<NetworkPortView, ElkPort> mapViewPort) {
        for (NetworkConnectionView connection : diagramController.getConnections()) {
            ElkPort sourceElkPort = mapViewPort.get(diagramController.getSource(connection));
            ElkPort targetElkPort = mapViewPort.get(diagramController.getTarget(connection));

            ElkEdge elkEdge = ElkGraphUtil.createEdge(parent);
            elkEdge.getSources().add(sourceElkPort);
            elkEdge.getTargets().add(targetElkPort);
        }
    }

    private void processComponents(ElkNode parent, Map<NetworkComponentView, ElkNode> mapViewNode, Map<NetworkPortView, ElkPort> mapViewPort) {
        for (NetworkComponentView component : diagramController.getComponents()) {
            ComponentController<Point> componentController = componentsFacility.getController(component);
            Point componentPosition = componentsFacility.getModelForm(component);
            Rectangle componentBounds = componentController.getBounds(componentPosition);

            ElkNode node = ElkGraphUtil.createNode(parent);
            node.setLocation(componentBounds.getX(), componentBounds.getY());
            node.setDimensions(componentBounds.getWidth(), componentBounds.getHeight());

            layoutPropertiesProvider.setNodeProperties(node);
            double mid = componentBounds.getX() + componentBounds.getWidth() / 2;

            for (NetworkPortView componentPort : diagramController.getPorts(component)) {
                PortController componentPortController = diagramController.getPortController(componentPort);
                Rectangle componentPortBounds = componentPortController.getBounds();

                ElkPort port = ElkGraphUtil.createPort(node);
                port.setLocation(componentPortBounds.getX(), componentPortBounds.getY());
                port.setDimensions(componentPortBounds.getWidth(), componentPortBounds.getHeight());

                if (componentPort instanceof InterfaceEndpointView) {
                    layoutPropertiesProvider.setPortProperties(port, ((InterfaceEndpointView) componentPort).isSource());
                } else if (componentPort instanceof FunctionBlockPortView) {
                    layoutPropertiesProvider.setPortProperties(port, componentPortBounds.getX() > mid);
                }
                mapViewPort.put(componentPort, port);
            }
            mapViewNode.put(component, node);
        }
    }

    private void applyLayout(Map<NetworkComponentView, ElkNode> mapViewNode, Map<NetworkPortView, ElkPort> mapViewPort) {
        applyNodeLayout(mapViewNode);
        applyEdgeLayout(mapViewPort);
    }

    private void applyNodeLayout(Map<NetworkComponentView, ElkNode> mapViewNode) {
        for (NetworkComponentView component : diagramController.getComponents()) {
            ElkNode node = mapViewNode.get(component);
            componentSynchronizer.setForm(component, new Point((int) node.getX(), (int) node.getY()));
        }
    }

    private void applyEdgeLayout(Map<NetworkPortView, ElkPort> mapViewPort) {
        for (NetworkConnectionView connection : diagramController.getConnections()) {
            ElkPort sourceElkPort = mapViewPort.get(diagramController.getSource(connection));
            for (ElkEdge incomingEdge : sourceElkPort.getOutgoingEdges()) {
                List<Point> points = getPointsFromEdge(incomingEdge);
                assertTrue(points.size() >= 2 && points.size() % 2 == 0);
                FBConnectionPath connectionPath = getConnectionPathFromPoints(points);
                connectionSynchronizer.setPath(connection, connectionPath);
            }
        }
    }

    private ArrayList<Point> getPointsFromEdge(ElkEdge elkEdge) {
        ArrayList<Point> points = new ArrayList<>();
        for (ElkEdgeSection section : elkEdge.getSections()) {
            points.add(new Point((int) section.getStartX(), (int) section.getStartY()));
            for (ElkBendPoint bendPoint : section.getBendPoints()) {
                points.add(new Point((int) bendPoint.getX(), (int) bendPoint.getY()));
            }
            points.add(new Point((int) section.getEndX(), (int) section.getEndY()));
        }
        return points;
    }

    private FBConnectionPath getConnectionPathFromPoints(List<Point> points) {
        assertTrue(points.size() >= 2 && points.size() % 2 == 0);
        int x1 = 0, y = 0, x2 = 0;
        ConnectionPath.Kind pathKind = ConnectionPath.Kind.Straight;
        int count = points.size();
        if (count > 2) {
            x1 = points.get(1).x;
            pathKind = ConnectionPath.Kind.TwoAngles;
            if (count > 4) {
                x2 = points.get(count - 2).x;
                y = points.get(2).y;
                pathKind = ConnectionPath.Kind.FourAngles;
            }
        }
        return new FBConnectionPath(points.get(0), points.get(count - 1), pathKind, x1, y, x2);
    }
}
