package org.fbme.ide.richediting.adapters.fbnetwork;

import jetbrains.mps.lang.smodel.generator.smodelAdapter.SNodeOperations;
import jetbrains.mps.nodeEditor.EditorSettings;
import jetbrains.mps.nodeEditor.MPSColors;
import jetbrains.mps.nodeEditor.cells.EditorCell;
import jetbrains.mps.nodeEditor.cells.EditorCell_Collection;
import jetbrains.mps.openapi.editor.EditorContext;
import jetbrains.mps.smodel.adapter.structure.MetaAdapterFactory;
import org.fbme.ide.richediting.adapters.fbnetwork.fb.DiagramColors;
import org.fbme.ide.richediting.viewmodel.NetworkConnectionView;
import org.fbme.lib.iec61499.fbnetwork.ConnectionPath;
import org.fbme.lib.iec61499.fbnetwork.EntryKind;
import org.fbme.scenes.controllers.LayoutUtil;
import org.fbme.scenes.controllers.diagram.ConnectionController;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.mps.openapi.language.SConcept;
import org.jetbrains.mps.openapi.model.SNode;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FBConnectionController implements ConnectionController<FBConnectionCursor, FBConnectionPath> {
    private static final int SELECTION_PADDING = 4;
    private static final int ENDPOINT_HOVER_LENGTH = FBConnectionUtils.ENDPOINTS_PADDING;
    private static final int ENDPOINTS_PADDING = FBConnectionUtils.ENDPOINTS_PADDING;

    private final EntryKind myKind;
    private final boolean myIsEditable;

    private final EditorCell_Collection myFakeCell;

    private Color myHighlightColor = null;

    public FBConnectionController(EditorContext context, NetworkConnectionView view) {
        myKind = view.getKind();
        myIsEditable = view.isEditable();
        SNode associatedNode = view.getAssociatedNode();
        myFakeCell = FakeCells.createCollection(context, associatedNode);
        Iterator<SNode> conncetionPaths = SNodeOperations.ofConcept(SNodeOperations.getChildren(associatedNode), CONCEPTS.ConnectionPath$IA).iterator();
        if (conncetionPaths.hasNext()) {
            myFakeCell.addEditorCell(FakeCells.create(context, conncetionPaths.next()));
        }
    }

    public Color getHighlightColor() {
        return myHighlightColor;
    }

    public void setHighlightColor(Color highlightColor) {
        myHighlightColor = highlightColor;
    }

    @Nullable
    @Override
    public EditorCell getConnectionCell() {
        return myFakeCell;
    }

    @Override
    public void paintConnection(FBConnectionPath path, FBConnectionCursor cursor, boolean selected, Graphics graphics) {
        graphics = graphics.create();
        FBConnectionPathPainter painter = new FBConnectionPathPainter(path, cursor, scale(ENDPOINT_HOVER_LENGTH));
        if (myHighlightColor != null) {
            FBConnectionPathPainter.setupHighlightPathPaint(graphics, scale(1));
            graphics.setColor(myHighlightColor);
            painter.paint(graphics, false);
        }
        graphics.setColor(DiagramColors.getColorFor(myKind, myIsEditable));
        if (selected) {
            FBConnectionPathPainter.setupSelectedPathPaint(graphics, scale(1));
        } else {
            FBConnectionPathPainter.setupRegularPathPaint(graphics, scale(1));
        }
        painter.paint(graphics, selected);
    }


    @Override
    public void paintTrace(FBConnectionPath path, Graphics graphics) {
        graphics.setColor(MPSColors.GRAY);
        FBConnectionPathPainter.setupShadowPathPaint(graphics, scale(1));
        new FBConnectionPathPainter(path, null, scale(ENDPOINT_HOVER_LENGTH)).paint(graphics, false);
    }

    @Nullable
    @Override
    public Function<Point, FBConnectionPath> getEdgeTransformation(FBConnectionPath path, int ex, int ey) {
        if (!myIsEditable) {
            return null;
        }

        List<Point> bendPoints = path.getBendPoints();
        for (int i = 1; i < bendPoints.size(); i++) {
            Point u = bendPoints.get(i - 1);
            Point v = bendPoints.get(i);
            boolean isHorizontal = i % 2 == 0;

            if (hasIntersection(ex, ey, u, v, isHorizontal)) {
                return getPathSectionTransformation(path, i);
            }
        }
        return null;
    }

    private boolean hasIntersection(int ex, int ey, Point u, Point v, boolean isHorizontal) {
        return isHorizontal ? hasHorizontalIntersection(ex, ey, u, v) : hasVerticalIntersection(ex, ey, u, v);
    }

    private boolean hasVerticalIntersection(int ex, int ey, Point u, Point v) {
        return Math.abs(ex - u.x) < scale(SELECTION_PADDING) && (Math.min(u.y, v.y) < ey && ey < Math.max(u.y, v.y));
    }

    private boolean hasHorizontalIntersection(int ex, int ey, Point u, Point v) {
        return Math.abs(ey - u.y) < scale(SELECTION_PADDING) && (Math.min(u.x, v.x) < ex && ex < Math.max(u.x, v.x));
    }

    private Function<Point, FBConnectionPath> getPathSectionTransformation(FBConnectionPath path, int index) {
        List<Point> bendPoints = path.getBendPoints();
        boolean isHorizontal = index % 2 == 0;

        return eventPosition -> {
            List<Point> newBendPoints = deepCopy(bendPoints);
            Point u = newBendPoints.get(index - 1);
            Point v = newBendPoints.get(index);
            if (isHorizontal) {
                u.y = v.y = eventPosition.y;
            } else {
                u.x = v.x = eventPosition.x;
            }
            magnetize(index, newBendPoints);

            return new FBConnectionPath(
                    path.getSourcePosition(),
                    path.getTargetPosition(),
                    newBendPoints
            );
        };
    }

    private FBConnectionPath magnetized(FBConnectionPath path) {
        List<Point> bendPoints = path.getBendPoints();

        if (bendPoints.isEmpty()) {
            return path;
        }

        if (bendPoints.size() == 2) {
            Point u = bendPoints.get(0);
            Point v = bendPoints.get(1);

            if (u.y == v.y) {
                return new FBConnectionPath(path.getSourcePosition(), path.getTargetPosition());
            }
        }

        List<Point> newBendPoints = deepCopy(bendPoints);

        for (int i = 1; i < newBendPoints.size(); i++) {
            magnetize(i, newBendPoints);
        }

        return new FBConnectionPath(
                path.getSourcePosition(),
                path.getTargetPosition(),
                newBendPoints
        );
    }

    private void magnetize(int index, List<Point> bendPoints) {
        boolean isHorizontal = index % 2 == 0;
        if (isHorizontal) {
            magnetizeHorizontal(index, bendPoints);
        } else {
            magnetizeVertical(index, bendPoints);
        }
    }

    private void magnetizeHorizontal(int index, List<Point> bendPoints) {
        Point u = bendPoints.get(index - 1);
        Point v = bendPoints.get(index);
        Point uPrev = bendPoints.get(index - 2);
        Point vNext = bendPoints.get(index + 1);
        if (Math.abs(vNext.y - v.y) < scale(SELECTION_PADDING)) {
            u.y = vNext.y;
            bendPoints.remove(index + 1);
            bendPoints.remove(index);
        }
        if (Math.abs(u.y - uPrev.y) < scale(SELECTION_PADDING)) {
            v.y = uPrev.y;
            bendPoints.remove(index - 1);
            bendPoints.remove(index - 2);
        }
    }

    private void magnetizeVertical(int index, List<Point> bendPoints) {
        Point u = bendPoints.get(index - 1);
        Point v = bendPoints.get(index);
        Point uPrev = index - 2 >= 0 ? bendPoints.get(index - 2) : null;
        Point vNext = index + 1 < bendPoints.size() ? bendPoints.get(index + 1) : null;
        if (vNext != null && Math.abs(vNext.x - v.x) < scale(SELECTION_PADDING)) {
            u.x = vNext.x;
            bendPoints.remove(index + 1);
            bendPoints.remove(index);
        }
        if (uPrev != null && Math.abs(u.x - uPrev.x) < scale(SELECTION_PADDING)) {
            v.x = uPrev.x;
            bendPoints.remove(index - 1);
            bendPoints.remove(index - 2);
        }
    }

    @NotNull
    public static List<Point> deepCopy(List<Point> bendPoints) {
        List<Point> newBendPoints = new ArrayList<>();
        for (Point bendPoint : bendPoints) {
            newBendPoints.add(new Point(bendPoint.x, bendPoint.y));
        }
        return newBendPoints;
    }

    @Override
    public boolean isSourceTransformableAt(FBConnectionPath path, int ex, int ey) {
        if (!myIsEditable) {
            return false;
        }
        return isOnSourceHoverArea(ex, ey, path);
    }

    @Override
    public boolean isTargetTransformableAt(FBConnectionPath path, int ex, int ey) {
        if (!myIsEditable) {
            return false;
        }
        return isOnTargetHoverArea(ex, ey, path);
    }

    @Nullable
    @Override
    public Function<Point, FBConnectionPath> getSourceTransformation(FBConnectionPath path) {
        if (!myIsEditable) {
            return null;
        }
        return getConnectionSourceTransformation(path);
    }

    @Nullable
    @Override
    public Function<Point, FBConnectionPath> getTargetTransformation(FBConnectionPath path) {
        if (!myIsEditable) {
            return null;
        }
        return getConnectionTargetTransformation(path);
    }

    @Nullable
    @Override
    public BiFunction<Point, Point, FBConnectionPath> getEndpointsTransformation(final FBConnectionPath path) {
        if (!myIsEditable) {
            return null;
        }

        List<Point> bendPoints = path.getBendPoints();

        return (sourcePosition, targetPosition) -> {
            Point originalSourcePosition = path.getSourcePosition();
            int dx = sourcePosition.x - originalSourcePosition.x;
            int dy = sourcePosition.y - originalSourcePosition.y;

            List<Point> newBendPoints = new ArrayList<>();

            for (Point bendPoint : bendPoints) {
                newBendPoints.add(new Point(bendPoint.x + dx, bendPoint.y + dy));
            }

            return new FBConnectionPath(sourcePosition, targetPosition, newBendPoints);
        };
    }

    @Override
    public boolean isSelectableAt(FBConnectionPath path, int ex, int ey) {
        Point s = path.getSourcePosition();
        Point t = path.getTargetPosition();

        List<Point> bendPoints = path.getBendPoints();

        if (bendPoints.isEmpty()) {
            return hasHorizontalIntersection(ex, ey, s, t);
        } else {
            if (hasHorizontalIntersection(ex, ey, s, bendPoints.get(0))) {
                return true;
            }
            for (int i = 1; i < bendPoints.size(); i++) {
                Point u = bendPoints.get(i - 1);
                Point v = bendPoints.get(i);
                boolean isHorizontal = (i % 2) == 0;

                if (hasIntersection(ex, ey, u, v, isHorizontal)) {
                    return true;
                }
            }
            return hasHorizontalIntersection(ex, ey, bendPoints.get(bendPoints.size() - 1), t);
        }
    }

    @Override
    public FBConnectionCursor getCursorAt(FBConnectionPath path, int ex, int ey) {
        if (!myIsEditable) {
            return null;
        }

        if (isOnSourceHoverArea(ex, ey, path)) {
            return FBConnectionCursor.SOURCE_ENDPOINT;
        } else if (isOnTargetHoverArea(ex, ey, path)) {
            return FBConnectionCursor.TARGET_ENDPOINT;
        }
        return null;
    }

    private boolean isOnTargetHoverArea(int ex, int ey, FBConnectionPath path) {
        Point s = path.getSourcePosition();
        Point t = path.getTargetPosition();

        List<Point> bendPoints = path.getBendPoints();

        return hasHorizontalIntersection(ex, ey, new Point(getTargetHover(s, t, bendPoints), t.y), t);
    }

    private boolean isOnSourceHoverArea(int ex, int ey, FBConnectionPath path) {
        Point s = path.getSourcePosition();
        Point t = path.getTargetPosition();

        List<Point> bendPoints = path.getBendPoints();

        return hasHorizontalIntersection(ex, ey, s, new Point(getSourceHover(s, t, bendPoints), s.y));
    }

    @Override
    public Rectangle getBounds(FBConnectionPath path) {
        Point s = path.getSourcePosition();
        Point t = path.getTargetPosition();

        int xmin = Math.min(s.x, t.x);
        int xmax = Math.max(s.x, t.x);
        int ymin = Math.min(s.y, t.y);
        int ymax = Math.max(s.y, t.y);

        List<Point> bendPoints = path.getBendPoints();

        for (Point bendPoint : bendPoints) {
            xmin = Math.min(xmin, bendPoint.x);
            xmax = Math.max(xmax, bendPoint.x);
            ymin = Math.min(ymin, bendPoint.y);
            ymax = Math.max(ymax, bendPoint.y);
        }
        return new Rectangle(xmin, ymin, xmax - xmin, ymax - ymin);
    }

    private Function<Point, FBConnectionPath> getConnectionSourceTransformation(FBConnectionPath originalPath) {
        final PathSourceChangeDiffCalculator calculator = new PathSourceChangeDiffCalculator(originalPath);
        return calculator::calculatePath;
    }

    private Function<Point, FBConnectionPath> getConnectionTargetTransformation(FBConnectionPath originalPath) {
        final PathTargetChangeDiffCalculator calculator = new PathTargetChangeDiffCalculator(originalPath);
        return calculator::calculatePath;
    }

    @Override
    public void updateCellWithPath(FBConnectionPath path) {
//        ConnectionController.super.updateCellWithPath(path);
    }

    @Override
    public void updateCellSelection(boolean selected) {
//        ConnectionController.super.updateCellSelection(selected);
    }

    private class PathTargetChangeDiffCalculator {
        private final FBConnectionPath myOriginalPath;

        private PathTargetChangeDiffCalculator(FBConnectionPath originalPath) {
            myOriginalPath = originalPath;
        }

        private FBConnectionPath calculatePath(Point newTarget) {
            Point s = myOriginalPath.getSourcePosition();
            Point t = myOriginalPath.getTargetPosition();

            int ntx = newTarget.x;
            int nty = newTarget.y;

            int x1 = myOriginalPath.getX1();
            int y = myOriginalPath.getY();
            int x2 = myOriginalPath.getX2();

            ConnectionPath.Kind kind = myOriginalPath.getPathKind();
            if (kind == ConnectionPath.Kind.Straight) {
                x1 = (t.x + s.x) / 2;
            }

            switch (kind) {
                case Straight:
                case TwoAngles:
                    if (ntx >= s.x + 2 * scale(ENDPOINTS_PADDING)) {
                        kind = ConnectionPath.Kind.TwoAngles;
                        if (t.x != s.x) {
                            x1 = s.x + (ntx - s.x) * (x1 - s.x) / (t.x - s.x);
                        } else {
                            x2 = (s.x + ntx) / 2;
                        }
                    } else {
                        kind = ConnectionPath.Kind.FourAngles;

                        x1 = s.x + scale(ENDPOINTS_PADDING);
                        x2 = ntx - scale(ENDPOINTS_PADDING);
                        y = (s.y + nty) / 2;
                        if (y >= s.y && y - scale(ENDPOINTS_PADDING) < s.y) {
                            y = s.y + scale(ENDPOINTS_PADDING);
                        } else if (y < s.y && y + scale(ENDPOINTS_PADDING) > s.y) {
                            y = s.y - scale(ENDPOINTS_PADDING);
                        }
                    }
                    break;
                case FourAngles:
                case MoreThanFour:
                    if (ntx >= x1 + t.x - x2) {
                        kind = ConnectionPath.Kind.TwoAngles;
                        if (t.x - x2 != x1 - s.x) {
                            x1 = s.x + (ntx - s.x) * (x1 - s.x) / (t.x - x2 + x1 - s.x);
                        } else {
                            x1 = ntx;
                        }
                    } else {
                        kind = ConnectionPath.Kind.FourAngles;
                        x2 = ntx - (t.x - x2);
                    }
                    break;
            }
            FBConnectionPath newConnectionPath = new FBConnectionPath(s, newTarget, kind, x1, y, x2);

            return magnetized(newConnectionPath);
        }
    }

    public class PathSourceChangeDiffCalculator {
        private final FBConnectionPath myOriginalPath;

        private PathSourceChangeDiffCalculator(FBConnectionPath originalPath) {
            myOriginalPath = originalPath;
        }

        private FBConnectionPath calculatePath(Point newSource) {
            Point s = myOriginalPath.getSourcePosition();
            Point t = myOriginalPath.getTargetPosition();

            int nsx = newSource.x;
            int nsy = newSource.y;

            int x1 = myOriginalPath.getX1();
            int y = myOriginalPath.getY();
            int x2 = myOriginalPath.getX2();

            ConnectionPath.Kind kind = myOriginalPath.getPathKind();

            if (kind == ConnectionPath.Kind.Straight) {
                x1 = (t.x + s.x) / 2;
            }

            switch (kind) {
                case Straight:
                case TwoAngles:
                    if (nsx <= t.x - 2 * scale(ENDPOINTS_PADDING)) {
                        kind = ConnectionPath.Kind.TwoAngles;
                        if (t.x != s.x) {
                            x1 = t.x - (t.x - nsx) * (t.x - x1) / (t.x - s.x);
                        } else {
                            x1 = (t.x + nsx) / 2;
                        }
                    } else {
                        kind = ConnectionPath.Kind.FourAngles;

                        x1 = nsx + scale(ENDPOINTS_PADDING);
                        x2 = t.x - scale(ENDPOINTS_PADDING);
                        y = (t.y + nsy) / 2;
                        if (y >= t.y && y - scale(ENDPOINTS_PADDING) < t.y) {
                            y = t.y + scale(ENDPOINTS_PADDING);
                        } else if (y < t.y && y + scale(ENDPOINTS_PADDING) > t.y) {
                            y = t.y - scale(ENDPOINTS_PADDING);
                        }
                    }
                    break;
                case FourAngles:
                case MoreThanFour:
                    if (nsx <= x2 + s.x - x1) {
                        kind = ConnectionPath.Kind.TwoAngles;
                        if (t.x - s.x != x2 - x1) {
                            x1 = nsx + (t.x - nsx) * (x1 - s.x) / (t.x - x2 + x1 - s.x);
                        } else {
                            x1 = nsx;
                        }
                    } else {
                        kind = ConnectionPath.Kind.FourAngles;
                        x1 = nsx + x1 - s.x;
                    }
                    break;
            }
            FBConnectionPath newConnectionPath = new FBConnectionPath(newSource, t, kind, x1, y, x2);

            return magnetized(newConnectionPath);
        }
    }

    private int getFontSize() {
        return LayoutUtil.INSTANCE.getFontSize(myFakeCell.getStyle());
    }

    private int scale(int size) {
        return size * getFontSize() / EditorSettings.getInstance().getFontSize();
    }

    private int getTargetHover(Point s, Point t, List<Point> bendPoints) {
        return Math.max(t.x - scale(ENDPOINT_HOVER_LENGTH), bendPoints.isEmpty() ? (s.x + t.x) / 2 : bendPoints.get(bendPoints.size() - 1).x);
    }

    private int getSourceHover(Point s, Point t, List<Point> bendPoints) {
        return Math.min(s.x + scale(ENDPOINT_HOVER_LENGTH), bendPoints.isEmpty() ? (s.x + t.x) / 2 : bendPoints.get(0).x);
    }

    private static final class CONCEPTS {
        /*package*/ static final SConcept ConnectionPath$IA = MetaAdapterFactory.getConcept(0x6594f3404d734027L, 0xb7d3c6ca2e70a53bL, 0x3bbd127730611f52L, "org.fbme.ide.iec61499.lang.structure.ConnectionPath");
    }
}
